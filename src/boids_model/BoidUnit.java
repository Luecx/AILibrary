package boids_model;


import core.vector.DenseVector;
import core.vector.Vector;
import core.vector.Vector2d;

import java.util.ArrayList;

public class BoidUnit extends Unit {

    public BoidUnit(Vector position) {
        super(position);
    }

    public BoidUnit(double... position) {
        super(position);
    }

    public BoidUnit(int dimensions) {
        super(dimensions);
    }

    //the range within the unit interacts with other units
    private int visibility = 500;
    private int minDistance = 50;
    private int maxSpeed = 1000;

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    @Override
    public Vector calculateDirection(Swarm swarm) {
        ArrayList<Unit> nearby = swarm.getUnits(getPosition(), visibility);


        //seperation
        Vector center = this.getPosition().newInstance();
        Vector avgDirection = this.getPosition().newInstance();
        Vector fleeing = this.getPosition().newInstance();


        for(Unit u:nearby){
            center.self_add(u.getPosition().sub(this.getPosition()));
            avgDirection.self_add(u.getDirection());
            Vector con = u.getPosition().sub(this.getPosition()).negate();
            if(con.length() > 0.01)
                fleeing.self_add(con.self_scale(minDistance/con.length()));
        }

        //System.out.println(center.length() + "  " + fleeing.length());

        Vector total = center.scale(0.4);
        total.self_add(avgDirection.scale(0.01));
        total.self_add(fleeing.scale(6));

        //System.out.println(center.length() + "   "+ avgDirection.length() + "  " + fleeing.length());


        if(total.length() >= maxSpeed)
            total.set_self_Length(maxSpeed);
        return total;
    }
}
