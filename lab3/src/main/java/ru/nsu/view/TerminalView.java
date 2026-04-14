package ru.nsu.view;

import ru.nsu.model.GameModel;

public class TerminalView {
    private final GameModel model;

    public TerminalView(GameModel model) { 
        this.model = model; 
    }

    public void render() {
        System.out.print("\u001B[H\u001B[2J");
        System.out.flush();

        System.out.println("SCORE: " + model.getScore());
        int[][] grid = model.getBoard().getGrid();

        for (int y = 0; y < 20; y++) {
            System.out.print("|");
            for (int x = 0; x < 10; x++) {
                boolean isPiece = false;
                if (model.getCur() != null) {
                    int[][] s = model.getCur().shape;
                    int px = x - model.getFx(), py = y - model.getFy();
                    if (py >= 0 && py < s.length && px >= 0 && px < s[0].length && s[py][px] != 0) {
                        System.out.print(model.getCur().ansiColor + "■ " + "\u001B[0m");
                        isPiece = true;
                    }
                }

                if (!isPiece) {
                    if (grid[y][x] != 0) System.out.print("\u001B[37m■ \u001B[0m");
                    else System.out.print(". ");
                }
            }
            System.out.println("|");
        }
        System.out.println("================");
        System.out.println("A/D - move | S - rotate | W/Space - drop | Q - quit");
        System.out.flush();
    }
}


