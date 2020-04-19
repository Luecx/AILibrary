package algebra.nodes;

import algebra.nodes.basic.Add;
import core.exceptions.NotMatchingSlotsException;
import core.tensor.Tensor;
import neuralnetwork.builder.BuildException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public abstract class Cell<T extends Cell<T>> {


    private Node inputNodes[];
    private Node outputNodes[];






    public void resetGrad(){
        outputGradient.reset(0);
    }

    protected abstract Dimension selfCalcOutputDim() throws BuildException;
    protected abstract void selfInit();
    public abstract void calc();
    public abstract void autoDiff();
    public abstract T copy();

    private boolean connectNextNode(Cell n){
        for(Cell k:next_nodes){
            if(k == n){
                return false;
            }
        }
        if(next_nodes.size() >= maxNextNodes.nodes){
            return false;
        }
        next_nodes.add(n);
        return true;
    }
    private boolean connectPreviousNode(Cell n){
        for(Cell k:previous_nodes){
            if(k == n){
                return false;
            }
        }
        if(previous_nodes.size() >= maxPrevNodes.nodes){
            return false;
        }
        previous_nodes.add(n);
        return true;
    }
    private boolean connectPreviousNode(Cell n, int index){
        for(Cell k:previous_nodes){
            if(k == n){
                return false;
            }
        }
        if(previous_nodes.size() >= maxPrevNodes.nodes){
            return false;
        }
        previous_nodes.add(index, n);
        return true;
    }
    public boolean addPreviousNode(Cell n) {
        if(!previous_nodes.contains(n)){
            if(connectPreviousNode(n)){
                if(!n.connectNextNode(this)){
                    removePreviousNode(n);
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }
    public boolean addPreviousNode(Cell n, int index) {
        if(!previous_nodes.contains(n)){
            if(connectPreviousNode(n,index)){
                if(!n.connectNextNode(this)){
                    removePreviousNode(n);
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }

    public boolean addNextNode(Cell n){
        return n.addPreviousNode(this);
    }

    public void removePreviousNode(Cell n){
        if(previous_nodes.contains(n)){
            n.next_nodes.remove(this);
            previous_nodes.remove(n);
        }
    }
    public void removeNextNode(Cell n){
        n.removePreviousNode(this);
    }

    public boolean replacePreviousNode(Cell old, Cell newNode){
        int index = previous_nodes.indexOf(old);
        if(index == -1) return false;

        removePreviousNode(old);

        return addPreviousNode(newNode, index);
    }
    public boolean replaceWith(Cell newNode){
        boolean works = true;
        ArrayList<Cell> nextNodes = new ArrayList<>();
        for(Cell next:getNextNodes()){
            nextNodes.add(next);
        }
        for(Cell nd:nextNodes){
            works = works && nd.replacePreviousNode(this, newNode);
        }
        return works;
    }

    public boolean hasNextNode(){
        return next_nodes.size() > 0;
    }
    public boolean hasPreviousNode(){
        return previous_nodes.size() > 0;
    }
    public Cell getPreviousNode(){return previous_nodes.get(0);}
    public Cell getPreviousNode(int index){return  previous_nodes.get(index);}
    public ArrayList<Cell> getPreviousNodes(){return previous_nodes;}
    public ArrayList<Cell> getPreviousNodesCopy(){
        ArrayList<Cell> nodes = new ArrayList<>();
        for(Cell n:previous_nodes){
            nodes.add(n.copy());
        }
        return nodes;
    }
    public ArrayList<Cell> getNextNodes(){return next_nodes;}
    public ArrayList<Cell> getNextNodesCopy(){
        ArrayList<Cell> nodes = new ArrayList<>();
        for(Cell n:next_nodes){
            nodes.add(n.copy());
        }
        return nodes;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        if(this.identifier == null && identifier != null)
            this.identifier = identifier;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell node = (Cell) o;
        return
                identifier.equals(node.identifier) &&
                outputDimension.equals(((Cell) o).outputDimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputDimension, identifier);
    }





}
