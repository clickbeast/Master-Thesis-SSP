package util;

import core.Result;

import java.util.Arrays;

public class General {


    public static int[][] mapToSequence(int[][] grid, int[] sequence) {
        int[][] out = new int[grid.length][];

        for (int i = 0; i < sequence.length; i++) {
            out[i] = grid[sequence[i]];
        }

        return out;
    }

    public static int[] mapToSequence(int[] array, int[] sequence) {
        int[] out = new int[array.length];

        for (int seqPos = 0; seqPos < sequence.length; seqPos++) {
            out[seqPos] = array[sequence[seqPos]];
        }
        return out;
    }


    public static void printGridPBinary(Result result) {
        //
    }

    public static void printGridP(Result result) {
        printTransposeGrid(mapToSequence(result.getJobToolMatrix(),result.getSequence()));
    }

    public static void printArrayP(int[] array, int[] sequence){
        System.out.println(Arrays.toString(mapToSequence(array, sequence)));
    }


    public static void printGrid(int[][] grid) {
        for(int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }


    public static void printGrid(double[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    public static void printTransposeGrid(int[][] grid) {
        int[][] tgrid = transposeMatrix(grid);
        printGrid(tgrid);
    }



    //TODO: CLEAN UP
    public static int[][] transposeMatrix(int[][] grid) {

        int[][] out = new int[grid[0].length][grid.length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                int grab = grid[i][j];
                out[j][i] = grab;
            }
        }


        return out;
    }


    public static void printTransposeGrid(double[][] grid) {
        double[][] tgrid = transposeMatrix(grid);
        printGrid(tgrid);
    }



    //TODO: CLEAN UP
    public static double[][] transposeMatrix(double[][] grid) {

        double[][] out = new double[grid[0].length][grid.length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                double grab = grid[i][j];
                out[j][i] = grab;
            }
        }


        return out;
    }


    public static int[][] convertToBinaryGrid(Result result) {
        int[][] gridCopy = new int[result.getJobToolMatrix().length][result.getJobToolMatrix()[0].length];
        for (int i = 0; i < result.getJobToolMatrix().length; i++) {
            for (int j = 0; j < result.getJobToolMatrix()[i].length; j++) {
                if(result.getJobToolMatrix()[i][j] == 1 || result.getJobToolMatrix()[i][j] == result.getKtnsId()) {
                    gridCopy[i][j] = 1;
                }else{
                    gridCopy[i][j] = 0;
                }
            }
        }
        return gridCopy;

    }



    public static int[][] copyGrid(int[][] grid) {
        int[][] gridCopy = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, gridCopy[i], 0, grid[i].length);
        }
        return gridCopy;
    }

}
