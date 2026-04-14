package ru.nsu.model;

import java.util.Random;

public class GameModel {
    private final Board board = new Board();
    private Figure cur;
    private int fx, fy, score;
    private boolean over;
    private boolean gameOverNotified;
    private Runnable updateListener;
    private Runnable gameOverListener;

    public void setUpdateListener(Runnable l) {
        this.updateListener = l;
    }

    public void setGameOverListener(Runnable l) {
        this.gameOverListener = l;
    }

    private void notifyView() {
        if (updateListener != null) updateListener.run();
    }

    private void notifyGameOverOnce() {
        if (!over || gameOverNotified || gameOverListener == null) return;
        gameOverNotified = true;
        gameOverListener.run();
    }

    public void initNewGame() {
        board.clear();
        score = 0;
        over = false;
        gameOverNotified = false;
        spawn();
        notifyView();
    }

    public void spawn() {
        cur = Figure.values()[new Random().nextInt(Figure.values().length)];
        fx = Board.WIDTH / 2 - 1;
        fy = 0;
        if (!isValid(fx, fy, cur)) over = true;
        notifyGameOverOnce();
    }

    public boolean isValid(int nx, int ny, Figure f) {
        for (int r = 0; r < f.shape.length; r++) {
            for (int c = 0; c < f.shape[r].length; c++) {
                if (f.shape[r][c] == 0) continue;
                int bx = nx + c, by = ny + r;
                if (bx < 0 || bx >= Board.WIDTH || by >= Board.HEIGHT) return false;
                if (by >= 0 && board.getGrid()[by][bx] != 0) return false;
            }
        }
        return true;
    }

    public void moveLeft() {
        move(-1, 0);
    }

    public void moveRight() {
        move(1, 0);
    }

    public void moveDown() {
        move(0, 1);
    }

    public void move(int dx, int dy) {
        if (over) return;
        if (isValid(fx + dx, fy + dy, cur)) {
            fx += dx;
            fy += dy;
        } else if (dy > 0) {
            board.mergeFigure(fx, fy, cur);
            int cleared = board.clearLines();
            score += pointsForLines(cleared);
            spawn();
        }
        notifyView();
    }

    public void rotate() {
        int[][] s = cur.shape;
        int[][] next = new int[s[0].length][s.length];
        for (int r = 0; r < s.length; r++)
            for (int c = 0; c < s[0].length; c++)
                next[c][s.length - 1 - r] = s[r][c];

        cur.setShape(next);
        if (!isValid(fx, fy, cur))
            cur.setShape(s);
        notifyView();
    }

    public Board getBoard() {
        return board;
    }

    public Figure getCur() {
        return cur;
    }

    public int getFx() {
        return fx;
    }

    public int getFy() {
        return fy;
    }

    public int getScore() {
        return score;
    }

    public boolean isOver() {
        return over;
    }

    public static int pointsForLines(int lines) {
        if (lines <= 0) return 0;
        return switch (lines) {
            case 1 -> 40;
            case 2 -> 100;
            case 3 -> 300;
            case 4 -> 1200;
            default -> 0;
        };
    }
}