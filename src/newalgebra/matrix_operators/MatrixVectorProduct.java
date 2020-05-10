package newalgebra.matrix_operators;

import newalgebra.cells.Cell;
import newalgebra.cells.Dimension;
import newalgebra.cells.Input;
import newalgebra.cells.Output;

import java.io.Serializable;


public class MatrixVectorProduct extends Cell<MatrixVectorProduct> implements Serializable {



    public MatrixVectorProduct() {
        super(2, 1);
    }

    public MatrixVectorProduct(Cell mat, int matOutput, Cell vec, int vecOutput) {
        super(new Object[]{mat, matOutput, 0, vec, vecOutput, 1}, 1);
    }


    @Override
    public void calc() {
        Input mat = getMat();
        Input vec = getVec();


        for(int i = 0; i < this.getOutput().getDimension().getHeight(); i++){
            double s = 0;
            for(int n = 0; n < mat.getDimension().getWidth(); n++){
                s += mat.getValue().get(i,n) * vec.getValue().get(n);
            }
            getOutput().getValue().set(s, i);
        }
    }

    @Override
    public void autoDiff() {
        Input mat = getMat();
        Input vec = getVec();

        for(int n = 0; n < mat.getDimension().getWidth(); n++){

            double vecDiff = 0;

            for(int i = 0; i < mat.getDimension().getHeight(); i++){
                mat.getGradient().add(getOutput().getGradient().get(i) * vec.getOutput().getValue().get(n), i,n);

                vecDiff += mat.getOutput().getValue().get(i,n) * getOutput().getGradient().get(i);
            }


            vec.getGradient().add(vecDiff, n);
        }
    }

    @Override
    public boolean inputCountOK() {
        return inputCount() == 2;
    }

    @Override
    public void generateOutputDimension() {
        Input mat = getMat();
        Input vec = getVec();

        boolean works = false;

        if (
                mat.getDimension().size() == 1 && vec.getDimension().size() == 1 ||
                mat.getDimension().dimCount() <= 2 &&
            mat.getDimension().dimCount() - vec.getDimension().dimCount() == 1 &&
            mat.getDimension().getWidth() == vec.getDimension().getHeight()) {

            works = true;
        }


        if(!works){
            throw new RuntimeException("matrix and vector required. " + mat.getDimension().toStringShort()
                                       + "  " + vec.getDimension().toStringShort());
        }

        this.getOutput().setDimension(new Dimension(mat.getDimension().getHeight()));
    }


//    @Override
//    protected void selfInit() {
//
//    }
//
//    @Override
//    public void calc() {
//
//        Node mat = getMatNode();
//        Node vec = getVecNode();
//
//        for(int i = 0; i < this.getOutputDimension().getHeight(); i++){
//            double s = 0;
//            for(int n = 0; n < mat.getOutputDimension().getWidth(); n++){
//                s += mat.getOutputValue().get(i,n) * vec.getOutputValue().get(n);
//            }
//            getOutputValue().set(s, i);
//        }
//    }
//
//    @Override
//    public void autoDiff() {
//
//        Node mat = getMatNode();
//        Node vec = getVecNode();
//
//        for(int n = 0; n < mat.getOutputDimension().getWidth(); n++){
//
//            double vecDiff = 0;
//
//            for(int i = 0; i < mat.getOutputDimension().getHeight(); i++){
//                mat.getOutputGradient().add(getOutputGradient().get(i) * vec.getOutputValue().get(n), i,n);
//
//                vecDiff += mat.getOutputValue().get(i,n) * getOutputGradient().get(i);
//            }
//
//            vec.getOutputGradient().add(vecDiff, n);
//        }
//    }
//
//    @Override
//    public MatrixVectorProduct copy() {
//        return new MatrixVectorProduct();
//    }

    public Input getMat(){
        return getInput(0);
    }

    public Input getVec(){
        return getInput(1);
    }

    public Output getOutput() {
        return getOutput(0);
    }
}
