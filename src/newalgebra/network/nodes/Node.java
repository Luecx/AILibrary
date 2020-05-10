package newalgebra.network.nodes;

import newalgebra.cells.Cell;
import newalgebra.cells.Input;
import newalgebra.cells.Output;

import java.io.Serializable;

/**
 * A node is a cell that maps one input to one output.
 */
public class Node<T extends Node<T>> extends Cell<T> implements Serializable {

    public Node() {
        super(1, 1);
    }

    public Node(Cell prev, int prevOut) {
        super(new Object[]{prev,prevOut,0},1);
    }

    public Node(Cell prev) {
        super(new Object[]{prev,0,0},1);
    }

    @Override
    public final boolean inputCountOK() {
        return this.inputCount() == 1;
    }


    public Input getInput(){
        return getInput(0);
    }

    public Output getOutput() {
        return getOutput(0);
    }

    public int getOutputDepth() {
        return this.getOutput().getDimension().getDepth();
    }


    public int getOutputWidth() {
        return this.getOutput().getDimension().getWidth();
    }


    public int getOutputHeight() {
        return this.getOutput().getDimension().getHeight();
    }


    public int getInputDepth() {
        return this.getInput().getDimension().getDepth();
    }


    public int getInputWidth() {
        return this.getInput().getDimension().getWidth();
    }


    public int getInputHeight() {
        return this.getInput().getDimension().getHeight();
    }



}

