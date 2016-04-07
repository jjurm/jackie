package net.talentum.jackie.module.impl;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Class for converting {@link org.opencv.core.Mat} to {@link BufferedImage} and
 * vice-versa.
 * 
 * @author padr31
 *
 */
public class BufferedImageMatConverterModule {

	public Mat toMat(BufferedImage im) {
		// Convert INT to BYTE not needed for the second (commented-out) method - I have no idea why
		im = toBufferedImageOfType(im, BufferedImage.TYPE_3BYTE_BGR);

		byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer())
				.getData();

		Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);

		image.put(0, 0, pixels);

		return image;
	}
	
	public BufferedImage toBufferedImage(Mat matrix) {  
		int cols = matrix.cols();  
		int rows = matrix.rows();  
		int elemSize = (int)matrix.elemSize();  
		byte[] data = new byte[cols * rows * elemSize];  
		int type;  
		matrix.get(0, 0, data);  
		switch (matrix.channels()) {  
		case 1:  
			type = BufferedImage.TYPE_BYTE_GRAY;  
			break;  
		case 3:  
			type = BufferedImage.TYPE_3BYTE_BGR;  
			// bgr to rgb  
			byte b;  
			for(int i=0; i<data.length; i=i+3) {  
				b = data[i];  
				data[i] = data[i+2];  
				data[i+2] = b;  
			}  
			break;  
		default:  
			return null;  
		}  
		BufferedImage image2 = new BufferedImage(cols, rows, type);  
		image2.getRaster().setDataElements(0, 0, cols, rows, data);  
		return image2;  
	} 
/*//This is not converting bgr to rgb
	public BufferedImage toBufferedImage(Mat in) {
		BufferedImage out;
		int cols = in.cols();
		int rows = in.rows();
		byte[] data = new byte[cols * rows * (int) in.elemSize()];
		int type;
		in.get(0, 0, data);

		if (in.channels() == 1)
			type = BufferedImage.TYPE_BYTE_GRAY;
		else
			type = BufferedImage.TYPE_3BYTE_BGR;

		out = new BufferedImage(cols, rows, type);

		out.getRaster().setDataElements(0, 0, cols, rows, data);
		return out;
	}
*/
	/**
	 * Converting between types of {@link BufferedImage}
	 * @param original
	 * @param type 
	 * @return
	 */
	public BufferedImage toBufferedImageOfType(BufferedImage original, int type) {
		if (original == null) {
			throw new IllegalArgumentException("original == null");
		}

		if (original.getType() == type) {
			return original;
		}

		BufferedImage image = new BufferedImage(original.getWidth(),
				original.getHeight(), type);
		image.getGraphics().drawImage(original, 0, 0, null);

		return image;
	}

}
