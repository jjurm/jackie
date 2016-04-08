package net.talentum.jackie.robot;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.talentum.jackie.comm.Commander;
import net.talentum.jackie.image.ImageSupplier;
import net.talentum.jackie.robot.state.LineFollowingState;
import net.talentum.jackie.robot.state.State;
import net.talentum.jackie.system.Config;

/**
 * One instance of this class represents one robot (there should naturally be at
 * most one instance). Robot is responsible for constructing {@link Moment}s
 * 
 * @author JJurM
 */
public class Robot {

	/**
	 * Holds object that is capable of supplying webcam images.
	 */
	ImageSupplier imageSupplier;

	/**
	 * Shallow history of past moments
	 * 
	 * @see #constructMoment()
	 */
	public Deque<Moment> moments = new LinkedList<Moment>();
	
	public final Commander commander;
	
	protected AtomicBoolean run = new AtomicBoolean(true);

	protected List<Runnable> configChangedListeners = new ArrayList<Runnable>();

	/**
	 * Actual state, its method {@link State#getMotorInstructions()} is called
	 * in a loop.
	 */
	private State state;

	public Robot(Commander commander) {
		this.commander = commander;

		// create strategy
		state = new LineFollowingState(this);
	}

	public void setImageSupplier(ImageSupplier imageSupplier) {
		this.imageSupplier = imageSupplier;
	}

	/**
	 * Constructs {@link Moment}, which involves taking an image from webcam
	 * supplier and collecting sensor data.
	 * 
	 * @return
	 */
	public Moment constructMoment() {
		BufferedImage image = imageSupplier.getImage();
		if (image == null) {
			System.out.println("Got null image");
			return null;
		}
		SensorData sensorData = SensorData.collect();

		/*-Image scaled = image.getScaledInstance(image.getWidth() / 2, image.getHeight() / 2,
				BufferedImage.SCALE_FAST);
		image = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();*/

		Moment moment = new Moment(image, sensorData);

		moments.push(moment);
		while (moments.size() > 2) {
			moments.pollLast();
		}
		return moment;
	}

	/**
	 * Runs {@link #runOnce()} repeatedly in a {@code while(true)} loop.
	 */
	public void runCycle() {
		while (run.get()) {
			state.run();
		}
	}
	
	/**
	 * Stops the main cycle (method {@link #runCycle()}).
	 */
	public void stop() {
		run.set(false);
	}

	/**
	 * Method called on the robot by a superior class when the configuration has
	 * been changed.
	 */
	public void configurationReloaded() {
		Config.reload();
		for (Runnable listener : configChangedListeners) {
			listener.run();
		}
	}

	/**
	 * Adds listener that is called when Robot has detected a change in the
	 * configuration. Values from the configuration should be reloaded.
	 * 
	 * @param listener
	 *            a runnable
	 */
	public void addConfigChangedListener(Runnable listener) {
		configChangedListeners.add(listener);
	}

	/**
	 * Method for setting the Robot's {@link State}
	 * 
	 * @param state
	 */
	public void setState(State state) {
		System.out.println("State changed to " + state.getClass().getName());
		this.state = state;
	}
	
}
