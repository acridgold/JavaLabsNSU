package ru.nsu.controller;

import ru.nsu.model.GameModel;
import ru.nsu.view.MainWindow;

public class CommandHandler {
    private final GameModel model;
    private final MainWindow view;

    public CommandHandler(GameModel model, MainWindow view) {
        this.model = model;
        this.view = view;
    }

    public void handleNewGame() {
        model.initNewGame();
        view.updateView();
    }

    public void handleExit() {
        System.exit(0);
    }
}