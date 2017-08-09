package com.utilfreedom.brainmath.model;

/**
 * Created by kennywang on 8/5/17.
 */

public class DifficultyOption {
    String difficulty;
    double duration;
    int range;

    public DifficultyOption(String difficulty) {
        this.difficulty = difficulty;

        switch (difficulty) {
            case "easy":
                this.duration = 5;
                this.range = 5;
                break;
            case "medium":
                this.duration = 3.5;
                this.range = 5;
                break;
            case "hard":
                this.duration = 2.5;
                this.range = 5;
                break;
            case "insane":
                this.duration = 1.5;
                this.range = 5;
                break;
        }
    }

    public String getDifficulty() {
        return difficulty;
    }

    public double getDuration() {
        return duration;
    }

    public int getRange() {
        return range;
    }
}
