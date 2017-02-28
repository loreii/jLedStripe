package it.loreii.ledstripe;

import java.security.InvalidParameterException;
import java.util.Arrays;
import static it.loreii.ledstripe.LedStripe.*;

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
		
		if(matrix.length > LED_MATRIX_HEIGHT){
			throw new InvalidParameterException("Matrix size not supported");
		}
		for(byte[] row:matrix){
			if(row.length > LED_MATRIX_WIDTH){
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

	/**
	 * from original matrix return a new matrix with a fixed size 7x21 from index i
	 * */
	public static byte[][] slice(byte[][] matrix, int i) {
		byte[][] r = new byte[LED_MATRIX_HEIGHT][LED_MATRIX_WIDTH];
		for(int y=0;y<r.length;++y)
			for(int x=0;x<r[y].length;++x){
				r[y][x]=matrix[y][x+i];
			}
				
		return r;
	}

	/**
	 * pretty print a matrix on terminal, useful for debug
	 * */
	public static void print(byte[][] matrix) {
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
	public static byte[][] write(String str, byte[][] matrix,boolean trimSpace) {
		int length = str.length();
		char[] dst = new char[length];
		str.getChars(0, length, dst, 0);
		int counter = 0;
		int delta = 0;
		for (char c : dst) {
			int lastOne = 0;
				
			byte[][] charMap = CharToByteArray.decodeMap.get(c);
			for (int y = 0; y < charMap.length; ++y)
				for (int x = 0; x < charMap[y].length; ++x) {
					matrix[y][x + (counter * charMap[y].length) - delta] = charMap[y][x];
					lastOne = charMap[y][x] > 0?Math.max(x,lastOne):lastOne;
				}
			delta = trimSpace? 5-lastOne+delta:0;
			++counter;
		}
		return matrix;
	}
	
}
