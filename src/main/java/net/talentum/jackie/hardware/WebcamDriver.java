package net.talentum.jackie.hardware;

import java.util.List;

import com.github.sarxos.webcam.Webcam;

public class WebcamDriver {

	static List<Webcam> webcams;

	public static void init() {
		// retrieve list of webcams
		webcams = Webcam.getWebcams();
	}
	
	public static List<Webcam> getWebcams() {
		return webcams;
	}

	Webcam webcam;

	public WebcamDriver(int index) {
		webcam = webcams.get(index);
		webcam.open();
	}

	public void close() {
		webcam.close();
	}

}
