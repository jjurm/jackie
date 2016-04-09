package net.talentum.jackie.image.supplier;

import java.awt.image.BufferedImage;

import net.talentum.jackie.module.impl.BufferedImageMatConverterModule;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;


/**
 * Class for getting images from cameras using OpenCV.
 * @author padr31
 *
 */
public class OpenCVImageSupplier implements ImageSupplier {

	private VideoCapture videoCapture;
	private BufferedImageMatConverterModule bimc;
	
	public OpenCVImageSupplier(VideoCapture videoCapture, BufferedImageMatConverterModule bimc) {
		this.videoCapture = videoCapture;
		this.bimc = bimc;

	}
	
	
	/**
	 * This method returns reads an actual image from the camera.
	 * @return {@link BufferedImage} if the camera is open, otherwise returns null.
	 */
	@Override
	public BufferedImage getImage() {
		 Mat image = new Mat();
		 videoCapture.read(image);
		 
		 return(bimc.toBufferedImage(image));
	}
	
	@Override
	public void close() {
		videoCapture.release();
	}
	
	public static class Provider extends ImageSupplierProvider{

		public Provider(String name) {
			super(name);
		}

		@Override
		public ImageSupplier provide(String param) {
			VideoCapture videoCapture = new VideoCapture(Integer.parseInt(param));
			
			return new OpenCVImageSupplier(videoCapture, new BufferedImageMatConverterModule());
		}
		
	}

}
