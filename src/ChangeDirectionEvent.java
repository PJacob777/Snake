import java.util.EventObject;

class ChangeDirectionEvent extends EventObject {
    private Direction newDirection;

    public ChangeDirectionEvent(Object source, Direction newDirection) {
        super(source);
        this.newDirection = newDirection;
    }

    public Direction getNewDirection() {
        return newDirection;
    }
}
