package algebra.nodes;

import core.exceptions.NotMatchingSlotsException;
import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.builder.BuildException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public abstract class Node {



    private String identifier;

    private Dimension outputDimension;
    protected Tensor outputValue;
    protected Tensor outputDerivative;
    protected Tensor outputGradient;


    private NodeCount max_prev_nodes;
    private NodeCount max_next_nodes;

    ArrayList<Node> previous_nodes = new ArrayList<>();
    ArrayList<Node> next_nodes = new ArrayList<>();

    public Node(NodeCount max_prev_nodes, NodeCount max_next_nodes) {
        this.max_prev_nodes = max_prev_nodes;
        this.max_next_nodes = max_next_nodes;
    }
    public Node(
            Dimension dimension,
            NodeCount max_prev_nodes,
            NodeCount max_next_nodes) {
        this.outputDimension = new Dimension(dimension);
        this.max_prev_nodes = max_prev_nodes;
        this.max_next_nodes = max_next_nodes;
    }

    public void calcOutputDim(){
        try{
            Dimension dim = selfCalcOutputDim();
            if(dim != null){
                this.outputDimension = new Dimension(dim);
            }
            if(this.outputDimension == null || this.outputDimension.size() == 0){
                throw new BuildException(this, "output_size=0");
            }
        } catch (BuildException e) {
            e.printStackTrace();
        }
    }
    private void init(){
        init();

        outputValue = new Tensor3D(outputDimension.getDepth(), outputDimension.getWidth(), outputDimension.getHeight());
        outputDerivative = new Tensor3D(outputDimension.getDepth(), outputDimension.getWidth(), outputDimension.getHeight());
        outputGradient = new Tensor3D(outputDimension.getDepth(), outputDimension.getWidth(), outputDimension.getHeight());

    }
    public void build(){
        calcOutputDim();
        init();
    }



    protected abstract Dimension selfCalcOutputDim() throws BuildException;
    protected abstract void selfInit();
    public abstract void calc();
    public abstract void autoDiff();

    private boolean connectNextNode(Node n){
        if(next_nodes.size() >= max_next_nodes.nodes){
            return false;
        }
        next_nodes.add(n);
        return true;
    }
    private boolean connectPreviousNode(Node n){
        if(previous_nodes.size() >= max_prev_nodes.nodes){
            return false;
        }
        previous_nodes.add(n);
        return true;
    }
    public void addPreviousNode(Node n){
        if(!previous_nodes.contains(n)){
            if(connectPreviousNode(n))
                n.connectNextNode(this);
        }
    }

    public void addNextNode(Node n){
        n.addPreviousNode(this);
    }
    public void removePreviousNode(Node n){
        if(previous_nodes.contains(n)){
            n.next_nodes.remove(this);
            previous_nodes.remove(n);
        }
    }
    public void removeNextNode(Node n){
        n.removePreviousNode(this);
    }
    public boolean hasNextNode(){
        return next_nodes.size() > 0;
    }
    public boolean hasPreviousNode(){
        return previous_nodes.size() > 0;
    }
    public Node getPreviousNode(){return previous_nodes.get(0);}
    public ArrayList<Node> getPreviousNodes(){return previous_nodes;}
    public ArrayList<Node> getNextNodes(){return next_nodes;}

    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(String identifier) {
        if(this.identifier == null && identifier != null)
            this.identifier = identifier;
    }

    public Dimension getOutputDimension() {
        return outputDimension;
    }
    public Tensor getOutputValue() {
        return outputValue;
    }
    public Tensor getOutputDerivative() {
        return outputDerivative;
    }
    public Tensor getOutputGradient() {
        return outputGradient;
    }

    public Dimension getInputDimension(int index) {
        return previous_nodes.get(index).getOutputDimension();
    }
    public Tensor getInputValue(int index){
        return previous_nodes.get(index).getOutputValue();
    }
    public Tensor getInputGradient(int index){
        return previous_nodes.get(index).getOutputGradient();
    }
    public Tensor getInputDerivatvie(int index){
        return previous_nodes.get(index).getOutputDerivative();
    }

    public Dimension getInputDimension() {
        return previous_nodes.get(0).getOutputDimension();
    }
    public Tensor getInputValue(){
        return previous_nodes.get(0).getOutputValue();
    }
    public Tensor getInputGradient(){
        return previous_nodes.get(0).getOutputGradient();
    }
    public Tensor getInputDerivatvie(){
        return previous_nodes.get(0).getOutputDerivative();
    }

    public void setOutputValue(Tensor output_value) {
        if(output_value.size() == this.outputDimension.size())
            this.outputValue.setData(Arrays.copyOf(output_value.getData(), output_value.size()));
        else{
            throw new NotMatchingSlotsException(this.outputDimension.size(), output_value.size());
        }
    }
    public void setOutputDerivative(Tensor3D output_derivative) {
        if(outputValue.size() == this.outputDimension.size())
            this.outputDerivative.setData(Arrays.copyOf(output_derivative.getData(), output_derivative.size()));
        else{
            throw new NotMatchingSlotsException(this.outputDimension.size(), output_derivative.size() );
        }
    }
    public void setOutputGradient(Tensor3D output_gradient) {
        if(outputValue.size() == this.outputDimension.size())
            this.outputGradient.setData(Arrays.copyOf(output_gradient.getData(), output_gradient.size()));
        else{
            throw new NotMatchingSlotsException(this.outputDimension.size(), output_gradient.size());
        }
    }

    public static final boolean matchingDimension(Node node, Tensor tensor){
        if(
                tensor.rank() == 3 &&
                tensor.getDimension(0) == node.getOutputDimension().getDepth() &&
                tensor.getDimension(1) == node.getOutputDimension().getWidth() &&
                tensor.getDimension(2) == node.getOutputDimension().getHeight()) return true;

        if(
                tensor.rank() == 2 &&
                        node.getOutputDimension().getDepth() == 1 &&
                        tensor.getDimension(0) == node.getOutputDimension().getWidth() &&
                        tensor.getDimension(1) == node.getOutputDimension().getHeight()) return true;

        if(
                tensor.rank() == 1 &&
                        node.getOutputDimension().getDepth() == 1 &&
                        node.getOutputDimension().getWidth() == 1 &&
                        tensor.getDimension(0) == node.getOutputDimension().getHeight()) return true;
        
        return false;
    }
    public static final boolean matchingDimension(Node node1, Node node2){
        return node1.getOutputDimension().equals(node2.getOutputDimension());
    }
    public static final boolean matchingDimension(Tensor tensor1, Tensor tensor2){
        return Arrays.equals(tensor1.getDimensions(), tensor2.getDimensions());
    }
    public static final boolean sameDimension(Node... nodes){
        for(int i = 1; i < nodes.length;i++){
            if(!matchingDimension(nodes[i], nodes[0])) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return
                identifier.equals(node.identifier) &&
                outputDimension.equals(((Node) o).outputDimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputDimension, identifier);
    }
}
