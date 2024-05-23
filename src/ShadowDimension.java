import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * SWEN20003 Project 2, Semester 2, 2022
 *
 * @author Tran Nhat Minh Dang - 1242580
 */
public class ShadowDimension extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024, WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String WORLD_FILE_0 = "res/level0.csv", WORLD_FILE_1 = "res/level1.csv";
    private final Image BACKGROUND_IMAGE_0 = new Image("res/background0.png"),
                        BACKGROUND_IMAGE_1 = new Image("res/background1.png");

    private final static int TITLE_FONT_SIZE = 75, INSTRUCTION_FONT_SIZE = 40;
    private final static int TITLE_X = 260, TITLE_Y = 250;
    private final static int INS_X_OFFSET = 90, INS_Y_OFFSET = 190;
    private final static int INS_X_LEVEL_1 = 350, INS_Y_LEVEL_1 = 350;
    private final Font TITLE_FONT = new Font("res/frostbite.ttf", TITLE_FONT_SIZE);
    private final Font INSTRUCTION_FONT = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);
    private final static String INSTRUCTIONS_LEVEL_0 = "PRESS SPACE TO START\nUSE ARROW KEYS TO FIND GATE",
                                INSTRUCTIONS_LEVEL_1 = "PRESS SPACE TO START\nPRESS A TO ATTACK\nDEFEAT NAVEC TO WIN";
    private final static String GAME_OVER_MESSAGE = "GAME OVER!";
    private final static String WIN_MESSAGE = "CONGRATULATIONS!";
    private final static String LEVEL_COMPLETE = "LEVEL COMPLETE!";
    private final static int LEVEL_END_DURATION = 3;

    private final static int WALL_ARRAY_SIZE = 52, S_HOLE_ARRAY_SIZE = 5, TREE_ARRAY_SIZE = 15, DEMON_ARRAY_SIZE = 5;
    private final static Wall[] walls = new Wall[WALL_ARRAY_SIZE];
    private final static Sinkhole[] sinkholesLevel0 = new Sinkhole[S_HOLE_ARRAY_SIZE];
    private final static Sinkhole[] sinkholesLevel1 = new Sinkhole[S_HOLE_ARRAY_SIZE];
    private final static Tree[] trees = new Tree[TREE_ARRAY_SIZE];
    private final static Demon[] demons = new Demon[DEMON_ARRAY_SIZE];
    /**
     * Framerate of 60Hz
     */
    public final static int FRAME_RATE = 60;
    /**
     * invincible duration of all entities in game
     */
    public final static int INVINCIBLE_DURATION = 3000;
    private int frameCount;
    private Point topLeft, bottomRight;
    private Player player, player1;
    private Navec navec;
    private boolean hasStarted, gameOver, level0Win, level0End, level1Started, level1Win;
    private int timeScale;

    public ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        readCSVLevel0();
        readCSVLevel1();
        hasStarted = false;
        gameOver = false;
        level0Win = false;
        level0End = false;
        level1Started = false;
        level1Win = false;
        timeScale = 0;
        frameCount = 0;
    }

    /**
     * Method used to read file and create objects for both levels
     */
    private void readCSVLevel0(){
        try (BufferedReader reader = new BufferedReader(new FileReader(WORLD_FILE_0))){

            String line;
            int currentWallCount = 0;
            int currentSinkholeCount = 0;

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                switch (sections[0]) {
                    case "Fae":
                        player = new Player(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "Wall":
                        walls[currentWallCount] = new Wall(Integer.parseInt(sections[1]),Integer.parseInt(sections[2]));
                        currentWallCount++;
                        break;
                    case "Sinkhole":
                        sinkholesLevel0[currentSinkholeCount] = new Sinkhole(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentSinkholeCount++;
                        break;
                    case "TopLeft":
                        topLeft = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "BottomRight":
                        bottomRight = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void readCSVLevel1(){
        try (BufferedReader reader = new BufferedReader(new FileReader(WORLD_FILE_1))){

            String line;
            int currentTreeCount = 0;
            int currentSinkholeCount = 0;
            int currentDemonCount = 0;

            while((line = reader.readLine()) != null){
                String[] sections = line.split(",");
                switch (sections[0]) {
                    case "Fae":
                        player1 = new Player(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "Tree":
                        trees[currentTreeCount] = new Tree(Integer.parseInt(sections[1]),Integer.parseInt(sections[2]));
                        currentTreeCount++;
                        break;
                    case "Sinkhole":
                        sinkholesLevel1[currentSinkholeCount] = new Sinkhole(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentSinkholeCount++;
                        break;
                    case "Demon":
                        demons[currentDemonCount] = new Demon(Integer.parseInt(sections[1]),
                                Integer.parseInt(sections[2]));
                        currentDemonCount++;
                        break;
                    case "Navec":
                        navec = new Navec(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "TopLeft":
                        topLeft = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                    case "BottomRight":
                        bottomRight = new Point(Integer.parseInt(sections[1]), Integer.parseInt(sections[2]));
                        break;
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension();
        game.run();
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // goes to level 0
        if (!level0End) {
            if(!hasStarted){
                drawStartScreen();
                if (input.wasPressed(Keys.SPACE)){
                    hasStarted = true;
                }
            }

            if (gameOver) {
                drawMessage(GAME_OVER_MESSAGE);
            }
            else if (level0Win && !level0End) {
                drawMessage(LEVEL_COMPLETE);
                frameCount++;
                if (frameCount == FRAME_RATE * LEVEL_END_DURATION) {
                    level0End = true;
                    frameCount = 0;
                }
            }

            // level 0 is running
            if (hasStarted && !gameOver && !level0Win){
                BACKGROUND_IMAGE_0.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

                for (Wall current: walls){
                    current.update();
                }
                for (Sinkhole current: sinkholesLevel0){
                    current.update();
                }
                player.update(input, this);

                if (player.isDead()){
                    gameOver = true;
                }

                if (player.reachedGate()){
                    level0Win = true;
                }
            }
        }

        // goes to level 1
        if (level0End) {
            if (!level1Started) {
                drawStartScreenLevel1();
            }
            if (input.wasPressed(Keys.SPACE)) {
                level1Started = true;
            }
            // level 1 is running
            if (gameOver) {
                drawMessage(GAME_OVER_MESSAGE);
            }
            if (level1Started && !level1Win) {
                BACKGROUND_IMAGE_1.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

                for (Tree current: trees) {
                    current.update();
                }

                for (Sinkhole current: sinkholesLevel1){
                    current.update();
                }

                changeTimeScale(input);

                for (Demon current: demons) {
                    current.update(input, this);
                    current.attack(player1);
                }

                navec.update(input, this);
                navec.attack(player1);

                player1.attack(input, navec);
                for (Demon current: demons) {
                    player1.attack(input, current);
                }
                player1.update(input, this);

                if (player1.isDead()){
                    gameOver = true;
                }
                if (navec.isDead()) {
                    level1Win = true;
                }
            }
            else if (level1Win) {
                drawMessage(WIN_MESSAGE);
            }
        }

    }

    /**
     * Method that checks for collisions between Fae and the other entities, and performs
     * corresponding actions.
     */
    public void checkCollisions(Player player){
        Rectangle faeBox = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(),
                player.getCurrentImage().getHeight());
        Rectangle faeBox1 = new Rectangle(player1.getPosition(), player1.getCurrentImage().getWidth(),
                player1.getCurrentImage().getHeight());

        // check collision for level 0
        if (!level0End) {
            for (Wall current : walls){
                Rectangle wallBox = current.getBoundingBox();
                if (faeBox.intersects(wallBox)){
                    player.moveBack();
                }
            }
            for (Sinkhole hole : sinkholesLevel0){
                Rectangle holeBox = hole.getBoundingBox();
                if (hole.isActive() && faeBox.intersects(holeBox)){
                    player.setHealthPoints(Math.max(player.getHealthPoints() - hole.getDamagePoints(), 0));
                    player.moveBack();
                    hole.setActive(false);
                    System.out.println("Sinkhole inflicts " + hole.getDamagePoints() + " damage points on Fae. " +
                            "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
                }
            }
        }

        // check collision for level 1
        if (!level1Win && level0End) {
            for (Tree current : trees){
                Rectangle treeBox = current.getBoundingBox();
                if (faeBox1.intersects(treeBox)){
                    player1.moveBack();
                }
            }
            for (Sinkhole hole : sinkholesLevel1){
                Rectangle holeBox = hole.getBoundingBox();
                if (hole.isActive() && faeBox1.intersects(holeBox)){
                    player1.setHealthPoints(Math.max(player.getHealthPoints() - hole.getDamagePoints(), 0));
                    player1.moveBack();
                    hole.setActive(false);
                    System.out.println("Sinkhole inflicts " + hole.getDamagePoints() + " damage points on Fae. " +
                            "Fae's current health: " + player1.getHealthPoints() + "/" + Player.getMaxHealthPoints());
                }
            }
        }
    }

    /**
     * Method to check collisions between enemies and obstacles
     */
    public void checkCollisions(Enemy enemy) {
        Rectangle enemyBox = new Rectangle(enemy.getPosition(), enemy.getCurrentImage().getWidth(),
                enemy.getCurrentImage().getHeight());
        for (Tree current : trees){
            Rectangle treeBox = current.getBoundingBox();
            if (enemyBox.intersects(treeBox)){
                if (enemy.movingVertical) {
                    enemy.movingUp = !enemy.movingUp;
                }
                else enemy.facingRight = !enemy.facingRight;
            }
        }
        for (Sinkhole hole : sinkholesLevel1){
            Rectangle holeBox = hole.getBoundingBox();
            if (hole.isActive() && enemyBox.intersects(holeBox)){
                if (enemy.movingVertical) {
                    enemy.movingUp = !enemy.movingUp;
                }
                else enemy.facingRight = !enemy.facingRight;
            }
        }
    }

    /**
     * Method that checks if Fae has gone out-of-bounds and performs corresponding action
     */
    public void checkOutOfBounds(Point currentPosition){
        if ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x)
                || (currentPosition.x > bottomRight.x)){
            player.moveBack();
            player1.moveBack();
        }
    }

    /**
     * Method to check if enemies had gone out-of-bounds and bounce back
     */
    public void checkOutOfBounds(Enemy enemy) {
        Point currentPosition = enemy.getPosition();
        if ((currentPosition.y > bottomRight.y) || (currentPosition.y < topLeft.y) || (currentPosition.x < topLeft.x)
                || (currentPosition.x > bottomRight.x)){
            if (enemy.movingVertical) {
                enemy.movingUp = !enemy.movingUp;
            }
            else enemy.facingRight = !enemy.facingRight;
        }
    }

    /**
     * Method to change timescale of enemies
     */
    public void changeTimeScale(Input input) {
        if (input.wasPressed(Keys.L) && timeScale + 1 <= 3) {
            timeScale += 1;
            for (Demon current: demons) {
                current.speed *= (150.0 / 100);
            }
            navec.speed *= (150.0 / 100);
            System.out.println("Sped up, Speed: " + timeScale);
        }
        if (input.wasPressed(Keys.K) && timeScale - 1 >= -3) {
            timeScale -= 1;
            for (Demon current: demons) {
                current.speed *= (50.0 / 100);
            }
            navec.speed *= (50.0 / 100);
            System.out.println("Slowed Down, Speed: " + timeScale);
        }
    }

    /**
     * Method used to draw the start screen title and instructions
     */
    private void drawStartScreen(){
        TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
        INSTRUCTION_FONT.drawString(INSTRUCTIONS_LEVEL_0,TITLE_X + INS_X_OFFSET, TITLE_Y + INS_Y_OFFSET);
    }

    /**
     * Method to draw start screen for level 1
     */
    private void drawStartScreenLevel1() {
        INSTRUCTION_FONT.drawString(INSTRUCTIONS_LEVEL_1, INS_X_LEVEL_1, INS_Y_LEVEL_1);
    }

    /**
     * Method used to draw end screen messages
     */
    private void drawMessage(String message){
        TITLE_FONT.drawString(message, (Window.getWidth()/2.0 - (TITLE_FONT.getWidth(message)/2.0)),
                (Window.getHeight()/2.0 + (TITLE_FONT_SIZE/2.0)));
    }
}