package net.talentum.jackie.image.output;

import java.awt.image.BufferedImage;

import net.talentum.jackie.system.StrategyComparatorPreview;

/**
 * Abstract class for objects that are capable of turning {@link Moment} into a
 * {@link BufferedImage}. Used in {@link StrategyComparatorPreview}.
 * 
 * @author JJurM
 */
public abstract class ImageOutput {

	protected String name;

	public ImageOutput(String name) {
		this.name = name;
	}

	/**
	 * Takes {@code Moment} and returns painted image to be displayed.
	 * 
	 * @param moment
	 * @return
	 */
	public abstract BufferedImage process(BufferedImage moment);

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

}
