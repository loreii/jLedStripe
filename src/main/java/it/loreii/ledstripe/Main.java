package it.loreii.ledstripe;

import javax.usb.UsbException;

public class Main {
	
	public static void main(String[] args) throws UsbException {
		LedStripe led = LedStripe.getInstance();
		
		{//sample direct matrix draw
			byte[][] matrix = { 
					{ 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
					{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 }, };
			led.write(matrix);
		}
		//keep writing some text
		while (true) {
			led.setBrightness(0);
			led.scrollTextLeft( "ABCDEFGHILMNOPQRSUVZ", 300);
			led.scrollTextRight( "ABCDEFGHILMNOPQRSUVZ".toLowerCase(), 300);
		}
	}

	
}