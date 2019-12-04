package neuralnetwork.data;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by finne on 26.01.2018.
 */
public class ArrayTools {

    public static void printDimensions(Object[] ar){
        if(ar == null) return;
        if(ar.length == 0) System.out.println(" 0");
        else{
            System.out.print(ar.length + ", ");
            if(ar[0] instanceof Object[]){
                printDimensions((Object[]) ar[0]);
            }else if(ar[0] instanceof double[]){
                System.out.println(((double[])ar[0]).length);
            }
        }
    }

    public static double[][][] copyArray(double[][][] array) {
        double[][][] out = new double[array.length][array[0].length][array[0][0].length];
        for(int i = 0; i < out.length; i++) {
            for(int n = 0; n<  out[0].length; n++) {
                for(int k = 0; k < out[0][0].length; k++) {
                    out[i][n][k] = array[i][n][k];
                }
            }
        }
        return out;
    }

    public static double[][][] normaliseValues(double[][][] out){
        double min = 10000;
        double max = -10000;
        for(int i = 0; i < out.length; i++) {
            for(int n = 0; n<  out[0].length; n++) {
                for(int k = 0; k < out[0][0].length; k++) {
                    if(min > out[i][n][k]) min = out[i][n][k];
                    if(max < out[i][n][k]) max = out[i][n][k];
                }
            }
        }
        for(int i = 0; i < out.length; i++) {
            for(int n = 0; n<  out[0].length; n++) {
                for(int k = 0; k < out[0][0].length; k++) {
                    out[i][n][k] = (out[i][n][k] - min) / (max - min);
                }
            }
        }
        return out;
    }

    public static double[][] copyArray(double[][] array) {
        double[][] out = new double[array.length][array[0].length];
        for(int i = 0; i < out.length; i++) {
            for(int n = 0; n<  out[0].length; n++) {
                    out[i][n] = array[i][n];

            }
        }
        return out;
    }

    public static double[] copyArray(double[] array) {
        double[] out = new double[array.length];
        for(int i = 0; i < out.length; i++) {
            out[i] = array[i];
        }
        return out;
    }

    public static double[] convertFlattenedArray(double[][][] array) {
        return array[0][0];
    }

    public static double[][][] convertFlatToComplexArray(double[] array, int depth, int width, int height){
        double[][][] out = new double[depth][width][height];
        for(int d = 0; d < depth; d++){
            for(int w = 0; w < width; w++){
                for(int h = 0; h < height; h++){
                    out[d][w][h] = array[d + w * depth + h * depth * width];
                }
            }
        }
        return out;
    }

    public static double[] convertComplexToFlatArray(double[][][] array){
        double[] out = new double[array.length * array[0].length * array[0][0].length];
        for(int d = 0; d < array.length; d++){
            for(int w = 0; w < array[0].length; w++){
                for(int h = 0; h < array[0][0].length; h++){
                    out[d + w * array.length + h * array.length * array[0].length] = array[d][w][h];
                }
            }
        }
        return out;
    }

    public static double[][][] createComplexFlatArray(double... input) {
        return new double[][][]{{input}};
    }

    public static double[][][] flipWidthAndHeight(double[][][] input){
        double[][][] out = new double[input.length][input[0][0].length][input[0].length];
        for(int i = 0; i < out.length; i++) {
            for(int n = 0; n < out[0].length; n++) {
                for(int k = 0; k < out[0][0].length; k++) {
                    out[i][n][k] = input[i][k][n];
                }
            }
        }
        return out;
    }


    public static double[][][][] createRandomArray(int channels, int depth, int width, int height, double lower, double upper) {
        double[][][][] out = new double[channels][depth][width][height];
        for(int i = 0; i < channels; i++) {
            out[i] = createRandomArray(depth, width, height, lower, upper);
        }
        return out;
    }

    public static double[][][] createRandomArray(int depth, int width, int height, double lower, double upper) {
        double[][][] out = new double[depth][width][height];
        for(int i = 0; i < out.length; i++) {
            out[i] = createRandomArray(width, height, lower, upper);
        }
        return out;
    }

    public static double[][] createRandomArray(int width, int height, double lower, double upper) {
        double[][] out = new double[width][height];
        for(int i = 0; i < out.length; i++) {
            out[i] = createRandomArray(height, lower, upper);
        }
        return out;
    }

    public static double[] createRandomArray(int height, double lower, double upper) {
        double[] out = new double[height];
        for(int i = 0; i < out.length; i++) {
            out[i] = randomValue(lower, upper);
        }
        return out;
    }


    public static void randomiseArray(double[][][] array, double lower, double upper) {
        for(int i = 0; i < array.length; i++) {
            randomiseArray(array[i], lower, upper);
        }
    }

    public static void randomiseArray(double[][] array, double lower, double upper) {
        for(int i = 0; i < array.length; i++) {
            randomiseArray(array[i], lower, upper);
        }
    }

    public static void randomiseArray(double[] array, double lower, double upper) {
        for(int i = 0; i < array.length; i++) {
            array[i] = randomValue(lower, upper);
        }
    }

    public static <T extends Object> T[] extractBatch(T[] array, T[] empty, int size) {
        if (size > 0 && size < array.length) {
            ArrayList<T> out = new ArrayList<T>();
            int index = 0;
            Integer[] ids = ArrayTools.randomValues(0, array.length - 1, size);
            for (Integer i : ids) {
                out.add(array[i]);
            }
            return (out.toArray(empty));
        } else return array;
    }

    public static void shuffleArray(Object[] ar){
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            Object a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }

    }

    public static Integer[] randomValues(int lowerBound, int upperBound, int amount) {

        lowerBound --;

        if(amount > (upperBound-lowerBound)){
            return null;
        }

        Integer[] values = new Integer[amount];
        for(int i = 0; i< amount; i++){
            int n = (int)(Math.random() * (upperBound-lowerBound+1) + lowerBound);
            while(containsValue(values, n)){
                n = (int)(Math.random() * (upperBound-lowerBound+1) + lowerBound);
            }
            values[i] = n;
        }
        return values;
    }

    public static <T> boolean containsValue(T[] ar, T value){
        for(int i = 0; i < ar.length; i++){
            if(ar[i] != null){
                if(ar[i].equals(value)){
                    return true;
                }
            }

        }
        return false;
    }

    public static int indexOfHighestValue(double[] values){
        int index = 0;
        for(int i = 1; i < values.length; i++){
            if(values[i] > values[index]){
                index = i;
            }
        }
        return index;
    }

    public static double highestValue(double[][][] values){
        double max = -1000000000;
        for(double[][] ar1:values){
            for(double[] ar2:ar1){
                for(double d:ar2){
                    if(max < d){
                        max = d;
                    }
                }
            }
        }
        return max;
    }

    static Random r = new Random((long)(10000000L * Math.random()));
    public static double randomValue(double lower, double upper) {
        return Math.random() * (upper - lower) + lower;
//        double mid = lower + (upper - lower) / 2;
//        return r.nextGaussian() * (upper - mid) + mid;
    }
}
