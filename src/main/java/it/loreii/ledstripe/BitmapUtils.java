package it.loreii.ledstripe;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lpavesi on 2/27/2017.
 *
 * This class render on a bitmap the ASCII characters from 32 to 120, and then convert them into a byte matrix addressable by a HashMap.
 * This class is a toy like rasterization for address the bit inside the LED matrix, a better use shall be access the DataBufferByte
 * and use it around all the code.
 *
 * The generated java file is output into CharToByteArray.java (@see CharToByteArray.java)
 */
public class BitmapUtils extends JPanel{

    private static final boolean SHOW_RENDER_IN_FRAME = false;
    private static final boolean GENERATE_JAVA_BYTE_ARRAY = true;

    private static final int FIXED_MARGIN_PIXEL = 2;
    private static final char MIN_ASCII_CHAR = 32 ;
    private static final char MAX_ASCII_CHAR = 120 ;

    public static void main(String[] args) throws FileNotFoundException {

        if(SHOW_RENDER_IN_FRAME) {
            JFrame frame = new JFrame();
            frame.getContentPane().add(new BitmapUtils());

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(200, 200);
            frame.setVisible(true);
        }else if(GENERATE_JAVA_BYTE_ARRAY){ //TODO extract configuration parameters into CLI command

            PrintStream out = new PrintStream(new FileOutputStream("src/main/java/it/loreii/ledstripe/CharToByteArray.java"));

            out.println(
                    "package it.loreii.ledstripe;\n" +
                    "import java.util.HashMap;\n" +
                    "import java.util.Map;\n" +
                    "/**" +
                    " * Generated at: "+ Calendar.getInstance().getTime()+
                    " */");
            out.println( "final class CharToByteArray{");

            BitmapUtils bitmapUtils = new BitmapUtils();
            for(char c=MIN_ASCII_CHAR;c<MAX_ASCII_CHAR;++c)
               bitmapUtils.generateCode(out, c,7);
            out.println("static Map<Character, byte[][]> decodeMap = new HashMap<>();");
            out.println("static{");

            for(char c=MIN_ASCII_CHAR;c<MAX_ASCII_CHAR;++c) {
                out.println("decodeMap.put((char)"+(int)c+", CHAR_"+(int)c+");");
            }
            out.println("}}");
        }
    }

    private void generateCode(PrintStream out, char c, int height) {
            int width=height;

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            Graphics g = bufferedImage.getGraphics();
            g.setFont(new Font("TimesRoman", Font.PLAIN, height+FIXED_MARGIN_PIXEL));

            g.drawString(String.valueOf(c), 0, width);
            out.println("/**");
            for (int y = 0; y < 7; ++y)  {
                for(int x = 0; x<7;++x) {
                    out.print((bufferedImage.getRGB(x, y) == -1 ? "█" : "░") );
                }
                out.println();
            }
            out.println("*/");
            out.print("private static final byte[][] CHAR_"+(int)c+"={\n");
            for (int y = 0; y < height; ++y) {
                out.print("{");
                for (int x = 0; x < width; ++x) {
                    out.print((bufferedImage.getRGB(x, y) == -1 ? "0x01" : "0x00"));
                    if(x < width-1) out.print(",");
                }
                out.print("}");
                if(y < height-1) out.println(",");
            }

            out.println("};\n");
           g.clearRect(0,0,width,height);


        }


    public void paint(Graphics g) {
        Image img = createImageWithText('B');
        g.drawImage(img, 0,0,this);
    }

    private Image createImageWithText(char c){
        BufferedImage bufferedImage = new BufferedImage(7,7,BufferedImage.TYPE_BYTE_BINARY);
        Graphics g = bufferedImage.getGraphics();
        g.setFont(new Font("TimesRoman", Font.PLAIN, 9));
        System.out.println("Font "+ g.getFont());
        System.out.println("Font size "+ g.getFont().getSize());

        g.drawString("B", 0,7);

        for (int y = 0; y < 7; ++y)  {
            for(int x = 0; x<7;++x) {
                System.out.print((bufferedImage.getRGB(x, y) == -1 ? "█" : "░") );
            }
                System.out.println();
        }

        //Raster data = bufferedImage.getData(); //DataBufferByte will be more efficient also around

        return bufferedImage;
    }


}
