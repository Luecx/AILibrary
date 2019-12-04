package boids_model;

import java.util.ArrayList;

public abstract class Swarm {

    protected ArrayList<Unit> units = new ArrayList<>();

    public void update(double t){
        update();
        for(Unit u:units){
            u.setDirection(u.calculateDirection(this));
        }
        for(Unit u:units){
            u.updatePosition(t);
        }
    }

    protected abstract void update();

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }
}
