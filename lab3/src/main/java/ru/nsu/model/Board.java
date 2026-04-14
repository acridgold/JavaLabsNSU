package ru.nsu.model;

public class Board {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;
    private int[][] grid = new int[HEIGHT][WIDTH];

    public int[][] getGrid() {
        return grid;
    }

    public void clear() {
        grid = new int[HEIGHT][WIDTH];
    }

    public void mergeFigure(int x, int y, Figure f) {
        for (int r = 0; r < f.shape.length; r++) {
            for (int c = 0; c < f.shape[r].length; c++) {
                if (f.shape[r][c] != 0 && y + r >= 0)
                    grid[y + r][x + c] = f.ordinal() + 1;
            }
        }
    }

    public int clearLines() {
        int count = 0;
        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < WIDTH; x++)
                if (grid[y][x] == 0) {
                    full = false;
                    break;
                }
            if (full) {
                count++;
                for (int m = y; m > 0; m--)
                    System.arraycopy(grid[m - 1], 0, grid[m], 0, WIDTH);
                grid[0] = new int[WIDTH];
                y++;
            }
        }
        return count;
    }
}