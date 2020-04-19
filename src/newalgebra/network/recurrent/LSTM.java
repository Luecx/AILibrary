package newalgebra.network.recurrent;

import core.tensor.Tensor;
import newalgebra.*;
import newalgebra.element_operators.Add;
import newalgebra.element_operators.Mul;
import newalgebra.element_operators.functions.Pass;
import newalgebra.element_operators.functions.Sigmoid;
import newalgebra.element_operators.functions.TanH;
import newalgebra.matrix_operators.MatrixVectorProduct;
import newalgebra.network.Weight;
import newalgebra.network.rng.RNG;
import newalgebra.network.rng.Uniform;

import java.util.Arrays;
import java.util.List;

public class LSTM extends Cell {

    
    
    

    private Cell x_in;
    private Cell h_in;
    private Cell c_in;

    private Weight w_f;
    private Weight w_i;
    private Weight w_o;
    private Weight w_c;

    private Weight u_f;
    private Weight u_i;
    private Weight u_o;
    private Weight u_c;

    private Weight b_f;
    private Weight b_i;
    private Weight b_o;
    private Weight b_c;

    private MatrixVectorProduct mf1;
    private MatrixVectorProduct mi1;
    private MatrixVectorProduct mo1;
    private MatrixVectorProduct mc1;

    private MatrixVectorProduct mf2;
    private  MatrixVectorProduct mi2;
    private MatrixVectorProduct mo2;
    private MatrixVectorProduct mc2;

    private Add af;
    private Add ai;
    private Add ao;
    private Add ac;

    private Sigmoid ft;
    private Sigmoid it;
    private Sigmoid ot;
    private TanH c_t;

    private Mul mct1;
    private Mul mct2;

    private Add ct;
    private TanH ct_act;
    private Pass ct_out;
    private Mul ht;



    private Weight[] weights;

    private RNG rng = new Uniform(-1,1,123910239102L);
    public LSTM setWeightRNG(RNG rng){
        this.rng = rng;
        return this;
    }

    public LSTM() {
        super(3, 2);
        x_in = new Pass();
        h_in = new Pass();
        c_in = new Pass();
        w_f = new Weight();
        w_i = new Weight();
        w_o = new Weight();
        w_c = new Weight();
        u_f = new Weight();
        u_i = new Weight();
        u_o = new Weight();
        u_c = new Weight();
        b_f = new Weight();
        b_i = new Weight();
        b_o = new Weight();
        b_c = new Weight();
        mf1 = new MatrixVectorProduct(w_f, 0, x_in, 0);
        mi1 = new MatrixVectorProduct(w_i, 0, x_in, 0);
        mo1 = new MatrixVectorProduct(w_o, 0, x_in, 0);
        mc1 = new MatrixVectorProduct(w_c, 0, x_in, 0);
        mf2 = new MatrixVectorProduct(u_f, 0, h_in, 0);
        mi2 = new MatrixVectorProduct(u_i, 0, h_in, 0);
        mo2 = new MatrixVectorProduct(u_o, 0, h_in, 0);
        mc2 = new MatrixVectorProduct(u_c, 0, h_in, 0);
        af = new Add(mf1, 0, 0, mf2, 0, 1, b_f, 0, 2);
        ai = new Add(mi1, 0, 0, mi2, 0, 1, b_i, 0, 2);
        ao = new Add(mo1, 0, 0, mo2, 0, 1, b_o, 0, 2);
        ac = new Add(mc1, 0, 0, mc2, 0, 1, b_c, 0, 2);
        ft = new Sigmoid(af, 0);
        it = new Sigmoid(ai, 0);
        ot = new Sigmoid(ao, 0);
        c_t = new TanH(ac, 0);
        mct1 = new Mul(ft, 0, 0, c_in, 0, 1);
        mct2 = new Mul(it, 0, 0, c_t, 0, 1);
        ct = new Add(mct1, 0, 0, mct2, 0, 1);
        ct_act = new TanH(ct, 0);
        ct_out = new Pass(ct_act, 0);
        ht = new Mul(ot, 0, 0, ct_act, 0, 1);
        this.wrap(
                  x_in, h_in, c_in,
                  w_f, w_i, w_o, w_c,
                  u_f, u_i, u_o, u_c,
                  b_f, b_i, b_o, b_c,
                  mf1, mi1, mo1, mc1,
                  mf2, mi2, mo2, mc2,
                  af, ai, ao, ac,
                  ft, it, ot, c_t,
                  mct1, mct2,
                  ct, ct_act, ht,ct_out);

         weights = new Weight[]{
                 w_f, w_i, w_o, w_c,
                 u_f, u_i, u_o, u_c,
                 b_f, b_i, b_o, b_c
         };
    }



