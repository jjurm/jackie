package net.talentum.jackie.ui;

import java.awt.Dimension;

public class DimensionComboBoxItem {

	private Dimension dimension;

	public DimensionComboBoxItem(Dimension dimension) {
		this.dimension = dimension;
	}

	public Dimension getValue() {
		return dimension;
	}

	@Override
	public String toString() {
		return ((int) dimension.getWidth()) + "x" + ((int) dimension.getHeight());
	}

}
