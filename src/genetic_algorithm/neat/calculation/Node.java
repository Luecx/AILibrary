package luecx.ai.genetic_algorithm.neat.calculation;

import java.util.ArrayList;

public class Node implements Comparable<Node>{

    private ArrayList<Connection> connections = new ArrayList<>();
    private double output;
    private boolean finished;

    private double draw_x;

    public Node(double draw_x) {
        this.draw_x = draw_x;
    }

    public boolean calculate(){
        double s = 0;
        for(Connection c:connections){
            if(c.isActivated()){
                if(!c.getInput_node().isFinished()){
                    return false;
                }
                s+=c.getWeight() * c.getInput_node().getOutput();
            }
        }

        this.output = activation_function(s);
        this.finished = true;
        return true;
    }

    private double activation_function(double x){
        return 1d / (1 + Math.exp(-x));
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Connection> connections) {
        this.connections = connections;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public double getDraw_x() {
        return draw_x;
    }

    public void setDraw_x(double draw_x) {
        this.draw_x = draw_x;
    }

    public boolean isConnectedTo(Node n){
        for(Connection c:connections){
            if(c.getInput_node() == n) return true;
        }for(Connection c:n.connections){
            if(c.getInput_node() == this) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Node{" +
                ", output=" + output +
                ", finished=" + finished +
                ", draw_x=" + draw_x +
                '}';
    }

    @Override
    public int compareTo(Node o) {
        if(o.draw_x > this.draw_x) return 1;
        if(o.draw_x < this.draw_x) return -1;
        return 0;
    }
}
