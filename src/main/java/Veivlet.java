import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Veivlet {
    private final int Xdecomposition, Xquantity, a = 1,Ydecomposition, Yquantity;
    WaveletDOG Dog;
    WaveletMHAT MHAT;
    WaveletWAVE WAVE;
    int[] nX, mX,kX,mY, nY, kY;
    private final BufferedImage image,noiseImg,normImg,sobelImg,GrabImg;
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
        Dog = new WaveletDOG(normImg,kY,mX,nX,Xquantity,kX,Xdecomposition,mY,nY,Yquantity,Ydecomposition,getFileExtension(file),directory.getAbsolutePath());
        MHAT = new WaveletMHAT(normImg,kY,mX,nX,Xquantity,kX,Xdecomposition,mY,nY,Yquantity,Ydecomposition,getFileExtension(file),directory.getAbsolutePath());
        WAVE = new WaveletWAVE(normImg,kY,mX,nX,Xquantity,kX,Xdecomposition,mY,nY,Yquantity,Ydecomposition,getFileExtension(file),directory.getAbsolutePath());
    }

    public Image getsobelImg(){return SwingFXUtils.toFXImage(sobelImg,null);}
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
        for (int i = 0; i < pic.getWidth(); i++)for (int j = 0; j < pic.getHeight(); j++){
                if (raster.getPixel(i,j,new int[3])[0]<min)min = raster.getPixel(i,j,new int[3])[0];
                if (raster.getPixel(i,j,new double[3])[0]>max)max = raster.getPixel(i,j,new int[3])[0];
            }
        for (int i = 0; i < pic.getWidth(); i++) {
            for (int j = 0; j < pic.getHeight(); j++) {
                double[] pix = raster.getPixel(i,j,new double[3]);
                double res = ((pix[0]-min)*254)/(max-min);
                Arrays.fill(pix, res);
                raster.setPixel(i,j,pix);
            }
        }
        pic.setData(raster);
        return deepCopy(pic);
    }
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
        for (int iY = 1; iY < pic.getWidth() - 2; iY++) {
            for (int iX = 1; iX < pic.getHeight() - 2; iX++) {
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

    abstract class Wavelet extends Thread{
        AtomicReference<BufferedImage> Wavelet = new AtomicReference<>();
        AtomicReference<BufferedImage> WaveletPorog = new AtomicReference<>();
        AtomicReference<BufferedImage> dX = new AtomicReference<>();
        AtomicReference<BufferedImage> dY = new AtomicReference<>();
        Thread dXThread, dYThread;
        BufferedImage normalImage;
        int a = 3, Xquantity, Xdecomposition, Yquantity, Ydecomposition;
        int[] kY,mX,nX, kX, mY, nY;
        String fileExtention,Path;
        double SKOWavelet, SKOPorog, SKOGRAD_SKOSobel;
        double SNRGGWavelet, SNRGGPorog, SNRGGGrab;
        double SNRFWavelet, SNRFPorog, SNRFGrab;
        public Wavelet(BufferedImage normalImage, int[] kY, int[] mX, int[] nX, int Xquantity, int[] kX, int Xdecomposition, int[] mY, int [] nY, int Yquantity, int Ydecomposition, String fileExtention, String path){
            this.normalImage = normalImage;
            this.kY = kY;
            this.Ydecomposition = Ydecomposition;
            this.mX = mX;
            this.nX = nX;
            this.kX = kX;
            this.mY = mY;
            this.nY = nY;
            this.Yquantity = Yquantity;
            this.Xdecomposition = Xdecomposition;
            this.Xquantity = Xquantity;
            this.fileExtention = fileExtention;
            this.Path = path;
            this.start();
        }
        protected Image getWavelet() {return SwingFXUtils.toFXImage(deepCopy(Wavelet.get()),null);}
        protected Image getWaveletPorog() {return SwingFXUtils.toFXImage(deepCopy(WaveletPorog.get()),null);}
        abstract double WaveletF(double x);
        abstract double WaveletFP1(double x);
        abstract void save();
        protected double diskretWavelet(int x, double m, int n){return Math.pow(a,-m/2)*WaveletF(Math.pow(a,-m)*x-n);}
        protected double diskretWaveletFP1(int x, double m, int n){return Math.pow(a,-m/2)*WaveletFP1(Math.pow(a,-m)*x-n);}
        protected double[][][] DWTx(BufferedImage pic){
            WritableRaster raster = pic.getRaster();
            double[][][] DWTx = new double[kY.length][mX.length][nX.length];
            for (int y : kY) {
                double[][] DWT = new double[mX.length][nX.length];
                for (int m : mX) {
                    for (int n : nX) {
                        for (int x = 0; x < Xquantity-1; x++){
                            DWT[m][n]+= diskretWavelet(x,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                        }
                    }
                }
                DWTx[y] = DWT;
            }
            return DWTx;
        }
        protected BufferedImage dX(BufferedImage pic){
            WritableRaster raster = pic.getRaster();
            double[][][] DWTWAVEX = DWTx(pic);
            for (int y : kY) {
                for (int x : kX) {
                    double[] pix = new double[3];
                    for (int i = 0; i < Xdecomposition; i++) {
                        for (int j = 0; j < Xquantity-1; j++) {
                            pix[0]+=diskretWaveletFP1(x,Math.pow(2,i-1),j)*DWTWAVEX[y][i][j];
                        }
                    }
                    Arrays.fill(pix,Math.abs(pix[0]));
                    raster.setPixel(x,y,pix);
                }
            }
            pic.setData(raster);
            return pic;
        }
        protected double[][][] DWTy(BufferedImage pic){
            WritableRaster raster = pic.getRaster();
            double[][][]DWTMHY = new double[kX.length][mY.length][nY.length];
            for (int x : kX) {
                double[][] DWT = new double[mY.length][nY.length];
                for (int m : mY) {
                    for (int n : nY) {
                        for (int y = 0; y < Yquantity-1; y++){
                            DWT[m][n] += diskretWavelet(y,Math.pow(2,m-1),n)*(raster.getPixel(x,y,new int[3])[0]);
                        }
                    }
                }
                DWTMHY[x] = DWT;
            }
            return DWTMHY;
        }
        protected BufferedImage dY(BufferedImage pic){
            WritableRaster raster = pic.getRaster();
            double[][][] DWTWAVEY = DWTy(pic);
            for (int x : kX) {
                for (int y : kY) {
                    double summ = 0;
                    for (int i = 0; i < Ydecomposition; i++) {
                        for (int j = 0; j < Yquantity-1; j++) {
                            summ+=(diskretWaveletFP1(y,Math.pow(2,i-1),j)*DWTWAVEY[x][i][j]);
                        }
                    }
                    double[] pix = raster.getPixel(x,y,new double[3]);
                    Arrays.fill(pix,Math.abs(summ));
                    raster.setPixel(x,y, pix);
                }
            }
            pic.setData(raster);
            return (pic);
        }
        protected BufferedImage grab(BufferedImage DifferentX, BufferedImage DifferentY, BufferedImage pic){
            WritableRaster rasterX = DifferentX.getRaster(),rasterY = DifferentY.getRaster(),res = pic.getRaster();
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
            return pic;
        }
        protected BufferedImage NormFactor(BufferedImage pic){
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
            return pic;
        }
        protected BufferedImage deepCopy(BufferedImage bi) {
            ColorModel cm = bi.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = bi.copyData(null);
            return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
        }
        protected BufferedImage getPorog(BufferedImage pic){
            int summ = 0, avg;
            WritableRaster raster = pic.getRaster();
            for (int i = 0; i < raster.getHeight(); i++) {
                for (int j = 0; j < raster.getWidth(); j++) {
                    int[] pix = raster.getPixel(i,j,new int[3]);
                    summ+= pix[0];
                }
            }
            avg = summ/(raster.getWidth()*raster.getHeight());
            for (int i = 0; i < raster.getHeight(); i++) {
                for (int j = 0; j < raster.getWidth(); j++) {
                    int[] pix = raster.getPixel(i,j,new int[3]);
                    if (pix[0]-avg>avg+Math.sqrt(avg)) pix[0] = 255;
                    else pix[0] = 0;
                    Arrays.fill(pix, pix[0]);
                    raster.setPixel(i,j,pix);
                }
            }
            pic.setData(raster);
            return deepCopy(pic);
        }
        public void run(){
            dXThread = new Thread(()->{
                dX.set(dX(deepCopy(normalImage)));
            });
            dYThread = new Thread(()->{
                dY.set(dY(deepCopy(normalImage)));
            });
            try {
                dXThread.start();
                dYThread.start();
                dXThread.join();
                dYThread.join();
                Wavelet.set(NormFactor(grab(dX.get(), dY.get(), deepCopy(normalImage))));
                WaveletPorog.set(getPorog((deepCopy(Wavelet.get()))));
                this.SKOWavelet = SKO(Wavelet.get(),GrabImg);
                this.SKOPorog = SKO(WaveletPorog.get(),GrabImg);
                this.SKOGRAD_SKOSobel = SKO(sobelImg,GrabImg);
                this.SNRGGGrab = SNRGG(sobelImg);
                this.SNRGGWavelet = SNRGG(Wavelet.get());
                this.SNRGGPorog = SNRGG(WaveletPorog.get());
                this.SNRFWavelet = SNRF(Wavelet.get(),30,5,50);
                this.SNRFPorog = SNRF(WaveletPorog.get(),30,5,50);
                save();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        protected int[][] SGG(int SG[][], int i1, int j1, int NN){
            for (int i = i1; i <= i1+NN; i++) {
                SG[i][j1] = 1;
                SG[i][j1+NN] = 1;
            }
            for (int j = j1; j <=j1+NN ; j++) {
                SG[i1][j] = 1;
                SG[i1+NN][j] = 1;
            }
            return SG;
        }
        protected double SNRGG(BufferedImage mass){
            WritableRaster raster = mass.getRaster();
            int SS = 0;
            for (int i = 0; i < mass.getHeight()-1; i++) {
                for (int j = 0; j < mass.getWidth()-1; j++) {
                    SS+=raster.getPixel(i,j,new int[3])[0];
                }
            }
            SS /= (mass.getHeight()-1)*(mass.getWidth()-1);
            int V = 0;
            for (int i = 0; i < mass.getHeight()-1; i++) {
                for (int j = 0; j < mass.getWidth()-1; j++) {
                    V+=Math.pow(raster.getPixel(i,j,new int[3])[0] - SS,2);
                }
            }
            return ((255-SS)/Math.sqrt(V/((mass.getHeight()-1)*(mass.getWidth()-1))));
        }//Пиковый сигнал/шум
        protected double SNRF (BufferedImage pic, int i1, int j1, int NN){
            WritableRaster raster = pic.getRaster();
            double SS, SSF, VF;
            int summ = 0;
            for (int i = 0; i < pic.getHeight()-1; i++)for (int j = 0; j < pic.getWidth()-1; j++)summ += raster.getPixel(i,j, new int[3])[0];
            SS=summ/(pic.getHeight()-1)*(pic.getWidth()-1);
            summ = 0;
            for (int i = i1; i <= i1+NN; i++)for (int j = j1; j < j1+NN; j++)summ += raster.getPixel(i,j, new int[3])[0];
            SSF = (summ/Math.pow(NN,2));
            summ = 0;
            for (int i = i1; i <=i1+NN; i++)for (int j = j1; j <=j1+NN ; j++)summ+= raster.getPixel(i,j,new int[3])[0] - Math.pow(SSF,2);
            VF = summ/Math.pow(NN,2);
            return (255-SS)/Math.sqrt(VF);
        }//Пиковый сигнал шум по СКО фона
        protected double SKO(BufferedImage pic, BufferedImage pic2){
            WritableRaster raster = pic.getRaster();
            WritableRaster raster1 = pic2.getRaster();
            double SKO = 0;
            for (int i = 0; i < pic.getHeight(); i++) {
                for (int j = 0; j < pic.getWidth(); j++) {
                    SKO += Math.pow(raster1.getPixel(i,j,new int[3])[0] - raster.getPixel(i,j,new int[3])[0],2);
                }
            }
            return Math.sqrt(SKO / ((pic.getHeight()-1)*(pic.getWidth()-1)));
        }//Среднеквадратичное отклонение

    }

    class WaveletDOG extends Wavelet{


        public WaveletDOG(BufferedImage normalImage, int[] kY, int[] mX, int[] nX, int Xquantity, int[] kX, int Xdecomposition, int[] mY, int[] nY, int Yquantity, int Ydecomposition, String fileExtention, String path) {
            super(normalImage, kY, mX, nX, Xquantity, kX, Xdecomposition, mY, nY, Yquantity, Ydecomposition, fileExtention, path);
        }

        @Override
        protected double WaveletF(double x) {
            return  (Math.pow(Math.E,-Math.pow(x,2)/2) - 0.5*Math.pow(Math.E,-Math.pow(x,2)/8));
        }

        @Override
        protected double WaveletFP1(double x) {
            return (0.125 * x * Math.pow(Math.E,-Math.pow(x,2)/8) - x*Math.pow(Math.E,-Math.pow(x,2)/2));
        }

        @Override
        void save() {
            try {
                ImageIO.write(Wavelet.get(), fileExtention, new File(Path + "\\DOG." + fileExtention));
                ImageIO.write(WaveletPorog.get(),fileExtention,new File(Path+"\\PorogDOG."+fileExtention));
                System.out.println("DOG записан");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class WaveletMHAT extends Wavelet{


        public WaveletMHAT(BufferedImage normalImage, int[] kY, int[] mX, int[] nX, int Xquantity, int[] kX, int Xdecomposition, int[] mY, int[] nY, int Yquantity, int Ydecomposition, String fileExtention, String path) {
            super(normalImage, kY, mX, nX, Xquantity, kX, Xdecomposition, mY, nY, Yquantity, Ydecomposition, fileExtention, path);
        }
        @Override
        protected double WaveletF(double x) {
            return ((2*Math.pow(Math.PI,-0.25))/(Math.sqrt(3)))*(1-Math.pow(x,2))*Math.pow(Math.E,-(Math.pow(x,2)/2));
        }

        @Override
        protected double WaveletFP1(double x) {
            return (2*Math.sqrt(3)*x*Math.pow(Math.E,-Math.pow(x,2)/2)*(Math.pow(x,2)-3))/(3*Math.pow(Math.PI,1.5));
        }

        @Override
        void save() {
            try {
                ImageIO.write(Wavelet.get(), fileExtention, new File(Path + "\\MHAT." + fileExtention));
                ImageIO.write(WaveletPorog.get(),fileExtention,new File(Path+"\\PorogMHAT."+fileExtention));
                System.out.println("MHAT записан");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class WaveletWAVE extends Wavelet {


        public WaveletWAVE(BufferedImage normalImage, int[] kY, int[] mX, int[] nX, int Xquantity, int[] kX, int Xdecomposition, int[] mY, int[] nY, int Yquantity, int Ydecomposition, String fileExtention, String path) {
            super(normalImage, kY, mX, nX, Xquantity, kX, Xdecomposition, mY, nY, Yquantity, Ydecomposition, fileExtention, path);
        }



        @Override
        protected double WaveletF(double x) {
            return x*Math.pow(Math.E,-2*Math.pow(x,2));
        }

        @Override
        protected double WaveletFP1(double x) {
            return Math.pow(Math.E,-2*Math.pow(x,2))-Math.pow(x,2)*Math.pow(Math.E,-2*Math.pow(x,2));
        }

        @Override
        void save() {
            try {
                ImageIO.write(Wavelet.get(), fileExtention, new File(Path + "\\WAVE." + fileExtention));
                ImageIO.write(WaveletPorog.get(),fileExtention,new File(Path+"\\PorogWAVE."+fileExtention));
                System.out.println("Wave записан");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
