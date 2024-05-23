import bagel.Input;
import bagel.util.Point;

public abstract class Character {
    protected Point position;
    protected abstract void move(double xMove, double yMove);
}
