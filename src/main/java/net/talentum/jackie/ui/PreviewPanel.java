package net.talentum.jackie.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.sarxos.webcam.WebcamPicker;

import net.talentum.jackie.hardware.WebcamDriver;

public class PreviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JFrame parent;

	private WebcamPicker webcamSelection;
	private JComboBox<DimensionComboBoxItem> viewSizeSelection;
	private JPanel imagePaneA;
	private JPanel imagePaneB;

	/**
	 * Create the panel.
	 */
	public PreviewPanel(JFrame parent) {
		this.parent = parent;
		
		setLayout(new BorderLayout(0, 0));
		
		createElements();
	}
	
	public void init() {
		updateWebcam();
	}
	
	private void createElements() {
		JPanel menuPane = new JPanel();
		add(menuPane, BorderLayout.NORTH);
		menuPane.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		webcamSelection = new WebcamPicker(WebcamDriver.getWebcams());
		webcamSelection.setPreferredSize(new Dimension(160, 20));
		webcamSelection.addActionListener(e -> updateWebcam());
		menuPane.add(webcamSelection);

		viewSizeSelection = new JComboBox<DimensionComboBoxItem>();
		viewSizeSelection.setPreferredSize(new Dimension(80, 20));
		viewSizeSelection.addActionListener(e -> updateViewSize());
		menuPane.add(viewSizeSelection);

		JButton btnTakeImage = new JButton("Take image");
		menuPane.add(btnTakeImage);

		JPanel imageHolder = new JPanel();
		add(imageHolder, BorderLayout.CENTER);
		imageHolder.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		imagePaneA = new JPanel();
		imagePaneA.setBackground(Color.GRAY);
		imageHolder.add(imagePaneA);
		imagePaneA.setLayout(null);

		imagePaneB = new JPanel();
		imageHolder.add(imagePaneB);
		imagePaneB.setLayout(null);
	}

	void updateWebcam() {
		viewSizeSelection.setModel(new DefaultComboBoxModel<DimensionComboBoxItem>(
				Arrays.stream(webcamSelection.getSelectedWebcam().getViewSizes())
						.map((Dimension dimension) -> new DimensionComboBoxItem(dimension))
						.toArray(size -> new DimensionComboBoxItem[size])));
		updateViewSize();
	}

	void updateViewSize() {
		Stream.of(imagePaneA, imagePaneB).forEach(pane -> {
			pane.setPreferredSize(((DimensionComboBoxItem) viewSizeSelection.getSelectedItem()).getValue());
			pane.revalidate();
			pane.repaint();
		});
		parent.pack();
	}

}
