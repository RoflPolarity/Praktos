import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Veivlet {
    public static void main(String[] args) throws IOException {
        BufferedImage pic = ImageIO.read(new File("dom.bmp"));
        MassNoise(5,pic);
        ImageIO.write(pic,"bmp",new File("noise.bmp"));
        NormFactor(pic);
        ImageIO.write(pic,"bmp",new File("norm.bmp"));
        ImageIO.write(sobelOperator(pic),"bmp",new File("sobelDom.bmp"));
        ImageIO.write(NormFactor(grab(RSchmX(ImageIO.read(new File("dom.bmp"))),RSchmY(ImageIO.read(new File("dom.bmp"))),ImageIO.read(new File("dom.bmp")))),"bmp",new File("test.bmp"));
    }
    public static BufferedImage MassNoise(int sigma, BufferedImage pic){
        Random rand = new Random();
        for (int i = 0; i < pic.getWidth(); i++)for (int j = 0; j < pic.getHeight(); j++) pic.setRGB(i,j, Math.abs((int) (pic.getRGB(i,j) + sigma*rand.nextGaussian())));
        return pic;
    }
    public static BufferedImage NormFactor(BufferedImage pic){
        int min = pic.getRGB(0,0), max = pic.getRGB(0,0);
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++)if (pic.getRGB(i,j)<min)min = pic.getRGB(i,j);
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++)if (pic.getRGB(i,j)>max)max = pic.getRGB(i,j);
        for (int i = 0; i < pic.getHeight(); i++) for (int j = 0; j < pic.getWidth(); j++) pic.setRGB(i,j,((pic.getRGB(i,j)-min)*254)/(max-min));
        return pic;
    }
    public static BufferedImage veivletDog(BufferedImage pic){
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                int x = Math.abs(pic.getRGB(i,j));
                pic.setRGB(i,j,(int) (Math.pow(Math.E,-Math.pow(x,2)/2) - 0.5*Math.pow(Math.E,-Math.pow(x,2)/8)));
            }
        }
        return pic;
    }
    public static BufferedImage veivletDogP(BufferedImage pic){
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                int x = Math.abs(pic.getRGB(i,j));
                pic.setRGB(i,j,(int) (0.125 * x * Math.pow(Math.E,-Math.pow(x,2)/8) - x*Math.pow(Math.E,-Math.pow(x,2)/2)));
            }
        }
        return pic;
    }
    public static BufferedImage veivletMHAT(BufferedImage pic){
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                int x = Math.abs(pic.getRGB(i,j));
                pic.setRGB(i,j,(int) (((2*Math.pow(Math.PI,-0.25))/Math.sqrt(3))*(1-Math.pow(x,2))*Math.pow(Math.E,- Math.pow(x,2)/2)));
            }
        }
        return pic;
    }
    public static BufferedImage veivletMHATP(BufferedImage pic){
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                int x = Math.abs(pic.getRGB(i,j));
                pic.setRGB(i,j,(int) ((2*Math.sqrt(3)*x*Math.pow(Math.E,-Math.pow(x,2)/2)*(Math.pow(x,2)-3))/(3*Math.pow(Math.PI,0.25))));
            }
        }
        return pic;
    }
    public static BufferedImage sobelOperator(BufferedImage pic){
        int[][] MGx = {{1,0,-1},
                    {2,0,-2},
                    {1,0,-1}},
                MGy = {{1,2,1},
                        {0,0,0},
                        {-1,-2,-1}},
                matrix = new int[pic.getWidth()][pic.getWidth()],
                picMatrix = new int[pic.getWidth()][pic.getHeight()];

        for (int i = 0; i < matrix.length; i++)for (int j = 0; j < matrix[i].length; j++)matrix[i][j] = Math.abs(pic.getRGB(i,j));
        for (int iY  = 1; iY< pic.getHeight()-2; iY++){
            for (int iX = 1; iX < pic.getWidth()-2; iX++) {
                int GX = 0, GY = 0;
                int[][] A = getSubMatrix(matrix,iY-1,iY+1,iX-1,iX+1);
                for (int y = 0; y < 2; y++)for (int x = 0; x < 2; x++)GX += A[y][x]*MGx[y][x];
                for (int y = 0; y < 2; y++)for (int x = 0; x < 2; x++)GY += A[y][x]*MGy[y][x];
                picMatrix[iY][iX] = (int) Math.sqrt(Math.pow(GX,2)+(Math.pow(GY,2)));
            }
        }
        for (int i = 0; i < pic.getHeight(); i++)for (int j = 0; j < pic.getHeight(); j++)pic.setRGB(i,j,picMatrix[i][j]);
        return pic;
    }
    public static int[][] getSubMatrix(int[][] matrix, int firstRow, int destRow, int firstCol, int destCol){
        int[][] newMatrix = new int[destRow-firstRow+1][destCol-firstCol+1];
        for (int i = 0; i < newMatrix.length; i++, firstRow++) {
            int col = firstCol;
            for (int j = 0; j < newMatrix[i].length; j++, col++) {
                newMatrix[i][j] = matrix[firstRow][col];
            }
        }
        return newMatrix;
    }
    public static BufferedImage grab(BufferedImage DifferentX, BufferedImage DifferentY, BufferedImage pic){
        for (int x = 0; x < DifferentX.getWidth()-1; x++) {
            for (int y = 0; y < DifferentY.getWidth()-1; y++) {
                pic.setRGB(x,y,(int) Math.sqrt(Math.pow(DifferentX.getRGB(x,y),2)+Math.pow(DifferentY.getRGB(x,y),2)));
            }
        }
        return pic;
    }
    public static BufferedImage RSchmX(BufferedImage pic){
        for (int x = 1; x < pic.getHeight()-1; x++) {
            for (int y = 1; y < pic.getWidth()-1; y++) {
                pic.setRGB(y,x,pic.getRGB(y,x)-pic.getRGB(y,x-1));
            }
        }
        return pic;
    }
    public static BufferedImage RSchmY(BufferedImage pic) {
        for (int x = 1; x < pic.getHeight() - 1; x++) {
            for (int y = 1; y < pic.getWidth() - 1; y++) {
                pic.setRGB(y, x, pic.getRGB(y, x) - pic.getRGB(y-1, x));
            }
        }
        return pic;
    }
    //WRITE
    public static int dXDOG(BufferedImage pic){
        int res = 0;
        int Xdecomposition = (int) (pic.getHeight()/Math.log(2))-1, Xquantity = pic.getHeight();
        for (int y = 0; y < pic.getWidth()-1; y++) {
            for (int x = 0; x < pic.getWidth()-1; x++) {

            }
        }

        return res;
    }
}