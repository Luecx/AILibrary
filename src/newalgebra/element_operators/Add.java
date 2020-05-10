package newalgebra.element_operators;

import newalgebra.cells.Output;

import java.io.Serializable;

public class Add extends ElementOperator<Add> implements Serializable {


    public Add() {
        this(0);
    }

    public Add(int inputs) {
        super(inputs);
    }

    public Add(Object... args) {
        super(args);
    }

    @Override
    public boolean canAddInputs() {
        return true;
    }

    @Override
    public void calc() {

        Output o = this.getOutput();

        o.getValue().reset(0);

        for(int i = 0; i < inputCount(); i++){
            o.getValue().self_add(getInput(i).getValue());
        }

    }

    @Override
    public void autoDiff() {
        for(int i = 0; i < inputCount(); i++){
            getInput(i).getGradient().self_add(getOutput().getGradient());
        }
    }
}
