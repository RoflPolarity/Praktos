import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Veivlet {
    private final int Xdecomposition,Ydecomposition, Xquantity, Yquantity, a = 3;
    private final BufferedImage image;
    private final BufferedImage noiseImg;
    private final BufferedImage normImg;
    private final BufferedImage sobelImg;
    private final BufferedImage GrabImg;

    public Veivlet(String path) throws IOException {
        String username = System.getProperty("user.name");
        File file = new File(path), directory = new File("C:\\Users\\" + username +"\\Desktop\\"+file.getName().split("\\.")[0]);
        if (!directory.exists())directory.mkdir();
        image = ImageIO.read(file);
        Xquantity = image.getWidth();
        Yquantity = image.getHeight();
        Xdecomposition = (int) ((Math.log(Xquantity)/Math.log(2))-1);
        Ydecomposition = (int) ((Math.log(Yquantity)/Math.log(2))-1);
        noiseImg = MassNoise(5, image);
        ImageIO.write(noiseImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\noise." + getFileExtension(file)));
        normImg = NormFactor(noiseImg);
        ImageIO.write(normImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\norm." + getFileExtension(file)));
        sobelImg = sobelOperator(normImg);
        ImageIO.write(sobelImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\Sobel." + getFileExtension(file)));
        GrabImg = NormFactor(grab(RSchmX(ImageIO.read(file)),RSchmY(ImageIO.read(file)),ImageIO.read(file)));
        ImageIO.write(GrabImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\test." + getFileExtension(file)));

    }
    public Image getNoiseImg(){return SwingFXUtils.toFXImage(noiseImg,null);}
    public Image getNormImg(){return SwingFXUtils.toFXImage(normImg,null);}
    public Image getsobelImg(){return SwingFXUtils.toFXImage(sobelImg,null);}
    public Image getGrabImg(){return SwingFXUtils.toFXImage(GrabImg,null);}
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private static BufferedImage MassNoise(int sigma, BufferedImage pic){
        Random rand = new Random();
        for (int i = 0; i < pic.getWidth(); i++)for (int j = 0; j < pic.getHeight(); j++) pic.setRGB(i,j, Math.abs((int) (pic.getRGB(i,j) + sigma*rand.nextGaussian())));
        return deepCopy(pic);
    }
    private static BufferedImage NormFactor(BufferedImage pic){
        int min = pic.getRGB(0,0), max = pic.getRGB(0,0);
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++)if (pic.getRGB(i,j)<min)min = pic.getRGB(i,j);
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++)if (pic.getRGB(i,j)>max)max = pic.getRGB(i,j);
        for (int i = 0; i < pic.getWidth(); i++) for (int j = 0; j < pic.getHeight(); j++) pic.setRGB(i,j,((pic.getRGB(i,j)-min)*254)/(max-min));
        return deepCopy(pic);
    }
    private static int veivletDog(int x){return (int) (Math.pow(Math.E,-Math.pow(x,2)/2) - 0.5*Math.pow(Math.E,-Math.pow(x,2)/8));}
    private static int veivletDogP1(int x){return(int) (0.125 * x * Math.pow(Math.E,-Math.pow(x,2)/8) - x*Math.pow(Math.E,-Math.pow(x,2)/2));}
    private int diskretDog(int x, int m,int n){ return (int) (Math.pow(a,-m/2)*veivletDog((int) (Math.pow(a,-m)*x-n)));}
    private int diskretDogP1(int x, int m,int n){ return (int) (Math.pow(a,-m/2)*veivletDogP1((int) (Math.pow(a,-m)*x-n)));}
    private static BufferedImage veivletMHAT(BufferedImage pic){
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                int x = Math.abs(pic.getRGB(i,j));
                pic.setRGB(i,j,(int) (((2*Math.pow(Math.PI,-0.25))/Math.sqrt(3))*(1-Math.pow(x,2))*Math.pow(Math.E,- Math.pow(x,2)/2)));
            }
        }
        return pic;
    }
    private static BufferedImage veivletMHATP(BufferedImage pic){
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                int x = Math.abs(pic.getRGB(i,j));
                pic.setRGB(i,j,(int) ((2*Math.sqrt(3)*x*Math.pow(Math.E,-Math.pow(x,2)/2)*(Math.pow(x,2)-3))/(3*Math.pow(Math.PI,0.25))));
            }
        }
        return pic;
    }
    private static BufferedImage sobelOperator(BufferedImage pic){
        int[][] MGx = {{1,0,-1},
                {2,0,-2},
                {1,0,-1}},
                MGy = {{1,2,1},
                        {0,0,0},
                        {-1,-2,-1}},
                matrix = new int[pic.getWidth()][pic.getHeight()],
                picMatrix = new int[pic.getWidth()][pic.getHeight()];

        for (int i = 0; i < matrix.length; i++)for (int j = 0; j < matrix[i].length; j++)matrix[i][j] = Math.abs(pic.getRGB(i,j));
        for (int iY  = 1; iY< pic.getWidth()-2; iY++){
            for (int iX = 1; iX <pic.getHeight() -2; iX++) {
                int GX = 0, GY = 0;
                int[][] A = getSubMatrix(matrix,iY-1,iY+1,iX-1,iX+1);
                for (int y = 0; y < 2; y++)for (int x = 0; x < 2; x++)GX += A[y][x]*MGx[y][x];
                for (int y = 0; y < 2; y++)for (int x = 0; x < 2; x++)GY += A[y][x]*MGy[y][x];
                picMatrix[iY][iX] = (int) Math.sqrt(Math.pow(GX,2)+(Math.pow(GY,2)));
            }
        }
        for (int i = 0; i < pic.getWidth(); i++)for (int j = 0; j < pic.getHeight(); j++)pic.setRGB(i,j,picMatrix[i][j]);
        return deepCopy(pic);
    }
    private static int[][] getSubMatrix(int[][] matrix, int firstRow, int destRow, int firstCol, int destCol){
        int[][] newMatrix = new int[destRow-firstRow+1][destCol-firstCol+1];
        for (int i = 0; i < newMatrix.length; i++, firstRow++) {
            int col = firstCol;
            for (int j = 0; j < newMatrix[i].length; j++, col++) {
                newMatrix[i][j] = matrix[firstRow][col];
            }
        }
        return newMatrix;
    }
    private static BufferedImage grab(BufferedImage DifferentX, BufferedImage DifferentY, BufferedImage pic){
        for (int x = 0; x < DifferentX.getWidth()-1; x++) {
            for (int y = 0; y < DifferentY.getHeight()-1; y++) {
                pic.setRGB(x,y,(int) Math.sqrt(Math.pow(DifferentX.getRGB(x,y),2)+Math.pow(DifferentY.getRGB(x,y),2)));
            }
        }
        return deepCopy(pic);
    }
    private static BufferedImage RSchmX(BufferedImage pic){
        for (int x = 1; x < pic.getHeight()-1; x++) {
            for (int y = 1; y < pic.getWidth()-1; y++) {
                pic.setRGB(y,x,pic.getRGB(y,x)-pic.getRGB(y,x-1));
            }
        }
        return pic;
    }
    private static BufferedImage RSchmY(BufferedImage pic) {
        for (int x = 1; x < pic.getHeight() - 1; x++) {
            for (int y = 1; y < pic.getWidth() - 1; y++) {
                pic.setRGB(y, x, pic.getRGB(y, x) - pic.getRGB(y-1, x));
            }
        }
        return pic;
    }


    private int[][] DWTDOGX(BufferedImage pic){
        int[][] res = new int[pic.getHeight()][pic.getWidth()];
        int[][] DWT = new int[Xdecomposition][Xquantity-1];
        for (int y = 0; y < Yquantity-1; y++) {
            for (int m = 0; m < Xdecomposition; m++) {
                for (int n = 0; n < Xquantity-1; n++) {
                    int summ = 0;
                    for (int i = 0; i < Xquantity-1; i++) {
                        summ+= diskretDog(i, (int) Math.pow(2,m-1),n)*pic.getRGB(i,y);
                    }
                    DWT[m][n] = summ;
                }
            }
            res[y] = DWT[y];
        }
        return res;
    }
    private int dXDOG(BufferedImage pic){
        int res = 0;
        for (int y = 0; y < Xdecomposition; y++) {
            for (int x = 0; x < Xquantity-1; x++) {
                
            }
        }

        return res;
    }
}
