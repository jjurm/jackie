package net.talentum.jackie.robot.strategy;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import net.talentum.jackie.module.impl.BufferedImageMatConverterModule;
import net.talentum.jackie.robot.Moment;
import net.talentum.jackie.robot.RobotInstruction;

public class BallFinderStrategy extends RobotStrategy {

	@Override
	public RobotInstruction evaluate() {
		return null;
	}

	public static class ImageOutput extends net.talentum.jackie.image.output.ImageOutput {

		private BufferedImageMatConverterModule bimcModule;

		public ImageOutput(String name,
				BufferedImageMatConverterModule bimcModule) {
			super(name);
			this.bimcModule = bimcModule;
		}

		@Override
		public BufferedImage process(Moment moment) {
			Mat frame = bimcModule.toMat(moment.image);
			
			Mat thresholded = new Mat();
			Mat thresholded2 = new Mat();
			
			Scalar hsv_min = new Scalar(0, 0, 0, 0);  
		    Scalar hsv_max = new Scalar(255, 50, 100, 0);  
		    Scalar hsv_min2 = new Scalar(0, 120, 100, 0);  
		    Scalar hsv_max2 = new Scalar(255, 255, 255, 0); 
		     
		    Core.inRange(frame, hsv_min, hsv_max, thresholded);           
	        Core.inRange(frame, hsv_min2, hsv_max2, thresholded2);  
	        Core.bitwise_or(thresholded, thresholded2, thresholded);
	        
			Mat canny = new Mat();
			Imgproc.Canny(frame, canny, 0, 200);
			
			
			Mat grey = new Mat();
			Imgproc.cvtColor(frame, grey, Imgproc.COLOR_RGB2GRAY);
			
			Mat circles = new Mat();
	        Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded.height()/4, 500, 50, 0, 0); 
	        
           int rows = circles.rows();  
           int elemSize = (int)circles.elemSize(); //Returns 12 (3 * 4bytes in a float)  
           float[] data2 = new float[rows * elemSize/4];  
           if (data2.length>0){  
             circles.get(0, 0, data2);  
                           
             for(int i=0; i<data2.length; i=i+3) {  
               Point center= new Point(data2[i], data2[i+1]);  
               Imgproc.ellipse(frame, center, new Size((double)data2[i+2], (double)data2[i+2]), 0, 0, 360, new Scalar( 255, 255, 0 ), 4, 8, 0 );  
             }  
           }
           	
			return bimcModule.toBufferedImage(thresholded);
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getName();
		}

	}

}
