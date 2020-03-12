package boids_model.visual;

import boids_model.Swarm;
import boids_model.Unit;
import core.vector.Vector2d;
import visuals.Panel;

import java.awt.*;

public class SwarmPanel extends Panel {

    private Swarm swarm;

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0,0,this.getWidth(), this.getHeight());
        draw_grid((Graphics2D)g);
        for(Unit u:swarm.getUnits()){
            Vector2d screen = toScreenSpace(new Vector2d(u.getPosition()));
            paintUnit((Graphics2D)g, screen);
        }
    }

    public Vector2d getMouseLocation() {
        try{
            if(getMousePosition() != null)
                return toWorldSpace(new Vector2d(getMousePosition().getX(), getMousePosition().getY()));
        }catch (Exception ignored){ }


        return new Vector2d(0,0);
    }

    private void paintUnit(Graphics2D g2d, Vector2d screenSpace){
        g2d.fillOval((int)screenSpace.getX()-5, (int)screenSpace.getY()-5, 10,10);
    }

    public Swarm getSwarm() {
        return swarm;
    }

    public void setSwarm(Swarm swarm) {
        this.swarm = swarm;
    }
}
