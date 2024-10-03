package main;

import java.awt.*;

public class Column extends Entity implements Movable {

    private int speed;

    private String path = "../images/";

    public static int tileWidth = 60;

    public static int tileDifference = 80;
    private int height;

    public Column() {

    }

    public Column(int x, int y, int speed, Image img) {
        super(x, y, img);
        this.speed = speed;
    }

    public Column(int x, int y, String path) {
        super(x, y);
        this.speed = -4;
        this.setImageFromPath(this.path + path);
    }


    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }


    @Override
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
