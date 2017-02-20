# jLedStripe

This utility is intended to be used with Dream Link Ltd. "USB LED Message Board". 

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

```
 LedStripe str = LedStripe.getInstance();
 str.delay(100);//ms
 str.orientation(LedStripe.LEFT);//message animation
 str.write("This is a sample");
```


###references
1. [javax-usb quickstart page](http://usb4java.org/quickstart/javax-usb.html)
2. [similar project written in C](https://github.com/dingram/led-display)
3. [missile luncher usb4java example](https://github.com/usb4java/usb4java-javax-examples/blob/master/src/main/java/org/usb4java/javax/examples/MissileLauncher.java)
