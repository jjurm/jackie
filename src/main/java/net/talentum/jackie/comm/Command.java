package net.talentum.jackie.comm;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Interface representing classes that are able to process commands from an
 * input.
 * 
 * @author JJurM
 */
@FunctionalInterface
public interface Command {

	/**
	 * Method for the object to communicate with a client through the i/o
	 * streams.
	 * 
	 * @param args arguments
	 * @param br Input
	 * @param pw Output
	 * @throws StreamCloseRequest is thrown if there is the end of the stream or
	 *             the client requested to close the stream.
	 */
	void process(String[] args, BufferedReader br, PrintWriter pw) throws StreamCloseRequest;

}
