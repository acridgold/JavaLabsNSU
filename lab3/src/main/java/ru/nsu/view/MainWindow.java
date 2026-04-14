package ru.nsu.view;

import ru.nsu.controller.CommandHandler;
import ru.nsu.model.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {
    private final GamePanel panel;
    private final CommandHandler commandHandler;

    public MainWindow(GameModel model, int scalePercent, Runnable onClosed) {
        setTitle("Пробуди деда");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        panel = new GamePanel(model, scalePercent);
        add(panel, BorderLayout.CENTER);

        commandHandler = new CommandHandler(model, this);
        initMenu();

        pack();
        syncFrameSizeToContent();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (onClosed != null) {
                    onClosed.run();
                }
            }
        });
    }

    private void initMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu m = new JMenu("Game");
        JMenuItem res = new JMenuItem("New Game");
        res.addActionListener(e -> commandHandler.handleNewGame());
        JMenuItem scale = new JMenuItem("Scale");
        scale.addActionListener(e -> {
            AppSettings.cycleScale();
            applyWindowScale();
        });
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> commandHandler.handleExit());

        m.add(res);
        m.add(scale);
        m.addSeparator();
        m.add(exit);
        mb.add(m);

        setJMenuBar(mb);
    }

    public void applyWindowScale() {
        panel.applyScalePercent(AppSettings.getScalePercent());

        float fontSize = Math.max(11f, 14f * AppSettings.getScalePercent() / 100f);
        Font base = UIManager.getFont("MenuItem.font");
        Font f = (base != null ? base : getFont()).deriveFont(fontSize);

        if (getJMenuBar() != null) {
            for (int i = 0; i < getJMenuBar().getMenuCount(); i++) {
                JMenu menu = getJMenuBar().getMenu(i);
                menu.setFont(f);
                for (int j = 0; j < menu.getItemCount(); j++) {
                    JMenuItem item = menu.getItem(j);
                    if (item != null) {
                        item.setFont(f);
                    }
                }
            }
        }

        pack();
        syncFrameSizeToContent();
        setLocationRelativeTo(null);
        panel.repaint();
    }

    private void syncFrameSizeToContent() {
        JMenuBar mb = getJMenuBar();
        int mbH = mb != null ? mb.getPreferredSize().height : 0;
        Dimension d = panel.getPreferredSize();
        Insets ins = getInsets();
        setSize(
                d.width + ins.left + ins.right,
                d.height + ins.top + ins.bottom + mbH
        );
    }

    public void updateView() {
        panel.repaint();
    }
}
