package newalgebra;

import core.tensor.Tensor;

public class Variable extends Cell {

    public Variable(Dimension dimension) {
        super(0, 1);
        this.getOutput(0).setDimension(new Dimension(dimension));
    }

    public Variable() {
        super(0, 1);
    }

    public Dimension getDimension(){
        return this.getOutput(0).getDimension();
    }

    public void setDimension(Dimension dimension) {
        if(getDimension() != null) throw new RuntimeException("Dimension cannot be changed");
        this.getOutput(0).setDimension(dimension);
    }

    public Tensor getValue() {
        return this.getOutput(0).getValue();
    }

    public Output getOutput() {return this.getOutput(0);}

    public Tensor getGradient() {
        return this.getOutput(0).getGradient();
    }

    public void setValue(Tensor tensor) {
        this.getOutput(0).setValue(tensor);
    }

}
