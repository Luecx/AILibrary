package newalgebra.network;

import core.tensor.Tensor;
import newalgebra.cells.Cell;
import newalgebra.cells.Output;
import newalgebra.cells.Variable;
import newalgebra.network.weights.Weight;
import newalgebra.network.loss.Loss;
import newalgebra.network.optimiser.Optimiser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Network implements Serializable {

    private List<Variable> variables = new ArrayList<>();

    private List<Output> outputs = new ArrayList<>();
    private List<Output> weights = new ArrayList<>();

    private Cell cell;
    private Loss loss;

    private Optimiser optimiser;

    public Network(Cell function, Loss loss, Optimiser optimiser){

        this.cell = function;
        this.loss = loss;
        this.optimiser = optimiser;

        if(this.loss.inputCount() != function.outputCount()) throw new RuntimeException();
        if(this.loss.outputCount() != 1) throw new RuntimeException();


        List<Cell> allCells = cell.listAllChildsDeep();

        if(!Cell.isEnclosed(allCells.toArray(new Cell[0]))){
            throw new RuntimeException();
        }

        int outputIndex = 0;
        for(Cell<?> c:allCells){
            if(c instanceof Variable){
                if(c instanceof Weight){
                    weights.add(((Weight) c).getOutput());
                }else{
                    variables.add((Variable) c);
                }
            }
            for(Output o:c.getUnconnectedOutputs()){
                outputs.add(o);
                Cell.connectCells(c, loss, c.getOutputs().indexOf(o), outputIndex);
                outputIndex ++;
            }
        }

        if(this.cell.getOutput(0).getValue() == null){
            this.cell.build();
        }
        if(this.loss.getOutput(0).getValue() == null){
            this.loss.build();
        }

        optimiser.prepare(weights);
    }


    public void setInputs(Tensor... in){
        for(int i = 0; i < Math.min(in.length, variables.size()); i++){
            variables.get(i).setValue(in[i]);
        }
    }

    public Tensor[] getOutputs(){
        Tensor[] out = new Tensor[outputs.size()];
        for(int i = 0; i < out.length; i++){
            out[i] = outputs.get(i).getValue().copy();
        }
        return out;
    }

    public void setTargets(Tensor... targets) {
        for(int i = 0; i < targets.length; i++){
            loss.setTarget(targets[i], i);
        }
    }

    public void backprop(){
        loss.resetGrad(true);
        cell.resetGrad(true);

        loss.getOutput(0).getGradient().getData()[0] = 1;
        loss.autoDiff();
        cell.autoDiff();
    }



    public Tensor[] calc(Tensor... in){
        setInputs(in);
        cell.calc();
        return getOutputs();
    }

    public double train(Tensor[] in, Tensor[] out){

        calc(in);
        setTargets(out);

        loss.calc();
        backprop();

        optimiser.update();

        return loss.getLoss();
    }

    public double train(Tensor in, Tensor out){

        calc(in);
        setTargets(out);

        loss.calc();
        backprop();

        optimiser.update();

        return loss.getLoss();
    }

    public Cell getCell() {
        return cell;
    }

    public Loss getLoss() {
        return loss;
    }

    public Optimiser getOptimiser() {
        return optimiser;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Output> getWeights() {
        return weights;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        String tr = "#".repeat(100);

        builder.append(tr + "##########"+tr+"\n");
        builder.append(tr + " FUNCTION "+tr+"\n");
        builder.append(tr + "##########"+tr+"\n");
        builder.append(cell.toString());
        builder.append(tr + "##########"+tr+"\n");
        builder.append(tr + " LOSS ####"+tr+"\n");
        builder.append(tr + "##########"+tr+"\n");
        builder.append(loss.toString());
        builder.append(tr + "##########"+tr+"\n");

        return builder.toString();
    }
}
