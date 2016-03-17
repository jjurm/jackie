package net.talentum.jackie.system;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.talentum.jackie.ir.BWBooleanImageOutput;
import net.talentum.jackie.ir.ImageRecognitionOutput;
import net.talentum.jackie.ir.RobotStrategyIROutput;
import net.talentum.jackie.ir.SourceImageOutput;
import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.module.AveragingTrailWidthDeterminerModule;
import net.talentum.jackie.moment.module.BWBooleanImageFilterModule;
import net.talentum.jackie.moment.module.BasicAngularTurnHandlerModule;
import net.talentum.jackie.moment.module.BasicBorderFinderModule;
import net.talentum.jackie.moment.module.BasicLineFinderModule;
import net.talentum.jackie.moment.module.BlurImageModifierModule;
import net.talentum.jackie.moment.module.BottomLineStartFinderModule;
import net.talentum.jackie.moment.module.VectorDirectionManagerModule;
import net.talentum.jackie.moment.strategy.LineFollowingStrategy;
import net.talentum.jackie.ui.StrategyComparatorPanel;

public class StrategyComparatorPreview {

	static Parameters param;
	static ImageRecognitionOutput[] irOutputs;

	static JFrame previewFrame;
	static StrategyComparatorPanel strategyComparatorPanel;

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {
		param = new Parameters();

		createStrategies();

		EventQueue.invokeLater(() -> {
			createFrame();
		});
	}

	private static void createStrategies() {
		List<ImageRecognitionOutput> list = new ArrayList<ImageRecognitionOutput>();

		// @formatter:off
		list.add(new SourceImageOutput("Source"));
		list.add(new BWBooleanImageOutput("BW (treshold=100)", 100));
		list.add(new RobotStrategyIROutput("Strategy1", new LineFollowingStrategy(
				param,
				new BlurImageModifierModule(),
				new BWBooleanImageFilterModule(100),
				new BottomLineStartFinderModule(),
				(d) -> new AveragingTrailWidthDeterminerModule(d, 3),
				(d) -> new VectorDirectionManagerModule(8, 3),
				new BasicLineFinderModule(
						20.0 * (Math.PI / 180),
						new BasicBorderFinderModule(2, 140, 10, 0.5),
						new BasicAngularTurnHandlerModule()
				)
		)));
		// @formatter:on

		irOutputs = list.toArray(new ImageRecognitionOutput[0]);
	}

	private static void createFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		previewFrame = new JFrame("StrategyComparatorPreview");
		previewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		previewFrame.setBounds(100, 100, 900, 420);

		strategyComparatorPanel = new StrategyComparatorPanel(irOutputs);
		previewFrame.setContentPane(strategyComparatorPanel);

		/*-robot.setWebcamImageSupplier(() -> lastImage);
		strategyComparatorPanel.addProcessImageListener(image -> {
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
		strategyComparatorPanel.init();*/

		previewFrame.setVisible(true);
	}

	/*-public static BufferedImage getLastInstructionPaintedImage() {
		if (robot.lastInstruction == null)
			return null;
	
		return paintInstructionOnImage(lastImage, robot.lastInstruction);
	}*/

	public void shot() {

	}

}
