package net.talentum.jackie.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPicker;

import net.talentum.jackie.system.ImageRecognitionPreview;

public class PreviewPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JFrame parent;

	private WebcamPicker webcamSelection;
	private JComboBox<DimensionComboBoxItem> viewSizeSelection;
	private JPanel imagePaneA;
	private JPanel imagePaneB;

	private List<Consumer<Webcam>> webcamChangedListeners = new ArrayList<Consumer<Webcam>>();
	private List<Consumer<BufferedImage>> processImageListeners = new ArrayList<Consumer<BufferedImage>>();

	private Webcam lastWebcam = null;
	private BufferedImage lastImage;

	/**
	 * Create the panel.
	 */
	public PreviewPanel(JFrame parent) {
		this.parent = parent;

		setLayout(new BorderLayout(0, 0));

		createElements();
	}

	public void addWebcamChangedListener(Consumer<Webcam> listener) {
		webcamChangedListeners.add(listener);
	}

	public void addProcessImageListener(Consumer<BufferedImage> listener) {
		processImageListeners.add(listener);
	}

	public void init() {
		updateWebcam();
	}

	private void createElements() {
		JPanel menuPane = new JPanel();
		add(menuPane, BorderLayout.NORTH);
		menuPane.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		List<Webcam> webcams = Webcam.getWebcams();
		webcamSelection = new WebcamPicker(webcams);
		webcamSelection.setSelectedIndex(webcams.size() - 1);
		webcamSelection.setPreferredSize(new Dimension(160, 20));
		webcamSelection.addActionListener(e -> updateWebcam());
		menuPane.add(webcamSelection);

		viewSizeSelection = new JComboBox<DimensionComboBoxItem>();
		viewSizeSelection.setPreferredSize(new Dimension(80, 20));
		viewSizeSelection.addActionListener(e -> updateViewSize());
		menuPane.add(viewSizeSelection);

		JButton btnTakeImage = new JButton("Take image");
		btnTakeImage.addActionListener(e -> takeImage());
		menuPane.add(btnTakeImage);

		JButton btnProcessImage = new JButton("Process image");
		btnProcessImage.addActionListener(e -> processImage());
		menuPane.add(btnProcessImage);

		JPanel imageHolder = new JPanel();
		add(imageHolder, BorderLayout.CENTER);
		imageHolder.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		imagePaneA = new ImagePanel(() -> lastImage);
		imagePaneA.setBackground(Color.GRAY);
		imageHolder.add(imagePaneA);
		imagePaneA.setLayout(null);

		imagePaneB = new ImagePanel(() -> ImageRecognitionPreview.getLastInstructionPaintedImage());
		imageHolder.add(imagePaneB);
		imagePaneB.setLayout(null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(lastImage, 0, 0, null);
	}

	void updateWebcam() {
		Webcam webcam = webcamSelection.getSelectedWebcam();

		if (webcam != lastWebcam) {
			lastWebcam = webcam;
			viewSizeSelection.setModel(new DefaultComboBoxModel<DimensionComboBoxItem>(Arrays
					.stream(webcam.getViewSizes()).map((Dimension dimension) -> new DimensionComboBoxItem(dimension))
					.toArray(size -> new DimensionComboBoxItem[size])));
			viewSizeSelection.setSelectedIndex(webcam.getViewSizes().length - 1);

			for (Consumer<Webcam> listener : webcamChangedListeners) {
				listener.accept(webcam);
			}

			updateViewSize();
		}
	}
	
	public Dimension getViewSize() {
		return ((DimensionComboBoxItem) viewSizeSelection.getSelectedItem()).getValue();
	}

	void updateViewSize() {
		Webcam webcam = webcamSelection.getSelectedWebcam();

		Dimension viewSize = getViewSize();
		webcam.setViewSize(viewSize);
		Dimension d = new Dimension(viewSize.width, viewSize.height);
		Stream.of(imagePaneA, imagePaneB).forEach(pane -> {
			pane.setPreferredSize(d);
			pane.revalidate();
			pane.repaint();
		});
		parent.pack();
	}

	void takeImage() {
		lastWebcam.open();
		lastImage = webcamSelection.getSelectedWebcam().getImage();
		imagePaneA.repaint();
		lastWebcam.close();
	}

	void processImage() {
		if (lastImage == null)
			return;
		for (Consumer<BufferedImage> listener : processImageListeners) {
			listener.accept(lastImage);
		}
	}
	
	public void repaintResults() {
		imagePaneB.repaint();
	}

}
