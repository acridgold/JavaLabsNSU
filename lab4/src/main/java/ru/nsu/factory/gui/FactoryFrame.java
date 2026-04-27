package ru.nsu.factory.gui;

import ru.nsu.factory.dealer.Dealer;
import ru.nsu.factory.model.Accessory;
import ru.nsu.factory.model.Auto;
import ru.nsu.factory.model.Body;
import ru.nsu.factory.model.Motor;
import ru.nsu.factory.storage.Storage;
import ru.nsu.factory.supplier.PartSupplier;
import ru.nsu.factory.threadpool.ThreadPool;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FactoryFrame extends JFrame {

    private final Storage<Body> bodyStorage;
    private final Storage<Motor> motorStorage;
    private final Storage<Accessory> accessoryStorage;
    private final Storage<Auto> autoStorage;
    private final ThreadPool threadPool;
    private final AtomicInteger totalAutos;

    private final JLabel bodyLabel = new JLabel();
    private final JLabel motorLabel = new JLabel();
    private final JLabel accLabel = new JLabel();
    private final JLabel autoLabel = new JLabel();
    private final JLabel totalAutoLabel = new JLabel();
    private final JLabel queueLabel = new JLabel();

    public FactoryFrame(
            Storage<Body> bodyStorage,
            Storage<Motor> motorStorage,
            Storage<Accessory> accessoryStorage,
            Storage<Auto> autoStorage,
            PartSupplier<Body> bodySupplier,
            PartSupplier<Motor> motorSupplier,
            List<PartSupplier<Accessory>> accSuppliers,
            List<Dealer> dealers,
            ThreadPool threadPool,
            AtomicInteger totalAutos
    ) {
        super("Car Factory Emulator");
        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.autoStorage = autoStorage;
        this.threadPool = threadPool;
        this.totalAutos = totalAutos;

        setLayout(new BorderLayout(10, 10));
        setSize(550, 450);
        setLocationRelativeTo(null);

        // Панель статистики
        JPanel statsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Состояние складов"));
        statsPanel.add(new JLabel("Кузовов на складе:"));
        statsPanel.add(bodyLabel);
        statsPanel.add(new JLabel("Двигателей на складе:"));
        statsPanel.add(motorLabel);
        statsPanel.add(new JLabel("Аксессуаров на складе:"));
        statsPanel.add(accLabel);
        statsPanel.add(new JLabel("Машин на складе:"));
        statsPanel.add(autoLabel);
        statsPanel.add(new JLabel("Машин собрано всего:"));
        statsPanel.add(totalAutoLabel);
        statsPanel.add(new JLabel("Задач в очереди:"));
        statsPanel.add(queueLabel);
        add(statsPanel, BorderLayout.CENTER);

        // Панель ползунков скорости
        JPanel slidersPanel = new JPanel();
        slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
        slidersPanel.setBorder(BorderFactory.createTitledBorder("Скорость (задержка мс)"));
        slidersPanel.add(makeSlider("Кузов", bodySupplier));
        slidersPanel.add(makeSlider("Двигатель", motorSupplier));
        slidersPanel.add(makeGroupSlider(accSuppliers));
        slidersPanel.add(makeDealerSlider(dealers));
        add(slidersPanel, BorderLayout.SOUTH);

        new Timer(500, _ -> updateStats()).start();
    }

    private void updateStats() {
        bodyLabel.setText(bodyStorage.getCount() + " / " + bodyStorage.getCapacity()
                + "  (произведено: " + bodyStorage.getTotalProduced() + ")");
        motorLabel.setText(motorStorage.getCount() + " / " + motorStorage.getCapacity()
                + "  (произведено: " + motorStorage.getTotalProduced() + ")");
        accLabel.setText(accessoryStorage.getCount() + " / " + accessoryStorage.getCapacity()
                + "  (произведено: " + accessoryStorage.getTotalProduced() + ")");
        autoLabel.setText(autoStorage.getCount() + " / " + autoStorage.getCapacity());
        totalAutoLabel.setText(String.valueOf(totalAutos.get()));
        queueLabel.setText(String.valueOf(threadPool.getQueueSize()));
    }

    private JPanel makeSlider(String label, PartSupplier<?> supplier) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel valLabel = new JLabel(supplier.getDelayMs() + " мс");
        JSlider slider = new JSlider(100, 3000, supplier.getDelayMs());
        slider.addChangeListener(_ -> {
            supplier.setDelayMs(slider.getValue());
            valLabel.setText(slider.getValue() + " мс");
        });
        row.add(new JLabel(label + ":"));
        row.add(slider);
        row.add(valLabel);
        return row;
    }

    private JPanel makeGroupSlider(List<PartSupplier<Accessory>> suppliers) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        int initial = suppliers.isEmpty() ? 500 : suppliers.getFirst().getDelayMs();
        JLabel valLabel = new JLabel(initial + " мс");
        JSlider slider = new JSlider(100, 3000, initial);
        slider.addChangeListener(_ -> {
            suppliers.forEach(s -> s.setDelayMs(slider.getValue()));
            valLabel.setText(slider.getValue() + " мс");
        });
        row.add(new JLabel("Аксессуары" + ":"));
        row.add(slider);
        row.add(valLabel);
        return row;
    }

    private JPanel makeDealerSlider(List<Dealer> dealers) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        int initial = dealers.isEmpty() ? 1000 : dealers.getFirst().getDelayMs();
        JLabel valLabel = new JLabel(initial + " мс");
        JSlider slider = new JSlider(100, 5000, initial);
        slider.addChangeListener(_ -> {
            dealers.forEach(d -> d.setDelayMs(slider.getValue()));
            valLabel.setText(slider.getValue() + " мс");
        });
        row.add(new JLabel("Дилеры" + ":"));
        row.add(slider);
        row.add(valLabel);
        return row;
    }
}
