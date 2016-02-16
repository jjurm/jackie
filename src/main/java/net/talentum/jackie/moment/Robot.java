package net.talentum.jackie.moment;

import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

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

public class Robot {

	Parameters param;
	
	Supplier<BufferedImage> webcamImageSupplier;
	
	public Deque<Moment> moments = new LinkedList<Moment>();
	public RobotInstruction lastInstruction;

	protected RobotStrategy strategy;
	protected RobotStrategy lineFollowingStrategy;

	public Robot(Parameters param) {
		this.param = param;

		// @formatter:off
		lineFollowingStrategy = new LineFollowingStrategy(
				param,
				new BlurImageModifierModule(),
				new BWBooleanImageFilterModule(100),
				new BottomLineStartFinderModule(),
				(d) -> new AveragingTrailWidthDeterminerModule(d, 3),
				(d) -> new VectorDirectionManagerModule(12, 4),
				new BasicLineFinderModule(
						20.0 * (Math.PI / 180),
						new BasicBorderFinderModule(2, 70, 8),
						new BasicAngularTurnHandlerModule()
				)
		);
		// @formatter:on
		strategy = lineFollowingStrategy;
	}

	public void setWebcamImageSupplier(Supplier<BufferedImage> webcamImageSupplier) {
		this.webcamImageSupplier = webcamImageSupplier;
	}

	public Moment constructMoment() {
		BufferedImage image = webcamImageSupplier.get();
		SensorData sensorData = SensorData.collect();

		Moment moment = new Moment(image, sensorData);

		moments.push(moment);
		while (moments.size() > 2) {
			moments.pollLast();
		}
		return moment;
	}

	public final synchronized RobotInstruction process(Moment moment) {
		strategy.prepare(moment);
		RobotInstruction instruction = strategy.evaluate();
		
		lastInstruction = instruction;
		return instruction;
	}

}
