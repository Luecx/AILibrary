package luecx.ai.genetic_algorithm.neat.mario;


public abstract class Mario {

    protected double x;
    protected double y;

    protected double passed_time = 0;
    protected double speed = 4d;

    protected double speed_y = 0;
    protected double gravity = 10;

    protected World world;

    private boolean spectator = false;

    public Mario(World w, double x, double y){
        this.x = x;
        this.y = y;
        this.world = w;
    }

    public Mario(World w, double x, double y, boolean spectator) {
        this.x = x;
        this.y = y;
        this.world = w;
        this.spectator = spectator;
    }

    protected abstract boolean[] getControl();

    /**
     *
     * control array:
     * 0: right
     * 1: left
     * 2: jump
     *
     *
     * @param time
     * @param w
     */
    public void process(double time, World w){

        boolean[] control = getControl();

        if(spectator == false){
            hits_right_or_left(w);
            if(hits_bottom(w)){
                this.speed_y = 0;
                if(control[2]){
                    this.speed_y = 6;
                }
            }else{
                this.speed_y -= gravity * time;
            }

            this.y += speed_y * time;

            hits_top(w);
            hits_bottom(w);

            if(control[0] ^ control[1]){
                if(control[0]){
                    this.x += speed * time;
                }else{
                    this.x -= speed * time;
                }
            }
            hits_right_or_left(w);
        }else{
            if(control[0]){
                this.x += speed * 2 * time;
            }if(control[1]){
                this.x -= speed * 2 * time;
            }if(control[2]){
                this.y += speed * 2 * time;
            }if(control[3]){
                this.y -= speed * 2 * time;
            }
        }

    }

    public boolean hits_bottom(World w){
        if(x % 1d == 0d){
            if(w.getBlock((int)x, (int)y) == 1){
                this.y = (int) (this.y + 0.5);
                return true;
            }

            if((w.getBlock((int)x, (int)y -1) == 1) &&
                    (w.getBlock((int)x, (int)y - 1) == 1) &&
                    (y == (int)y)){
                return true;
            }
        }else{
            int x_a = (int) x;
            int x_b = (int) x + 1;

            int y_a = (int) y;

            if((w.getBlock(x_a, y_a) == 1 || w.getBlock(x_b, y_a) == 1) && y - y_a < 1){
                this.y = (int)(y + 0.1);
                return true;
            }
            if(((w.getBlock(x_a, y_a -1) == 1) ||
                    (w.getBlock(x_b, y_a - 1) == 1)) &&
                    (y == y_a)){
                return true;
            }
        }
        return false;
    }

    public boolean hits_top(World w){
        if((w.getBlock((int)x, (int) y + 1) == 1
                || (x % 1d != 0 && w.getBlock((int)x + 1, (int) y  + 1) == 1))
                && this.speed_y >= 0){
            this.speed_y = 0;
            this.y = (int)y - 0.01;
            System.err.println(y + "  " + speed_y);
            return true;
        }
        return false;
    }

    public boolean hits_right_or_left(World w){

        if(y % 1d == 0d){
            if(w.getBlock((int)x, (int)y) == 1){
                x = (int) (x + 1);
                return true;
            }

            if(w.getBlock((int)x + 1, (int)y) == 1){
                x = (int) (x);
                return true;
            }
        }else{
            int y_a = (int) y;
            int y_b = (int) y + 1;

            int x_a = (int) x;
            int x_b = (int) x + 1;

             if(((w.getBlock(x_b, y_a) == 1) || (w.getBlock(x_b, y_b) == 1))){
                x = (int) x;
                return true;
            }

            if(((w.getBlock(x_a, y_a) == 1) || (w.getBlock(x_a, y_b) == 1))){
                x = (int) x + 1;
                return true;
            }

        }
        return false;
    }


    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed_y() {
        return speed_y;
    }

    public void setSpeed_y(double speed_y) {
        this.speed_y = speed_y;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
