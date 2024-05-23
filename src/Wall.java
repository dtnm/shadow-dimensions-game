import bagel.Image;
import bagel.util.Rectangle;

public class Wall extends Obstacle {
    private final Image WALL = new Image("res/wall.png");

    public Wall(int startX, int startY){
        super(startX, startY);
    }

    /**
     * Method that performs state update
     */
    public void update() {
        WALL.drawFromTopLeft(this.position.x, this.position.y);
    }

    public Rectangle getBoundingBox(){
        return new Rectangle(this.position, WALL.getWidth(), WALL.getHeight());
    }
}