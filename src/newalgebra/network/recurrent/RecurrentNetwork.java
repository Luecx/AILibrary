package newalgebra.network.recurrent;

import core.tensor.Tensor;
import newalgebra.cells.*;
import newalgebra.network.loss.Loss;
import newalgebra.network.loss.MSE;
import newalgebra.network.nodes.recurrent.LSTM;
import newalgebra.network.optimiser.Adam;
import newalgebra.network.optimiser.Optimiser;
import newalgebra.network.optimiser.SGD;
import newalgebra.network.weights.Weight;

import java.io.Serializable;
import java.util.*;

public class RecurrentNetwork implements Serializable {


    private LinkedList<RecurrentMemory> recurrentMemories = new LinkedList<>();

    private Cell recurrentCell;
    private Loss loss;
    private Optimiser optimiser;


    private RecurrentConnection[] recurrentConnections;

    private List<Variable>           nonRecurrentInputs;
    private List<Output>             nonRecurrentOutputs;

    private List<Variable> inputs= new ArrayList<>();
    private List<Output> weights    = new ArrayList<>();
    private List<Output> outputs;




    /**
     * takes a cell with CONNECTED inputs. It is very important that no unconnected inputs exist
     * @param recurrentCell
     * @param recurrency
     */
    public RecurrentNetwork(Cell<?> recurrentCell, Loss loss, Optimiser optimiser, RecurrentConnection... recurrency){


        this.recurrentCell = recurrentCell;
        this.recurrentConnections = recurrency;
        this.loss = loss;
                this.optimiser = optimiser;

        this.outputs = recurrentCell.getOutputs();
        this.nonRecurrentOutputs = listNonRecurrentOutputs();

        int outputIndex = 0;
        for(Cell<?> c:recurrentCell.listAllChildsDeep()){


            if(c instanceof Weight){
                weights.add(((Weight) c).getOutput());
            }
            else if(c instanceof Variable){
                inputs.add((Variable) c);
            }

            for(Output o:c.getUnconnectedOutputs()){
                if(nonRecurrentOutputs.contains(o)){
                    outputs.add(o);
                    Cell.connectCells(c, loss, c.getOutputs().indexOf(o), outputIndex);
                    outputIndex ++;
                }

            }
        }


        this.nonRecurrentInputs = listNonRecurrentInputs();

        if(this.recurrentCell.getOutput(0).getValue() == null){
            this.recurrentCell.build();
        }
        if(this.loss.getOutput(0).getValue() == null){
            this.loss.build();
        }

        this.optimiser.prepare(weights);

        System.out.println(recurrentCell);
        System.out.println(loss);
    }




    private void applyGradientsFromNextIteration(){
        for(RecurrentConnection con:recurrentConnections){
            outputs.get(con.getOutputIndex()).getGradient().reset(0);
            outputs.get(con.getOutputIndex()).getGradient().self_add(inputs.get(con.getInputIndex()).getGradient());
        }
    }




    public Tensor[] retrieveOutputs(){
        Tensor[] out = new Tensor[outputs.size()];
        for(int i = 0; i < out.length; i++){
            out[i] = new Tensor(outputs.get(i).getValue());
        }
        return out;
    }

    public Tensor[] retrieveInputs(){
        Tensor[] out = new Tensor[inputs.size()];
        for(int i = 0; i < out.length; i++){
            out[i] = new Tensor(inputs.get(i).getValue());
        }
        return out;
    }


    public Tensor[] retrieveNonRecurrentOutputs(){
        Tensor[] out = new Tensor[nonRecurrentOutputs.size()];
        for(int i = 0; i < out.length; i++){
            out[i] = new Tensor(nonRecurrentOutputs.get(i).getValue());
        }
        return out;
    }





    private void applyInputFromMemory(RecurrentMemory memory){
        for(int i = 0; i < inputs.size(); i++){
            inputs.get(i).getValue().reset(0);
            inputs.get(i).getValue().self_add(memory.getIn()[i]);
        }

    }

    private void applyInputFromInput(Tensor... in){
        for(int i = 0; i < nonRecurrentInputs.size(); i++){
            nonRecurrentInputs.get(i).getValue().reset(0);
            nonRecurrentInputs.get(i).getValue().self_add(in[i]);
        }
    }

    private void applyInputFromPreviousIteration(RecurrentMemory memory){
        for(RecurrentConnection con:recurrentConnections){
            inputs.get(con.getInputIndex()).getValue().reset(0);
            inputs.get(con.getInputIndex()).getValue().self_add(memory.getOut()[con.getOutputIndex()]);
        }
    }



    private void generateLoss(Tensor[] out){
        this.loss.resetGrad(true);
        this.loss.getOutput().getGradient().reset(1);
        for(int i = 0; i< out.length; i++){
            this.loss.setTarget(out[i], i);
        }
        this.loss.calc();
        this.loss.autoDiff();
    }


