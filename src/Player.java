import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Player extends Character {
    private final static String FAE_LEFT = "res/fae/faeLeft.png";
    private final static String FAE_ATTACK_LEFT = "res/fae/faeAttackLeft.png";
    private final static String FAE_RIGHT = "res/fae/faeRight.png";
    private final static String FAE_ATTACK_RIGHT = "res/fae/faeAttackRight.png";
    private final static int MAX_HEALTH_POINTS = 100;
    private final static double MOVE_SIZE = 2;
    private final static int WIN_X = 950;
    private final static int WIN_Y = 670;

    private final static int HEALTH_X = 20;
    private final static int HEALTH_Y = 25;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final Font FONT = new Font("res/frostbite.ttf", FONT_SIZE);
    private final DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private Point position;
    private Point prevPosition;
    private int healthPoints;
    private Image currentImage;
    private boolean facingRight, isInvincible, isAttacking, isCooldown;
    private int frameCount, cooldownCount = 0, attackCount = 0;
    private static final int COOLDOWN_DURATION = 2000, DAMAGE = 20, ATTACK_DURATION = 1000;

    public Player(int startX, int startY){
        this.position = new Point(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = new Image(FAE_RIGHT);
        this.facingRight = true;
        isInvincible = false;
        isCooldown = false;
        isAttacking = false;
        COLOUR.setBlendColour(GREEN);
        frameCount = 0;
        cooldownCount = 0;
    }

    /**
     * Method that performs state update
     */
    public void update(Input input, ShadowDimension gameObject){
        if (input.isDown(Keys.UP)){
            setPrevPosition();
            move(0, -MOVE_SIZE);
        } else if (input.isDown(Keys.DOWN)){
            setPrevPosition();
            move(0, MOVE_SIZE);
        } else if (input.isDown(Keys.LEFT)){
            setPrevPosition();
            move(-MOVE_SIZE,0);
            facingRight = false;
        } else if (input.isDown(Keys.RIGHT)){
            setPrevPosition();
            move(MOVE_SIZE,0);
            facingRight = true;
        }
        attackTimer();
        cooldownTimer();
        if (isAttacking && !facingRight) currentImage = new Image(FAE_ATTACK_LEFT);
        else if (isAttacking && facingRight) currentImage = new Image(FAE_ATTACK_RIGHT);
        else if (facingRight) currentImage = new Image(FAE_RIGHT);
        else currentImage = new Image(FAE_LEFT);
        this.currentImage.drawFromTopLeft(position.x, position.y);
        gameObject.checkCollisions(this);
        renderHealthPoints();
        gameObject.checkOutOfBounds(this.position);
    }

    /**
     * Method to perform attack of player on enemies
     */
    public void attack(Input input, Enemy enemy) {
        Rectangle playerBound = new Rectangle(position, currentImage.getWidth(), currentImage.getHeight());
        Rectangle enemyBound = new Rectangle(enemy.getPosition(), enemy.getCurrentImage().getWidth(), enemy.getCurrentImage().getHeight());
        if (input.wasPressed(Keys.A)) {
            isAttacking = true;
        }
        if (!enemy.getInvincible() && isAttacking) {
            if (playerBound.intersects(enemyBound)) {
                enemy.setHealthPoints(enemy.getHealthPoints() - DAMAGE);
                System.out.println("Fae inflicts " + DAMAGE + " damage points on " + enemy.getClassName() + ". " + enemy.getClassName() +
                        "'s current health: " + enemy.getHealthPoints() + "/" + enemy.getMaxHealthPoints());
                enemy.setInvincible();
            }
        }
    }

    /**
     * Methoa to set timer for attack phase of player
     */
    public void attackTimer() {
        if (isAttacking) {
            if (attackCount >= (ATTACK_DURATION / 1000) * ShadowDimension.FRAME_RATE) {
                attackCount = 0;
                isAttacking = false;
                isCooldown = true;
            }
            else attackCount++;
        }
    }

    /**
     * Method to set timer for cooldown duration
     */
    public void cooldownTimer() {
        if (cooldownCount <= (COOLDOWN_DURATION / 1000) * ShadowDimension.FRAME_RATE && isCooldown) {
            cooldownCount++;
        }
        else {
            isCooldown = false;
            cooldownCount = 0;
        }
    }

    /**
     * Method that stores Fae's previous position
     */
    private void setPrevPosition(){
        this.prevPosition = new Point(position.x, position.y);
    }

    /**
     * Method that moves Fae back to previous position
     */
    public void moveBack(){
        this.position = prevPosition;
    }

    /**
     * Method that moves Fae given the direction
     */
    @Override
    protected void move(double xMove, double yMove){
        double newX = position.x + xMove;
        double newY = position.y + yMove;
        this.position = new Point(newX, newY);
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * 100;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method that checks if Fae's health has depleted
     */
    public boolean isDead() {
        return healthPoints == 0;
    }

    /**
     * Method that checks if Fae has found the gate
     */
    public boolean reachedGate(){
        return (this.position.x >= WIN_X) && (this.position.y >= WIN_Y);
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

    /**
     * Method to set player status to invincible
     */
    public void setInvincible() {
        this.isInvincible = true;
        if (isInvincible) {
            if (frameCount <= (ShadowDimension.INVINCIBLE_DURATION / 1000) * ShadowDimension.FRAME_RATE){
                frameCount++;
            }
            else {
                isInvincible = false;
                frameCount = 0;
            }
        }
    }
    public boolean getInvincible() {return isInvincible; }
    public static int getMaxHealthPoints() {
        return MAX_HEALTH_POINTS;
    }

}