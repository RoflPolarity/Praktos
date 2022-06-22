import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Veivlet {
    public static void main(String[] args) throws IOException {
        BufferedImage pic = ImageIO.read(new File("car.jpg"));
        ImageIO.write(MassNoise(1,pic),"jpg",new File("noise.jpg"));
        ImageIO.write(NormFactor(pic),"jpg",new File("norm.jpg"));
    }
    public static BufferedImage MassNoise(int sigma, BufferedImage pic){
        Random rand = new Random();
        for (int i = 0; i < pic.getWidth(); i++)for (int j = 0; j < pic.getHeight(); j++) pic.setRGB(i,j, (int) (Math.abs(pic.getRGB(i,j)) + sigma*rand.nextGaussian()));
        return pic;
    }
    public static BufferedImage NormFactor(BufferedImage pic){
        int min = Math.abs(pic.getRGB(0,0)), max = Math.abs(pic.getRGB(0,0));
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++)if (Math.abs(pic.getRGB(i,j))<min)min = Math.abs(pic.getRGB(i,j));
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++)if (Math.abs(pic.getRGB(i,j))>max)max = Math.abs(pic.getRGB(i,j));
        for (int i = 0; i < pic.getHeight(); i++) for (int j = 0; j < pic.getWidth(); j++) pic.setRGB(i,j,((pic.getRGB(i,j)-min)*254)/(max-min));
        return pic;
    }
}
