package net.talentum.jackie.comm;

import java.io.IOException;

import com.pi4j.io.i2c.I2CDevice;

/**
 * A class for manipulating with I2C devices. Its read/write methods are
 * synchronized in order to offer a transfer not interfered by other threads.
 * 
 * @author JJurM
 * @see I2CCommunicator
 */
public class Device {

	I2CDevice device;

	public Device(I2CDevice device) {
		this.device = device;
	}

	/**
	 * Writes command.
	 * 
	 * @param bytes
	 *            bytes of the command to write
	 * @see I2CCommunicator
	 */
	public synchronized void write(byte... bytes) {
		transfer(0, bytes);
	}

	/**
	 * Writes command and receives result. The result bytes are converted from
	 * signed bytes to integers.
	 * 
	 * @param size
	 *            number of bytes to read, nothing will be read when
	 *            {@code size == 0}
	 * @param bytes
	 *            bytes of the command to write
	 * @return Array of read bytes (converted to integers) or null in case of
	 *         read error. Array of zero length will be returned when
	 *         {@code size == 0}.
	 * @see I2CCommunicator
	 */
	public synchronized int[] transfer(int size, byte... bytes) {

		try {
			// write command
			device.write(bytes);

			// check if it is expected to read
			if (size > 0) {

				// create byte array of desired size, for reading
				byte[] arr = new byte[size];

				// read bytes
				int read = device.read(arr, 0, size);

				if (read != size) {
					// something went wrong
					throw new IOException("Received wrong number of bytes");
				}

				// create array of integers and convert bytes to integers
				int[] res = new int[size];
				for (int i = 0; i < size; i++) {
					res[i] = arr[i] & 0xFF;
				}

				return res;
			} else {
				return new int[0];
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	I2CDevice getI2CDevice() {
		return device;
	}

}
