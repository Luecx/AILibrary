package newalgebra.element_operators;

import core.tensor.Tensor;
import neuralnetwork.nodes.Node;
import newalgebra.Cell;
import newalgebra.Dimension;
import newalgebra.Input;
import newalgebra.Output;

/**
 * A node that requires all inputs to have the same size and each operation is done element wise.
 * This allows for the usage of easy derivatives.
 * No childs should exist for this type of cell
 */
public class ElementOperator extends Cell {


    /**
     * derivative of output with respect to each input
     */
    protected Tensor[] functionDerivative;

    public ElementOperator(int inputs) {
        super(inputs, 1);
    }

    public ElementOperator(Object... args) {
        super(args,1);
    }

    public void generateOutputDimension() {
        if(this.outputCount() > 1){
            throw new RuntimeException("Elementwise operations have singular outputs");
        }

        Dimension d0 = getInput(0).getDimension();
        for(Input i:getInputs()){
            if(!i.getDimension().equals(d0)){
                throw new RuntimeException("All dimensions must be equal");
            }
        }

        getOutput(0).setDimension(new Dimension(d0));
    }

    @Override
    public void initArrays() {
        functionDerivative = new Tensor[inputCount()];
        for(int i = 0; i< inputCount(); i++){
            functionDerivative[i] = getOutput().getDimension().emptyTensor();
        }
    }

    @Override
    public void autoDiff() {
        for(int i = 0; i < inputCount(); i++){
            for(int v = 0; v < getOutput().getDimension().size(); v++){
                getInput(i).getGradient().getData()[v] += getOutput().getGradient().getData()[v] * getFunctionDerivative()[i].getData()[v];
            }
        }
    }

    public Output getOutput(){
        return getOutput(0);
    }

    public Tensor[] getFunctionDerivative() {
        return functionDerivative;
    }

    public Tensor getFunctionDerivative(int input){
        return functionDerivative[input];
    }
}
