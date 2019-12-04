package boids_model;


import core.vector.DenseVector;
import core.vector.Vector;

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

    @Override
    public Vector calculateDirection(Swarm swarm) {
        if(swarm instanceof BoidSwarm){
            return (Vector) new DenseVector(this.getPosition()).sub(((BoidSwarm) swarm).getAveragePosition()).negate();
        }
        return new DenseVector();

    }
}
