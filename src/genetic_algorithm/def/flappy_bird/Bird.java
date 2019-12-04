package luecx.ai.genetic_algorithm.def.flappy_bird;


import luecx.ai.genetic_algorithm.def.GeneticClient;
import old_model.Network;
import old_model.NetworkBuilder;
import old_model.layers.DenseLayer;

import java.awt.*;

public class Bird implements GeneticClient {



    private double y;
    private double acceleration;

    private double score;
    private boolean alive;

    private Network network;

    public Bird() {
        NetworkBuilder network_builder = new NetworkBuilder(1,1,4);
        network_builder.addLayer(new DenseLayer(4));
        network_builder.addLayer(new DenseLayer(1));
        this.network = network_builder.buildNetwork();
    }

    public void process(double time, double acceleration, double gravity){
        if(acceleration == 0){
            this.acceleration += gravity * time;
        }else{
            this.acceleration = acceleration;
        }
        this.y += this.acceleration * time;
        if(this.y > 1) this.y = 1;
        if(this.y < 0) this.y = 0;
    }

    public void kill(double score){
        this.alive = false;
        this.score = score;
    }

    public void resurrect(){
        this.alive = true;
        this.score = 0;
        this.y = 0.5;
        this.acceleration = 0;
    }

    public void draw(Graphics2D graphics2D, double offset, int width, int height){
        int size = 20;
        graphics2D.setColor(new Color(200,200,200,100));
        graphics2D.fillOval((int)(offset * width - size), (int)((1-y) * height - size), size * 2, size * 2);
    }



    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
}
