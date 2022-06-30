import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Veivlet {
    private final int Xdecomposition, Xquantity, a = 3,Ydecomposition, Yquantity;
    private Thread DOG, MHAT, WAVE;
    int[] nX, mX,kX,mY, nY, kY;
    private final BufferedImage image,noiseImg,normImg,sobelImg;
    private BufferedImage GrabImg,VeivletDog,VeivletMHAT,VeivletWAVE, PorogDOG, PorogMHAT, PorogWave;
    private File file, directory;
    public Veivlet(String path) throws IOException {
        String username = System.getProperty("user.name");
        file = new File(path);
        directory = new File("C:\\Users\\" + username +"\\Desktop\\"+file.getName().split("\\.")[0]);
        if (!directory.exists())directory.mkdir();
        image = ImageIO.read(file);
        Xquantity = image.getWidth();
        Yquantity = image.getHeight();
        Xdecomposition = (int) ((Math.log(Xquantity)/Math.log(2))-1);
        Ydecomposition = (int) ((Math.log(Yquantity)/Math.log(2))-1);
        nX = new int[Xquantity];for (int i = 0; i < nX.length; i++)nX[i] = i;
        mX = new int[Xdecomposition+1];for (int i = 0; i < mX.length; i++)mX[i] = i;
        kX = new int[Xquantity];for (int i = 0; i < kX.length; i++)kX[i] = i;
        mY = new int[Ydecomposition+1];for (int i = 0; i<mY.length;i++)mY[i] = i;
        nY = new int[Yquantity];for (int i = 0; i < nY.length; i++)nY[i] = i;
        kY = new int[Yquantity];for (int i = 0; i < kY.length; i++)kY[i] = i;
        noiseImg = MassNoise(5, deepCopy(image));
        ImageIO.write(noiseImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\noise." + getFileExtension(file)));
        normImg = NormFactor(deepCopy(noiseImg));
        ImageIO.write(normImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\norm." + getFileExtension(file)));
        sobelImg = sobelOperator(deepCopy(normImg));
        ImageIO.write(sobelImg,getFileExtension(file), new File(directory.getAbsolutePath()+"\\Sobel." + getFileExtension(file)));
        GrabImg = NormFactor(grab(RSchmX(deepCopy(image)), RSchmY(deepCopy(image)), deepCopy(image)));
        ImageIO.write(GrabImg, getFileExtension(file), new File(directory.getAbsolutePath() + "\\test." + getFileExtension(file)));
        getDogged();
        getMHATed();
        getWAVEed();
    }
    public Image getNoiseImg(){return SwingFXUtils.toFXImage(noiseImg,null);}
    public Image getNormImg(){return SwingFXUtils.toFXImage(normImg,null);}
    public Image getsobelImg(){return SwingFXUtils.toFXImage(sobelImg,null);}
    public Image getGrabImg(){return SwingFXUtils.toFXImage(GrabImg,null);}
    public Image getDOGImg(){return SwingFXUtils.toFXImage(VeivletDog,null);}
    public Image getMHATImg(){return SwingFXUtils.toFXImage(VeivletMHAT,null);}
    public Image getWAVEImg(){return SwingFXUtils.toFXImage(VeivletWAVE,null);}

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

    private static BufferedImage MassNoise(int sigma, BufferedImage pic) {
        WritableRaster raster = pic.getRaster();
        Random rand = new Random();
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                double[] pix = raster.getPixel(i, j, new double[3]);
                double res = pix[0] + sigma+rand.nextGaussian();
                Arrays.fill(pix, res);
                raster.setPixel(i,j,pix);
            }
        }
        pic.setData(raster);
         return deepCopy(pic);
    }
    private static BufferedImage NormFactor(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        int min = raster.getPixel(0,0,new int[3])[0], max = raster.getPixel(0,0,new int[3])[0];
        for (int i = 0; i < pic.getHeight(); i++)for (int j = 0; j < pic.getWidth(); j++){
                if (raster.getPixel(i,j,new int[3])[0]<min)min = raster.getPixel(i,j,new int[3])[0];
                if (raster.getPixel(i,j,new double[3])[0]>max)max = raster.getPixel(i,j,new int[3])[0];
            }
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

    private double veivletDog(double x){return  (Math.pow(Math.E,-Math.pow(x,2)/2) - 0.5*Math.pow(Math.E,-Math.pow(x,2)/8)); }
    private double veivletDogP1(double x){return  (0.125 * x * Math.pow(Math.E,-Math.pow(x,2)/8) - x*Math.pow(Math.E,-Math.pow(x,2)/2));}
    private double diskretDog(int x, double m,int n){return (Math.pow(a,-(m/2))*veivletDog((Math.pow(a,-m)*x-n))); }
    private double diskretDogP1(int x, double m,int n){ return (Math.pow(a,-(m/2))*veivletDogP1((Math.pow(a,-m)*x-n)));}
    private double veivletMHAT(double x){return (((2*Math.pow(Math.PI,-0.25))/Math.sqrt(3))*(1-Math.pow(x,2))*Math.pow(Math.E,-Math.pow(x,2)/2));}
    private double veivletMHATP1(double x){return ((2*Math.sqrt(3)*x*Math.pow(Math.E,-Math.pow(x,2)/2)*(Math.pow(x,2)-3))/(3*Math.pow(Math.PI,0.25)));}
    private double diskretMHAT(int x, double m, int n){return Math.pow(a,-m/2)*veivletMHAT((Math.pow(a,-m)*x-n));}
    private double diskretMHATP1(int x, double m, int n){return Math.pow(a,-(m/2))*veivletMHATP1(Math.pow(a,-m)*x-n);}


    private double veivletWAVE(double x){return -x*Math.pow(Math.E,-Math.pow(x,2)/2);}
    private double veivletWAVEP1(double x){return Math.pow(x,2)*Math.pow(Math.E,-Math.pow(x,2)/2)-Math.pow(Math.E,-(0.5*Math.pow(x,2)));}
    private double diskretWave(int x, double m, int n){return Math.pow(a,-m/2)*veivletWAVE((Math.pow(a,-m)*x-n));}
    private double diskretWaveP1(int x, double m, int n){return Math.pow(a,-m/2)*veivletWAVEP1((Math.pow(a,-m)*x-n));}

    private static BufferedImage sobelOperator(BufferedImage pic) {
        WritableRaster raster = pic.getRaster();
        int[][] MGx = {{1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}},
                MGy = {{1, 2, 1},
                        {0, 0, 0},
                        {-1, -2, -1}};
               double[][] matrix = new double[pic.getWidth()][pic.getWidth()];

        for (int i = 0; i < matrix.length; i++)for (int j = 0; j < matrix[i].length; j++) matrix[i][j] = (raster.getPixel(i, j,new double[3])[0]);
        for (int iY = 1; iY < pic.getHeight() - 2; iY++) {
            for (int iX = 1; iX < pic.getWidth() - 2; iX++) {
                double GX = 0, GY = 0;
                double[][] A = getSubMatrix(matrix, iY - 1, iY + 1, iX - 1, iX + 1);
                for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) GX += A[y][x] * MGx[y][x];
                for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) GY += A[y][x] * MGy[y][x];
                double[] pix = new double[3];
                Arrays.fill(pix, Math.sqrt(Math.pow(GX, 2) + (Math.pow(GY, 2))));
                raster.setPixel(iY,iX,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private BufferedImage grab(BufferedImage DifferentX, BufferedImage DifferentY, BufferedImage pic){
            WritableRaster rasterX = DifferentX.getRaster();
            WritableRaster rasterY = DifferentY.getRaster();
            WritableRaster res = pic.getRaster();
            for (int x = 0; x <DifferentX.getWidth()-1 ; x++) {
                for (int y = 0; y < DifferentY.getWidth()-1; y++) {
                    int[] pix1 = rasterX.getPixel(x, y, new int[3]);
                    int[] pix2 = rasterY.getPixel(x, y, new int[3]);
                    double[] result = new double[3];
                    double resInt = Math.sqrt(Math.pow(pix1[0], 2) + Math.pow(pix2[0], 2));
                    Arrays.fill(result, resInt);
                    res.setPixel(x, y, result);
                }
            }
            pic.setData(res);
            return deepCopy(pic);
    }

    private static BufferedImage RSchmX(BufferedImage pic){
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
    private static BufferedImage RSchmY(BufferedImage pic) {
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

    private double[][][] DWTDOGX(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTDOGX = new double[kY.length][mX.length][nX.length];
        for (int y : kY) {
            double[][] DWT = new double[mX.length][nX.length];
            for (int m : mX) {
                for (int n : nX) {
                    for (int x = 0; x < Xquantity-1; x++){
                        DWT[m][n]+= diskretDog(x,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                    }
                }
            }
            DWTDOGX[y] = DWT;
        }
        return DWTDOGX;
    }
    private BufferedImage dXDOG(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTDOGX = DWTDOGX(pic);
        for (int y : kY) {
            for (int x : kX) {
                double[] pix = new double[3];
                for (int i = 0; i < Xdecomposition; i++) {
                    for (int j = 0; j < Xquantity-1; j++) {
                        pix[0]+=diskretDogP1(x,Math.pow(2,i-1),j)*DWTDOGX[y][i][j];
                    }
                }
                Arrays.fill(pix,Math.abs(pix[0]));
                raster.setPixel(x,y,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private double[][][] DWTDOGY(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][]DWTDOGY = new double[kX.length][mY.length][nY.length];
        for (int x : kX) {
            double[][] DWT = new double[mY.length][nY.length];
            for (int m : mY) {
                for (int n : nY) {
                    for (int y = 0; y < Yquantity-1; y++){
                        DWT[m][n] += diskretDog(y,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                    }
                }
            }
            DWTDOGY[x] = DWT;
        }
        return DWTDOGY;
    }
    private BufferedImage dYDOG(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTDOGY = DWTDOGY(pic);
        for (int x : kX) {
            for (int y : kY) {
                int summ = 0;
                for (int i = 0; i < Ydecomposition; i++) {
                    for (int j = 0; j < Yquantity-1; j++) {
                        summ+=(diskretDogP1(y,Math.pow(2,i-1),j)*DWTDOGY[x][i][j]);
                    }
                }
                double[] pix = raster.getPixel(x,y,new double[3]);
                Arrays.fill(pix,Math.abs(summ));
                raster.setPixel(x,y, pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private BufferedImage DOGPorog(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        for (int i = 0; i < pic.getHeight(); i++) {
            for (int j = 0; j < pic.getWidth(); j++) {
                double[] pix = raster.getPixel(i,j,new double[3]);
                Arrays.fill(pix,pix[0]/10.5);
                raster.setPixel(i,j,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private void getDogged(){
       DOG = new Thread(()->{
           try {
               VeivletDog = NormFactor(grab(dXDOG(deepCopy(normImg)), dYDOG(deepCopy(normImg)), deepCopy(image)));
               PorogDOG = getPorog(DOGPorog(deepCopy(VeivletDog)));
               ImageIO.write(VeivletDog, getFileExtension(file), new File(directory.getAbsolutePath() + "\\DOG." + getFileExtension(file)));
               ImageIO.write(PorogDOG,getFileExtension(file),new File(directory.getAbsolutePath()+"\\PorogDOG."+getFileExtension(file)));
               System.out.println("Dog записан");
           }catch (Exception e){
               e.printStackTrace();
           }
           });
       DOG.start();
    }

    private double[][][] DWTMHX(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTMHX = new double[kY.length][mX.length][nX.length];
        for (int y : kY) {
            double[][] DWT = new double[mX.length][nX.length];
            for (int m : mX) {
                for (int n : nX) {
                    for (int x = 0; x < Xquantity-1; x++){
                        DWT[m][n]+= diskretMHAT(x,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                    }
                }
            }
            DWTMHX[y] = DWT;
        }
        return DWTMHX;
    }
    private BufferedImage dXMH(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTMHX = DWTMHX(pic);
        for (int y : kY) {
            for (int x : kX) {
                double[] pix = new double[3];
                for (int i = 0; i < Xdecomposition; i++) {
                    for (int j = 0; j < Xquantity-1; j++) {
                        pix[0]+=diskretMHATP1(x,Math.pow(2,i-1),j)*DWTMHX[y][i][j];
                    }
                }
                Arrays.fill(pix,Math.abs(pix[0]));
                raster.setPixel(x,y,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private double[][][] DWTMHY(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][]DWTMHY = new double[kX.length][mY.length][nY.length];
        for (int x : kX) {
            double[][] DWT = new double[mY.length][nY.length];
            for (int m : mY) {
                for (int n : nY) {
                    for (int y = 0; y < Yquantity-1; y++){
                        DWT[m][n] += diskretMHAT(y,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                    }
                }
            }
            DWTMHY[x] = DWT;
        }
        return DWTMHY;
    }
    private BufferedImage dYMH(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTMHY = DWTMHY(pic);
        for (int x : kX) {
            for (int y : kY) {
                double summ = 0;
                for (int i = 0; i < Ydecomposition; i++) {
                    for (int j = 0; j < Yquantity-1; j++) {
                        summ+=(diskretMHATP1(y,Math.pow(2,i-1),j)*DWTMHY[x][i][j]);
                    }
                }
                double[] pix = raster.getPixel(x,y,new double[3]);
                Arrays.fill(pix,Math.abs(summ));
                raster.setPixel(x,y, pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private BufferedImage MHATPorog(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        for (int i = 0; i < pic.getHeight(); i++) {
            for (int j = 0; j < pic.getWidth(); j++) {
                double[] pix = raster.getPixel(i,j,new double[3]);
                Arrays.fill(pix,pix[0]/21);
                raster.setPixel(i,j,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private void getMHATed(){
        MHAT = new Thread(()->{
            try {
                VeivletMHAT = NormFactor(grab(dXMH(deepCopy(normImg)), dYMH(deepCopy(normImg)), deepCopy(image)));
                PorogMHAT = getPorog(MHATPorog(deepCopy(VeivletMHAT)));
                ImageIO.write(VeivletMHAT, getFileExtension(file), new File(directory.getAbsolutePath() + "\\MHAT." + getFileExtension(file)));
                ImageIO.write(PorogMHAT,getFileExtension(file),new File(directory.getAbsolutePath()+"\\PorogMHAT."+getFileExtension(file)));
                System.out.println("MHAT записан");
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        MHAT.start();
    }

    private double[][][] DWTWAVEX(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTWAVEX = new double[kY.length][mX.length][nX.length];
        for (int y : kY) {
            double[][] DWT = new double[mX.length][nX.length];
            for (int m : mX) {
                for (int n : nX) {
                    for (int x = 0; x < Xquantity-1; x++){
                        DWT[m][n]+= diskretWave(x,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                    }
                }
            }
            DWTWAVEX[y] = DWT;
        }
        return DWTWAVEX;
    }
    private BufferedImage dXWAVE(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTWAVEX = DWTWAVEX(pic);
        for (int y : kY) {
            for (int x : kX) {
                double[] pix = new double[3];
                for (int i = 0; i < Xdecomposition; i++) {
                    for (int j = 0; j < Xquantity-1; j++) {
                        pix[0]+=diskretWaveP1(x,Math.pow(2,i-1),j)*DWTWAVEX[y][i][j];
                    }
                }
                Arrays.fill(pix,Math.abs(pix[0]));
                raster.setPixel(x,y,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private double[][][] DWTWAVEY(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][]DWTWAVEY = new double[kX.length][mY.length][nY.length];
        for (int x : kX) {
            double[][] DWT = new double[mY.length][nY.length];
            for (int m : mY) {
                for (int n : nY) {
                    for (int y = 0; y < Yquantity-1; y++){
                        DWT[m][n] += diskretWave(y,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                    }
                }
            }
            DWTWAVEY[x] = DWT;
        }
        return DWTWAVEY;
    }
    private BufferedImage dYWAVE(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        double[][][] DWTWAVEY = DWTWAVEY(pic);
        for (int x : kX) {
            for (int y : kY) {
                double summ = 0;
                for (int i = 0; i < Ydecomposition; i++) {
                    for (int j = 0; j < Yquantity-1; j++) {
                        summ+=(diskretWaveP1(y,Math.pow(2,i-1),j)*DWTWAVEY[x][i][j]);
                    }
                }
                double[] pix = raster.getPixel(x,y,new double[3]);
                Arrays.fill(pix,Math.abs(summ));
                raster.setPixel(x,y, pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private BufferedImage WAVEPorog(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        for (int i = 0; i < pic.getHeight(); i++) {
            for (int j = 0; j < pic.getWidth(); j++) {
                double[] pix = raster.getPixel(i,j,new double[3]);
                Arrays.fill(pix,pix[0]/23);
                raster.setPixel(i,j,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
    private void getWAVEed(){
        WAVE = new Thread(()->{
            try {
                VeivletWAVE = NormFactor(grab(dXWAVE(deepCopy(normImg)), dYWAVE(deepCopy(normImg)), deepCopy(image)));
                PorogWave = getPorog(WAVEPorog(deepCopy(VeivletWAVE)));
                ImageIO.write(VeivletWAVE, getFileExtension(file), new File(directory.getAbsolutePath() + "\\WAVE." + getFileExtension(file)));
                ImageIO.write(PorogWave,getFileExtension(file),new File(directory.getAbsolutePath()+"\\PorogWAVE."+getFileExtension(file)));
                System.out.println("WAVE записан");
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        WAVE.start();
    }

    private BufferedImage getPorog(BufferedImage pic){
        WritableRaster raster = pic.getRaster();
        for (int i = 0; i < pic.getHeight(); i++) {
            for (int j = 0; j < pic.getWidth(); j++) {
                double[] pix = raster.getPixel(i,j,new double[3]);
                if (pix[0]>90&&pix[0]<255){
                    pix[0] = 255;
                    Arrays.fill(pix,pix[0]);
                }
                raster.setPixel(i,j,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
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