package net.talentum.jackie.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPicker;

import net.talentum.jackie.ir.ImageRecognitionOutput;
import net.talentum.jackie.moment.Moment;
import net.talentum.jackie.moment.SensorData;

public class StrategyComparatorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private ImageRecognitionOutput[] irOutputs;
	private ArrayList<IROutputPanel> irOutputPanels = new ArrayList<IROutputPanel>();
	private boolean webcamOpen = false;
	static ExecutorService executor = Executors.newSingleThreadExecutor();
	private boolean startedContinuous = false;

	private JPanel menu1;
	private WebcamPicker webcamSelection;
	private JComboBox<DimensionComboBoxItem> viewSizeSelection;
	private JButton btnOpenClose;
	private JPanel menu2;
	private JComboBox<ImageRecognitionOutput> irOutputSelection;
	private JButton btnAddStrategy;
	private Component horizontalStrut;
	private JScrollPane scrollPane;
	private JPanel centerPanel;
	private JButton btnShot;
	private JButton btnStart;
	private JButton btnStop;

	public StrategyComparatorPanel(ImageRecognitionOutput[] irOutputs) {
		this.irOutputs = irOutputs;

		setLayout(new BorderLayout(0, 0));
		createElements();
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
		menu1.add(webcamSelection);

		viewSizeSelection = new JComboBox<DimensionComboBoxItem>();
		viewSizeSelection.setPreferredSize(new Dimension(80, 20));
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

		menu2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) menu2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		menuPanel.add(menu2);

		irOutputSelection = new JComboBox<ImageRecognitionOutput>();
		irOutputSelection.setPreferredSize(new Dimension(100, 20));
		irOutputSelection.setModel(new DefaultComboBoxModel<ImageRecognitionOutput>(irOutputs));
		menu2.add(irOutputSelection);

		btnAddStrategy = new JButton("Add output");
		btnAddStrategy.addActionListener(e -> {
			// create new strategy panel
			ImageRecognitionOutput irOutput = (ImageRecognitionOutput) irOutputSelection.getSelectedItem();
			IROutputPanel panel = new IROutputPanel(irOutput);
			irOutputPanels.add(panel);
			centerPanel.add(panel);

			revalidate();
			repaint();
		});
		menu2.add(btnAddStrategy);

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
			startedContinuous = false;
		});
		menu2.add(btnStop);

		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);

		centerPanel = new JPanel();
		scrollPane.setViewportView(centerPanel);
		centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
	}

	private void shot() {
		// contruct Moment
		Webcam webcam = webcamSelection.getSelectedWebcam();
		BufferedImage image = webcam.getImage();
		SensorData sensorData = SensorData.collect();
		Moment moment = new Moment(image, sensorData);

		// arrange for processing
		for (IROutputPanel p : irOutputPanels) {
			p.receive(moment);
		}
	}

	public void setEnabledFor(boolean enabled, JComponent... components) {
		for (JComponent component : components) {
			component.setEnabled(enabled);
		}
	}

	/**
	 * Image recognition output panel (each user-selected IROutput creates its
	 * own panel)
	 * 
	 * @author JJurM
	 */
	class IROutputPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private ImageRecognitionOutput irOutput;

		private ImagePanel imagePanel;

		public IROutputPanel(ImageRecognitionOutput irOutput) {
			this.irOutput = irOutput;

			Dimension dimension = new Dimension(320, 240);
			setPreferredSize(dimension);
			setMaximumSize(dimension);
			setBorder(new LineBorder(Color.GRAY, 1));

			setLayout(new BorderLayout(0, 0));

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			add(panel, BorderLayout.NORTH);

			JLabel lblName = new JLabel(irOutput.getName());
			lblName.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel.add(lblName, BorderLayout.WEST);

			JButton btnRemove = new JButton("Remove");
			btnRemove.setBorder(new EmptyBorder(5, 5, 5, 5));
			btnRemove.addActionListener(e -> {
				Container parent = getParent();
				parent.remove(this);
				irOutputPanels.remove(this);

				parent.revalidate();
				parent.repaint();
			});
			panel.add(btnRemove, BorderLayout.EAST);

			imagePanel = new ImagePanel();
			imagePanel.setPreferredSize(dimension);
			imagePanel.setMaximumSize(dimension);
			imagePanel.setBackground(Color.GRAY);
			add(imagePanel, BorderLayout.CENTER);
		}

		/**
		 * Start processing by ImageRecognitionOutput and displays result.
		 * 
		 * @param moment
		 */
		public void receive(Moment moment) {
			BufferedImage image = irOutput.process(moment);
			imagePanel.setImage(image);
			imagePanel.repaint();
		}

	}

	/**
	 * Image panel used for drawing images
	 * 
	 * @author JJurM
	 */
	static class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private BufferedImage image = null;

		public void setImage(BufferedImage image) {
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
