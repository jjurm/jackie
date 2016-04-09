package net.talentum.jackie.comm;

import com.pi4j.io.i2c.I2CDevice;

/**
 * Class for MPU-6050 accelerometer and gyroscope sensor.
 * 
 * @author JJurM
 */
public class MPU6050 extends Device {

	public static final int REGISTER_ACCEL = 0x3B;
	public static final int REGISTER_TEMP = 0x41;
	public static final int REGISTER_GYRO = 0x43;
	public static final int REGISTER_GYRO_X = 0x43;
	public static final int REGISTER_GYRO_Y = 0x45;
	public static final int REGISTER_GYRO_Z = 0x47;
	
	public MPU6050(I2CDevice device) {
		super(device);
	}

	/**
	 * Wakes up the device.
	 */
	public void wake() {
		transfer(0, (byte) 107, (byte) 0);
	}

}
