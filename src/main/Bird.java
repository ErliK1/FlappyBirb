package main;

import java.awt.*;

public class Bird extends Entity implements Movable{
    private int speed;
    private final String PATH = "../images/flappybird.png";

    public int width;
    public int height;

    public Bird(int x, int y,int speed, Image image ) {
        super(x, y, image);
        this.speed = speed;
    }

    public Bird(int x, int y, int speed) {
        super(x, y);
        this.speed = speed;
        this.setImageFromPath(PATH);
    }

    public Bird() {

    }




    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
