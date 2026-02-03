package ru.nsu;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try {
            Map<String, Integer> wordMap = readAndProcessFile();

            int totalWords = getTotalWordCount(wordMap);

            flushToFile(wordMap, totalWords);

            System.out.println("\n\u001B[32mExcellent\u001B[0m");
        } catch (IOException e) {
            printError(e);
        } finally {
            System.out.println("\n\u001B[34mDone\u001B[0m");
        }
    }

    private static Map<String, Integer> readAndProcessFile() throws IOException {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/input.txt")))) {

            Map<String, Integer> wordMap = new HashMap<>();
            var word = new StringBuilder();

            int character;
            while ((character = reader.read()) != -1) {
                if (!Character.isLetterOrDigit((char) character)) {
                    if (!word.isEmpty()) {
                        String currentWord = word.toString().toLowerCase();
                        wordMap.put(currentWord, wordMap.getOrDefault(currentWord, 0) + 1);
                        word.delete(0, word.length());
                    }
                    continue;
                }
                word.append((char) character);
            }

            // Последнее слово
            if (!word.isEmpty()) {
                String currentWord = word.toString().toLowerCase();
                wordMap.put(currentWord, wordMap.getOrDefault(currentWord, 0) + 1);
            }

            return wordMap;
        }
    }

    private static void flushToFile(Map<String, Integer> wordMap, int totalWords) {
        try (Writer writer = new FileWriter("output.csv")) {
            writer.write("Word, Freq, Percent\n");

            // Сортируем по частоте
            List<Map.Entry<String, Integer>> sortedEntries = wordMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .toList();

            for (Map.Entry<String, Integer> entry : sortedEntries) {
                double percentage = (double) entry.getValue() / totalWords * 100;
                writer.write(String.format("%s, %d, %d%%\n",
                        entry.getKey(), entry.getValue(), (int) percentage));
            }
        } catch (IOException e) {
            System.err.println("Writing error:  " + e.getMessage());
        }
    }

    private static int getTotalWordCount(Map<String, Integer> wordMap) {
        return wordMap.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private static void printError(IOException e) {
        System.out.println("\u001B[31mError: " + e.getLocalizedMessage() + "\u001B[0m");
    }
}
