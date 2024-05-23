import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

import java.util.Random;

public abstract class Enemy {
    protected final static int ORANGE_BOUNDARY = 65;
    protected final static int RED_BOUNDARY = 35;
    protected final static int FONT_SIZE = 15;
    private final static int HEALTH_Y_OFFSET = 6;
    private final static double MAX_SPEED = 0.7, MIN_SPEED = 0.2;
    protected final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    protected final DrawOptions COLOUR = new DrawOptions(),
                                        ROTATION = new DrawOptions();
    protected static final double ROTATE_90 = 1.57;
    protected static final double ROTATE_180 = 3.14;
    protected static final double ROTATE_270 = 4.71;
    protected final static Colour GREEN = new Colour(0, 0.8, 0.2);
    protected final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    protected final static Colour RED = new Colour(1, 0, 0);

    protected double healthX, healthY;
    protected Point position;
    protected int healthPoints;
    protected Image currentImage;
    protected boolean facingRight;
    protected boolean movingVertical, movingUp;
    protected double speed;
    protected boolean isAggressive, isInvincible;
    protected int frameCount;

    public Enemy(int startX, int startY) {
        this.position = new Point(startX, startY);
        facingRight = new Random().nextBoolean();
        speed = getRandomNumber(MIN_SPEED, MAX_SPEED);
        movingVertical = new Random().nextBoolean();
        movingUp = new Random().nextBoolean();
        COLOUR.setBlendColour(GREEN);
    }

    public abstract int getMaxHealthPoints();

    /**
     * Method to generate random number between Max and Min
     */
    public double getRandomNumber(double min, double max) {
        return ((Math.random() * (max - min)) + min);
    }

    /**
     * Method to perform a state update
     */
    public abstract void update(Input input, ShadowDimension gameObject);

    /**
     * Method that moves enemies given the direction
     */
    protected void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    protected void renderHealthPoints(double currentHealth, double maxHealth){
        double percentageHP = ((double) currentHealth/maxHealth) * 100;
        healthX = position.x;
        healthY = position.y - HEALTH_Y_OFFSET;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        if (!isDead()) FONT.drawString(Math.round(percentageHP) + "%", healthX, healthY, COLOUR);
    }

    /**
     * Method that checks if ememy's health has depleted
     */
    public boolean isDead() {
        return healthPoints <= 0;
    }

    public Point getPosition() {
        return position;
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
        if (this.healthPoints <= 0) this.healthPoints = 0;
    }

    protected abstract void setCurrentImage();

    public boolean getInvincible() {
        return isInvincible;
    }

    public void setInvincible() {
        this.isInvincible = true;
    }

    /**
     * Method to set timer for invincible phase of enemies
     */
    public void invincibleTimer() {
        if (isInvincible) {
            frameCount++;
            if (frameCount >= (ShadowDimension.INVINCIBLE_DURATION / 1000) * ShadowDimension.FRAME_RATE){
                frameCount = 0;
                isInvincible = false;
            }
        }
    }

    public abstract String getClassName();
}
