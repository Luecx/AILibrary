package newalgebra.cells;

import core.tensor.Tensor;
import core.tensor.Tensor2D;
import newalgebra.builder.CellBuilder;
import newalgebra.builder.Logger;
import newalgebra.element_operators.Add;
import newalgebra.element_operators.functions.LeakyReLU;
import newalgebra.element_operators.functions.Sigmoid;
import newalgebra.gla.GLA;
import newalgebra.matrix_operators.MatrixVectorProduct;
import newalgebra.network.Network;
import newalgebra.network.loss.MSE;
import newalgebra.network.nodes.Dense;
import newalgebra.network.nodes.Flatten;
import newalgebra.network.nodes.recurrent.LSTM;
import newalgebra.network.optimiser.Adam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class Cell<T extends Cell<T>> implements Serializable {


    private List<Input> inputs;
    private List<Output> outputs;

    private ArrayList<Cell> computationOrder;

    protected boolean build = false;


    public Cell(int inputs, int outputs){
        this.inputs = new ArrayList<>(inputs);
        this.outputs = new ArrayList<>(outputs);
        this.computationOrder = new ArrayList<>();

        for(int i = 0; i < inputs; i++){
            this.inputs.add(new Input());
        }
        for(int n = 0; n < outputs; n++){
            this.outputs.add(new Output());
        }
    }

    public Cell(Cell... cells){
        if(!isEnclosed(cells)) throw new RuntimeException("Cells are not enclosed");

        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        computationOrder = new ArrayList<>();

        for (Cell c : cells) {
            computationOrder.add(c);
            inputs.addAll(c.getUnconnectedInputs());
            outputs.addAll(c.getUnconnectedOutputs());
        }
    }

    /**
     * given a list of nodes and corresponding output indices and input indices, a new node will be generated.
     *
     * @param outputSize
     * @param args
     */
    public Cell(Object[] args, int outputSize){
        this(args.length/3, outputSize);


        if(args.length % 3 != 0) throw new RuntimeException("I dont understand this!");


        for(int i = 0; i < args.length; i+=3){
            Cell.connectCells((Cell) args[i], this, (int)args[i+1], (int)args[i+2]);
        }
    }

    public final void wrap(Cell... cells){
        if(!isEnclosed(cells)) Logger.getLogger().addWarning("Cells are not enclosed!");
        if(!this.computationOrder.isEmpty()) throw new RuntimeException("This cell already contains cells");
        if(!this.getConnectedInputs().isEmpty()) throw new RuntimeException("This cell has connected inputs");
        if(!this.getConnectedOutputs().isEmpty()) throw new RuntimeException("This cell has connected outputs");

        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        computationOrder = new ArrayList<>();

        for (Cell c : cells) {
            computationOrder.add(c);
            inputs.addAll(c.getUnconnectedInputs());
            outputs.addAll(c.getUnconnectedOutputs());
        }
    }



    /**
     * calculate output dimensions
     */
    public final void build(){

        if(hasBeenBuilt()){
            return;
        }

        if(!inputCountOK()){
            throw new RuntimeException("Input count incorrect at: " + this);
        }

        if(getUnconnectedInputs().size() > 0){
            throw new RuntimeException("Unconnected inputs at: " + this);
        }

        for(Input i:inputs){
            if(i.getDimension() == null){
                throw new RuntimeException("Cannot build when input has no dimension at: " + this);
            }
        }


        generateInternalVariableDimension();
        if(computationOrder.isEmpty()){
            generateOutputDimension();
        }else{
            for(Cell c:computationOrder){
                c.build();
            }
        }


        for(Output o:outputs){
            if(o.getDimension() == null) {
                throw new RuntimeException("Output without dimension at: " + this.getClass().getSimpleName());
            }
            o.initArrays();
        }
        initArrays();
        build = true;
    }

    /**
     * task is to take the inputs and calculate the outputs
     */
    public void calc(){
        for(Cell c:computationOrder){
            c.calc();
        }
    }

    /**
     * calculates partial gradients of the input and adds them
     */
    public void autoDiff() {
        for(int i = computationOrder.size()-1; i>= 0; i--){
            computationOrder.get(i).autoDiff();
        }
    }

    /**
     * returns true if this node has been built
     */
    public boolean hasBeenBuilt(){
        return build;
    }

    /**
     * sets the build state
     * @param hasBeenBuilt
     */
    public void setBuilt(boolean hasBeenBuilt){
        this.build = hasBeenBuilt;
    }

    /**
     * @param keepVariables     set to true if variable cells should not be copied but used for both instances
     * @return
     */
    public T copy(boolean keepVariables){
        //TODO implement by subchilds
        try {
            T _this = (T) this.getClass().getConstructor(null).newInstance();

            for(Cell<?> k:computationOrder){
                Cell<?> cop = k.copy(keepVariables);
                _this.getComputationOrder().add(cop);
            }

            for(Cell<?> k:_this.getComputationOrder()){

                for(int inputIndex = 0; inputIndex < k.inputCount(); inputIndex++){
                    Input i = k.getInput(inputIndex);
                    if(i.getOutput() == null) break;

                    int cellIndex = -1;
                    int outputIndex = -1;


                    outer:
                    for(int cI = 0; cI < computationOrder.size(); cI++){
                        for(int oI = 0; oI < computationOrder.get(cI).outputCount(); oI++){
                            if(computationOrder.get(cI).getOutput(oI) == i.getOutput()){
                                cellIndex = cI;
                                outputIndex = oI;
                                break outer;
                            }
                        }
                    }

                    if(cellIndex != -1 && outputIndex != -1){
                        Cell.connectCells(k, k.getComputationOrder().get(cellIndex), outputIndex, inputIndex);
                    }

                }

            }

            return _this;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * checks if the input count is OK.
     * It should check for the amount of inputs is correct
     * @return
     */
    public boolean inputCountOK(){
        //TODO implement by subchilds
        return true;
    }

    /**
     * can be used to calculate internal variable dimensions
     */
    public void generateInternalVariableDimension(){
        //TODO implement by subchilds
    }

    /**
     * used to generate the output dimension
     */
    public void generateOutputDimension(){
        //TODO implement by subchilds
    }
    /**
     * used to init arrays
     */
    public void initArrays(){
        //TODO implement by subchilds
    }

    /**
     * if canAddInputs() returns true, a builder can add new inputs.
     * @return
     */
    public boolean canAddInputs(){
        //TODO implement by subchilds
        return false;
    }

    /**
     * check if this cell uses the input or any child uses it
     * @param output
     * @return
     */
    public final boolean containsInput(Output output){
        for(Output o:outputs){
            if(o == output) return true;
        }
        for(Cell c:computationOrder){
            if(c.containsOutput(output)) return true;
        }
        return false;
    }

    /**
     * check if this cell uses the output or any child uses it
     * @param output
     * @return
     */
    public final boolean containsOutput(Output output){
        for(Output o:outputs){
            if(o == output) return true;
        }
        for(Cell c:computationOrder){
            if(c.containsOutput(output)) return true;
        }
        return false;
    }

    /**
     * check if the given cell is a subcell
     * @param cell
     * @return
     */
    public final boolean containsCell(Cell cell){
        for(Cell c:computationOrder){
            if(c == cell) return true;
            if(c.containsCell(cell)) return true;
        }
        return false;
    }

    /**
     * input output getter setters
     * @param index
     * @return
     */
    public final Output getOutput(int index){
        if(!hasOutput(index)) throw new RuntimeException("this output does not exist");
        return outputs.get(index);
    }

    public final Input createInput(){
        if(!canAddInputs()){
            throw new RuntimeException("Cannot add new inputs");
        }
        Input i = new Input();
        this.inputs.add(i);
        return i;
    }

    public final Input getInput(int index){
        if(!hasInput(index)) throw new RuntimeException("this input does not exist, at: " + this.getClass().getSimpleName());
        return inputs.get(index);
    }
    
    public final int outputCount(){
        return outputs.size();
    }

    public final int inputCount(){
        return inputs.size();
    }

    public final boolean hasOutput(int index){
        return index >= 0 && index < outputCount();
    }

    public final boolean hasInput(int index){
        return index >= 0 && index < inputCount();
    }

    /**
     * returns the amount of unconnected inputs
     * @return
     */
    public final int unconnectedInputs(){
        int count = 0;

        for(Input i:inputs){
            if(!i.hasConnectedOutput()){
                count++;
            }
        }

        return count;
    }

    /**
     * returns the amount of unconnected outputs
     * @return
     */
    public final int unconnectedOutputs(){
        int count = 0;

        for(Output i:outputs){
            if(i.getConnectedInputs().size() == 0){
                count++;
            }
        }

        return count;
    }


    /**
     * returns a shallow copy of the inputs
     * @return
     */
    public final List<Input> getInputs() {
        return new ArrayList<>(inputs);
    }

    /**
     * returns a shallow copy of the outputs
     * @return
     */
    public final List<Output> getOutputs() {
        return new ArrayList<>(outputs);
    }

    /**
     *
     * @return a list of connected inputs
     */
    public final List<Input> getConnectedInputs(){
        List<Input> inputs = new ArrayList<>();
        for(Input i:this.inputs){
            if(i.hasConnectedOutput()){
                inputs.add(i);
            }
        }
        return inputs;
    }

    /**
     *
     * @return a list of unconnected inputs
     */
    public final List<Input> getUnconnectedInputs(){
        List<Input> inputs = new ArrayList<>();
        for(Input i:this.inputs){
            if(!i.hasConnectedOutput()){
                inputs.add(i);
            }
        }
        return inputs;
    }

    /**
     *
     * @return a list of connected outputs
     */
    public final List<Output> getConnectedOutputs(){
        List<Output> inputs = new ArrayList<>();
        for(Output i:this.outputs){
            if(i.getConnectedInputs().size() != 0){
                inputs.add(i);
            }
        }
        return inputs;
    }

    /**
     *
     * @return a list of unconnected outputs
     */
    public final List<Output> getUnconnectedOutputs(){
        List<Output> inputs = new ArrayList<>();
        for(Output i:this.outputs){
            if(i.getConnectedInputs().size() == 0){
                inputs.add(i);
            }
        }
        return inputs;
    }

    /**
     * lists all childs and its subchilds.
     * the resulting list equals the complete computation order
     * @return
     */
    public final List<Cell> listAllChildsDeep(){
        List<Cell> nodes = new LinkedList<>();

        for(Cell c:this.computationOrder){
            nodes.add(c);
            nodes.addAll(c.listAllChildsDeep());
        }

        return nodes;
    }

    /**
     * lists all variables without duplicates
     * @return
     */
    public final List<Variable> listAllVariables(){
        Set<Variable> set = new HashSet<>();

        for(Cell c:listAllChildsDeep()){
            if(c instanceof Variable){
                set.add((Variable) c);
            }
        }

        return new ArrayList<>(set);
    }

    /**
     * resets the gradients
     */
    public final void resetGrad(boolean resetVariables){

        if(!resetVariables && this instanceof Variable){
            return;
        }
        if(this.computationOrder.isEmpty()){
            for(Output o:outputs){
                o.getGradient().reset(0);
            }
        }else{
            for(Cell c:this.computationOrder){
                c.resetGrad(resetVariables);
            }
        }
    }



    /**
     * returns a string representation
     *
     * @return
     */
    public final String toString(int mode) {
        StringBuilder builder = new StringBuilder();
        this.toString(0,mode,builder);
        return builder.toString();
    }


    /**
     * returns a string representation
     * @return
     */
    public final String toString(){
        StringBuilder builder = new StringBuilder();
        this.toString(0,1,builder);
        return builder.toString();
    }

    /**
     * returns the computation order
     * @return
     * */
    public ArrayList<Cell> getComputationOrder() {
        return new ArrayList<>(computationOrder);
    }

    /**
     * return a string representation with the given amount of spaces to the left
     *
     * modes:   1 = default
     *          2 = output
     *          3 = gradient
     *
     * @param spaces
     * @return
     */
    public final void toString(int spaces, int mode, StringBuilder builder){

        String lead = "|\t".repeat(spaces);

        //---------------------------------heading---------------------------------
        if(!computationOrder.isEmpty())
            builder.append(lead+"+"+"-".repeat(200)+"\n");


        //---------------------------------inputs---------------------------------
        builder.append(lead + (!computationOrder.isEmpty() ? "|":" ")
                       +this.getClass().getSimpleName() + " " + inputCount() + "->"+outputCount());
        builder.append(" ".repeat(50-(builder.length()-builder.lastIndexOf("\n")+2*spaces)) + " | (");

        for(Input i:inputs){
            if (i.hasConnectedOutput()) {
                builder.append(Integer.toHexString(i.getOutput().hashCode()));
                if (i.getDimension() != null) {
                    if (mode == 2 && i.getValue() != null) {
                        builder.append(i.getDimension().toStringShort() + "{" + Arrays.toString(i.getValue().getData()) + "}");
                    } else if (mode == 3 && i.getGradient() != null) {
                        builder.append(i.getDimension().toStringShort() + "{" + Arrays.toString(i.getGradient().getData()) + "}");
                    } else {
                        builder.append(i.getDimension().toStringShort());
                    }
                    //System.out.println(i.getValue());
                }
            } else {
                builder.append(" - ");
            }
            if (i != inputs.get(inputs.size() - 1)) {
                builder.append(" ");
            }
        }
        builder.append(") -> (");


        //---------------------------------outputs---------------------------------
        for(Output i:outputs){
            builder.append(Integer.toHexString(i.hashCode()));
            if(i.getDimension() != null){


                if (mode == 2 && i.getValue() != null) {
                    builder.append(i.getDimension().toStringShort() + "{" + Arrays.toString(i.getValue().getData()) + "}");
                } else if (mode == 3 && i.getGradient() != null) {
                    builder.append(i.getDimension().toStringShort() + "{" + Arrays.toString(i.getGradient().getData()) + "}");
                } else {
                    builder.append(i.getDimension().toStringShort());
                }
            }
            if(i != outputs.get(outputs.size()-1))
                builder.append(" ");
        }
        builder.append(")\n");




        for(Cell c:computationOrder){
            c.toString(spaces+1,mode, builder);
        }


        if(!computationOrder.isEmpty())
            builder.append(lead+"+"+"-".repeat(200)+"\n");

    }


    /**
     * connects the first n i/o of both cells where n = min(c1.unconnectedOutputs, c2.unconnectedInputs)
     * @param c1
     * @param c2
     */
    public final static void connectCellsComplete(Cell c1, Cell c2){
        while(!c1.getConnectedOutputs().isEmpty() && !c2.getUnconnectedInputs().isEmpty()){
            connectCellsNext(c1,c2);
        }
    }

    /**
     * connects the next free i/o of both cells
     * @param c1
     * @param c2
     */
    public final static void connectCellsNext(Cell c1, Cell c2){
        if(c2.getUnconnectedInputs().size() == 0 && c2.canAddInputs()){
            c2.createInput();
        }
        connectCells(c1,c2,c1.getOutputs().indexOf(c1.getUnconnectedOutputs().get(0)), c2.getInputs().indexOf(c2.getUnconnectedInputs().get(0)));
    }

    /**
     * connects 2 cells and links their first output/input respectively
     * @param c1
     * @param c2
     */
    public final static void connectCells(Cell c1, Cell c2){
        connectCells(c1,c2,0,0);
    }

    /**
     * creates a connection between c1 at output 0 to c2 at input "input"
     * @param c1
     * @param c2
     * @param input
     */
    public final static void connectCells(Cell c1, Cell c2, int input){
        connectCells(c1,c2,0,input);
    }


    /**
     * creates a connection between c1's output and c2's input
     * @param c1
     * @param c2
     * @param output
     * @param input
     */
    public final static void connectCells(Cell c1, Cell c2, int output, int input){
        if(c2 instanceof Variable){
            throw new RuntimeException("Variables cannot have inputs");
        }

        if(!c1.hasOutput(output) || !c2.hasInput(input)){
            throw new RuntimeException("This input/output does not exist");
        }

        if(c1.getOutput(output).isConnectedTo(c2.getInput(input))){
            throw new RuntimeException("They are already connected");
        }

        c1.getOutput(output).addConnectedInput(c2.getInput(input));
    }

    public final static void connectIO(Output o, Input i){
        if(o.isConnectedTo(i)) {
            throw new RuntimeException("They are already connected");
        }
        o.addConnectedInput(i);
    }

    /**
     * checks if the given cells are only connected with each other and no connection is missing
     * @param cells
     * @return
     */
    public final static boolean isEnclosed(Cell... cells) {
        ArrayList<Output> connectedOutputs = new ArrayList<>();
        ArrayList<Input> connectedInputs = new ArrayList<>();

        for (Cell c : cells) {
            connectedOutputs.addAll(c.getConnectedOutputs());
            connectedInputs.addAll(c.getConnectedInputs());
        }

        for(Input i:connectedInputs){
            if(!connectedOutputs.contains(i.getOutput())) {
                System.err.println(i + "  " + i.getOutput());
                return false;
            }
        }
        for(Output o:connectedOutputs){
            for(Input i:o.getConnectedInputs()){
                if(!connectedInputs.contains(i)) {
                    return false;
                }
            }
        }

        return true;
    }



    public static void main(String[] args) throws IOException {

//        Variable mat = new Variable(new Dimension(2,2));
//        Variable vec = new Variable(new Dimension(2));
//
//        MatrixVectorProduct mul = new MatrixVectorProduct(mat,0,vec,0);
//
//
//
//        Cell full = new Cell(mat, vec, mul);
//        full.build();
//
//        System.out.println(full);
//        Tensor2D matrix = new Tensor2D(new double[][]{{1,2},{3,4}});
//        Tensor   vector = new Tensor  (new double[]   {1,2});
//
//
//
//        for(int i = 0; i < 100; i++){
//
//            mat.setValue(matrix);
//            vec.setValue(vector);
//
//            full.calc();
//            Tensor out = full.getOutput(0).getValue();
//            System.out.println(out);
//
//            full.resetGrad(true);
//            full.getOutput(0).getGradient().reset(0);
//            full.getOutput(0).getGradient().self_add(full.getOutput(0).getValue());
//            full.autoDiff();
//            System.out.println(mat.getGradient());
//
//            Tensor matGradient = mat.getGradient();
//
//            matrix.self_sub(matGradient.scale(0.1));
//        }
//


//        final CellBuilder builder = new CellBuilder();
//        builder.add(new Variable(new Dimension(2,3,3)));
//        builder.add(new Flatten());
//        builder.add(new Dense(250));
//        builder.add(new LeakyReLU());
//        builder.add(new Dense(50));
//        builder.add(new LeakyReLU());
//        builder.add(new Dense(25));
//        builder.add(new LeakyReLU());
//        for(int i = 0; i < 10; i++){
//            builder.add(new Dense(25));
//            builder.add(new LeakyReLU());
//        }
//
//        builder.add(new Dense(2));
//
//        final Cell cell = builder.build();
//
//
//        Network network = network = new Network(cell, new MSE(), new Adam());
//
//        final BufferedImage img = new BufferedImage(3000, 3000, 1);
//        final Graphics2D g2d = img.createGraphics();
//        final GLA gla = new GLA();
//        gla.drawGraph(g2d, cell, true);
//        ImageIO.write(img, "PNG", new File("test.png"));
        LSTM d = new LSTM();
        System.out.println(d);
        System.out.println(d.copy(true));
    }

}
