import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;

public class Navec extends Enemy{
    private final static String NAVEC_LEFT = "res/navec/navecLeft.png";
    private final static String NAVEC_RIGHT = "res/navec/navecRight.png";
    private final static String NAVEC_INVINC_LEFT = "res/navec/navecInvincibleLeft.png";
    private final static String NAVEC_INVINC_RIGHT = "res/navec/navecInvincibleRight.png";
    private final static Image FIRE = new Image("res/navec/navecFire.png");
    private final int MAX_HEALTH_POINTS = 80;
    private final static int ATTACK_RANGE = 200, DAMAGE = 20;
    private int frameCount;

    public Navec(int startX, int startY) {
        super(startX, startY);
        healthPoints = MAX_HEALTH_POINTS;
        currentImage = new Image(NAVEC_RIGHT);
        isAggressive = true;
        isInvincible = false;
    }

    /**
     * Method to perform a state update
     */
    public void update(Input input, ShadowDimension gameObject){
        if (!movingVertical) {
            if (facingRight) move(speed, 0);
            else move(-speed, 0);
        }
        else {
            if (movingUp) move(0, -speed);
            else move(0, speed);
        }
        invincibleTimer();
        setCurrentImage();
        gameObject.checkOutOfBounds(this);
        gameObject.checkCollisions(this);
        if (!isDead()) this.currentImage.drawFromTopLeft(position.x, position.y);
        renderHealthPoints(healthPoints, MAX_HEALTH_POINTS);
    }

    public String getClassName() {return "Navec"; }

    public int getMaxHealthPoints() {
        return MAX_HEALTH_POINTS;
    }

    /**
     * Method to set image to render according to current state of navec
     */
    protected void setCurrentImage() {
        if (facingRight && !isInvincible) currentImage = new Image(NAVEC_RIGHT);
        else if (facingRight && isInvincible) currentImage = new Image(NAVEC_INVINC_RIGHT);
        else if (!isInvincible) currentImage = new Image(NAVEC_LEFT);
        else currentImage = new Image(NAVEC_INVINC_LEFT);
    }

    /**
     * Method to perform attack on player
     * @param player
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
                    System.out.println("Navec inflicts " + DAMAGE + " damage points on Fae. " +
                            "Fae's current health: " + player.getHealthPoints() + "/" + Player.getMaxHealthPoints());
                }
                player.setInvincible();
            }
        }
    }


}
