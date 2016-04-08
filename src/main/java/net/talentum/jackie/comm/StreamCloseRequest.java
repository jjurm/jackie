package net.talentum.jackie.comm;

/**
 * Class interpreting request for stream close.
 * 
 * @author JJurM
 */
public class StreamCloseRequest extends Exception {
	private static final long serialVersionUID = 1L;

	public StreamCloseRequest() {
		super("Request for stream close");
	}

}