    public void storeInMemory(){
        recurrentMemories.addLast(new RecurrentMemory(retrieveInputs(), retrieveOutputs()));

        if(recurrentMemories.size() > 10){
            recurrentMemories.removeFirst();
        }
    }

    public Tensor[] calc(Tensor... in){
        if(!recurrentMemories.isEmpty()){
            applyInputFromPreviousIteration(recurrentMemories.getLast());
        }else{
            for(Variable i:inputs){
                i.getValue().reset(0);
            }
        }
        applyInputFromInput(in);
        recurrentCell.calc();

        return retrieveNonRecurrentOutputs();
    }


    /**
     * resets the memory
     */
    public void resetMemory(){
        this.recurrentMemories.clear();
    }

    /**
     * only works if only one input/output is required
     * @param in
     * @param out
     * @return
     */
    public double train(Tensor in, Tensor out){
        if(this.nonRecurrentInputs.size() != 1 || this.nonRecurrentOutputs.size() != 1) throw new RuntimeException();

        return this.train(new Tensor[]{in}, new Tensor[]{out});
    }


    /**
     * trains
     * @param in
     * @param out
     * @return
     */
    public double train(Tensor[] in, Tensor[] out){


        calc(in);
        storeInMemory();

        recurrentCell.resetGrad(true);
        generateLoss(out);
        recurrentCell.autoDiff();

        double loss = this.loss.getLoss();

        for(int i = recurrentMemories.size() - 1; i >= 0; i--){
            this.recurrentCell.resetGrad(false);
            this.applyGradientsFromNextIteration();
            this.applyInputFromMemory(recurrentMemories.get(i));
            this.recurrentCell.calc();
            this.recurrentCell.autoDiff();
        }

        this.optimiser.update();

        return loss;
    }


    private List<Output> listNonRecurrentOutputs(){
        List<Output> outputs = new ArrayList<>();
        for(int i = 0; i < this.outputs.size(); i++){
            if(!isRecurrentOutput(i)){
                outputs.add(this.outputs.get(i));
            }
        }
        return outputs;
    }

    private List<Variable> listNonRecurrentInputs(){
        List<Variable> inputs = new ArrayList<>();
        for(int i = 0; i < this.inputs.size(); i++){
            if(!isRecurrentInput(i)){
                inputs.add(this.inputs.get(i));
            }
        }
        return inputs;
    }

    private boolean isRecurrentInput(int inputIndex){
        for(RecurrentConnection connection:recurrentConnections){
            if(connection.getInputIndex() == inputIndex) return true;
        }
        return false;
    }

    private boolean isRecurrentOutput(int outputIndex){
        for(RecurrentConnection connection:recurrentConnections){
            if(connection.getOutputIndex() == outputIndex) return true;
        }
        return false;
    }





    public static Tensor[] copyOf(Tensor[] ar){
        Tensor[] c = new Tensor[ar.length];
        for(int i = 0; i < c.length; i++){
            c[i] = ar[i].copy();
        }
        return c;
    }

    public static void main(String[] args) {


        Variable x_in = new Variable(new Dimension(1));
        Variable h_0 = new Variable(new Dimension(2));
        Variable c_0 = new Variable(new Dimension(2));

        LSTM lstm = new LSTM();

        Cell.connectCells(x_in, lstm, 0);
        Cell.connectCells(h_0, lstm, 1);
        Cell.connectCells(c_0, lstm, 2);


        Cell total = new Cell(x_in, h_0, c_0, lstm);
        total.build();


            RecurrentNetwork recurrentNetwork = new RecurrentNetwork(total, new MSE(), new Adam(),
                                                                 new RecurrentConnection(0,2),
                                                                 new RecurrentConnection(2,1));




        for(int i = 0; i < 1000; i++){

            Tensor in = new Tensor(new double[]{0});
            Tensor out = new Tensor(new double[]{0.5});

            System.out.println(recurrentNetwork.train(in,out));

            recurrentNetwork.resetMemory();

             in = new Tensor(new double[]{1});
             out = new Tensor(new double[]{0.2});

            System.out.println(recurrentNetwork.train(in,out));

            recurrentNetwork.resetMemory();

//            System.out.println(Arrays.toString(recurrentNetwork.calc(in)));
////            System.out.println("-".repeat(100));
////            //System.out.println(recurrentNetwork.retrieveInputs()[1]);
////            System.out.println(recurrentNetwork.retrieveNonRecurrentOutputs()[0]);
////            System.out.println("-".repeat(100));
        }

        recurrentNetwork.resetMemory();


//        for(int i = 0; i < 10; i++){
//
//            Tensor in = new Tensor(new double[]{0});
//
//            System.out.println(Arrays.toString(recurrentNetwork.calc(in)));
//
//        }


        //System.out.println(total.toString(1));

//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
//
//        recurrentNetwork.resetMemory();
//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));

//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
        //System.out.println(total.toString(2));
//        System.out.println(Arrays.toString(recurrentNetwork.calc(new Tensor(new double[]{1}))));
//        System.out.println(total.toString(2));


    }
}
