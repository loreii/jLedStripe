package it.loreii.ledstripe;

import java.util.Arrays;
import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;

public class LedStripe {
	/**The standard height of led matrix*/
	public static final int LED_MATRIX_HEIGHT = 7;
	
	/**The standard width of led matrix*/
	public static final int LED_MATRIX_WIDTH = 21;

	/** The vendor ID of the led stripe. */
	private static final short VENDOR_ID = 0x1D34;

	/** The product ID of the led stripe. */
	private static final short PRODUCT_ID = 0x0013;
	
	private UsbDevice device;
	private static LedStripe instance;
	/**
	 * 0..2 range integer for matrix brightness
	 */
	private int brightness;

	public static LedStripe getInstance() throws UsbException{
		if(instance == null)
			instance= new LedStripe();
		return instance;
	}
	
	private LedStripe() throws UsbException {

		device = find(UsbHostManager.getUsbServices().getRootUsbHub(), VENDOR_ID, PRODUCT_ID);
		if (device == null) {
			throw new UsbException("Device not found.");
		}

		// Claim the interface
		UsbConfiguration configuration = device.getUsbConfiguration((byte) 1);
		UsbInterface iface = configuration.getUsbInterface((byte) 0);
		iface.claim(new UsbInterfacePolicy() {
			public boolean forceClaim(UsbInterface usbInterface) {
				return true;
			}
		});

		
	}

	/**
	 * Output on the device the text with a delay in ms
	 * 
	 * @param text
	 * @param delay
	 * @throws UsbException
	 */
	public void scrollTextLeft(String text, int delay) throws UsbException {
		scrollTextLeft(this.device, text, delay);
	}
	
	/**
	 * Output on the device the text with a delay in ms
	 * 
	 * @param text
	 * @param delay
	 * @throws UsbException
	 */
	public void scrollTextRight(String text, int delay) throws UsbException {
		scrollTextRight(this.device, text, delay);
	}
	
	/**
	 * Raw write of a 7x21 matrix
	 *
	 * @param message
	 * @throws UsbException
	 */
	public void write(byte[][] message) throws UsbException {
		sendMatrix(device, message);;
	}
	
	/**
	 * Scroll the text string on the device led matrix from Right to Left
	 * 
	 * @param device
	 * @param text
	 * @throws UsbException
	 */
	private void scrollTextLeft(UsbDevice device, String text, int delay) throws UsbException {
		byte[][] matrix = new byte[LED_MATRIX_HEIGHT][text.length() * LED_MATRIX_HEIGHT];
		matrix = MatrixUtils.write(text, matrix);

		for (int i = 0; i < matrix[0].length-LED_MATRIX_WIDTH; ++i) {
			byte[][] window = MatrixUtils.slice(matrix, i);
			sendMatrix(device, window);
			wait(delay);
		}
	}

	/**
	 * Scroll the text string on the device led matrix from Left to Right
	 * 
	 * @param device
	 * @param text
	 * @throws UsbException
	 */
	private void scrollTextRight(UsbDevice device, String text, int delay) throws UsbException {
		byte[][] matrix = new byte[LED_MATRIX_HEIGHT][text.length() * LED_MATRIX_HEIGHT];
		matrix = MatrixUtils.write(text, matrix);

		for (int i = matrix[0].length - LED_MATRIX_WIDTH; i >= 0; --i) {
			byte[][] window = MatrixUtils.slice(matrix, i);
			sendMatrix(device, window);
			wait(delay);
		}
	}

	private static void wait(int delay) {
		try {
			Thread.sleep(delay);			//TODO change to scheduled Task
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void sendMessage(UsbDevice device, byte[] message) throws UsbException {

		UsbControlIrp irp = device.createUsbControlIrp((byte) 0x21, (byte) 0x09, (short) 0x0200, (short) 0x0000);
		irp.setData(message);

		device.syncSubmit(irp);
		irp.waitUntilComplete(100);

	}

	private void sendMatrix(UsbDevice device, byte[][] matrix) throws UsbException {
		byte[] message = MatrixUtils.fromMatrixToMessage(matrix, getBrightness());

		sendMessage(device, Arrays.copyOfRange(message, 0, 8));

		sendMessage(device, Arrays.copyOfRange(message, 8, 16));

		sendMessage(device, Arrays.copyOfRange(message, 16, 24));

		sendMessage(device, Arrays.copyOfRange(message, 24, message.length));
	}

	/**
	 * Find the device with vendorId and productId
	 * 
	 * @param hub
	 * @param vendorId
	 * @param productId
	 * @return USB device or null if not found.
	 */
	private static UsbDevice find(UsbHub hub, short vendorId, short productId) {
		UsbDevice launcher = null;

		for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
			if (device.isUsbHub()) {
				launcher = find((UsbHub) device, vendorId, productId);
				if (launcher != null)
					return launcher;
			} else {
				UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
				if (desc.idVendor() == vendorId && desc.idProduct() == productId)
					return device;
			}
		}
		return null;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness>2?2:brightness;
		this.brightness = brightness<0?0:brightness;
	}
}
