package newalgebra.network.loss;

import core.tensor.Tensor;
import newalgebra.Cell;
import newalgebra.Dimension;
import newalgebra.Output;

public abstract class Loss extends Cell {


    public abstract void setTarget(Tensor target, int index);

    public Loss(int inputs) {
        super(inputs, 1);
    }

    @Override
    public void generateOutputDimension() {
        this.getOutput(0).setDimension(new Dimension(1));
    }

    public double getLoss(){
        return getOutput(0).getValue().getData()[0];
    }
}
