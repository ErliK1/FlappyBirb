package main;

import javax.swing.*;
import java.awt.*;

public abstract class Entity {
    private int x;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    private int y;

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private Image image;

    Entity(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Entity() {

    }

    public void setImageFromPath(String path) {
        this.image = new ImageIcon(this.getClass().getResource(path)).getImage();
    }
}
