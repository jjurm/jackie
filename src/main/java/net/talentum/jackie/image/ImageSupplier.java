package net.talentum.jackie.image;

import java.awt.image.BufferedImage;

/**
 * A class that is capable of returning an image whenever requested. Used in
 * context of retrieving webcam images.
 * 
 * @author JJurM
 */
public interface ImageSupplier {

	/**
	 * Returns an image.
	 * 
	 * @return
	 */
	public BufferedImage getImage();

}
