package newalgebra;

import core.tensor.Tensor;
import core.tensor.Tensor3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Output {

    private Tensor value;
    private Tensor gradient;

    private Dimension dimension;

    private List<Input> connectedInputs = new ArrayList<>();

    public Output() {
    }

    public Output(Dimension dimension){
        this.dimension = dimension;
    }

    public void initArrays(){
        this.value = dimension.emptyTensor();
        this.gradient = dimension.emptyTensor();
    }

    public Tensor getValue() {
        return value;
    }

    public void setValue(final Tensor value) {
        if(!Dimension.fromTensor(value).equals(this.getDimension())){
            throw new RuntimeException("Wrong input dimension: " + this.getDimension() + " =/= " + Dimension.fromTensor(value));
        }
        this.value = value;
    }

    public Tensor getGradient() {
        return gradient;
    }

    public void setDimension(Dimension dimension) {
        if(value != null){
            throw new RuntimeException("Cannot change dimension when already initialised");
        }
        this.dimension = dimension;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public List<Input> getConnectedInputs() {
        return connectedInputs;
    }

    public void addConnectedInput(Input input){
        if(isConnectedTo(input)) throw new RuntimeException("Already connected to input");

        if(input.hasConnectedOutput()) input.removeConnectedOutput();

        input.setConnectedOutput(this);
        connectedInputs.add(input);
    }

    public void removeConnectedInput(Input input){
        this.connectedInputs.remove(input);
    }

    public boolean isConnectedTo(Input input){
        return connectedInputs.contains(input);
    }

}
