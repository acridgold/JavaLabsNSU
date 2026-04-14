package ru.nsu.view;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Objects;

public final class ScaledDialogs {
    private ScaledDialogs() {
    }

    public static int scaled(int basePixels) {
        return AppSettings.scaled(basePixels);
    }

    private static float factor() {
        return AppSettings.getScalePercent() / 100f;
    }

    public static void scaleComponentTree(Component c) {
        float f = factor();
        scaleComponentTree(c, f);
    }

    private static void scaleComponentTree(Component c, float f) {
        if (c instanceof JComponent jc) {
            Font font = jc.getFont();
            if (font != null) {
                jc.setFont(font.deriveFont(font.getSize2D() * f));
            }
            if (jc instanceof JTable table) {
                Dimension vp = table.getPreferredScrollableViewportSize();
                if (vp != null && vp.width > 0 && vp.height > 0) {
                    table.setPreferredScrollableViewportSize(
                            new Dimension((int) (vp.width * f), (int) (vp.height * f)));
                }
                int rh = table.getRowHeight();
                table.setRowHeight(Math.max(scaled(14), (int) ((rh > 0 ? rh : 16) * f)));
                JTableHeader h = table.getTableHeader();
                if (h != null) {
                    Font hf = h.getFont();
                    if (hf != null) {
                        h.setFont(hf.deriveFont(hf.getSize2D() * f));
                    }
                    h.setPreferredSize(new Dimension(0, scaled(26)));
                }
            }
            if (jc instanceof JTextField tf) {
                int cols = tf.getColumns();
                if (cols > 0) {
                    tf.setColumns(Math.max(8, (int) (cols * Math.min(1.4f, f))));
                }
            }
        }
        if (c instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                scaleComponentTree(child, f);
            }
        }
    }

    public static void showMessage(Component parent, Object message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setModal(true);
        scaleComponentTree(dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static String showInput(Component parent, String message, String title) {
        JTextField tf = new JTextField(20);
        JPanel panel = new JPanel(new BorderLayout(0, scaled(8)));
        JLabel lab = new JLabel(message);
        panel.add(lab, BorderLayout.NORTH);
        panel.add(tf, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(scaled(8), scaled(12), scaled(8), scaled(12)));

        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setModal(true);
        scaleComponentTree(dialog);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        Object value = pane.getValue();
        if (Objects.equals(value, JOptionPane.OK_OPTION)) {
            return tf.getText();
        }
        return null;
    }
}
