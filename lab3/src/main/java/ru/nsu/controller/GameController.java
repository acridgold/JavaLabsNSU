package ru.nsu.controller;

import ru.nsu.model.GameModel;
import ru.nsu.model.ScoreEntry;
import ru.nsu.model.ScoreStorage;
import ru.nsu.view.MainWindow;
import ru.nsu.view.ScaledDialogs;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController extends KeyAdapter {
    private final GameModel model;
    private final MainWindow view;
    private final ScoreStorage scoreStorage;

    public GameController(GameModel model, MainWindow view, ScoreStorage scoreStorage) {
        this.model = model;
        this.view = view;
        this.scoreStorage = scoreStorage;

        model.setGameOverListener(() -> SwingUtilities.invokeLater(this::onGameOver));

        view.addKeyListener(this);
        view.setFocusable(true);

        Timer timer = new Timer(600, e -> {
            if (!model.isOver()) {
                model.moveDown();
            }
            view.updateView();
        });
        timer.start();
    }

    private void onGameOver() {
        String name = ScaledDialogs.showInput(view, "Имя для таблицы результатов:", "Игра окончена");
        if (name == null) {
            name = "";
        }
        name = name.trim();
        if (name.isEmpty()) {
            name = "Player";
        }
        scoreStorage.add(new ScoreEntry(name, model.getScore()));
        ScaledDialogs.showMessage(view,
                "Набрано очков: " + model.getScore(),
                "Игра окончена",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (model.isOver()) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> model.moveLeft();
            case KeyEvent.VK_RIGHT -> model.moveRight();
            case KeyEvent.VK_DOWN -> model.moveDown();
            case KeyEvent.VK_UP -> model.rotate();
        }
        view.updateView();
    }
}
