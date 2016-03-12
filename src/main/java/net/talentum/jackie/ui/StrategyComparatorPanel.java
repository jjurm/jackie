package net.talentum.jackie.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPicker;

import net.talentum.jackie.moment.strategy.RobotStrategy;

public class StrategyComparatorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private RobotStrategy[] strategies;

	private WebcamPicker webcamSelection;
	private JComboBox<DimensionComboBoxItem> viewSizeSelection;
	private JPanel menu1;
	private JPanel menu2;
	private JButton btnAddStrategy;
	private Component horizontalStrut;
	private JScrollPane scrollPane;
	private JPanel centerPanel;
	private JComboBox<RobotStrategy> strategySelection;
	private JButton btnStop;
	private JButton btnStart;

	public StrategyComparatorPanel(RobotStrategy[] strategies) {
		this.strategies = strategies;

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

		menu2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) menu2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		menuPanel.add(menu2);

		strategySelection = new JComboBox<RobotStrategy>();
		strategySelection.setPreferredSize(new Dimension(100, 20));
		strategySelection.setModel(new DefaultComboBoxModel<RobotStrategy>(strategies));
		menu2.add(strategySelection);

		btnAddStrategy = new JButton("Add strategy");
		btnAddStrategy.addActionListener(e -> {
			// create new strategy panel
			RobotStrategy strategy = (RobotStrategy) strategySelection.getSelectedItem();
			centerPanel.add(new StrategyPanel(strategy));
			revalidate();
			repaint();
		});
		menu2.add(btnAddStrategy);

		horizontalStrut = Box.createHorizontalStrut(20);
		menu2.add(horizontalStrut);

		JButton btnShot = new JButton("Shot");
		menu2.add(btnShot);

		btnStart = new JButton("Start continuous");
		menu2.add(btnStart);

		btnStop = new JButton("Stop");
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

	/**
	 * Strategy panel (each user-selected strategy creates its own panel)
	 * 
	 * @author JJurM
	 */
	static class StrategyPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public StrategyPanel(RobotStrategy strategy) {
			Dimension dimension = new Dimension(320, 240);
			setPreferredSize(dimension);
			setMaximumSize(dimension);
			setBorder(new LineBorder(Color.GRAY, 1));

			setLayout(new BorderLayout(0, 0));

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			add(panel, BorderLayout.NORTH);

			JLabel lblName = new JLabel(strategy.getName());
			lblName.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel.add(lblName, BorderLayout.WEST);

			JButton btnRemove = new JButton("Remove");
			btnRemove.setBorder(new EmptyBorder(5, 5, 5, 5));
			btnRemove.addActionListener(e -> {
				Container parent = getParent();
				parent.remove(this);
				parent.revalidate();
				parent.repaint();
			});
			panel.add(btnRemove, BorderLayout.EAST);

			JPanel imagePanel = new JPanel();
			imagePanel.setPreferredSize(dimension);
			imagePanel.setMaximumSize(dimension);
			imagePanel.setBackground(new Color((int)(Math.random() * 0x1000000)));
			add(imagePanel, BorderLayout.CENTER);
		}

	}
}
