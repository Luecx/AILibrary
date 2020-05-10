package newalgebra.network.nodes;

import newalgebra.cells.Cell;
import newalgebra.cells.Dimension;
import newalgebra.element_operators.Add;
import newalgebra.element_operators.functions.Pass;
import newalgebra.matrix_operators.MatrixVectorProduct;
import newalgebra.network.weights.Weight;
import newalgebra.network.weights.rng.RNG;
import newalgebra.network.weights.rng.Uniform;

import java.io.Serializable;

public class Dense extends Node<Dense> implements Serializable {

    private Weight weights;
    private Weight bias;

    private Pass x;

    private MatrixVectorProduct matmul;
    private Add add;


    private RNG rng = null;
    private int nodeCount;

    public Dense(int nodeCount, Weight weights, Weight bias) {
        super();

        this.nodeCount = nodeCount;

        this.weights = weights;
        this.bias = bias;

        this.x = new Pass();
        this.matmul = new MatrixVectorProduct(weights, 0,x,0);
        this.add = new Add(matmul, 0,0,bias,0,1);

        this.wrap(this.x, this.weights, this.bias, this.matmul, this.add);
    }

    public Dense(int nodeCount) {
        super();

        this.nodeCount = nodeCount;

        this.weights = new Weight();
        this.bias = new Weight();

        this.x = new Pass();
        this.matmul = new MatrixVectorProduct(weights, 0,x,0);
        this.add = new Add(matmul, 0,0,bias,0,1);

        this.wrap(this.x, this.weights, this.bias, this.matmul, this.add);
    }

    @Override
    public void generateInternalVariableDimension() {
        this.weights.setDimension(new Dimension(nodeCount, getInputHeight()));
        this.bias.setDimension(new Dimension(nodeCount));
    }

    @Override
    public void initArrays() {
        if(rng == null){
            rng = new Uniform(-1d / getInputHeight(), 1d/getInputHeight(),123910239L);
        }
        rng.apply(this.weights.getValue());
        rng.apply(this.bias.getValue());
    }

    @Override
    public Dense copy(boolean keepVariables) {
        if(keepVariables){
            Dense d = new Dense(nodeCount, this.weights, this.bias);
            if(this.hasBeenBuilt()){
                d.setBuilt(true);
            }
            return d;
        }else{
            return new Dense(nodeCount);
        }
    }




    public Dense setWeightRNG(RNG rng){
        this.rng = rng;
        return this;
    }

    public int getInputHeight() {
        return getInput(0).getDimension().getHeight();
    }

    public Weight getWeights(){
        return weights;
    }

    public Weight getBias(){
        return bias;
    }

    public static void main(String[] args) {
        Dense d1 = new Dense(5);
        //d1.build();
        System.out.println(d1);
    }

}
