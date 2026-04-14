package ru.nsu.view;

import ru.nsu.model.Board;
import ru.nsu.model.GameModel;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final GameModel model;

    public GamePanel(GameModel m, int scalePercent) {
        this.model = m;
        applyScalePercent(scalePercent);
    }


    private static int hudReserve() {
        return AppSettings.scaled(28);
    }

    public void applyScalePercent(int scalePercent) {
        int base = 30;
        int cell = Math.max(10, base * scalePercent / 100);
        int w = Board.WIDTH * cell;
        int h = hudReserve() + Board.HEIGHT * cell;
        setPreferredSize(new Dimension(w, h));
        revalidate();
        repaint();
    }

    private void drawBackgroundCover(Graphics2D g2, int w, int h) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            int w = getWidth();
            int h = getHeight();
            int minHud = hudReserve();

            int cell = Math.min(w / Board.WIDTH, (h - minHud) / Board.HEIGHT);
            cell = Math.max(1, cell);

            int gridTop = h - Board.HEIGHT * cell;
            int ox = (w - Board.WIDTH * cell) / 2;

            drawBackgroundCover(g2, w, h);

            if (gridTop > 0) {
                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRect(0, 0, w, gridTop);
            }

            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, Math.max(12f, cell * 0.6f)));
            FontMetrics fm0 = g2.getFontMetrics();
            int scoreY = Math.max(fm0.getAscent(), gridTop - 4);
            g2.drawString("Score: " + model.getScore(), ox + Math.max(4, cell / 4), scoreY);

            int[][] grid = model.getBoard().getGrid();
            for (int y = 0; y < Board.HEIGHT; y++) {
                for (int x = 0; x < Board.WIDTH; x++) {
                    if (grid[y][x] != 0) {
                        g2.setColor(Color.GRAY);
                        g2.fillRect(ox + x * cell, gridTop + y * cell, cell - 1, cell - 1);
                    }
                }
            }

            if (model.getCur() != null) {
                g2.setColor(model.getCur().color);
                int[][] s = model.getCur().shape;
                for (int r = 0; r < s.length; r++) {
                    for (int c = 0; c < s[r].length; c++) {
                        if (s[r][c] != 0) {
                            g2.fillRect(ox + (model.getFx() + c) * cell, gridTop + (model.getFy() + r) * cell,
                                    cell - 1, cell - 1);
                        }
                    }
                }
            }

            if (model.isOver()) {
                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRect(0, gridTop, w, h - gridTop);
                String msg = "GAME OVER";
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, cell * 1.2f));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(msg);
                g2.setColor(Color.WHITE);
                g2.drawString(msg, (w - tw) / 2, gridTop + (h - gridTop) / 2 + fm.getAscent() / 3);
            }
        } finally {
            g2.dispose();
        }
    }
}
