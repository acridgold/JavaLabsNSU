package ru.nsu.view;

import ru.nsu.controller.GameController;
import ru.nsu.model.GameModel;
import ru.nsu.model.ScoreStorage;

import javax.swing.*;
import java.awt.*;

public class StartMenuFrame extends JFrame {
    private static final int BASE_MENU_W = 300;
    private static final int BASE_MENU_H = 240;

    private final GameModel model;
    private final ScoreStorage storage;
    private final JButton scaleButton;
    private final JPanel root;
    private final JPanel buttons;

    public StartMenuFrame(GameModel model, ScoreStorage storage) {
        this.model = model;
        this.storage = storage;
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(500, 500);

        root = new JPanel(new BorderLayout(8, 8));
        buttons = new JPanel(new GridLayout(4, 1, 0, 8));
        JButton play = new JButton("Play");
        play.addActionListener(e -> startGame());
        JButton leaderboard = new JButton("Leaderboard");
        leaderboard.addActionListener(e -> ScoreDialog.showTable(this, storage));
        scaleButton = new JButton();
        updateScaleButton();
        scaleButton.addActionListener(e -> {
            AppSettings.cycleScale();
            updateScaleButton();
            applyMenuWindowScale();
        });
        JButton exit = new JButton("Exit");
        exit.addActionListener(e -> System.exit(0));

        buttons.add(play);
        buttons.add(leaderboard);
        buttons.add(scaleButton);
        buttons.add(exit);
        root.add(buttons, BorderLayout.CENTER);
        add(root);
        applyMenuWindowScale();
        setLocationRelativeTo(null);
    }

    private void updateScaleButton() {
        scaleButton.setText("Scale: " + AppSettings.getScalePercent() + "%");
    }

    private void applyMenuWindowScale() {
        int p = AppSettings.getScalePercent();
        int pad = AppSettings.scaled(16);
        int padH = AppSettings.scaled(24);
        int gap = AppSettings.scaled(8);
        root.setBorder(BorderFactory.createEmptyBorder(pad, padH, pad, padH));
        ((GridLayout) buttons.getLayout()).setVgap(gap);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        float fontSize = Math.max(11f, 14f * p / 100f);
        Font base = getFont() != null ? getFont() : UIManager.getFont("Button.font");
        Font f = base.deriveFont(fontSize);
        for (Component c : buttons.getComponents()) {
            c.setFont(f);
        }

        Dimension s = AppSettings.scaledSize(BASE_MENU_W, BASE_MENU_H);
        setSize(s.width, s.height);
        validate();
    }

    private void startGame() {
        setVisible(false);
        MainWindow window = new MainWindow(model, AppSettings.getScalePercent(), () ->
                SwingUtilities.invokeLater(() -> {
                    applyMenuWindowScale();
                    setVisible(true);
                }));
        model.setUpdateListener(window::updateView);
        new GameController(model, window, storage);
        model.initNewGame();
        window.setVisible(true);
        window.requestFocus();
    }
}
