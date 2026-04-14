package ru.nsu.model;
import java.awt.Color;

public enum Figure {
    I(new int[][]{{1, 1, 1, 1}}, Color.CYAN, "\u001B[36m"),
    O(new int[][]{{1, 1}, {1, 1}}, Color.YELLOW, "\u001B[33m"),
    T(new int[][]{{0, 1, 0}, {1, 1, 1}}, Color.MAGENTA, "\u001B[35m"),
    S(new int[][]{{0, 1, 1}, {1, 1, 0}}, Color.GREEN, "\u001B[32m"),
    Z(new int[][]{{1, 1, 0}, {0, 1, 1}}, Color.RED, "\u001B[31m"),
    J(new int[][]{{1, 0, 0}, {1, 1, 1}}, Color.BLUE, "\u001B[34m"),
    L(new int[][]{{0, 0, 1}, {1, 1, 1}}, Color.ORANGE, "\u001B[37m");

    public int[][] shape;
    public final Color color;
    public final String ansiColor;

    Figure(int[][] shape, Color color, String ansiColor) {
        this.shape = shape;
        this.color = color;
        this.ansiColor = ansiColor;
    }

    public void setShape(int[][] newShape) { this.shape = newShape; }
}