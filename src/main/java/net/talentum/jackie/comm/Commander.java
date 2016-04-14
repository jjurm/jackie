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

	// ===== Constants =====

	public static final int MOTOR_LEFT = 0;
	public static final int MOTOR_RIGHT = 1;
	public static final int MOTOR_ARM = 2;
	public static final int MOTOR_SHUTTER = 4;
	public static final int MOTOR_CAMERA = 5;

	public static final int BACKLIGHT = 0;
	public static final int FLASHLIGHT = 1;

	public static final int ULTRASONIC_FRONT = 0;
	public static final int ULTRASONIC_LEFT = 4;
	public static final int ULTRASONIC_RIGHT = 5;

	public final I2CCommunicator i2c;

	public Commander(I2CCommunicator i2c) {
		this.i2c = i2c;
	}

	// ===== Helper methods =====

	protected byte cmd(int type, int subcommand) {
		return (byte) ((type << S) | (subcommand & 0xFF));
	}

	protected byte b(boolean value) {
		return (byte) (value ? 1 : 0);
	}

	protected byte b(int number) {
		return (byte) number;
	}

	protected int join(int[] arr) {
		int num = 0;
		for (int i : arr) {
			num = (num << 8) | i;
		}
		return num;
	}

	// ===== Public methods =====

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
		return device.transfer(1, cmd(0x01, 0), (byte) number)[0];
	}

	/**
	 * Automatically test all I2C devices. Returns {@code true} if the test was
	 * successful, {@code false} otherwise.
	 * 
	 * @return {@code true} if the test was successful
	 */
	public boolean testI2CAll() {
		for (Device d : i2c.devices) {
			for (int i = 0; i < 5; i++) {
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
		i2c.each(d -> d.transfer(0, cmd(0x02, 1)));
	}

	/**
	 * Sends stop command to all I2C devices.
	 */
	public void stop() {
		i2c.each(d -> d.transfer(0, cmd(0x02, 0)));
	}

	/**
	 * Writes LED value.
	 * 
	 * @param led
	 *            index of the LED (0-7)
	 * @param value
	 *            boolean value to write
	 * @param
	 */
	public void writeLED(int led, boolean value) {
		i2c.deviceC.transfer(0, cmd(0x08, led), b(value));
	}

	/**
	 * Blinks LED for constant time.
	 * 
	 * @param led
	 *            index of the LED (0-7)
	 */
	public void blinkLED(int led) {
		i2c.deviceC.transfer(0, cmd(0x09, led));
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
		i2c.deviceA.transfer(0, cmd(0x0A, index), b(value));
	}

	/**
	 * Write motor values. Both values are converted to bytes.
	 * 
	 * @param left
	 *            motor controlling the left wheel (-90 to 90)
	 * @param right
	 *            motor controlling the right wheel (-90 to 90)
	 */
	public void writePropulsionMotors(int left, int right) {
		left = 90 + left;
		right = 90 + right;
		i2c.deviceA.transfer(0, cmd(0x0B, 0), b(left), b(right));
	}

	/**
	 * Write propulsion motor values.
	 * 
	 * @param both
	 *            value for both right and left motors (0-90 to 90)
	 * @see #writePropulsionMotors(int, int)
	 */
	public void writePropulsionMotors(int both) {
		writePropulsionMotors(both, both);
	}

	/**
	 * Returns device that corresponds to the light of the specified index.
	 * 
	 * @param index
	 * @return
	 */
	protected Device getDeviceLight(int index) {
		Device d;
		switch (index) {
		case 0:
		default:
			d = i2c.deviceA;
		case 1:
			d = i2c.deviceB;
		}
		return d;
	}

	/**
	 * Turns light on or off.
	 * 
	 * @param index
	 *            index of the light
	 * @param value
	 *            {@code true =} on, {@code false =} off
	 */
	public void light(int index, boolean value) {
		Device d = getDeviceLight(index);
		d.transfer(0, cmd(12, index), b(value));
	}

	/**
	 * Writes light analog value.
	 * 
	 * @param index
	 *            index of the light
	 * @param value
	 *            analog value (0-255)
	 */
	public void lightAnalog(int index, int value) {
		Device d = getDeviceLight(index);
		d.transfer(0, cmd(13, index), b(value));
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
		int[] res = i2c.deviceB.transfer(1, cmd(0x10 + (group & 0x01), index));
		return res[0] != 0;
	}

	/**
	 * Reads all buttons in a group. Returns one byte for each button.
	 * 
	 * @param group
	 * @return
	 */
	public int[] readMultipleButtons(int group) {
		int[] res = i2c.deviceC.transfer(3, cmd(0x12, group & 0x01));
		return res;
	}

	/**
	 * Reads one switch value.
	 * 
	 * @param index
	 *            index of the switch
	 * @return
	 */
	public boolean readSwitch(int index) {
		int[] res = i2c.deviceB.transfer(1, cmd(0x13, index));
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
		int[] res = i2c.deviceA.transfer(2, cmd(0x14, index));
		if (res == null) {
			return 500;
		}
		int pulse = join(res);
		return pulse / 58.138;
	}

	/**
	 * Reads value of infrared sensor specified by the index.
	 * 
	 * @param index
	 *            index of the infrared sensor
	 * @return measured analog value (0-1023)
	 */
	public int readInfraredSensor(int index) {
		int[] res = i2c.deviceA.transfer(2, (byte) ((21 << S) + (index & 0x07)));
		return join(res);
	}

	// ===== MPU-6050 =====

	/**
	 * Reads acceleration data from MPU-6050. Returns array containing 3 values
	 * - acceleration data of axes {@code x}, {@code y} and {@code z}.
	 * 
	 * @return array of length 3
	 */
	public int[] getAcceleration() {
		int[] res = i2c.mpu6050.transfer(6, (byte) MPU6050.REGISTER_ACCEL);
		int[] accel = new int[3];
		accel[0] = (res[0] << 8) | res[1]; // X
		accel[1] = (res[2] << 8) | res[3]; // Y
		accel[2] = (res[4] << 8) | res[5]; // Z
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
		gyro[0] = (res[0] << 8) | res[1]; // X
		gyro[1] = (res[2] << 8) | res[3]; // Y
		gyro[2] = (res[4] << 8) | res[5]; // Z
		return gyro;
	}

	/**
	 * Reads gyroscope data for Z-axis rotation.
	 * 
	 * @return
	 */
	public int getGyroZ() {
		int[] res = i2c.mpu6050.transfer(14, (byte) MPU6050.REGISTER_GYRO_Z);
		return join(res);
	}

}
