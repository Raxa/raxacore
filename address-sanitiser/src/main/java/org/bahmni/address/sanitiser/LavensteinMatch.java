package org.bahmni.address.sanitiser;

public class LavensteinMatch<T> {
    private T bestSuggestion;
    private int distance;

    public LavensteinMatch(T bestSuggestion, int distance) {
        this.bestSuggestion = bestSuggestion;
        this.distance = distance;
    }

    public T matchValue() {
        return bestSuggestion;
    }

    public int getDistance() {
        return distance;
    }
}
