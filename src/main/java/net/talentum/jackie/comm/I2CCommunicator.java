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
 * <th>Command</th>
 * <th>Description</th>
 * <th>Subcommand</th>
 * <th>Arguments</th>
 * <th>Result</th>
 * </tr>
 * <tr>
 * <th>1</th>
 * <td>Test command</td>
 * <td></td>
 * <td>
 * <li>number to send</li></td>
 * <td>one byte, returning the same number that was sent</td>
 * </tr>
 * 
 * <tr>
 * <th>8/9</th>
 * <td>Write LED digital value, group A/B</td>
 * <td>LED index in the group</td>
 * <td>value (0/1)</td>
 * <td></td>
 * </tr>
 * 
 * <tr>
 * <th>10</th>
 * <td>Write motor value</td>
 * <td>motor index
 * <ul>
 * <li>0 - propulsion motor left</li>
 * <li>1 - propulsion motor right</li>
 * <li>2 - front arm</li>
 * <li>4 - back shutter</li>
 * <li>5 - camera up/down</li>
 * </ul>
 * </td>
 * <td>
 * <li>motor value</li></td>
 * <td></td>
 * </tr>
 * 
 * <tr>
 * <th>11</th>
 * <td>Write value for multiple motors</td>
 * <td>motor group index
 * <ul>
 * <li>0 - propulsion motors</li>
 * </ul>
 * </td>
 * <td>
 * <li>motor A value</li>
 * <li>motor B value</li>
 * </td>
 * <td></td>
 * </tr>
 * 
 * <tr>
 * <th>16/17</th>
 * <td>Read button state, group A/B</td>
 * <td>button index in the group</td>
 * <td></td>
 * <td>button value (0/1)</td>
 * </tr>
 * 
 * <tr>
 * <th>18</th>
 * <td>Read button group states</td>
 * <td>button group index ({@code 0=A, 1=B})</td>
 * <td></td>
 * <td>Byte (8 bits) corresponding to 8 buttons in a group. Every {@code i}-th
 * bit from the right (LSB) describes state of the {@code i}-th button.</td>
 * </tr>
 * 
 * <tr>
 * <th>19</th>
 * <td>Read switch state</td>
 * <td>switch index</td>
 * <td></td>
 * <td>switch value(0/1)</td>
 * </tr>
 * 
 * <tr>
 * <th>20</th>
 * <td>Read ultrasonic sensor value</td>
 * <td>ultrasonic sensor index</td>
 * <td></td>
 * <td>received pulse width in microseconds (0-38000). Since
 * <tt>log<sub>2</sub>(38000)<=16</tt>, the command returns one 16-bit number
 * split into <i>2 bytes</i>, with MSB bits first.</td>
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
