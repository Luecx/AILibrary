package algebra.nodes.basic;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import neuralnetwork.builder.BuildException;

public class MatrixVectorProduct extends Node<MatrixVectorProduct> {

    public MatrixVectorProduct(Node mat, Node x){
        super(NodeCount.TWO, NodeCount.UNLIMITED);
        this.addPreviousNode(mat);
        this.addPreviousNode(x);
    }


    public MatrixVectorProduct() {
        super(NodeCount.TWO, NodeCount.UNLIMITED);
    }

    @Override
    protected Dimension selfCalcOutputDim() throws BuildException {
        Node mat = getPreviousNode(0);
        Node vec = getPreviousNode(1);


        boolean works = false;

        if (mat.getOutputDimension().dimCount() <= 2 &&
                mat.getOutputDimension().dimCount() - vec.getOutputDimension().dimCount() == 1 &&
                mat.getOutputDimension().getWidth() == vec.getOutputDimension().getHeight()) {

            works = true;
        }

        if(!works)throw new BuildException(this, "matrix and vector required");

        return new Dimension(mat.getOutputDimension().getHeight());
    }

    @Override
    protected void selfInit() {

    }

    @Override
    public void calc() {

        Node mat = getMatNode();
        Node vec = getVecNode();

        for(int i = 0; i < this.getOutputDimension().getHeight(); i++){
            double s = 0;
            for(int n = 0; n < mat.getOutputDimension().getWidth(); n++){
                s += mat.getOutputValue().get(i,n) * vec.getOutputValue().get(n);
            }
            getOutputValue().set(s, i);
        }
    }

    @Override
    public void autoDiff() {

        Node mat = getMatNode();
        Node vec = getVecNode();

        for(int n = 0; n < mat.getOutputDimension().getWidth(); n++){

            double vecDiff = 0;

            for(int i = 0; i < mat.getOutputDimension().getHeight(); i++){
                mat.getOutputGradient().add(getOutputGradient().get(i) * vec.getOutputValue().get(n), i,n);

                vecDiff += mat.getOutputValue().get(i,n) * getOutputGradient().get(i);
            }

            vec.getOutputGradient().add(vecDiff, n);
        }
    }

    @Override
    public MatrixVectorProduct copy() {
        return new MatrixVectorProduct();
    }

    public Node getMatNode(){
        return getPreviousNode(0);
    }

    public Node getVecNode(){
        return getPreviousNode(1);
    }
}
