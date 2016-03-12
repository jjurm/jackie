package net.talentum.jackie.system;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.talentum.jackie.moment.Parameters;
import net.talentum.jackie.moment.Robot;
import net.talentum.jackie.moment.module.AveragingTrailWidthDeterminerModule;
import net.talentum.jackie.moment.module.BWBooleanImageFilterModule;
import net.talentum.jackie.moment.module.BasicAngularTurnHandlerModule;
import net.talentum.jackie.moment.module.BasicBorderFinderModule;
import net.talentum.jackie.moment.module.BasicLineFinderModule;
import net.talentum.jackie.moment.module.BlurImageModifierModule;
import net.talentum.jackie.moment.module.BottomLineStartFinderModule;
import net.talentum.jackie.moment.module.VectorDirectionManagerModule;
import net.talentum.jackie.moment.strategy.LineFollowingStrategy;
import net.talentum.jackie.moment.strategy.RobotStrategy;
import net.talentum.jackie.tools.InstructionPainter;
import net.talentum.jackie.ui.StrategyComparatorPanel;

public class StrategyComparatorPreview {

	static Parameters param;
	static RobotStrategy[] strategies;

	static JFrame previewFrame;
	static StrategyComparatorPanel strategyComparatorPanel;

	static BufferedImage lastImage;
	public static Robot robot;

	static InstructionPainter painter = new InstructionPainter();
	static ExecutorService executor = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {
		param = new Parameters();

		createStrategies();
		// robot = new Robot(param);

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

	private static void createStrategies() {
		List<RobotStrategy> strategiesList = new ArrayList<RobotStrategy>();

		// @formatter:off
		strategiesList.add(new LineFollowingStrategy(
				"Strategy1",
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
		));
		// @formatter:on

		strategies = strategiesList.toArray(new RobotStrategy[0]);
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

		strategyComparatorPanel = new StrategyComparatorPanel(strategies);
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

}
