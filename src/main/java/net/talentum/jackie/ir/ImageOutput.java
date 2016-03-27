package net.talentum.jackie.ir;

import java.awt.image.BufferedImage;

import net.talentum.jackie.moment.Moment;

public abstract class ImageOutput {

	private String name;

	public ImageOutput(String name) {
		this.name = name;
	}

	/**
	 * Takes {@code Moment} and returns painted image to be displayed.
	 * 
	 * @param moment
	 * @return
	 */
	public abstract BufferedImage process(Moment moment);

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

}
