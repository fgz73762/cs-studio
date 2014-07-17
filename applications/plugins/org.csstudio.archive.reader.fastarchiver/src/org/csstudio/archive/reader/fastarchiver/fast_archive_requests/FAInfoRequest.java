package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import from_fa_archiver.EndOfStreamException;

/**
 * Class to communicate with Fast Archiver about non-data requests. 
 * @author pvw24041
 */

public class FAInfoRequest extends FARequest{

	public FAInfoRequest(String url) {
		super(url);
	}


	/* METHODS USING SOCKETS DIRECTLY */
	/**
	 * Creates a list of all BPMs in the archiver, as returned by the archiver
	 * @return String[] of all names
	 */
	private String[] getAllBPMs() throws UnknownHostException, IOException, EndOfStreamException {
		Socket socket = new Socket(host, port);
		OutputStream outToServer = socket.getOutputStream();
		InputStream inFromServer = socket.getInputStream();		
		
		// get String Array of fa-ids
		writeToArchive("CL\n", outToServer);
		
		byte[] buffer;
		StringBuffer allIds = new StringBuffer();
		String infoFromServer = "";
		int readNumBytes = 0;
		while (readNumBytes != -1){
			buffer = new byte[16384];
			allIds.append(infoFromServer);
			readNumBytes = inFromServer.read(buffer);
			infoFromServer = new String(buffer);
			//System.out.println(infoFromServer);
			//System.out.println();
			
		}
		//System.out.println("ID's: "+allIds);
		
		socket.close();
		String [] names = allIds.toString().split("\n");

		// to remove trailing newline char
		return Arrays.copyOfRange(names, 0, names.length-1);
	}
	
	/* OTHER METHODS */
	/**
	 * Creates a hasmap of all BPMs in the archiver, with names as keys and BPM number and coordinates in an int array as values. 
	 * @return Hashmap with names as keys and BPM number and coordinates in an int array as values. 
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws EndOfStreamException
	 */
	public HashMap<String, int[]> createMapping() throws UnknownHostException, IOException, EndOfStreamException {
		String[] allBPMs = getAllBPMs();
		HashMap<String, int[]> bpmMapping = new HashMap<String, int[]>();
		
		// split descriptions
		boolean stored;
		int bpmId;
		String coordinate1;
		String coordinate2;
		String name;
		String newName;
		
		Pattern pattern = Pattern.compile("(\\*| )([0-9]+) ([^ ]+) ([^ ]+) ([^ ]*)");
		
		for (String description: allBPMs){
			Matcher matcher = pattern.matcher(description);
			if (matcher.matches()) {
				stored = matcher.group(1).equals("*");
				bpmId = Integer.parseInt(matcher.group(2));
				coordinate1 = matcher.group(3);
				coordinate2 = matcher.group(4);
				name = matcher.group(5);
				if (name.equals(""))
					name = new String("FA-ID-"+bpmId);
				
				if (stored){
					if (coordinate1.equals(coordinate2)){
						coordinate1 = coordinate1+"1";
						coordinate2 = coordinate2+"2";
					}
					newName = new String(name+":"+coordinate1);
					bpmMapping.put(newName, new int[]{bpmId, 0});
					
					newName = new String(name+":"+coordinate2);
					bpmMapping.put(newName, new int[]{bpmId, 1});
			}
				
				
			} else {
				System.out.println("BPM did not have valid name");
			}
			
		}

		return bpmMapping;
	}
	

}