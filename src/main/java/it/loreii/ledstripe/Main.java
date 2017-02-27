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

		byte[][] matrix = { { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };

		while (true) {
		    String text = "this is a scrolling sample";
		    byte LED_BUFFER_SIZE = 3;
		    for(int i=0;i<text.length()-LED_BUFFER_SIZE;++i){
		        String substring = text.substring(i,i+LED_BUFFER_SIZE);
				matrix = write(substring,matrix);
                sendMatrix(device, matrix);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
		}
	}

    /**
     * write on matrix the first 3 chars of string
     * */
	public static byte[][] write(String str, byte[][] matrix){
	    if(str.length()>3){
	        str = str.substring(0,3);
        }
		int length = str.length();
		char[] dst = new char[length];
		str.getChars(0, length,dst,0);
		int counter=0;
		for(char c : dst){
			byte[][] charMap = CharToByteArray.decodeMap.get(c);
			for(int y=0;y<charMap.length;++y)
				for(int x=0;x<charMap[y].length;++x){	
					matrix[y][x+counter*charMap[y].length]=charMap[y][x];
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