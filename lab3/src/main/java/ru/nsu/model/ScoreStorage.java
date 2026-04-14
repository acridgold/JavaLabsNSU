package ru.nsu.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScoreStorage {
    private static final int MAX_RECORDS = 20;
    private static final Pattern LINE = Pattern.compile("^\\s*(.+?)\\s*:\\s*(\\d+)\\s*$");

    private final Path file;

    public ScoreStorage(Path file) {
        this.file = file;
    }

    public static Path defaultRecordsPath() {
        return Path.of("src/main/java/ru/nsu/rodata/records.txt");
    }

    public List<ScoreEntry> load() {
        List<ScoreEntry> list = new ArrayList<>();
        if (!Files.isRegularFile(file)) {
            return list;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                Matcher m = LINE.matcher(line);
                if (m.matches()) {
                    list.add(new ScoreEntry(m.group(1).trim(), Integer.parseInt(m.group(2))));
                }
            }
        } catch (IOException ignored) {
        }
        Collections.sort(list);
        return list;
    }

    public void add(ScoreEntry entry) {
        List<ScoreEntry> list = new ArrayList<>(load());
        list.add(entry);
        Collections.sort(list);
        while (list.size() > MAX_RECORDS) {
            list.removeLast();
        }
        try {
            Files.createDirectories(file.getParent());
            StringBuilder sb = new StringBuilder();
            for (ScoreEntry e : list) {
                sb.append(e.name()).append(": ").append(e.score()).append(System.lineSeparator());
            }
            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Failed to save records: " + e.getMessage());
        }
    }
}
