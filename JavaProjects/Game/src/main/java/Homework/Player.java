package Homework;

import java.awt.Color;
import java.io.Serializable;

public class Player implements Serializable {
    private final String name;
    private final Color stoneColor;

    private static final long serialVersionUID = 1L;

    public Player(String name, Color stoneColor) {
        this.name = name;
        this.stoneColor = stoneColor;
    }

    public String getName() {
        return name;
    }
}

