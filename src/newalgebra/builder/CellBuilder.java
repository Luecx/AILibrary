package newalgebra.builder;

import core.tensor.Tensor;
import newalgebra.cells.*;
import newalgebra.element_operators.functions.Sigmoid;
import newalgebra.network.Network;
import newalgebra.network.nodes.Dense;
import newalgebra.network.loss.MSE;
import newalgebra.network.nodes.Flatten;
import newalgebra.network.optimiser.Adam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CellBuilder implements Serializable {


    private HashMap<String, Cell> addedCells = new HashMap<>();

    private ArrayList<Cell> calcOrder = new ArrayList<>();

    /**
     * adds a cell.
     * @param cell
     * @param args
     */
    public void add(Cell cell, Object... args){
        add(cell, createName(cell), args);
    }

    /**
     * adds a cell.
     *
     * args equals the identifiers to connect to.
     * If none are given, the last few will be taken.
     *
     * args are organised like this:
     *
     * identifier_1 key_1 key_2 key_3 ... identifier_2 key_1 key_2 ...
     *
     * Where identifier identifies an already added node.
     * If this node does not exist, an exception will be thrown
     *
     * The keys have different meaning depending on the amount:
     *
     * ---0 keys---
     * the first output without any inputs will be used. same for the input.
     * If there is no output with any inputs, the first output will be used
     *
     * ---1 key---
     * key_1: specifies which output will be taken
     *
     * ---2 keys---
     * key_1: specifies which output will be taken
     * key_2: specifies which unconnected input will be used
     *
     * @param cell
     * @param args
     */
    public void add(Cell cell, String name, Object... args){
        if(nameExists(name)) throw new RuntimeException("This name already exists");

        List<Input> inputs = cell.getUnconnectedInputs();

        HashMap<Input, Output> inOutMapping = new HashMap<>();

        for(int i = 0; i < args.length; i++){
            if(args[i] instanceof String){
                String id = (String) args[i];
                ArrayList<Integer> keys = new ArrayList<>();
                while(i+1 < args.length && !(args[i+1] instanceof String)){
                    keys.add((int)args[i+1]);
                    i++;
                }

                Cell prev = addedCells.get(id);


                if(prev == null) throw new RuntimeException("no node labeled: " + id);

                Output out;
                Input in = null;

                switch (keys.size()) {
                    case 0:
                        List<Output> uncon = prev.getUnconnectedOutputs();
                        if(uncon.isEmpty()){
                            out = prev.getOutput(0);
                        }else{
                            out = uncon.get(0);
                        }
                        if(inputs.size() > 0)
                            in = inputs.get(0);
                        break;
                    case 1:
                        out = prev.getOutput(keys.get(0));
                        if(inputs.size() > 0)
                            in = inputs.get(0);
                        break;
                    case 2:
                        out = prev.getOutput(keys.get(0));
                        in = inputs.get(keys.get(1));
                        break;
                    default:
                        throw new RuntimeException();
                }

                if(in == null && cell.canAddInputs()){
                    in = cell.createInput();
                }

                if(out == null || in == null) throw new RuntimeException();

                if(inOutMapping.containsKey(in)) throw new RuntimeException();

                inOutMapping.put(in,out);
            }
        }

        for(Input i:inOutMapping.keySet()){
            inputs.remove(i);
        }

        Collections.reverse(inputs);
        for(Input k:inputs){

            boolean found = false;
            outer:
            for(int i = calcOrder.size()-1; i >= 0; i--){
                Cell invest = calcOrder.get(i);

                for(int n = invest.outputCount()-1; n>= 0; n--){
                    Output o = invest.getOutput(n);
                    if(o.getConnectedInputs().size() == 0 && !inOutMapping.containsValue(o)){
                        found = true;
                        inOutMapping.put(k, o);
                        break outer;
                    }
                }
            }
            if(!found){
                throw new RuntimeException();
            }
        }

        for(Input i:inOutMapping.keySet()){
            inOutMapping.get(i).addConnectedInput(i);
        }

        addedCells.put(name, cell);
        calcOrder.add(cell);

        if(!cell.inputCountOK()){
            throw new RuntimeException();
        }

    }


    public boolean nameExists(String name) {
        for(String s:addedCells.keySet()){
            if(s.equals(name)) return true;
        }
        return false;
    }

    public String createName(Cell cell){

        String name = cell.getClass().getSimpleName();
        int index = 1;

        while(nameExists(name +"_" + index)){
            index++;
        }
        return name + "_" + index;
    }


    public Cell build(){

        Cell c = new Cell(calcOrder.toArray(new Cell[0]));
        c.build();
        return c;
    }

    public static void main(String[] args) {

        final CellBuilder builder = new CellBuilder();
        builder.add(new Flatten());
        builder.add(new Sigmoid());

        final Cell cell = builder.build();

        Network network = new Network(cell, new MSE(), new Adam());

        System.out.println(cell);




        Tensor in = new Tensor(3,3,3);

        in.randomizeRegular(0,1);

        Tensor out = new Tensor(27);
        out.randomizeRegular(0,1);

        network.train(new Tensor[]{in}, new Tensor[]{out});
        network.train(new Tensor[]{in}, new Tensor[]{out});

//        // Liki Relu
//        builder.addNode(new OutputNode());

//        final TrainSet set = Main.loadTrainSet();
//        for (int i = 0; i < 100; i++) {
//            for (int a = 0; a < set.size(); a++)
//                Main.network.train(new Tensor3D[] { set.getInput(a) }, new Tensor3D[] { set.getOutput(a) });
//            set.shuffle();
//        }

    }

}
