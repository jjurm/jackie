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
		return device.transfer(1, (byte) (1 << S), (byte) number)[0];
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
	 * Sends start command to all I2C devices.
	 */
	public void start() {
		i2c.each(d -> d.transfer(0, (byte) ((2 << S) + 1)));
	}

	/**
	 * Sends stop command to all I2C devices.
	 */
	public void stop() {
		i2c.each(d -> d.transfer(0, (byte) (2 << S)));
	}

	/**
	 * Writes LED value.
	 * 
	 * @param group
	 *            group of the LED ({@code 0=A, 1=B})
	 * @param led
	 *            index of the LED (0-7)
	 * @param value
	 *            boolean value to write
	 * @param
	 */
	public void writeLED(int group, int led, boolean value) {
		i2c.deviceB.transfer(0, (byte) (((0x08 + (group & 0x01)) << S) + (led & 0x07)), (byte) (value ? 1 : 0));
	}

	/**
	 * Write value of one motor.
	 * 
	 * @param index
	 *            index of the motor
	 * @param value
	 *            value (0-180)
	 */
	public void writeMotor(int index, int value) {
		i2c.deviceA.transfer(0, (byte) ((0x0A << S) + (index & 0x07)), (byte) value);
	}

	/**
	 * Write motor values. Both values are converted to bytes.
	 * 
	 * @param left
	 *            motor controlling the left wheel
	 * @param right
	 *            motor controlling the right wheel
	 */
	public void writePropulsionMotors(int left, int right) {
		System.out.println(String.format("Writing motor values (left=%d, right=%d)", left, right));
		i2c.deviceA.transfer(0, (byte) (11 << S), (byte) (left & 0xFF), (byte) (right & 0xFF));
	}

	/**
	 * Reads one button value.
	 * 
	 * @param group
	 *            group of the button
	 * @param index
	 *            index of the button
	 * @return {@code true} if the button is pressed
	 */
	public boolean readButton(int group, int index) {
		int[] res = i2c.deviceB.transfer(1, (byte) (((0x10 + (group & 0x01)) << S) + (index & 0x07)));
		return res[0] != 0;
	}

	/**
	 * Reads all buttons in a group. Returns one byte containing all values.
	 * Every {@code i}-th bit from the right (LSB) describes state of the
	 * {@code i}-th button.
	 * 
	 * @param group
	 * @return
	 */
	public int readMultipleButtons(int group) {
		int[] res = i2c.deviceB.transfer(1, (byte) ((0x12 << S) + (group & 0x01)));
		return res[0];
	}

	/**
	 * Reads one switch value.
	 * 
	 * @param index
	 *            index of the switch
	 * @return
	 */
	public boolean readSwitch(int index) {
		int[] res = i2c.deviceB.transfer(1, (byte) (((0x13) << S) + (index & 0x07)));
		return res[0] != 0;
	}

	/**
	 * Reads value of ultrasonic sensor specified by the index.
	 * 
	 * @param index
	 *            index of the ultrasonic sensor
	 * @return distance in centimeters
	 */
	public double readUltrasonicSensor(int index) {
		int[] res = i2c.deviceA.transfer(2, (byte) (20 << S), (byte) index);
		int pulse = res[0] << 8 + res[1];
		return pulse / 58.138;
	}

	/**
	 * Reads acceleration data from MPU-6050. Returns array containing 3 values
	 * - acceleration data of axes {@code x}, {@code y} and {@code z}.
	 * 
	 * @return array of length 3
	 */
	public int[] getAcceleration() {
		int[] res = i2c.mpu6050.transfer(6, (byte) MPU6050.REGISTER_ACCEL);
		int[] accel = new int[3];
		accel[0] = res[0] << 8 | res[1]; // X
		accel[1] = res[2] << 8 | res[3]; // Y
		accel[2] = res[4] << 8 | res[5]; // Z
		return accel;
	}

	/**
	 * Reads gyroscope data from MPU-6050. Returns array containing 3 values -
	 * gyro data of axes {@code x}, {@code y} and {@code z}.
	 * 
	 * @return
	 */
	public int[] getGyro() {
		int[] res = i2c.mpu6050.transfer(14, (byte) MPU6050.REGISTER_GYRO);
		int[] gyro = new int[3];
		gyro[0] = res[0] << 8 | res[1]; // X
		gyro[1] = res[2] << 8 | res[3]; // Y
		gyro[2] = res[4] << 8 | res[5]; // Z
		return gyro;
	}

	/**
	 * Reads gyroscope data for Z-axis rotation.
	 * 
	 * @return
	 */
	public int getGyroZ() {
		int[] res = i2c.mpu6050.transfer(14, (byte) MPU6050.REGISTER_GYRO_Z);
		return res[0] << 8 | res[1];
	}

}
