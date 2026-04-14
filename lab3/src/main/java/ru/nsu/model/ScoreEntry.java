package ru.nsu.model;

public record ScoreEntry(String name, int score) implements Comparable<ScoreEntry> {
    @Override
    public int compareTo(ScoreEntry o) {
        int c = Integer.compare(o.score, this.score);
        return c != 0 ? c : this.name.compareToIgnoreCase(o.name);
    }
}
