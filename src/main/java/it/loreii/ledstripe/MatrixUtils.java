package it.loreii.ledstripe;

import java.security.InvalidParameterException;
import java.util.Arrays;

public class MatrixUtils {
	private static final int MESSAGE_SIZE = 32;
	/**
	 * Convert a readable representation of a 7*21 led matrix with to a led matrix protocol message
	 * 
	 * @param matrix 7x21 byte[][] with 1 to represent led on
	 * @param brightness led intensity 0..2
	 * @return
	 */
	public static byte[] fromMatrixToMessage(byte[][] matrix, int brightness ){
		
		if(matrix.length > 7){
			throw new InvalidParameterException("Matrix size not supported");
		}
		for(byte[] row:matrix){
			if(row.length > 21){
				throw new InvalidParameterException("Matrix size not supported");
			}
		}
		if(brightness > 2||brightness<0){
			throw new InvalidParameterException("brightness range is 0..2");
		}
		
		byte[] message = new byte[64];
		byte c = 0;
		for (int row : Arrays.asList(0, 2, 4, 6)) {
			message[c++] = (byte) brightness; // brightness
			message[c++] = (byte) row; // current row
			c = lineToByte(matrix, message, c, row);
			row++;
			if(row>6) continue;
			c = lineToByte(matrix, message, c, row);
		}

		return Arrays.copyOfRange(message,0,MESSAGE_SIZE);
	}

	private static byte lineToByte(byte[][] matrix, byte[] message, byte c, int row) {
		byte next;
		next = (byte) 0xE0;
		for (byte col = 20; col >= 0; col--) {
			if (col == 15 || col == 7) {
				message[c++] = next;
				next = 0;
			}
			next |= (matrix[row][col] > 0 ? 0 : 1) << (col % 8);
		}
		message[c++] = next;
		return c;
	}
	
}
