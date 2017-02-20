package it.loreii.ledstripe;

import static org.junit.Assert.*;

import org.junit.Test;

public class MatrixUtilsTest {

	static byte[][] matrix = { { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };

	static byte[] testMessage = { (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFD, (byte) 0x7F, (byte) 0x00, (byte) 0x02, (byte) 0xFF, (byte) 0xFB, (byte) 0xBF, (byte) 0xFF,
			(byte) 0xF7, (byte) 0xDF, (byte) 0x00, (byte) 0x04, (byte) 0xFF, (byte) 0xFB, (byte) 0xBF, (byte) 0xFF,
			(byte) 0xFD, (byte) 0x7F, (byte) 0x00, (byte) 0x06, (byte) 0xFF, (byte) 0xFE, (byte) 0xFF, (byte) 0x00,
			(byte) 0x00, (byte) 0x00 };

	@Test
	public void fromMatrixToMessageTest() {

		byte[] message = MatrixUtils.fromMatrixToMessage(matrix, 0);

		System.err.println(message.length);

		System.err.println(testMessage.length);
		assertArrayEquals(message, testMessage);

	}

}
