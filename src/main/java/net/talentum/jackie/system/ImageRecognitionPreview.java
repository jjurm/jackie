package net.talentum.jackie.system;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.talentum.jackie.hardware.WebcamDriver;
import net.talentum.jackie.ui.PreviewPanel;

public class ImageRecognitionPreview {

	static JFrame previewFrame;
	static PreviewPanel previewPanel;

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {

		WebcamDriver.init();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					createFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	public static void createFrame() {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		previewFrame = new JFrame("Image Recognition Preview");
		previewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		previewFrame.setBounds(100, 100, 600, 500);

		previewPanel = new PreviewPanel(previewFrame);
		previewFrame.setContentPane(previewPanel);
		previewPanel.init();
		
		previewFrame.setVisible(true);
	}

}
