package it.loreii.ledstripe;

import java.util.Arrays;
import java.util.Collections;
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

public class Main {
	/** The vendor ID of the led stripe. */
	private static final short VENDOR_ID = 0x1D34;

	/** The product ID of the led stripe. */
	private static final short PRODUCT_ID = 0x0013;

	public static void main(String[] args) throws UsbException {

		UsbDevice device = find(UsbHostManager.getUsbServices().getRootUsbHub(), VENDOR_ID, PRODUCT_ID);
		if (device == null) {
			System.err.println("device not found.");
			System.exit(1);
			return;
		}

		// Claim the interface
		UsbConfiguration configuration = device.getUsbConfiguration((byte) 1);
		UsbInterface iface = configuration.getUsbInterface((byte) 0);
		iface.claim(new UsbInterfacePolicy() {
			public boolean forceClaim(UsbInterface usbInterface) {
				return true;
			}
		});

		
		{
			byte[][] matrix = { { 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
								{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
								{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
								{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
								{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
								{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
								{ 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 }, };
			sendMatrix(device, matrix);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String text = "ABCDEFGHILMNOPQRSUVZ";
		byte[][] matrix = new byte[7][text.length() * 7];
		matrix = write(text, matrix);

		while (true) {

			for (int i = 0; i < text.length()*7 - 1 ; ++i) {
				byte[][] window = slice(matrix, i);

				sendMatrix(device, window);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * from original matrix return a new matrix with a fixed size 7x21 from index i
	 * */
	private static byte[][] slice(byte[][] matrix, int i) {
		byte[][] r = new byte[7][21]; //extract constants
		for(int y=0;y<r.length;++y)
			for(int x=0;x<r[y].length;++x){
				r[y][x]=matrix[y][x+i];
			}
				
		return r;
	}

	/**
	 * pretty print a matrix on terminal, useful for debug
	 * */
	private static void print(byte[][] matrix) {
		for (int y = 0; y < matrix.length; ++y) {
			for (int x = 0; x < matrix[y].length; ++x)
				if (matrix[y][x] > 0)
					System.out.print("X");
				else
					System.out.print(" ");
			System.out.println();
		}
	}
	
	/**
	 * This method concatenate two matrix creating a new one, not really
	 * efficient because is created each time new byte array
	 * 
	 * @param matrix
	 * @param c
	 * @return
	 */
	private static byte[][] collate(byte[][] matrix, char c) {

		byte[][] charMap = CharToByteArray.decodeMap.get(c);
		for (int y = 0; y < charMap.length; ++y) {
			int newLenght = matrix[y].length + charMap[y].length;
			byte[] fusion = new byte[newLenght];
			for (int x = 0; x < newLenght; ++x) {
				fusion[x] = x < matrix[y].length ? matrix[y][x] : charMap[y][x];
			}
		}

		return matrix;

	}

	/**
	 * write on matrix
	 */
	public static byte[][] write(String str, byte[][] matrix) {
		int length = str.length();
		char[] dst = new char[length];
		str.getChars(0, length, dst, 0);
		int counter = 0;
		for (char c : dst) {
			byte[][] charMap = CharToByteArray.decodeMap.get(c);
			for (int y = 0; y < charMap.length; ++y)
				for (int x = 0; x < charMap[y].length; ++x) {
					matrix[y][x + counter * charMap[y].length] = charMap[y][x];
				}
			++counter;
		}
		return matrix;
	}

	public static void sendMessage(UsbDevice device, byte[] message) throws UsbException {

		UsbControlIrp irp = device.createUsbControlIrp((byte) 0x21, (byte) 0x09, (short) 0x0200, (short) 0x0000);
		irp.setData(message);

		device.syncSubmit(irp);
		irp.waitUntilComplete();

	}

	public static void sendMatrix(UsbDevice device, byte[][] matrix) throws UsbException {
		byte[] message = MatrixUtils.fromMatrixToMessage(matrix, 0);

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
	public static UsbDevice find(UsbHub hub, short vendorId, short productId) {
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
}