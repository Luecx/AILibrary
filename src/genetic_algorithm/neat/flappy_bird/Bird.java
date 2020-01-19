package genetic_algorithm.neat.flappy_bird;


import genetic_algorithm.neat.neat.Client;

import java.awt.*;

public class Bird {


    private double y;
    private double acceleration;

    private boolean alive;
    private Client client;

    public Bird(Client client) {
        this.client = client;
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
        this.client.setScore(score);
    }

    public void resurrect(){
        this.alive = true;
        this.client.setScore(0);
        this.acceleration = 0;
        this.y = 0.5;
    }

    public void draw(Graphics2D graphics2D, double offset, int width, int height){
        int size = 20;
        graphics2D.setColor(new Color(200,200,200,100));
        graphics2D.fillOval((int)(offset * width - size), (int)((1-y) * height - size), size * 2, size * 2);
    }


    public double[] calculate(double... in){
        return this.client.calculate(in);
    }

    public double getScore(){
        return this.client.getScore();
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
