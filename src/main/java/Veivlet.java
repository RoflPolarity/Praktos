import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Veivlet {
    public static void main(String[] args) throws IOException {
        File picPath = new File("car.jpg");
        System.out.println(picPath.getAbsolutePath());
        BufferedImage pic = ImageIO.read(picPath);
        byte[] pixels = ((DataBufferByte) pic.getRaster().getDataBuffer()).getData();
        System.out.println(Arrays.toString(pixels));
    }
    public static byte[] MassNoise(byte[] mass, int sigma, BufferedImage pic){
        byte[] res = new byte[mass.length];
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                res
            }
        }
    }

}
