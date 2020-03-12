package boids_model.visual;

import boids_model.BoidSwarm;
import boids_model.BoidUnit;
import boids_model.Swarm;
import core.vector.Vector;
import core.vector.Vector2d;
import visuals.Panel;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    private Swarm swarm;

    private SwarmPanel panel;
    private final Frame frame = this;
    private Thread updater = new Thread(){

        @Override
        public void run() {
            super.run();
            try {
                while(!this.isInterrupted()){
                    Thread.sleep(10);

                    Vector leaderPosition = swarm.getUnits().get(0).getPosition().copy();

                    frame.swarm.update(100/10000d);
                    frame.repaint();
                    Vector2d target = panel.getMouseLocation();

                    frame.swarm.getUnits().get(0).setPosition(leaderPosition);
                    frame.swarm.getUnits().get(0).setDirection(leaderPosition.sub(target).negate());
                    if(frame.swarm.getUnits().get(0).getDirection().length() > 0){
                        frame.swarm.getUnits().get(0).getDirection().set_self_Length(100);
                        frame.swarm.getUnits().get(0).updatePosition(100/1000d);
                    }
                }

                this.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.interrupt();
            }
        }
    };

    public Frame(Swarm swarm) {
        this.swarm = swarm;
        this.panel = new SwarmPanel();
        this.panel.setCenter(new Vector2d(0,0));
        this.panel.setScale(new Vector2d(5000,5000));
        this.panel.setSwarm(swarm);

        this.setSize(720,480);

        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);

        this.updater.start();

        this.setVisible(true);
    }

    public static void main(String[] args) {
        BoidSwarm boidSwarm = new BoidSwarm();
        for(int i = 0; i < 1000; i++){
            boidSwarm.getUnits().add(new BoidUnit(new Vector2d(Math.random() * 1000, Math.random() * 1000)));
        }
        new Frame(boidSwarm);
        //new JFrame().setVisible(true);
    }
}
