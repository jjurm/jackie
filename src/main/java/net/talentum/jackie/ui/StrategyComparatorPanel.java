package net.talentum.jackie.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPicker;

import net.talentum.jackie.ir.ImageRecognitionOutput;
import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.SensorData;

public class StrategyComparatorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private ImageRecognitionOutput[] irOutputs;
	private CopyOnWriteArrayList<IROutputFrame> irOutputFrames = new CopyOnWriteArrayList<IROutputFrame>();
	private Webcam lastWebcam;
	private boolean webcamOpen = false;
	static ExecutorService executor = Executors.newSingleThreadExecutor();
	private boolean startedContinuous = false;
	static int openFrameCount = 0;

	private JPanel menu1;
	private WebcamPicker webcamSelection;
	private JComboBox<DimensionComboBoxItem> viewSizeSelection;
	private JButton btnOpenClose;
	private JPanel menu2;
	private JComboBox<ImageRecognitionOutput> irOutputSelection;
	private JButton btnAddOutput;
	private Component horizontalStrut;
	private JPanel centerPanel;
	private JButton btnShot;
	private JButton btnStart;
	private JButton btnStop;
	private JLabel lblFps;
	private Component horizontalStrut_1;
	private JLabel lblScale;
	private JComboBox<Double> scaleSelection;
	private JDesktopPane desktopPane;

	public StrategyComparatorPanel(ImageRecognitionOutput[] irOutputs) {
		this.irOutputs = irOutputs;

		setLayout(new BorderLayout(0, 0));
		createElements();
		webcamChanged();
	}

	private void createElements() {
		List<Webcam> webcams = Webcam.getWebcams();

		JPanel menuPanel = new JPanel();
		add(menuPanel, BorderLayout.NORTH);
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

		menu1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) menu1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		menuPanel.add(menu1);

		webcamSelection = new WebcamPicker(webcams);
		webcamSelection.setSelectedIndex(webcams.size() - 1);
		webcamSelection.setPreferredSize(new Dimension(160, 20));
		webcamSelection.addActionListener(e -> webcamChanged());
		menu1.add(webcamSelection);

		viewSizeSelection = new JComboBox<DimensionComboBoxItem>();
		viewSizeSelection.setPreferredSize(new Dimension(80, 20));
		viewSizeSelection.addActionListener(e -> viewSizeChanged());
		menu1.add(viewSizeSelection);

		btnOpenClose = new JButton("Open");
		btnOpenClose.addActionListener(e -> {
			btnOpenClose.setEnabled(false);
			if (!webcamOpen) {
				// camera closed
				setEnabledFor(false, webcamSelection, viewSizeSelection);

				executor.submit(() -> {
					final boolean opened = webcamSelection.getSelectedWebcam().open();
					webcamOpen = opened;

					EventQueue.invokeLater(() -> {
						if (opened)
							btnOpenClose.setText("Close");
						btnOpenClose.setEnabled(true);
					});
				});
			} else {
				// camera open
				stop();

				executor.submit(() -> {
					webcamOpen = false;
					webcamSelection.getSelectedWebcam().close();

					EventQueue.invokeLater(() -> {
						btnOpenClose.setText("Open");
						setEnabledFor(true, webcamSelection, viewSizeSelection, btnOpenClose);
					});
				});
			}
		});
		menu1.add(btnOpenClose);

		horizontalStrut_1 = Box.createHorizontalStrut(20);
		menu1.add(horizontalStrut_1);

		lblScale = new JLabel("Scale:");
		menu1.add(lblScale);

		scaleSelection = new JComboBox<Double>();
		scaleSelection.setModel(new DefaultComboBoxModel<Double>(new Double[] { 0.25, 0.5, 1.0, 2.0, 4.0 }));
		scaleSelection.setSelectedItem(1.0);
		scaleSelection.addActionListener(e -> scaledViewSizeChanged());
		menu1.add(scaleSelection);

		menu2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) menu2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		menuPanel.add(menu2);

		irOutputSelection = new JComboBox<ImageRecognitionOutput>();
		irOutputSelection.setPreferredSize(new Dimension(200, 20));
		irOutputSelection.setModel(new DefaultComboBoxModel<ImageRecognitionOutput>(irOutputs));
		irOutputSelection.setMaximumRowCount(20);
		menu2.add(irOutputSelection);

		btnAddOutput = new JButton("Add output");
		btnAddOutput.addActionListener(e -> {
			// create new strategy panel
			ImageRecognitionOutput irOutput = (ImageRecognitionOutput) irOutputSelection.getSelectedItem();
			IROutputFrame frame = new IROutputFrame(irOutput);
			irOutputFrames.add(frame);
			desktopPane.add(frame);
			try {
				frame.setSelected(true);
			} catch (Exception e1) {
			}

			desktopPane.revalidate();
			desktopPane.repaint();
		});
		menu2.add(btnAddOutput);

		horizontalStrut = Box.createHorizontalStrut(20);
		menu2.add(horizontalStrut);

		btnShot = new JButton("Shot");
		btnShot.addActionListener(e -> {
			if (!webcamOpen)
				return;
			setEnabledFor(false, btnShot, btnStart);
			executor.submit(() -> {
				shot();
				EventQueue.invokeLater(() -> setEnabledFor(true, btnShot, btnStart));
			});
		});
		menu2.add(btnShot);

		btnStart = new JButton("Start continuous");
		btnStart.addActionListener(e -> {
			if (!webcamOpen)
				return;
			setEnabledFor(false, btnShot, btnStart);
			btnStop.setEnabled(true);
			startedContinuous = true;
			executor.submit(() -> {
				while (startedContinuous) {
					if (!webcamOpen)
						break;
					shot();
				}
				setEnabledFor(true, btnShot, btnStart);
				btnStop.setEnabled(false);
			});
		});
		menu2.add(btnStart);

		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.addActionListener(e -> {
			stop();
		});
		menu2.add(btnStop);
		
		lblFps = new JLabel("");
		menu2.add(lblFps);

		centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));

		desktopPane = new JDesktopPane();
		centerPanel.add(desktopPane, BorderLayout.CENTER);
		desktopPane.setPreferredSize(new Dimension(500, 500));
	}

	private void webcamChanged() {
		Webcam webcam = webcamSelection.getSelectedWebcam();

		if (webcam != lastWebcam) {
			lastWebcam = webcam;
			viewSizeSelection.setModel(new DefaultComboBoxModel<DimensionComboBoxItem>(Arrays
					.stream(webcam.getViewSizes()).map((Dimension dimension) -> new DimensionComboBoxItem(dimension))
					.toArray(size -> new DimensionComboBoxItem[size])));
			viewSizeSelection.setSelectedIndex(webcam.getViewSizes().length - 1);
		}

		viewSizeChanged();
	}

	private Dimension getViewSize() {
		return ((DimensionComboBoxItem) viewSizeSelection.getSelectedItem()).getValue();
	}

	private double getScale() {
		return (Double) scaleSelection.getSelectedItem();
	}

	private Dimension getScaledViewSize() {
		Dimension orig = getViewSize();
		double scale = getScale();
		return new Dimension((int) (orig.width * scale), (int) (orig.height * scale));
	}

	private void viewSizeChanged() {
		Dimension viewSize = getViewSize();
		webcamSelection.getSelectedWebcam().setViewSize(viewSize);

		scaledViewSizeChanged();
	}

	private void scaledViewSizeChanged() {
		Dimension scaled = getScaledViewSize();
		irOutputFrames.stream().forEach(panel -> {
			panel.viewSizeChanged(scaled);
		});

		revalidate();
		repaint();
	}

	private void shot() {
		long t = System.currentTimeMillis();
		try {
			// construct Moment
			Webcam webcam = webcamSelection.getSelectedWebcam();
			BufferedImage image = webcam.getImage();
			SensorData sensorData = SensorData.collect();
			Moment moment = new Moment(image, sensorData);

			// arrange for processing
			for (IROutputFrame p : irOutputFrames) {
				p.receive(moment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lblFps.setText("FPS: "+1000/(System.currentTimeMillis() - t));
	}

	public void setEnabledFor(boolean enabled, JComponent... components) {
		for (JComponent component : components) {
			component.setEnabled(enabled);
		}
	}
	
	public void stop() {
		startedContinuous = false;
	}

	/**
	 * Image recognition output panel (each user-selected IROutput creates its
	 * own panel)
	 * 
	 * @author JJurM
	 */
	class IROutputFrame extends JInternalFrame {
		private static final long serialVersionUID = 1L;
		static final int xOffset = 20, yOffset = 20;

		private ImageRecognitionOutput irOutput;
		private ImagePanel imagePanel;

		public IROutputFrame(ImageRecognitionOutput irOutput) {
			super(irOutput.getName(), false, true, false, true);
			this.irOutput = irOutput;
			openFrameCount++;

			setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
			setLayout(new BorderLayout(0, 0));
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addInternalFrameListener(new IROutputFrameListener());

			imagePanel = new ImagePanel();
			imagePanel.setPreferredSize(getScaledViewSize());
			// imagePanel.setBackground(Color.GRAY);
			add(imagePanel, BorderLayout.CENTER);

			pack();
			setVisible(true);
		}

		private void viewSizeChanged(Dimension d) {
			imagePanel.setPreferredSize(d);
			pack();

			imagePanel.revalidate();
			imagePanel.repaint();
		}

		/**
		 * Start processing by ImageRecognitionOutput and displays result.
		 * 
		 * @param moment
		 */
		void receive(Moment moment) {
			Image image = irOutput.process(moment);

			Dimension scaled = getScaledViewSize();
			image = image.getScaledInstance(scaled.width, scaled.height, Image.SCALE_FAST);

			imagePanel.setImage(image);
			imagePanel.repaint();
		}

		class IROutputFrameListener extends InternalFrameAdapter {

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				openFrameCount--;

				IROutputFrame frame = (IROutputFrame) e.getInternalFrame();
				desktopPane.remove(frame);
				irOutputFrames.remove(frame);

				desktopPane.revalidate();
				desktopPane.repaint();
			}

		}

	}

	/**
	 * Image panel used for drawing images
	 * 
	 * @author JJurM
	 */
	static class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private Image image = null;

		public void setImage(Image image) {
			this.image = image;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null) {
				g.drawImage(image, 0, 0, null);
			}
		}

	}
}
