package newalgebra.network.loss;

import core.tensor.Tensor;
import newalgebra.builder.Logger;
import newalgebra.cells.Cell;
import newalgebra.cells.Dimension;
import newalgebra.cells.Output;

import java.io.Serializable;

public abstract class Loss extends Cell implements Serializable {


    protected abstract void addTarget(Tensor target, int index);

    public void setTarget(Tensor target, int index){
        if(!hasInput(index)){
            Logger.getLogger().addWarning("Unsafe input at: " +this.getClass().getSimpleName());
        }
        if(!Dimension.fromTensor(target).equals(getInput(index).getDimension())){
            throw new RuntimeException("Cannot set this target value. Dimensions do not match: " + Dimension.fromTensor(target) + " =/= " + getInput(index).getDimension());
        }
        addTarget(target, index);
    }

    public void setTarget(Tensor target){
        this.setTarget(target);
    }

    public Loss(int inputs) {
        super(inputs, 1);
    }

    @Override
    public void generateOutputDimension() {
        this.getOutput().setDimension(new Dimension(1));
    }

    public Output getOutput(){
        return getOutput(0);
    }

    public double getLoss(){
        return getOutput().getValue().getData()[0];
    }
}
