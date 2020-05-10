package newalgebra.network.nodes;

import newalgebra.cells.Cell;
import newalgebra.cells.Dimension;

import java.io.Serializable;

public class Flatten extends Node implements Serializable {

    public Flatten() {
    }

    public Flatten(Cell prev, int prevOut) {
        super(prev, prevOut);
    }

    public Flatten(Cell prev) {
        super(prev);
    }



    @Override
    public void calc() {
        for(int i = 0; i < getOutput().getDimension().size(); i++){
            getOutput().getValue().getData()[i] = getInput().getValue().getData()[i];
        }
    }

    @Override
    public void autoDiff() {
        for(int i = 0; i < getOutput().getDimension().size(); i++){
            getInput().getGradient().getData()[i] += getOutput().getGradient().getData()[i];
        }
    }

    @Override
    public void generateOutputDimension() {
        this.getOutput().setDimension(new Dimension(getInput().getDimension().size()));
    }
}
