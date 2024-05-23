import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Random;

public class Demon extends Enemy {
    private final static String DEMON_LEFT = "res/demon/demonLeft.png";
    private final static String DEMON_RIGHT = "res/demon/demonRight.png";
    private final static String DEMON_INVINC_RIGHT = "res/demon/demonInvincibleRight.png";
    private final static String DEMON_INVINC_LEFT = "res/demon/demonInvincibleRight.png";
    private final static Image FIRE = new Image("res/demon/demonFire.png");
    private final int MAX_HEALTH_POINTS = 40;
    private final static int ATTACK_RANGE = 150, DAMAGE = 10;


    public Demon(int startX, int startY){
        super(startX, startY);
        this.healthPoints = MAX_HEALTH_POINTS;
        isInvincible = false;
        isAggressive = new Random().nextBoolean();
    }

    /**
     * Method that performs state update
     */
    public void update(Input input, ShadowDimension gameObject){
        if (isAggressive) {
            if (!movingVertical) {
                if (facingRight) move(speed, 0);
                else move(-speed, 0);
            }
            else {
                if (movingUp) move(0, -speed);
                else move(0, speed);
            }
        }
        setCurrentImage();
        invincibleTimer();
        gameObject.checkOutOfBounds(this);
        gameObject.checkCollisions(this);
        if (!isDead()) this.currentImage.drawFromTopLeft(position.x, position.y);
        renderHealthPoints(healthPoints, MAX_HEALTH_POINTS);
    }
    public String getClassName() {return "Demon"; }

    public int getMaxHealthPoints() {
        return MAX_HEALTH_POINTS;
    }

    /**
     * Method to set image to render according to the state of demon
     */
    protected void setCurrentImage() {
        if (facingRight && !isInvincible) currentImage = new Image(DEMON_RIGHT);
        else if (facingRight && isInvincible) currentImage = new Image(DEMON_INVINC_RIGHT);
        else if (!isInvincible) currentImage = new Image(DEMON_LEFT);
        else currentImage = new Image(DEMON_INVINC_LEFT);
    }

    /**
     * Method to perform an attack on player
     */
    public void attack(Player player) {
        Point demonCentre = new Point(position.x + currentImage.getWidth()/2,
                                      position.y + currentImage.getHeight()/2);
        Point playerCenter = new Point(player.getPosition().x + player.getCurrentImage().getWidth()/2,
                                       player.getPosition().y + player.getCurrentImage().getHeight()/2);

        double distance = Math.sqrt(Math.pow(demonCentre.x - playerCenter.x, 2) + Math.pow(demonCentre.y - playerCenter.y, 2));

        Rectangle playerBound = new Rectangle(player.getPosition(), player.getCurrentImage().getWidth(), player.getCurrentImage().getHeight());
        Rectangle demonBound  = new Rectangle(position, currentImage.getWidth(), currentImage.getHeight());
        Rectangle fireBound;

        if (distance <= ATTACK_RANGE && !isDead()) {
            if (playerCenter.x <= demonCentre.x && playerCenter.y <= demonCentre.y) {
                FIRE.drawFromTopLeft(position.x - FIRE.getWidth(), position.y - FIRE.getHeight());
                fireBound = new Rectangle(new Point(position.x - FIRE.getWidth(), position.y - FIRE.getHeight()),
                        FIRE.getWidth(), FIRE.getHeight());
            }
            else if (playerCenter.x <= demonCentre.x) {
                FIRE.drawFromTopLeft(position.x - FIRE.getWidth(), demonBound.bottom(), ROTATION.setRotation(ROTATE_270));
                fireBound = new Rectangle(new Point(position.x - FIRE.getWidth(), demonBound.bottom()),
                        FIRE.getWidth(), FIRE.getHeight());
            }
            else if (playerCenter.y <= demonCentre.y) {
                FIRE.drawFromTopLeft(demonBound.right(), position.y - FIRE.getHeight(), ROTATION.setRotation(ROTATE_90));
                fireBound = new Rectangle(new Point(demonBound.right(), position.y - FIRE.getHeight()),
                        FIRE.getWidth(), FIRE.getHeight());
            }
            else {
                FIRE.drawFromTopLeft(demonBound.right(), demonBound.bottom(), ROTATION.setRotation(ROTATE_180));
                fireBound = new Rectangle(new Point(demonBound.right(), demonBound.bottom()),
                        FIRE.getWidth(), FIRE.getHeight());
            }
            if (playerBound.intersects(fireBound)) {
                if (!player.getInvincible()) {
                    player.setHealthPoints(player.getHealthPoints() - DAMAGE);
                    System.out.println("Demon inflicts " + DAMAGE + " damage points on Fae. " +
                            "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
                }
                player.setInvincible();
            }
        }
    }

}
