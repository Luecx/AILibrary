package boids_model;

import core.vector.Vector;

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

    public ArrayList<Unit> getUnits(Vector center, double distance){
        ArrayList<Unit> units = new ArrayList<>();
        for(Unit u:this.units){
            //System.out.println(u.getPosition().sub(center).length() + "   "  + center + "   " + u.getPosition());
            if(u.getPosition().sub(center).length() < distance){
                units.add(u);
            }
        }
        return units;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }
}
