package net.talentum.jackie.comm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.pi4j.io.i2c.I2CBus;
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
 * 
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
 * <th>2</th>
 * <td>Start / stop</td>
 * <td>{@code 1 =} start, {@code 0 =} stop</td>
 * <td></td>
 * <td></td>
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
 * <li>0 - propulsion motors (left, right)</li>
 * </ul>
 * </td>
 * <td>
 * <li>motor A value</li>
 * <li>motor B value</li></td>
 * <td></td>
 * </tr>
 * 
 * <tr>
 * <th>12</th>
 * <td>Turn light on or off</td>
 * <td>index of the light
 * <ul>
 * <li>0 - bottom backlight</li>
 * <li>1 - flashlight</li>
 * </ul>
 * </td>
 * <td>
 * <li>digital value (1/0)</li></td>
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
 * 
 * <tr>
 * <th>21</th>
 * <td>Read infrared sensor value</td>
 * <td>infrared sensor index</td>
 * <td></td>
 * <td>Measured analog value (0-1023). The command returns one 16-bit number
 * split into <i>2 bytes</i>, with MSB bits first.</td>
 * </tr>
 * </table>
 * 
 * @author JJurM
 */
public class I2CCommunicator {

	protected I2CBus bus;

	public Device deviceA;
	public Device deviceB;
	public Device deviceC;
	public MPU6050 mpu6050;

	public Device[] devices;

	public Map<String, Device> deviceMap = new HashMap<String, Device>();

	public I2CCommunicator() {
		try {

			// get i2c bus
			bus = I2CFactory.getInstance(I2CBus.BUS_1);

			// create predefined devices
			deviceA = new Device(bus.getDevice(0x04));
			deviceB = new Device(bus.getDevice(0x05));
			deviceC = new Device(bus.getDevice(0x06));
			mpu6050 = new MPU6050(bus.getDevice(0x68));

			devices = new Device[] { deviceA, deviceB, deviceC };

			deviceMap.put("a", deviceA);
			deviceMap.put("b", deviceB);
			deviceMap.put("c", deviceC);
			deviceMap.put("m", mpu6050);

			mpu6050.wake();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs the command on each device.
	 * 
	 * @param c
	 */
	public void each(Consumer<Device> c) {
		for (Device device : devices) {
			c.accept(device);
		}
	}

	/**
	 * Searches the list of devices for one specified with the key string.
	 * 
	 * @param address
	 * @return found device or {@code null}
	 */
	public Device getDevice(String key) {
		return deviceMap.get(key.toLowerCase());
	}

}
