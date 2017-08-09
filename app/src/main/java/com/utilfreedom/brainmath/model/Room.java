package com.utilfreedom.brainmath.model;

import java.util.HashMap;

/**
 * Created by kennywang on 8/5/17.
 */

public class Room {

    private String UID;
    private String name;
    private HashMap<String, Object> players;
    private String difficulty;

    private Room() {}

    public Room (String name, HashMap<String, Object> players, String difficulty) {
        this.name = name;
        this.players = players;
        this.difficulty = difficulty;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> getPlayers() {
        return players;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
