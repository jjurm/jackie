package net.talentum.jackie.system;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.Robot;
import net.talentum.jackie.moment.RobotInstruction;
import net.talentum.jackie.ui.PreviewPanel;

public class ImageRecognitionPreview {

	static JFrame previewFrame;
	static PreviewPanel previewPanel;

	static BufferedImage lastImage;
	public static Robot robot;

	static ExecutorService executor = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {
		Parameters param = new Parameters();

		robot = new Robot(param);

		EventQueue.invokeLater(new Runnable() {
			@Override
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
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		previewFrame = new JFrame("Image Recognition Preview");
		previewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		previewFrame.setBounds(100, 100, 600, 500);

		previewPanel = new PreviewPanel(previewFrame);
		previewFrame.setContentPane(previewPanel);

		robot.setWebcamImageSupplier(() -> lastImage);
		previewPanel.addProcessImageListener(image -> {
			lastImage = image;
			executor.submit(() -> {
				try {
					robot.constructMoment();
					long start = System.currentTimeMillis();
					robot.process(robot.moments.element());
					System.out.println(System.currentTimeMillis() - start);
					previewPanel.repaintResults();

					ImageIO.write(paintInstructionOnImage(lastImage, robot.lastInstruction), "png",
							new File("C:\\Users\\JJurM\\Documents\\output.png"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		previewPanel.init();

		previewFrame.setVisible(true);
	}

	public static BufferedImage getLastInstructionPaintedImage() {
		if (robot.lastInstruction == null)
			return null;

		return paintInstructionOnImage(lastImage, robot.lastInstruction);
	}

	public static BufferedImage paintInstructionOnImage(BufferedImage image, RobotInstruction instruction) {
		BufferedImage target = new BufferedImage(instruction.momentData.image.getWidth(),
				instruction.momentData.image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = target.getGraphics();

		for (int y = 0; y < image.getHeight() - 10; y++) {
			for (int x = 0; x < image.getWidth() - 10; x++) {
				g.setColor(instruction.momentData.bw[x][y] ? Color.BLACK : Color.WHITE);
				g.fillRect(x, y, 1, 1);
			}
		}

		//g.drawImage(image, -5, -5, null);
		drawPolyline(g, instruction.momentData.line, Color.red, 3);
		drawPolyline(g, instruction.momentData.bordersL, Color.green, 2);
		drawPolyline(g, instruction.momentData.bordersR, Color.green, 2);

		g.setColor(Color.BLUE);
		instruction.momentData.notFound.stream().forEach(p -> g.fillRect(p.x - 1, p.y - 1, 2, 2));

		g.setColor(Color.CYAN);
		instruction.momentData.highlight.stream().forEach(p -> g.fillRect(p.x - 2, p.y - 2, 4, 4));

		/*
		 * g.setColor(Color.blue); for (Point p : notFound) { g.fillRect(p.x -
		 * 1, p.y - 1, 2, 2); }
		 */

		g.dispose();

		return target;
	}

	public static void drawPolyline(Graphics g, List<Point> points, Color color, int width) {
		g.setColor(color);
		Point a, b;
		b = points.get(0);
		for (int i = 1; i < points.size() - 1; i++) {
			a = points.get(i);
			g.drawLine(b.x, b.y, a.x, a.y);
			g.fillRect(a.x - (width / 2), a.y - (width / 2), width, width);
			b = a;
		}
	}

}
