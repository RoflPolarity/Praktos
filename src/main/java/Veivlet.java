import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Veivlet {
    public static void main(String[] args) throws IOException {
        File picPath = new File("car.jpg");
        System.out.println(picPath.getAbsolutePath());
        BufferedImage pic = ImageIO.read(picPath);
        int[][] pixels = new int[pic.getWidth()][pic.getHeight()];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                pic.setRGB(i,j,MassNoise(5,pic)[i][j]);
            }
        }
        File noise = new File("noise.jpg");
        ImageIO.write(pic,"jpg",noise);
        File norm = new File("norm.jpg");
        ImageIO.write(NormFactor(pic),"jpg",norm);
    }
    public static int[][] MassNoise(int sigma, BufferedImage pic){
        Random rand = new Random();
        int[][] res = new int[pic.getHeight()][pic.getWidth()];
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                res[i][j] = (int) (Math.abs(pic.getRGB(i,j)) + sigma*rand.nextGaussian());
            }
        }
        return res;
    }
    public static BufferedImage NormFactor(BufferedImage pic){
        int min = Math.abs(pic.getRGB(0,0)), max = Math.abs(pic.getRGB(0,0));
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++)if (Math.abs(pic.getRGB(i,j))<min)min = Math.abs(pic.getRGB(i,j));
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++)if (Math.abs(pic.getRGB(i,j))>max)max = Math.abs(pic.getRGB(i,j));
        for (int i = 0; i < pic.getHeight(); i++) for (int j = 0; j < pic.getWidth(); j++) pic.setRGB(i,j,((pic.getRGB(i,j)-min)*254)/(max-min));
        return pic;
    }
}
