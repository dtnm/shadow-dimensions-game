import bagel.Image;
import bagel.util.Rectangle;

public class Tree extends Obstacle{
    private final Image TREE = new Image("res/tree.png");

    public Tree(int startX, int startY){
        super(startX, startY);
    }

    /**
     * Method that performs state update
     */
    public void update() {
        TREE.drawFromTopLeft(this.position.x, this.position.y);
    }

    public Rectangle getBoundingBox(){
        return new Rectangle(this.position, TREE.getWidth(), TREE.getHeight());
    }
}
