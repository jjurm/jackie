package net.talentum.jackie.moment.module;

import java.awt.image.BufferedImage;

/**
 * Module interface that takes one image and returns another, modified.
 * 
 * @author JJurM
 */
public interface ImageModifierModule {

	public BufferedImage modify(BufferedImage image);
	
}
