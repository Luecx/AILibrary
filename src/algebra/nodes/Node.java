package algebra.nodes;

import algebra.nodes.basic.Add;
import core.exceptions.NotMatchingSlotsException;
import core.tensor.Tensor;
import neuralnetwork.builder.BuildException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public abstract class Node<T extends Node<T>> {



    private String                  identifier = null;

    private Dimension               outputDimension;
    protected Tensor                outputValue;
    protected Tensor                outputGradient;

    private NodeCount               maxPrevNodes;
    private NodeCount               maxNextNodes;

    ArrayList<Node> previous_nodes = new ArrayList<>();
    ArrayList<Node> next_nodes = new ArrayList<>();

    public Node(NodeCount maxPrevNodes,
                NodeCount maxNextNodes) {
        this.maxPrevNodes = maxPrevNodes;
        this.maxNextNodes = maxNextNodes;
    }
    public Node(
            Dimension dimension,
            NodeCount maxPrevNodes,
            NodeCount maxNextNodes) {
        this.outputDimension = new Dimension(dimension);
        this.maxPrevNodes = maxPrevNodes;
        this.maxNextNodes = maxNextNodes;
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
        outputValue = outputDimension.emptyTensor();
        outputGradient = outputDimension.emptyTensor();
    }

    public void build(){
        calcOutputDim();
        init();
    }
    public void resetGrad(){
        outputGradient.reset(0);
    }

    protected abstract Dimension selfCalcOutputDim() throws BuildException;
    protected abstract void selfInit();
    public abstract void calc();
    public abstract void autoDiff();
    public abstract T copy();

    private boolean connectNextNode(Node n){
        for(Node k:next_nodes){
            if(k == n){
                return false;
            }
        }
        if(next_nodes.size() >= maxNextNodes.nodes){
            return false;
        }
        next_nodes.add(n);
        return true;
    }
    private boolean connectPreviousNode(Node n){
        for(Node k:previous_nodes){
            if(k == n){
                return false;
            }
        }
        if(previous_nodes.size() >= maxPrevNodes.nodes){
            return false;
        }
        previous_nodes.add(n);
        return true;
    }
    private boolean connectPreviousNode(Node n, int index){
        for(Node k:previous_nodes){
            if(k == n){
                return false;
            }
        }
        if(previous_nodes.size() >= maxPrevNodes.nodes){
            return false;
        }
        previous_nodes.add(index, n);
        return true;
    }
    public boolean addPreviousNode(Node n) {
        if(!previous_nodes.contains(n)){
            if(connectPreviousNode(n)){
                if(!n.connectNextNode(this)){
                    removePreviousNode(n);
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }
    public boolean addPreviousNode(Node n, int index) {
        if(!previous_nodes.contains(n)){
            if(connectPreviousNode(n,index)){
                if(!n.connectNextNode(this)){
                    removePreviousNode(n);
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }

    public boolean addNextNode(Node n){
        return n.addPreviousNode(this);
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

    public boolean replacePreviousNode(Node old, Node newNode){
        int index = previous_nodes.indexOf(old);
        if(index == -1) return false;

        removePreviousNode(old);

        return addPreviousNode(newNode, index);
    }
    public boolean replaceWith(Node newNode){
        boolean works = true;
        ArrayList<Node> nextNodes = new ArrayList<>();
        for(Node next:getNextNodes()){
            nextNodes.add(next);
        }
        for(Node nd:nextNodes){
            works = works && nd.replacePreviousNode(this, newNode);
        }
        return works;
    }

    public boolean hasNextNode(){
        return next_nodes.size() > 0;
    }
    public boolean hasPreviousNode(){
        return previous_nodes.size() > 0;
    }
    public Node getPreviousNode(){return previous_nodes.get(0);}
    public Node getPreviousNode(int index){return  previous_nodes.get(index);}
    public ArrayList<Node> getPreviousNodes(){return previous_nodes;}
    public ArrayList<Node> getPreviousNodesCopy(){
        ArrayList<Node> nodes = new ArrayList<>();
        for(Node n:previous_nodes){
            nodes.add(n.copy());
        }
        return nodes;
    }
    public ArrayList<Node> getNextNodes(){return next_nodes;}
    public ArrayList<Node> getNextNodesCopy(){
        ArrayList<Node> nodes = new ArrayList<>();
        for(Node n:next_nodes){
            nodes.add(n.copy());
        }
        return nodes;
    }

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


    public Dimension getInputDimension() {
        return previous_nodes.get(0).getOutputDimension();
    }
    public Tensor getInputValue(){
        return previous_nodes.get(0).getOutputValue();
    }
    public Tensor getInputGradient(){
        return previous_nodes.get(0).getOutputGradient();
    }


    public NodeCount getMaxPrevNodes() {
        return maxPrevNodes;
    }
    public NodeCount getMaxNextNodes() {
        return maxNextNodes;
    }

    public void setOutputValue(Tensor output_value) {
        if(output_value.size() == this.outputDimension.size())
            this.outputValue.setData(Arrays.copyOf(output_value.getData(), output_value.size()));
        else{
            throw new NotMatchingSlotsException(this.outputDimension.size(), output_value.size());
        }
    }

    public void setOutputGradient(Tensor output_gradient) {
        if(outputValue.size() == this.outputDimension.size())
            this.outputGradient.setData(Arrays.copyOf(output_gradient.getData(), output_gradient.size()));
        else{
            throw new NotMatchingSlotsException(this.outputDimension.size(), output_gradient.size());
        }
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

    public static void main(String[] args) {

        System.out.println(new Add(new Add(), new Add()));

    }
}
