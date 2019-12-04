package boids_model;


import core.vector.DenseVector;
import core.vector.Vector;

public class BoidSwarm extends Swarm {

    private DenseVector averagePosition;

    @Override
    protected void update() {
        if(this.units.size() == 0) return;
        this.averagePosition = new DenseVector(this.units.get(0).getPosition().getSize());
        for(Unit i:units){
            averagePosition.add(new DenseVector(i.getPosition()).scale(1d / units.size()));
        }
    }

    public DenseVector getAveragePosition() {
        return averagePosition;
    }

    public void setAveragePosition(DenseVector averagePosition) {
        this.averagePosition = averagePosition;
    }
}
