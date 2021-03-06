package net.talentum.jackie.system;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.sarxos.webcam.Webcam;

import net.talentum.jackie.image.SubtractingImageBallFinder;
import net.talentum.jackie.image.output.BlurredBooleanImageOutput;
import net.talentum.jackie.image.output.BooleanImageOutput;
import net.talentum.jackie.image.output.ImageOutput;
import net.talentum.jackie.image.output.RobotStrategyIROutput;
import net.talentum.jackie.image.output.SourceImageOutput;
import net.talentum.jackie.image.supplier.ImageOutputSupplier;
import net.talentum.jackie.image.supplier.ImageSupplier;
import net.talentum.jackie.image.supplier.ImageSupplierProvider;
import net.talentum.jackie.image.supplier.LocalWebcamImageSupplier;
import net.talentum.jackie.image.supplier.OpenCVImageSupplier;
import net.talentum.jackie.image.supplier.ServerImageSupplier;
import net.talentum.jackie.module.impl.AveragingTrailWidthDeterminerModule;
import net.talentum.jackie.module.impl.BasicAngularTurnHandlerModule;
import net.talentum.jackie.module.impl.BasicBorderFinderModule;
import net.talentum.jackie.module.impl.BasicIntersectionSolver;
import net.talentum.jackie.module.impl.BasicLineFinderModule;
import net.talentum.jackie.module.impl.BlurImageModifierModule;
import net.talentum.jackie.module.impl.BottomLineStartFinderModule;
import net.talentum.jackie.module.impl.BufferedImageMatConverterModule;
import net.talentum.jackie.module.impl.UnivBooleanImageFilterModule;
import net.talentum.jackie.module.impl.VectorDirectionManagerModule;
import net.talentum.jackie.robot.strategy.BallFinderStrategy;
import net.talentum.jackie.robot.strategy.HorizontalLevelObservingStrategy;
import net.talentum.jackie.robot.strategy.LineFollowingStrategy;
import net.talentum.jackie.tools.MathTools;
import net.talentum.jackie.ui.StrategyComparatorPanel;

/**
 * Runnable class.
 * 
 * <p>
 * Opens windows allowing user to compare different strategies or
 * {@link ImageOutput}s. Those are defined in method
 * {@link #createImageOutputs()}.
 * </p>
 * 
 * @author JJurM
 */
public class StrategyComparatorPreview {

	static ImageOutputSupplier[] imageOutputSuppliers;
	static ImageSupplierProvider[] imageSupplierProviders;

	static JFrame previewFrame;
	static StrategyComparatorPanel strategyComparatorPanel;

	public static void main(String[] args) {

		run(args);

	}

	public static void run(String[] args) {
		// initialize configuration manager
		ConfigurationManager.init();

		Run.loadOpenCV();
		
		createImageOutputs();
		
		createImageSupplierProviders();

		EventQueue.invokeLater(() -> {
			createFrame();
		});
	}

	private static void createImageSupplierProviders() {
		List<ImageSupplierProvider> list = new ArrayList<ImageSupplierProvider>();
		List<Webcam> webcams = Webcam.getWebcams();
		for (Webcam w : webcams) {
			list.add(new ImageSupplierProvider(w.getName()) {
				@Override
				public ImageSupplier provide(String param) {
					return new LocalWebcamImageSupplier(w);
				}
			});
		}
		list.add(new OpenCVImageSupplier.Provider("OpenCV"));
		list.add(new ServerImageSupplier.Provider("Server"));
		
		imageSupplierProviders = list.toArray(new ImageSupplierProvider[0]);
	}

	/**
	 * Here are defined {@link ImageOutput}s to offer in GUI.
	 */
	private static void createImageOutputs() {
		List<ImageOutputSupplier> list = new ArrayList<ImageOutputSupplier>();

		// @formatter:off
		list.add(p -> new SourceImageOutput("Source"));
		list.add(p -> new BlurredBooleanImageOutput("Blur + BW(100)", 100));
		list.add(p -> new BlurredBooleanImageOutput(String.format("Blur + BW(%s)", p), MathTools.parseDefault(p, 0)));
		list.add(p -> new BlurImageModifierModule("Blur"));
		list.add(p -> new BooleanImageOutput("BW(100)", 100));
		list.add(p -> new BooleanImageOutput("Green", new Function<Color, Boolean>() {
			@Override
			public Boolean apply(Color c) {
				return ((double) c.getGreen()) / (c.getBlue() + c.getRed() + 1) > 0.64;
			}
		}));
		list.add(p -> new BlurredBooleanImageOutput("Green + blur", new Function<Color, Boolean>() {
			@Override
			public Boolean apply(Color c) {
				return ((double) c.getGreen()) / (c.getBlue() + c.getRed() + 1) > 0.64;
			}
		}));
		list.add(p -> new BlurredBooleanImageOutput("Green () + blur", new Function<Color, Boolean>() {
			@Override
			public Boolean apply(Color c) {
				return ((double) c.getGreen()) / (c.getBlue() + c.getRed() + 1) > Double.parseDouble(p);
			}
		}));
		list.add(p -> new RobotStrategyIROutput("*LineFollowing", new LineFollowingStrategy(
				new BlurImageModifierModule(),
				new UnivBooleanImageFilterModule(100),
				new BottomLineStartFinderModule(),
				(d) -> new AveragingTrailWidthDeterminerModule(d, 3),
				(d) -> new VectorDirectionManagerModule(8, 3),
				new BasicLineFinderModule(
						20.0 * (Math.PI / 180),
						new BasicBorderFinderModule(2, 140, 10),
						new BasicAngularTurnHandlerModule()
				)
		)));
		list.add(p -> new HorizontalLevelObservingStrategy.ImageOutput("*HorizontalLevelObserving (100)", new HorizontalLevelObservingStrategy(
				new BlurImageModifierModule(),
				new UnivBooleanImageFilterModule(100),
				new BasicBorderFinderModule(2, 600, 3),
				new BasicIntersectionSolver()
		)));
		list.add(p -> new HorizontalLevelObservingStrategy.ImageOutput(String.format("*HorizontalLevelObserving (%s)", p), new HorizontalLevelObservingStrategy(
				new BlurImageModifierModule(),
				new UnivBooleanImageFilterModule(MathTools.parseDefault(p, 100)),
				new BasicBorderFinderModule(2, 600, 3),
				new BasicIntersectionSolver()
		)));
		list.add(p -> new BallFinderStrategy.ImageOutput("*BallFinding", new BufferedImageMatConverterModule()));
		list.add(p -> new SubtractingImageBallFinder("*SubtractingImageBallFinder"));
		// @formatter:on

		imageOutputSuppliers = list.toArray(new ImageOutputSupplier[0]);
	}

	private static void createFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		previewFrame = new JFrame("StrategyComparatorPreview");
		previewFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		previewFrame.setBounds(100, 100, 900, 700);
		previewFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				strategyComparatorPanel.stop();
				System.exit(0);
			}
		});

		strategyComparatorPanel = new StrategyComparatorPanel(imageOutputSuppliers, imageSupplierProviders);
		previewFrame.setContentPane(strategyComparatorPanel);

		previewFrame.setVisible(true);
	}

}
