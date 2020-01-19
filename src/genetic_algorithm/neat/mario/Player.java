package genetic_algorithm.neat.mario;

public class Player extends Mario {


    public Player(World w, double x, double y, boolean spectator) {
        super(w, x, y, spectator);
    }

    public Player(World w, double x, double y) {
        super(w, x, y);
    }

    private boolean[] control = new boolean[4];

    @Override
    protected boolean[] getControl() {
        return control;
    }

    public void setMoveUp(boolean val) {
        control[2] = val;
    }
    public void setMoveDown(boolean val) {
        control[3] = val;
    }
    public void setMoveRight(boolean val) {
        control[0] = val;
    }
    public void setMoveLeft(boolean val) {
        control[1] = val;
    }

}
