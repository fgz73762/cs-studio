package org.csstudio.archive.reader.fastarchiver.fast_archive_requests;

import java.io.IOException;
import java.io.OutputStream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Class with common methods for communicating with the fast archiver 
 * @author pvw24041
 *
 */

public abstract class FARequest {
	
	protected static final int TIMESTAMP_BYTE_LENGTH = 18;
	protected static final String CHAR_ENCODING = "US-ASCII";
	protected String host; // "pc0044" or "fa-archiver"
	protected int port;
	protected enum Decimation {UNDEC, DEC, DOUBLE_DEC}; 


	public FARequest(String url){
		Pattern pattern = Pattern.compile("fads://([A-Za-z0-9-]+)(:[0-9]+)?");
		Matcher matcher = pattern.matcher(url);
		if (matcher.matches()) {
			this.host = matcher.group(1);
			if (matcher.group(2) == null)
				this.port = 8888;
			else
				this.port = Integer.parseInt(matcher.group(2).substring(1));
		} else {
			//
		}

	}
	
	
	// METHODS USING SOCKET STREAMS

	/**
	 * Writes a message to the fa-archiver to request data, only to be used by
	 * methods creating sockets
	 */
	protected static void writeToArchive(String message, OutputStream outToServer) {
		try {
			outToServer.write(message.getBytes(CHAR_ENCODING));
			outToServer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}