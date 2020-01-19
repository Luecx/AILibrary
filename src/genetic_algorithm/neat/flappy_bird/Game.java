package genetic_algorithm.neat.flappy_bird;


import genetic_algorithm.neat.neat.Neat;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Game {


    private ArrayList<Bird> birds = new ArrayList<>();
    private LinkedList<Pipe> pipes = new LinkedList<>();

    private Neat algorithm;
    private int birds_alive;
    private double distance_travelled;

    private double bird_offset = 0.1;
    private double bird_acceleration = 0.3;
    private double bird_gravity = -1.2;

    private double pipe_speed = 0.22;
    private double pipe_distance = 0.3;
    private double pipe_next = 0;
    private double pipe_width = 0.05;
    private double pipe_size = 0.22;

    public Game(int size) {

        this.algorithm = new Neat(4, 1, size);
        for(int i = 0; i < size; i++){
            this.birds.add(new Bird(this.algorithm.getClient(i)));
        }
        this.restart();
    }

    public double[] getInput(Bird b){
        Pipe next = null;
        for(Pipe p:pipes){
            if(p.isNext(bird_offset)){
                next = p;
                break;
            }
        }
        if(next == null){
            return new double[]{b.getY(), 0,0,0};
        }else{
            return new double[]{b.getY(), next.getX() - bird_offset,next.getBot(),next.getTop()};
        }
    }

    public void restart(){
        pipes.clear();
        birds_alive = birds.size();
        distance_travelled = 0;
        pipe_next = 0;
        for(Bird b:birds){
            b.resurrect();
        }


    }

    public void process(double time){

        if(pipe_next <= 0){
            pipe_next = pipe_distance;
            pipes.add(new Pipe(pipe_width, pipe_size));
        }

        while(pipes.size() > 0 && pipes.get(0).isVisible() == false){
            pipes.remove(0);
        }

        for(Pipe p:pipes){
            p.move(time, pipe_speed);
            for(int bird = 0; bird < birds.size(); bird++){
                if(p.collision(birds.get(bird), bird_offset) && birds.get(bird).isAlive()){
                    birds.get(bird).kill(distance_travelled);
                    birds_alive--;
                }
            }
        }

        if(Math.random() < 0.1){
            System.out.println(distance_travelled);
        }


        for(Bird bird:birds){
            double bird_acc = bird.calculate(getInput(bird))[0];
            bird.process(time, bird_acc > 0.5 ? bird_acceleration: 0,bird_gravity);
            if((bird.getY() == 0 || bird.getY() == 1) && bird.isAlive()){
                bird.kill(distance_travelled);
                birds_alive--;
            }
        }

        pipe_next -= pipe_speed * time;
        distance_travelled += pipe_speed * time;

        if(birds_alive <= 0){
            double max = Collections.max(birds, new Comparator<Bird>() {
                @Override
                public int compare(Bird o1, Bird o2) {
                    if(o1.getScore() > o2.getScore()) return 1;
                    if(o2.getScore() > o1.getScore()) return -1;
                    return 0;
                }
            }).getScore();
            System.out.println("max: " + max);
            algorithm.evolve();
            restart();
        }

    }

    public ArrayList<Bird> getBirds() {
        return birds;
    }

    public void draw(Graphics2D graphics2D, int width, int height){
        graphics2D.fillRect(0,0,width,height);
        for(Pipe p:pipes){
            p.draw(graphics2D, width, height);
        }
        for(Bird b:birds){
            if(b.isAlive())
            b.draw(graphics2D, bird_offset, width, height);
        }
    }
}
