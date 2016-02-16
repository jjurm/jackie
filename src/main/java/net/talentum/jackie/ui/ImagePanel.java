package net.talentum.jackie.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Supplier<BufferedImage> imageSupplier;

	public ImagePanel(Supplier<BufferedImage> imageSupplier) {
		this.imageSupplier = imageSupplier;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage image = imageSupplier.get();
		g.drawImage(image, 0, 0, null);
	}

}
