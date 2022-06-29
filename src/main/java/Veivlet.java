import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Veivlet {
    private final int Xdecomposition, Xquantity, a = 3,nX, mX,kX;
    private final int Ydecomposition, Yquantity, mY, nY, kY;
    private Thread DOG;
    private final BufferedImage image;
    private final BufferedImage noiseImg;
    private final BufferedImage normImg;
    private final BufferedImage sobelImg;
    private BufferedImage GrabImg;
    private BufferedImage DxDog;
    private BufferedImage DyDog;
    private BufferedImage VeivletDog;
    private String path;
    private File file, directory;
    public Veivlet(String path) throws IOException {
        String username = System.getProperty("user.name");
        this.path = path;
        file = new File(path);
        directory = new File("C:\\Users\\" + username +"\\Desktop\\"+file.getName().split("\\.")[0]);
        if (!directory.exists())directory.mkdir();
        image = ImageIO.read(file);


        Xquantity = image.getWidth();
        Yquantity = image.getHeight();
        Xdecomposition = (int) ((Math.log(Xquantity)/Math.log(2))-1);
        Ydecomposition = (int) ((Math.log(Yquantity)/Math.log(2))-1);
        nX = Xquantity-1;
        mX = Xdecomposition;
        kX = Xquantity-1;
        mY = Ydecomposition;
        nY = Yquantity-1;
        kY = Yquantity-1;


        noiseImg = MassNoise(5, deepCopy(image));
        ImageIO.write(noiseImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\noise." + getFileExtension(file)));
        normImg = NormFactor(deepCopy(noiseImg));
        ImageIO.write(normImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\norm." + getFileExtension(file)));
        sobelImg = sobelOperator(deepCopy(normImg));
        ImageIO.write(sobelImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\Sobel." + getFileExtension(file)));
        GrabImg = NormFactor(grab(RSchmX(ImageIO.read(new File(path))), RSchmY(ImageIO.read(new File(path))), deepCopy(image)));
        ImageIO.write(GrabImg, getFileExtension(file), new File(directory.getAbsolutePath() + "\\test." + getFileExtension(file)));
        getDogged();
        System.out.println("Запущенно");
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

    public static BufferedImage MassNoise(int sigma, BufferedImage pic) {
        WritableRaster raster = pic.getRaster();
        Random rand = new Random();
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                double[] pix = raster.getPixel(i, j, new double[3]);
                double res = pix[0] + sigma*rand.nextGaussian();
                Arrays.fill(pix, res);
                raster.setPixel(i,j,pix);
            }
        }
        pic.setData(raster);
         return deepCopy(pic);
    }
    public static BufferedImage NormFactor(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double min = raster.getPixel(0,0,new double[3])[0], max = raster.getPixel(0,0,new double[3])[0];
        for (int i = 0; i < pic.getHeight(); i++) for (int j = 0; j < pic.getWidth(); j++)if (raster.getPixel(i,j,new double[3])[0]<min)min = raster.getPixel(i,j,new double[3])[0];
        for (int i = 0; i < pic.getHeight(); i++) for (int j = 0; j < pic.getWidth(); j++)if (raster.getPixel(i,j,new double[3])[0]>max)max = raster.getPixel(i,j,new double[3])[0];
        for (int i = 0; i < pic.getHeight(); i++) {
            for (int j = 0; j < pic.getWidth(); j++) {
                double[] pix = raster.getPixel(i,j,new double[3]);
                double res = ((pix[0]-min)*254)/(max-min);
                Arrays.fill(pix, res);
                raster.setPixel(i,j,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private double veivletDog(double x){return (Math.pow(Math.E,-Math.pow(x,2)/2) - 0.5*Math.pow(Math.E,-Math.pow(x,2)/8));}
    private double veivletDogP1(double x){return (0.125 * x * Math.pow(Math.E,-Math.pow(x,2)/8) - x*Math.pow(Math.E,-Math.pow(x,2)/2));}
    private double diskretDog(int x, double m,int n){ return (Math.pow(a,-m/2)*veivletDog((Math.pow(a,-m)*x-n)));}
    private double diskretDogP1(int x, double m,int n){ return (Math.pow(a,-m/2)*veivletDogP1((Math.pow(a,-m)*x-n)));}
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
    public static BufferedImage sobelOperator(BufferedImage pic) {
        WritableRaster raster = pic.getRaster();
        int[][] MGx = {{1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}},
                MGy = {{1, 2, 1},
                        {0, 0, 0},
                        {-1, -2, -1}};
               double[][] matrix = new double[pic.getWidth()][pic.getWidth()];

        for (int i = 0; i < matrix.length; i++)for (int j = 0; j < matrix[i].length; j++) matrix[i][j] = (raster.getPixel(i, j,new double[3])[0]);
        for (int iY = 1; iY < pic.getWidth() - 2; iY++) {
            for (int iX = 1; iX < pic.getHeight() - 2; iX++) {
                int GX = 0, GY = 0;
                double[][] A = getSubMatrix(matrix, iY - 1, iY + 1, iX - 1, iX + 1);
                for (int y = 0; y < 2; y++) for (int x = 0; x < 2; x++) GX += A[y][x] * MGx[y][x];
                for (int y = 0; y < 2; y++) for (int x = 0; x < 2; x++) GY += A[y][x] * MGy[y][x];
                double[] pix = new double[3];
                Arrays.fill(pix, Math.sqrt(Math.pow(GX, 2) + (Math.pow(GY, 2))));
                raster.setPixel(iY,iX,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    public BufferedImage grab(BufferedImage DifferentX, BufferedImage DifferentY, BufferedImage pic){
            WritableRaster rasterX = DifferentX.getRaster(), rasterY = DifferentY.getRaster(), res = pic.getRaster();
            for (int x = 0; x < DifferentX.getWidth() - 1; x++) {
                for (int y = 0; y < DifferentY.getWidth() - 1; y++) {
                    int[] pix1 = rasterX.getPixel(x, y, new int[3]);
                    int[] pix2 = rasterY.getPixel(x, y, new int[3]);
                    int[] result = new int[3];
                    int resInt = (int) Math.sqrt(Math.pow(pix1[0], 2) + Math.pow(pix2[0], 2));
                    Arrays.fill(result, resInt);
                    res.setPixel(x, y, result);
                }
            }
            pic.setData(res);
            return pic;
    }
    public static BufferedImage RSchmX(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] arr = new double[pic.getHeight()][pic.getWidth()][3];
            for (int x = 1; x < pic.getHeight()-1; x++) {
                for (int y = 1; y < pic.getWidth()-1; y++) {
                    double res = (raster.getPixel(y,x-1,new double[3])[0] - raster.getPixel(y,x-1,new double[3])[0]);
                    double[] resArr = new double[3];
                    Arrays.fill(resArr,res);
                    arr[y][x] = resArr;
                }
            }
        for (int i = 1; i < raster.getHeight()-1; i++) {
            for (int j = 1; j < raster.getWidth()-1; j++) {
                raster.setPixel(j,i,arr[j][i]);
            }
        }
            pic.setData(raster);
        return pic;
    }
    public static BufferedImage RSchmY(BufferedImage pic) {
        WritableRaster raster = pic.getRaster();
        double[][][] arr = new double[pic.getHeight()][pic.getWidth()][3];

        for (int x = 1; x < pic.getHeight() - 1; x++) {
                for (int y = 1; y < pic.getWidth() - 1; y++) {
                    double res = (raster.getPixel(y,x,new double[3])[0] - raster.getPixel(y-1,x,new double[3])[0]);
                    double[] resArr = new double[3];
                    Arrays.fill(resArr,res);
                    arr[y][x] = resArr;
                }
            }
        for (int i = 1; i < raster.getHeight()-1; i++) {
            for (int j = 1; j < raster.getWidth()-1; j++) {
                raster.setPixel(j,i,arr[j][i]);
            }
        }
        pic.setData(raster);
        return pic;
    }


    private int[][] DWTDOGX(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        int[][] DWT = new int[mX][nX];
        for (int y = 0; y < kY; y++) {
            for (int m = 0; m < mX; m++) {
                for (int n = 0; n < nX; n++) {
                    int summ = 0;
                    for (int x = 0; x < Xquantity-1; x++)summ += diskretDog(x,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                    DWT[m][n] = (summ);
                }
            }
        }
        return DWT;
    }
    private BufferedImage dXDOG(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        int[][] DWTDOGX = DWTDOGX(pic);
        for (int y = 0; y < kY; y++) {
            for (int x = 0; x < kX; x++) {
                int summ = 0;
                for (int i = 0; i < Xdecomposition; i++) {
                    for (int j = 0; j < Xquantity-1; j++) {
                        summ+=diskretDogP1(x,Math.pow(2,i-1),j)*DWTDOGX[i][j];
                    }
                }
                int[] pix = raster.getPixel(x,y,new int[3]);
                Arrays.fill(pix,summ);
                raster.setPixel(x,y,pix);
            }
        }
        pic.setData(raster);
        return pic;
    }


    private int[][] DWTDOGY(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        int[][] DWT = new int[Ydecomposition][Yquantity-1];
        for (int x = 0; x < kX; x++) {
            for (int m = 0; m < mY; m++) {
                for (int n = 0; n < nY; n++) {
                    int summ = 0;
                    for (int y = 0; y < Yquantity-1; y++) {
                        summ+= diskretDog(x,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                    }
                    DWT[m][n] = (summ);
                }
            }
        }
        return DWT;
    }
    private BufferedImage dYDOG(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        int[][] DWTDOGY = DWTDOGY(pic);
        for (int x = 0; x < kX; x++) {
            for (int y = 0; y < kY; y++) {
                int summ = 0;
                for (int i = 0; i < Ydecomposition; i++) {
                    for (int j = 0; j < Yquantity-1; j++) {
                        summ+=diskretDogP1(y,Math.pow(2,i-1),j)*DWTDOGY[i][j];
                    }
                }
                int[] pix = raster.getPixel(x,y,new int[3]);
                if (summ<255) summ-=255;
                Arrays.fill(pix,summ);
                raster.setPixel(x,y, pix);
            }
        }
        pic.setData(raster);
        return pic;
    }
    private void getDogged(){
       DOG = new Thread(()->{
           try {
               DxDog = dXDOG(deepCopy(image));
               DyDog = dYDOG(deepCopy(image));
               VeivletDog = NormFactor(grab(deepCopy(DxDog),deepCopy(DyDog),deepCopy(image)));
               ImageIO.write(DxDog,getFileExtension(file), new File(directory.getAbsolutePath()+"\\DOGdx." + getFileExtension(file)));
               System.out.println("DxDog записан");
               ImageIO.write(DyDog,getFileExtension(file), new File(directory.getAbsolutePath()+"\\DOGdy." + getFileExtension(file)));
               System.out.println("DyDog записан");
               ImageIO.write(VeivletDog,getFileExtension(file), new File(directory.getAbsolutePath()+"\\DOG." + getFileExtension(file)));
               System.out.println("Dog записан");
           } catch (IOException e) {
               e.printStackTrace();
           }


       });
       DOG.start();
    }
    private static double[][] getSubMatrix(double[][] matrix, int firstRow, int destRow, int firstCol, int destCol){
        double[][] newMatrix = new double[destRow-firstRow+1][destCol-firstCol+1];
        for (int i = 0; i < newMatrix.length; i++, firstRow++) {
            int col = firstCol;
            for (int j = 0; j < newMatrix[i].length; j++, col++) {
                newMatrix[i][j] = matrix[firstRow][col];
            }
        }
        return newMatrix;
    }
}
