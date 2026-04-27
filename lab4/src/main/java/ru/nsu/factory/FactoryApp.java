package ru.nsu.factory;

import ru.nsu.factory.config.*;
import ru.nsu.factory.dealer.*;
import ru.nsu.factory.factory.*;
import ru.nsu.factory.gui.*;
import ru.nsu.factory.logger.*;
import ru.nsu.factory.model.*;
import ru.nsu.factory.storage.*;
import ru.nsu.factory.supplier.*;
import ru.nsu.factory.threadpool.*;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FactoryApp {

    public static void main(String[] args) throws IOException {
        FactoryConfig config = new FactoryConfig("config.properties");

        // Склады 
        Storage<Body> bodyStorage = new Storage<>(config.getInt("StorageBodySize"));
        Storage<Motor> motorStorage = new Storage<>(config.getInt("StorageMotorSize"));
        Storage<Accessory> accessoryStorage = new Storage<>(config.getInt("StorageAccessorySize"));
        Storage<Auto> autoStorage = new Storage<>(config.getInt("StorageAutoSize"));

        SaleLogger saleLogger = new SaleLogger("factory_sales.log", config.getBool("LogSale"));
        AtomicInteger totalAutos = new AtomicInteger(0);
        ThreadPool threadPool = new ThreadPool(config.getInt("Workers"));

        // Поставщики
        PartSupplier<Body> bodySupplier = new PartSupplier<>("BodySupplier", bodyStorage, Body::new, 500);
        PartSupplier<Motor> motorSupplier = new PartSupplier<>("MotorSupplier", motorStorage, Motor::new, 500);

        List<PartSupplier<Accessory>> accSuppliers = new ArrayList<>();
        for (int i = 0; i < config.getInt("AccessorySuppliers"); i++) {
            accSuppliers.add(new PartSupplier<>(
                    "AccessorySupplier-" + i, accessoryStorage, Accessory::new, 500));
        }

        // Контроллер склада
        StockController controller = new StockController(
                autoStorage, bodyStorage, motorStorage, accessoryStorage,
                threadPool, totalAutos::incrementAndGet
        );

        // Диллеры
        List<Dealer> dealers = new ArrayList<>();
        for (int i = 1; i <= config.getInt("Dealers"); i++) {
            dealers.add(new Dealer(i, autoStorage, saleLogger, 1000));
        }

        // Запуск потоков
        Thread bodyThread = new Thread(bodySupplier, "BodySupplier");
        Thread motorThread = new Thread(motorSupplier, "MotorSupplier");
        Thread controllerThread = new Thread(controller, "StockController");

        bodyThread.start();
        motorThread.start();
        controllerThread.start();

        List<Thread> accThreads = new ArrayList<>();
        List<Thread> dealerThreads = new ArrayList<>();

        for (PartSupplier<Accessory> s : accSuppliers) {
            Thread t = new Thread(s, s.getName());
            t.start();
            accThreads.add(t);
        }

        for (Dealer d : dealers) {
            Thread t = new Thread(d, "Dealer-" + d.getNumber());
            t.start();
            dealerThreads.add(t);
        }

        // GUI
        SwingUtilities.invokeLater(() -> {
            FactoryFrame frame = new FactoryFrame(
                    bodyStorage, motorStorage, accessoryStorage, autoStorage,
                    bodySupplier, motorSupplier, accSuppliers, dealers,
                    threadPool, totalAutos
            );

            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    bodySupplier.stop();
                    motorSupplier.stop();
                    accSuppliers.forEach(PartSupplier::stop);
                    controller.stop();
                    dealers.forEach(Dealer::stop);
                    threadPool.shutdown();

                    try {
                        saleLogger.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    
                    bodyThread.interrupt();
                    motorThread.interrupt();
                    accThreads.forEach(Thread::interrupt);
                    controllerThread.interrupt();
                    dealerThreads.forEach(Thread::interrupt);

                    frame.dispose();
                    System.exit(0);
                }
            });

            frame.setVisible(true);
        });
    }
}