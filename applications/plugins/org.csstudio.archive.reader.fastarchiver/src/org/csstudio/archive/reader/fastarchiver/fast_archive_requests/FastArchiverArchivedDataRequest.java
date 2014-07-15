package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.reader.fastarchiver.FastArchiverValueIterator;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;

import from_fa_archiver.DataNotAvailableException;
import from_fa_archiver.EndOfStreamException;

/**
 * Class to communicate with Fast Archiver about archived data requests. 
 * @author Friederike Johlinger
 */

public class FastArchiverArchivedDataRequest extends FastArchiverRequest{
	
	public enum ValueType {MEAN, MIN, MAX, STD}
	ValueType defaultValueType;
	
	//need to change for optional host????
	public FastArchiverArchivedDataRequest(String url) {
		super(url);
		defaultValueType = ValueType.MEAN;
	}
	
	// PUBLIC METHODS
	/**
	 * Used to get undecimated data out of the archiver.
	 * @param name String 
	 * @param start timestamp of first sample
	 * @param end timestamp of last sample
	 * @return
	 */
	public ValueIterator getRawValues(String name, Timestamp start,
			Timestamp end) {
		// !!need to make coordinate dependent on name
		int coordinate = 0;
		
		// create request string
		int bpm = bpmMapping.get(name);
		String request = translate(start, end, bpm, Decimation.UNDEC, defaultValueType);
		// make request, returning ValueIterator 

		try {
			return getValues(request, start, end, coordinate);
		} catch (IOException | EndOfStreamException | DataNotAvailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ValueIterator getOptimisedValues(String name, Timestamp start,
			Timestamp end, int count) {
		// !!need to make coordinate dependent on name
		int coordinate = 0;
		
		// create request string
		int bpm = bpmMapping.get(name);
		Decimation dec = calculateDecimation(start, end, count);
		String request = translate(start, end, bpm, dec, defaultValueType);
		// make request, returning ValueIterator 

		try {
			
			return getValues(request, start, end, coordinate);
		} catch (IOException | EndOfStreamException | DataNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	// SOCKET METHODS
	
	//! need to implement
	private Decimation calculateDecimation(Timestamp start, Timestamp end,
			int count) {
		//Not entirely sure if actual socket method. Might need frequency and decimation values
		return Decimation.DOUBLE_DEC;
	}
	/**
	 * @param coordinate 0 or 1 indicating the x or y coordinate, respectively
	 * data is form [zero char][sample count(8b)][block size(4b)][offset(4b)] ([timestamp(8b)][duration(4b)][datasets(4b*blocksize)])*N 
	 */
	private ValueIterator getValues(String request, Timestamp start, Timestamp end, int coordinate) throws UnknownHostException, IOException, EndOfStreamException, DataNotAvailableException {
		//check if data is available --> use A in request instead
		/*if (!dataAvailable(start, end)){
			System.out.println("Not all data available");
			throw new DataNotAvailableException("Timeframe specified not completely archived");
		}*/
		
		//System.out.println("getValues is called");
		
		// create socket
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();
		
		// write request to archiver
		writeToArchive(request, outToServer);
		System.out.println(request);

		/* Check if first byte reply is zero -> data is sent */
		// change into ifError(inFromServer);
		byte[] firstChar;
		firstChar = readNumBytes(1, inFromServer);
		// checking for error messages
		if (firstChar[0] != '\0') {
			String message = getServerErrorMessage(firstChar[0], inFromServer);
			socket.close();
			throw new DataNotAvailableException(message);
		}

		/* Get number of samples sent, first 8 bytes */
		byte[] bA;
		bA = readNumBytes(8, inFromServer);
		long sampleCount = longFromByteArray(bA);
		
		/* Get block size */
		bA = readNumBytes(4, inFromServer);
		int blockSize = intFromByteArray(bA);
		
		/* Get offset */
		bA = readNumBytes(4, inFromServer);
		int offset = intFromByteArray(bA);

		/* Get actual data */
		int sampleLength = 12 + 8 * blockSize;  // 8 bytes timestamp + 4 bytes duration + (4 bytes * 2 coordinates) * blockSize 
		int numBytesToRead = calcDataLength((int)sampleCount, sampleLength, blockSize, offset);
		System.out.println("NBtR: "+numBytesToRead + ", samplecount: " +sampleCount+ ", offset: "+offset);
		bA = readNumBytes(numBytesToRead, inFromServer);
		
		socket.close();
		
		//convert to readable 
		int value;
		long timestamp = 0;//should be initialised
		int duration = 0; // should be initialised
		VType[] values = new ArchiveVNumber[(int)sampleCount];
		byte[] miniBuffer;
		int indexBA = 0;
		//get first timeStamp and duration, if start with offset:
		if (offset != 0) {
			miniBuffer = Arrays.copyOfRange(bA, indexBA, indexBA + 8);
			timestamp = longFromByteArray(miniBuffer);
			indexBA += 8;

			miniBuffer = Arrays.copyOfRange(bA, indexBA, indexBA + 4);
			duration = intFromByteArray(miniBuffer); 
			indexBA += 4;
			//System.out.println("TimeStamp: "+timestamp+", Duration: "+duration+", offset: "+offset);
		}
		for(int indexValues = 0; indexValues < sampleCount; indexValues += 1){
			//when to read in timeStamps and durations
			if ((indexValues+offset)%blockSize == 0){
				miniBuffer = Arrays.copyOfRange(bA, indexBA, indexBA+8);
				timestamp = longFromByteArray(miniBuffer);
				indexBA += 8;
				
				miniBuffer = Arrays.copyOfRange(bA, indexBA, indexBA+4);
				duration = intFromByteArray(miniBuffer);
				indexBA += 4;
			}
			miniBuffer = Arrays.copyOfRange(bA, indexBA+coordinate*4, indexBA+4+coordinate*4);
			value = intFromByteArray(miniBuffer);
			indexBA += 8;
			System.out.println("Value: "+value+", TimeStamp: "+timestamp+", Duration: "+duration);
			
			timestamp += (duration*((offset+indexValues)%blockSize))/blockSize;
			values[indexValues] = new ArchiveVNumber(timeStampFromMicroS(timestamp), AlarmSeverity.NONE, "status", null, value);
		}
		
		// create valueIterator for this format 
		System.out.println(request);
		return new FastArchiverValueIterator(values); 
	}
		
	private byte[] getTimeStamp(String request) throws UnknownHostException,
			IOException {
		// get time for oldest data (seconds from Unix UTC epoch)
		byte[] buffer = new byte[TIMESTAMP_BYTE_LENGTH];
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();
		// sent request to archive
		writeToArchive(request, outToServer);
		inFromServer.read(buffer);
		socket.close();
		return buffer;

	}
	// METHODS USING SOCKET STREAMS

	/**
	 * Returns a byte Array with length as specified filled with data from the
	 * InputStream
	 * @return byte[]
	 */
	private byte[] readNumBytes(int length, InputStream inFromServer)
			throws EndOfStreamException {
		//System.out.println("length: "+length);
		byte[] buffer = new byte[length];
		int read = 0;
		int lastRead = 0;

		try {
			while (read != length) {
				lastRead = inFromServer.read(buffer, read, (length - read));
				if (lastRead != -1) {
					read += lastRead;
				} else {
					throw new EndOfStreamException("Not enough bytes to read");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
	/**
	 * In case the message written to the server is invalid, it returns an error
	 * message. This function is used to convert this message to a String.
	 */
	private String getServerErrorMessage(byte firstChar,
			InputStream inFromServer) {
		// read data from stream
		int length = 100; // !! What length should be used?
		byte[] buffer = new byte[length];
		buffer[0] = firstChar;

		int read = 1;
		int lastRead = 0;
		/*try {
			read = inFromServer.read(buffer);
			buffer = Arrays.copyOfRange(buffer, 0, read);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		 try {
			while (read != length) {
				lastRead = inFromServer.read(buffer, read, (length - read));
				if (lastRead != -1) {
					read += lastRead;
				} else {
					break;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} 

		// convert to String and return
		String message = new String(buffer);

		return message;
	}
		
	// STATIC METHODS
	
	/**
	 * Translates the given dates and bpm number into a String for a request to
	 * the fa-archiver
	 */
	private static String translate(Timestamp start, Timestamp end, int bpm, Decimation dec, ValueType set) {
		// Needs format
		// "R[decimation]M[number of BPM][start time in seconds from epoch]E[end time in seconds from epoch]N[include sample count]A[all data available]\n"
		String decimation = "F"; // need way to make this variable always dependent
		if (dec == Decimation.UNDEC){
			decimation = "F";
		} else if (dec == Decimation.DEC){
			decimation = "D";
		} else if (dec == Decimation.DOUBLE_DEC){
			decimation = "DD";
		}
		System.out.println("Decimation = "+ decimation);
		String dataSet = "";
		if (dec == Decimation.UNDEC){
			dataSet = "";
		} else if (set == ValueType.MEAN){
			dataSet = "F1";
		} else if (set == ValueType.MIN){
			dataSet = "F2";
		} else if (set == ValueType.MAX){
			dataSet = "F3";
		} else if (set == ValueType.STD){
			dataSet = "F4";
		}
		System.out.println("dataSet = "+dataSet); //RFM1S1405096100ES1405096101
		return String.format("R%s%sM%dS%sES%sNATE\n", decimation, dataSet, bpm, start.getSec(), end.getSec());
		//return "RDDF1M4S1405417272ES1405417274NATE\n";
	}
	
	/**
	 * Forms a long integer from a byte array
	 */
	private static long longFromByteArray(byte[] bA) {
		ByteBuffer bb = ByteBuffer.wrap(bA);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getLong();
	}

	/**
	 * Forms an integer from a byte array
	 */
	private static int intFromByteArray(byte[] bA) {
		ByteBuffer bb = ByteBuffer.wrap(bA);
		bb.position(0);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}
	
	private static int calcDataLength(int sampleCount, int sampleLength, int blockSize, int offset) {
		int length = 0;
		if (sampleCount >= blockSize - offset) {
			length += 12 + (blockSize - offset) * 8; // length first block
			length += sampleLength
					* ((sampleCount - (blockSize - offset)) / blockSize); // complete
																			// blocks
			if ((sampleCount + offset) / blockSize != 0)
				length += 12 + ((sampleCount + offset) % blockSize) * 8;
		} else
			length += 12 + 8 * sampleCount;
		return length;
	}

	private static Timestamp timeStampFromMicroS(long timeInMicroS) {
		long seconds = timeInMicroS/1000000;
		int nanoseconds = (int)(timeInMicroS%1000000)*1000;
		return Timestamp.of(seconds, nanoseconds);
	}

	// OTHER METHODS
	
	/**
	 * Checks if the specified time interval starts after oldest data and stops
	 * before latest data
	 * 
	 * @throws DataNotAvailableException
	 */
	private boolean dataAvailable(Timestamp start, Timestamp end)
			throws DataNotAvailableException {

		// get time for oldest data (seconds from Unix UTC epoch)
		byte[] buffer;
		try {
			buffer = getTimeStamp("CT\n");
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataNotAvailableException("Could not get timestamp");
		}
		// Convert to long and compare to wanted start
		String StringFromBuffer = new String(buffer);
		long earliestSampleTime = new Long(StringFromBuffer.substring(0, 10));
		boolean tooEarly = (start.getSec()) < earliestSampleTime;

		// get time for latest data (seconds from Unix UTC epoch)
		try {
			buffer = getTimeStamp("CU\n");
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataNotAvailableException("Could not get timestamp");
		}
		// Convert to long and compare to wanted end
		StringFromBuffer = new String(buffer);
		long latestSampleTime = new Long(StringFromBuffer.substring(0, 10));
		boolean tooLate = (end.getSec()) > latestSampleTime;
		return (!tooEarly && !tooLate && start.getSec() <= end.getSec());
	}
	
}
