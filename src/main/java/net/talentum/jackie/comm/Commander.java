package net.talentum.jackie.comm;

import net.talentum.jackie.tools.MathTools;

/**
 * Class with methods in which command behaviors are defined. Provides basic
 * methods for writing motor values, reading sensors, detecting button states,
 * controlling LEDs etc.
 * 
 * @author JJurM
 */
public class Commander {
	
	/**
	 * Number of least significant bits that divide the first byte into
	 * <i>command</i> part and <i>subcommand</i> part.
	 * 
	 * @see I2CCommunicator
	 */
	static final int S = 3;

	public final I2CCommunicator i2c;

	public Commander(I2CCommunicator i2c) {
		this.i2c = i2c;
	}

	/**
	 * Sends test command to the given device. Returns the received result
	 * number.
	 * 
	 * @param device
	 *            device to test
	 * @param number
	 *            number to send
	 * @return received response
	 */
	public int testI2C(Device device, int number) {
		return device.transfer(1, new byte[] { 1 << S, (byte) number })[0];
	}

	/**
	 * Automatically test all I2C devices. Returns {@code true} if the test was
	 * successful, {@code false} otherwise.
	 * 
	 * @return {@code true} if the test was successful
	 */
	public boolean testI2CAuto() {
		for (Device d : i2c.devices) {
			for (int i = 0; i < 3; i++) {
				int num = MathTools.randomRange(1, 255);
				int got = testI2C(d, num);
				if (num != got) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Write motor values. Both values are converted to bytes.
	 * 
	 * @param left motor controlling the left wheel
	 * @param right motor controlling the right wheel
	 */
	public void writePropulsionMotors(int left, int right) {
		System.out.println(String.format("Writing motor values (left=%d, right=%d)", left, right));
		i2c.deviceA.transfer(0, (byte) (11 << S), (byte) left, (byte) right);
	}

}
