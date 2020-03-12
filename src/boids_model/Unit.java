package boids_model;


import core.vector.DenseVector;
import core.vector.Vector;
import core.vector.Vector2d;
import core.vector.Vector3d;

public abstract class Unit {

    private Vector position = new Vector2d();
    private Vector direction = new Vector2d();

    public Unit(Vector position) {
        this.position = position;
    }

    public Unit(double... position) {
        this.position = new DenseVector(position);
    }

    public Unit(int dimensions){
        this.position = new DenseVector(dimensions);
    }

    public void updatePosition(double t){
        position.self_add(this.direction.scale(t));
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public abstract Vector calculateDirection(Swarm swarm);
}
