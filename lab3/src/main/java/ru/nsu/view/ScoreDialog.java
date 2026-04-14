package ru.nsu.view;

import ru.nsu.model.ScoreEntry;
import ru.nsu.model.ScoreStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class ScoreDialog {
    private ScoreDialog() {}

    public static void showTable(Component parent, ScoreStorage storage) {
        java.util.List<ScoreEntry> entries = storage.load();
        String[] cols = {"Игрок", "Очки"};
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (ScoreEntry e : entries) {
            tm.addRow(new Object[]{e.name(), e.score()});
        }
        JTable table = new JTable(tm);
        table.setFillsViewportHeight(true);

        table.setPreferredScrollableViewportSize(new Dimension(360, 240));
        JScrollPane sp = new JScrollPane(table);
        ScaledDialogs.showMessage(parent, sp, "Таблица результатов", JOptionPane.INFORMATION_MESSAGE);
    }
}
