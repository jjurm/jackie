package net.talentum.jackie.moment.module;

import java.awt.image.BufferedImage;

/**
 * Module interface that takes an image and returns boolean array.
 * 
 * @author JJurM
 */
public interface BooleanImageFilterModule {

	public boolean[][] filter(BufferedImage image);

}
