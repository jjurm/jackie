package net.talentum.jackie.comm;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Class responsible for I2C communication.
 * 
 * <p>
 * <b>Command syntax</b><br/>
 * Each command consists of 8 bits. The first 5 bits (most significant bits)
 * specify the command type. The following 3 bits (least significant bits)
 * specify the subcommand.
 * </p>
 * 
 * <p>
 * <b>List of commands</b><br/>
 * </p>
 * <table border="1" cellspacing="0">
 * <tr>
 * <th>Command type</th>
 * <th>Subcommands</th>
 * <th>Description</th>
 * <th>Arguments</th>
 * <th>Result</th>
 * </tr>
 * <tr>
 * <td><b>1</b></td>
 * <td></td>
 * <td>Test command</td>
 * <td>
 * <ul>
 * <li>number to send</li>
 * </ul>
 * </td>
 * <td>one byte, returning the same number that was sent</td>
 * </tr>
 * </table>
 * 
 * @author JJurM
 */
public class I2CCommunicator {

	/**
	 * Number of least significant bits that divide the first byte into
	 * <i>command</i> part and <i>subcommand</i> part.
	 * 
	 * @see I2CCommunicator
	 */
	static final int S = 3;

	protected I2CBus bus;

	public Device arduino;
	public Device mpu6050;

	public I2CCommunicator() {
		try {

			// get i2c bus
			bus = I2CFactory.getInstance(I2CBus.BUS_1);

			// create predefined devices
			arduino = new Device(bus.getDevice(0x04));
			mpu6050 = new Device(bus.getDevice(0x68));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @deprecated Use predefined devices of {@link Device} class instead.
	 * @param address
	 * @return
	 * @throws IOException
	 */
	public I2CDevice getDevice(int address) throws IOException {
		return bus.getDevice(address);
	}

	// ===== command methods =====

	public int cTest(Device device, int number) {
		return device.transfer(1, new byte[] { 1 << S, (byte) number })[0];
	}

}
