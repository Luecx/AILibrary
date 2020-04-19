package newalgebra.network.feedforward.cells;

import newalgebra.Cell;
import newalgebra.Dimension;
import newalgebra.element_operators.Add;
import newalgebra.element_operators.functions.Pass;
import newalgebra.matrix_operators.MatrixVectorProduct;
import newalgebra.network.Weight;
import newalgebra.network.recurrent.LSTM;
import newalgebra.network.rng.RNG;
import newalgebra.network.rng.Uniform;

public class Dense extends Cell {

    private Weight weights;
    private Weight bias;

    private Pass x;

    private MatrixVectorProduct matmul;
    private Add add;


    private RNG rng = null;
    private int nodeCount;

    public Dense(int nodeCount) {
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