    @Override
    public void generateInternalVariableDimension() {
        if (getC_in().getDimension().getHeight() != getH_in().getDimension().getHeight()) throw new RuntimeException();
        int d = getX_in().getDimension().getHeight();
        int h = getC_in().getDimension().getHeight();
        w_f.setDimension(new Dimension(h, d));
        w_i.setDimension(new Dimension(h, d));
        w_o.setDimension(new Dimension(h, d));
        w_c.setDimension(new Dimension(h, d));
        u_f.setDimension(new Dimension(h, h));
        u_i.setDimension(new Dimension(h, h));
        u_o.setDimension(new Dimension(h, h));
        u_c.setDimension(new Dimension(h, h));
        b_f.setDimension(new Dimension(h));
        b_i.setDimension(new Dimension(h));
        b_o.setDimension(new Dimension(h));
        b_c.setDimension(new Dimension(h));
    }

    @Override
    public void generateOutputDimension() {
        if (getX_in().getDimension().dimCount() != 1) throw new RuntimeException();
        if (getC_in().getDimension().dimCount() != 1) throw new RuntimeException();
        if (getH_in().getDimension().dimCount() != 1) throw new RuntimeException();
        super.generateOutputDimension();
    }

    @Override
    public void initArrays() {
        super.initArrays();

        this.rng.apply(w_f.getValue());
        this.rng.apply(w_i.getValue());
        this.rng.apply(w_o.getValue());
        this.rng.apply(w_c.getValue());
        this.rng.apply(u_f.getValue());
        this.rng.apply(u_i.getValue());
        this.rng.apply(u_o.getValue());
        this.rng.apply(u_c.getValue());
        this.rng.apply(b_f.getValue());
        this.rng.apply(b_i.getValue());
        this.rng.apply(b_o.getValue());
        this.rng.apply(b_c.getValue());

    }

    public Weight[] getWeights(){
        return weights;
    }
    
    public Input getC_in() {
        return getInput(2);
    }

    public Input getH_in() {
        return getInput(1);
    }

    public Input getX_in() {
        return getInput(0);
    }

    public Output getH_out() {
        return getOutput(0);
    }

    public Output getC_out() {
        return getOutput(1);
    }


    public static void main(String[] args) {


        Variable x_in = new Variable(new Dimension(1));
        Variable h_0 = new Variable(new Dimension(1));
        Variable c_0 = new Variable(new Dimension(1));

        LSTM lstm = new LSTM();

        Cell.connectCells(x_in, lstm, 0);
        Cell.connectCells(h_0, lstm, 1);
        Cell.connectCells(c_0, lstm, 2);


        Cell total = new Cell(x_in, h_0, c_0, lstm);
        total.build();


        x_in.setValue(new Tensor(new double[]{1}));
        h_0.setValue(new Tensor(new double[]{1}));
        c_0.setValue(new Tensor(new double[]{1}));

        total.calc();
        total.resetGrad();

        lstm.getH_out().getGradient().reset(0);
        lstm.getH_out().getGradient().self_add(lstm.getH_out().getValue());
        total.autoDiff();




        System.out.println(total.toString(3));



//        Tensor out = lstm.getH_out().getValue();
//        System.out.println(out);
    }
}
