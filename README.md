# jLedStripe     ![travis](https://travis-ci.org/loreii/jLedStripe.svg?branch=master)  [![Coverage Status](https://coveralls.io/repos/github/loreii/jLedStripe/badge.svg?branch=master)](https://coveralls.io/github/loreii/jLedStripe?branch=master)        ![logo](https://cloud.githubusercontent.com/assets/1318102/23132748/e6f0c56c-f78e-11e6-801b-a0fc4f761cc8.png) 

This utility is intended to be used as java driver for Dream Cheeky LED Message Board. 

![image](https://cloud.githubusercontent.com/assets/1318102/23132181/14c09758-f78d-11e6-91b6-b2085a39bfe0.png)

Right now the application is able to conect to the HID device with libusb java library usb4java: 

|Name                | Value |
|--------------------|-------|
|Vendor ID           | 0x1D34|
|Product ID          | 0x0013|
|Manufacturer String |Dream Link|
|Product String      |USB LED Message Board v1.0|
|Version             | 0x01|
|Serial              |Number 1| 

and decode a 1:1 byte array matrix rapresentign the single led to the legacy protocol of the board.
The future imporvements will provide a library to be used in any java aplication for interact in a simply way to the led matrix in a java style approach like:

####Sample direct matrix draw
```
 	LedStripe led = LedStripe.getInstance();
	
			byte[][] matrix = { 
					{ 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
					{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
					{ 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 }, };
			led.write(matrix);
```
####Keep writing some text
```
		while (true) {
			led.setBrightness(0);
			led.scrollTextLeft( "ABCDEFGHILMNOPQRSUVZ", 300);
			led.scrollTextRight( "ABCDEFGHILMNOPQRSUVZ".toLowerCase(), 300);
		}
```


###References
1. [javax-usb quickstart page](http://usb4java.org/quickstart/javax-usb.html)
2. [similar project written in C](https://github.com/dingram/led-display)
3. [missile luncher usb4java example](https://github.com/usb4java/usb4java-javax-examples/blob/master/src/main/java/org/usb4java/javax/examples/MissileLauncher.java)
4. [Dream Cheeky LED Message Board](http://dreamcheeky.com/led-message-board)
