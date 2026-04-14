package ru.nsu;

import ru.nsu.model.GameModel;
import ru.nsu.model.ScoreStorage;
import ru.nsu.view.StartMenuFrame;
import ru.nsu.view.TerminalView;
import javax.swing.SwingUtilities;
import java.util.Scanner;


/**
 * Можно скейл применять чисто
*/
public class Main {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        boolean cliMode = args.length > 0 && args[0].equals("--cli");

        if (cliMode) {
            TerminalView view = new TerminalView(model);
            model.setUpdateListener(view::render);
            model.initNewGame();

            Object lock = new Object();

            new Thread(() -> {
                while (!model.isOver()) {
                    try {
                        Thread.sleep(800);
                        synchronized (lock) { model.move(0, 1); }
                    } catch (Exception e) {}
                }
            }).start();

            Scanner sc = new Scanner(System.in);
            while (!model.isOver()) {
                String in = sc.nextLine().trim().toLowerCase();
                synchronized (lock) {
                    switch (in) {
                        case "a" -> model.moveLeft();
                        case "d" -> model.moveRight();
                        case "s" -> model.rotate();
                        case "w" -> {
                            synchronized (lock) {
                                while (model.isValid(model.getFx(), model.getFy() + 1, model.getCur())) {
                                    model.move(0, 1);
                                }
                                model.move(0, 1);
                            }
                        }
                        case "q" -> { System.exit(0); }
                    }
                }
            }
        } else {
            ScoreStorage storage = new ScoreStorage(ScoreStorage.defaultRecordsPath());
            SwingUtilities.invokeLater(() -> {
                StartMenuFrame menu = new StartMenuFrame(model, storage);
                menu.setVisible(true);
            });
        }
    }
}
