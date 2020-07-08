package util;

public class General {
    public static void printGrid(int[][] grid) {
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


    public static int[][] copyGrid(int[][] grid) {
        int[][] gridCopy = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, gridCopy[i], 0, grid[i].length);
        }
        return gridCopy;
    }

}
