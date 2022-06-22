import Jama.Matrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Veivlet {
    public static void main(String[] args) throws IOException {
        BufferedImage pic = ImageIO.read(new File("car.jpg"));
        MassNoise(1,pic);
        ImageIO.write(pic,"jpg",new File("noise.jpg"));
        NormFactor(pic);
        ImageIO.write(pic,"jpg",new File("norm.jpg"));
        ImageIO.write(sobelOperator(pic),"jpg",new File("sobelCar.jpg"));
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
        int[][] MGx = {{1,0,-1},{2,0,-2},{1,0,-1}},
                MGy = {{1,2,1},{0,0,0},{-1,-2,-1}},
                matrix = new int[pic.getWidth()][pic.getWidth()];
        Matrix realMatrix = new Matrix(pic.getWidth(),pic.getWidth()), A = realMatrix.copy();
        int GX = 0, GY = 0;
        for (int i = 0; i < matrix.length; i++)for (int j = 0; j < matrix[i].length; j++)realMatrix.set(i,j, Math.abs(pic.getRGB(i,j)));
        for (int i  = 1; i< pic.getHeight()-2; i++){
            for (int j = 1; j < pic.getWidth()-2; j++) {
                A.setMatrix(i-1,i+1,j-1,j+1, realMatrix);
                for (int k = 0; k < MGx.length; k++)for (int l = 0; l < MGx[k].length; l++)GX += A.get(k,l)*MGx[k][l];
                for (int k = 0; k < MGy.length; k++) for (int l = 0; l < MGy[k].length; l++)GY += A.get(k,l)*MGy[k][l];
                int G = (int) Math.sqrt(Math.pow(GX,2)+Math.pow(GY,2));
                pic.setRGB(i,j,G);
            }
        }
        return pic;
    }
    public static int[][] getSubMatrix(int[][] matrix, int firstRow, int destRow, int firstCol, int destCol){
        int[][] newMatrix = new int[destRow-firstRow+1][destCol-firstCol+1];
        for (int i = 0; i < newMatrix.length; i++, firstRow++) {
            for (int j = 0; j < newMatrix[i].length; j++, firstCol++) {
                newMatrix[i][j] = matrix[firstRow][firstCol];
            }
        }
        return newMatrix;
    }
    int[][] multiplyMatrices(int[][] firstMatrix, int[][] secondMatrix) {
        int[][] result = new int[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
            }
        }

        return result;
    }
    int multiplyMatricesCell(int[][] firstMatrix, int[][] secondMatrix, int row, int col) {
        int cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }
}
