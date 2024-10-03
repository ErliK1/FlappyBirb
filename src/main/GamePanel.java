package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class GamePanel extends JPanel implements KeyListener, Runnable {

    private final int WINDOW_WIDTH = 360;

    private final int WINDOW_HEIGHT = 640;

    private final int ORIGINAL_TILE_SIZE = 16;

    private final int FACTOR = 2;

    private final int TILE_SIZE = ORIGINAL_TILE_SIZE * FACTOR;

    private final int GRAVITY_CONS = 6;

    private final int BIRB_HITS_GROUND = WINDOW_HEIGHT - 98;

    private int startSafeHeight = 0;

    private int endSafeHeight = 0;


    int pipeX = WINDOW_WIDTH;
    int pipeY = 0;

    int pipeWidth = 64;
    int pipeHeight = 512;


    Bird bird;

    Column column;

    Thread thread;

    Image backgroundImage;

    private final int FPS = 60;

    private final double RATE = 1000000000 / FPS;

    private  int gravity = GRAVITY_CONS;

    private ArrayList<Column> columns;

    private Date startTime;

    private Date currentTime;

    private final int COLUMN_POSITION = WINDOW_WIDTH - Column.tileWidth;

    private final int ALLOWED_HEIGHT = WINDOW_HEIGHT - 65;

    private final int GAP_BETWEEN = 150;

    private final int CALCULATED_MAX_HEIGHT = ALLOWED_HEIGHT - GAP_BETWEEN;

    private int points = 0;

    JLabel label;

    HashMap<Column, Boolean> hashMap = new HashMap<>();

    private boolean gameOver = false;

    File file;

    AudioInputStream stream;
    Clip clip;

    Thread audioThread;

    Date startTimeReward;





    public GamePanel() {
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        thread = new Thread(this);
        backgroundImage = new ImageIcon(this.getClass().getResource("../images/flappybirdbg.png")).getImage();
        bird = new Bird(30, 360, -60);
        bird.height = TILE_SIZE;
        bird.width = TILE_SIZE;
        column = new TopColumn(WINDOW_WIDTH - Column.tileWidth, -200);
        columns = new ArrayList<>();
        columns.add(column);
        startTime = new Date();
        this.column.setHeight(100);
        label = new JLabel("" + points);
        this.add(label);
        try {
            file = new File("D:\\projects\\Java\\FlappyBirb\\src\\images\\reward_sound.wav");
            this.stream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(stream);
        }
        catch (Exception e) {
            System.out.println(e);
        }






    }

    public void startGameThread() {
        thread.start();
    }

   @Override
    public void run() {

        long startTime = System.nanoTime();
        long currentTime;
        double delta;

        while (this.thread != null && !gameOver) {
            currentTime = System.nanoTime();
            delta = (currentTime - startTime) / RATE;

            if (delta >= 1) {
                update();
                repaint();
                delta = 0;
                startTime = currentTime;
            }

        }

    }

    public void addColumnsToHashMap() {
        for (Column column1: columns) {
            if (!hashMap.containsKey(column1)) {
                hashMap.put(column1, false);
            }
        }
    }

    public void update() {
        ArrayList<Column> columnsToBeDropped = new ArrayList<>();
        int birb = bird.getY() + gravity;
        bird.setY(Math.max(birb, 0));
        if (bird.getY() >= BIRB_HITS_GROUND) {
           gameOver = true;
        }
        addColumnsToHashMap();
        checkIfBirbDied();
        currentTime = new Date();
        long difference = currentTime.getTime() - startTime.getTime();
        if (difference >= 1500) {
            columns.addAll(calculateColumnsPosition());
            startTime = currentTime;
        }
        for (Column column1: columns) {
            column1.setX(column1.getX() + column1.getSpeed());
            if (column1.getX() <= -50) {
                columnsToBeDropped.add(column1);
            }
        }

        if (points == 10) {
            playRewardSound();
        }

        if (startTimeReward != null) {
            Date now = new Date();
            long timeDiff = now.getTime() - startTimeReward.getTime();
            if (timeDiff >= 6000) {
                bird.setImageFromPath("../images/flappybird.png");
            }
        }

        columns.removeAll(columnsToBeDropped);

    }

    public void playRewardSound() {
        if (clip != null) {
            clip.start();
            Image img = new ImageIcon(this.getClass().getResource("../images/ovo.jpeg")).getImage();
            bird.setImage(img);
            startTimeReward = new Date();

        }
    }

    private ArrayList<Column> calculateColumnsPosition() {
        ArrayList<Column> columnsToBeAdded = new ArrayList<>();
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = WINDOW_HEIGHT/4;
        System.out.println(randomPipeY);
        Column topPipe = new TopColumn(pipeX, randomPipeY);
        columnsToBeAdded.add(topPipe);
        Column botPipe = new BottomColumn(pipeX, topPipe.getY() + pipeHeight + openingSpace);
        columnsToBeAdded.add(botPipe);
        return columnsToBeAdded;
    }

    public HashMap<Column, Boolean> checkIfBirbDied() {
        int birbX = bird.getX();
        int birbY = bird.getY();
        int localPoints = 0;
        addColumnsToHashMap();
        for(Column column1: columns) {
            if (collision(bird, column1)) {
                gameOver = true;
            }
            else if(xCollision(bird, column1) && !hashMap.get(column1)) {
                localPoints++;
                hashMap.put(column1, true);
            }

        }
    points += (localPoints / 2);
    return hashMap;
    }

    private boolean xCollision(Bird a, Column b) {
        return a.getX() < b.getX() + Column.tileWidth &&
                a.getX() + a.width > b.getX();
    }

    private boolean yCollision(Bird a, Column b) {
        return  a.getY() < b.getY() + pipeHeight &&
                a.getY() + a.height > b.getY();
    }

    public boolean collision(Bird a, Column b) {
        return  xCollision(a, b) && yCollision(a, b);
    }


    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(backgroundImage, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, null);
        g2d.drawImage(bird.getImage(), bird.getX(), bird.getY(), bird.height, bird.width, null);
        for (Column column1: columns) {
            g2d.drawImage(column1.getImage(), column1.getX(), column1.getY(), pipeWidth, pipeHeight, null);
        }
        g2d.drawString("" + points, 10, 20);
    }

    public void restartGame() {
        bird.setX(30);
        bird.setY(100);
        hashMap.clear();
        columns.clear();
        gameOver = false;
        points = 0;
        thread = null;
        thread = new Thread(this);
        try {
            stream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(stream);
        }
        catch (Exception e) {}
        startGameThread();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameOver) {
                bird.setY(bird.getY() + bird.getSpeed());
                gravity = 0;
            }
            else {
                restartGame();
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            gravity = GRAVITY_CONS;
        }
    }
}
