package neuralnetwork.nodes;

import core.matrix.sparse_matrix.HashMatrix;
import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.builder.BuildException;
import neuralnetwork.functions.ReLU;
import neuralnetwork.functions.Sigmoid;
import neuralnetwork.network.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class Node {

    private String identifier;

    private int output_depth;
    private int output_width;
    private int output_height;
    private int output_size;
    private int input_depth;
    private int input_width;
    private int input_height;
    private int input_size;

    protected Tensor3D output_value;
    protected Tensor3D output_derivative;
    protected Tensor3D output_loss;
    protected Tensor3D input_loss;  //only needed for splitting
    protected Tensor3D input_value;  //only needed for splitting
    protected Tensor3D input_derivative;  //only needed for splitting

    private boolean max_prev_nodes = false;
    private boolean max_next_nodes = false;

    ArrayList<Node> previous_nodes = new ArrayList<>();
    ArrayList<Node> next_nodes = new ArrayList<>();

    public Node(boolean max_prev_nodes, boolean max_next_nodes) {
        this.max_prev_nodes = max_prev_nodes;
        this.max_next_nodes = max_next_nodes;
    }
    public Node(int output_depth, int output_width, int output_height, boolean max_prev_nodes, boolean max_next_nodes) {
        this.output_depth = output_depth;
        this.output_width = output_width;
        this.output_height = output_height;
        this.max_prev_nodes = max_prev_nodes;
        this.max_next_nodes = max_next_nodes;
    }

    private void genArrays(){
        abs_genArrays();

        output_value = new Tensor3D(output_depth, output_width, output_height);
        output_derivative = new Tensor3D(output_depth, output_width, output_height);
        output_loss = new Tensor3D(output_depth, output_width, output_height);

        if(!(this instanceof InputNode)){
            if(max_prev_nodes){
                input_value = previous_nodes.get(0).output_value;
                input_derivative = previous_nodes.get(0).output_derivative;
                if(previous_nodes.get(0).hasMaxNextNodes()){
                    input_loss = previous_nodes.get(0).output_loss;
                }else{
                    input_loss = new Tensor3D(previous_nodes.get(0).output_depth,
                            previous_nodes.get(0).output_width, previous_nodes.get(0).output_height);
                }
            }
        }

    }

    public void build(){
        calcOutputDim();
        genArrays();
    }
    public void calcOutputDim(){
        try{
            if(max_prev_nodes && previous_nodes.size() == 1){
                input_size = previous_nodes.get(0).output_size;
                input_width = previous_nodes.get(0).output_width;
                input_height = previous_nodes.get(0).output_height;
                input_depth = previous_nodes.get(0).output_depth;
            }
            abs_calcOutputDim();
            output_size = this.output_depth * this.output_width * this.output_height;
            if(this.output_size == 0){
                throw new BuildException(this, "output_size=0");
            }
        } catch (BuildException e) {
            e.printStackTrace();
        }
    }



    protected abstract void abs_calcOutputDim() throws BuildException;
    public abstract void abs_genArrays();
    public abstract void abs_feedForward();
    public abstract void abs_feedBackward();
    public abstract void abs_updateWeights(double eta);

    private void connectNextNode(Node n){
        if(max_next_nodes && next_nodes.size() > 0){
            removeNextNode(next_nodes.get(0));
        }
        next_nodes.add(n);
    }
    private void connectPreviousNode(Node n){
        if(max_prev_nodes && previous_nodes.size() > 0){
            removePreviousNode(previous_nodes.get(0));
        }
        previous_nodes.add(n);
    }
    public void addPreviousNode(Node n){
        if(previous_nodes.contains(n) == false){
            connectPreviousNode(n);
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
    public String getIdentifier() {
        return identifier;
    }
    public int getOutputDepth() {
        return output_depth;
    }
    public int getOutputWidth() {
        return output_width;
    }
    public int getOutputHeight() {
        return output_height;
    }
    public int getOutputSize() {return output_size;}
    public void setIdentifier(String identifier) {
        if(this.identifier == null && identifier != null)
            this.identifier = identifier;
    }
    public void setOutputDepth(int output_depth) {
        if(this.output_depth == 0 && output_depth != 0)
            this.output_depth = output_depth;
    }
    public void setOutputWidth(int output_width) {
        if(this.output_width == 0 && output_width != 0)
            this.output_width = output_width;
    }
    public void setOutputHeight(int output_height) {
        if(this.output_height == 0 && output_height != 0)
            this.output_height = output_height;
    }
    public int getInputDepth() {
        return input_depth;
    }
    public int getInputWidth() {
        return input_width;
    }
    public int getInputHeight() {
        return input_height;
    }
    public int getInputSize() {
        return input_size;
    }

    public Tensor3D getOutputValue() {
        return output_value;
    }
    public Tensor3D getOutputDerivative() {
        return output_derivative;
    }
    public Tensor3D getOutputLoss() {
        return output_loss;
    }
    public Tensor3D getInputLoss() {
        return input_loss;
    }
    public Tensor3D getInputValue() {
        return input_value;
    }
    public Tensor3D getInputDerivative() {
        return input_derivative;
    }

    public void setOutputValue(Tensor3D output_value) {
        if(this instanceof InputNode){
            if(output_value.size() == this.output_size){
                this.output_value = output_value;
                ((InputNode) this).get_next_node().input_value = output_value;
            }
        }

    }
    public void setOutputDerivative(Tensor3D output_derivative) {
        this.output_derivative = output_derivative;
    }
    public void setOutputLoss(Tensor3D output_loss) {
        this.output_loss = output_loss;
    }

    public boolean hasMaxPrevNodes() {
        return max_prev_nodes;
    }
    public boolean hasMaxNextNodes() {
        return max_next_nodes;
    }
    public ArrayList<Node> getPreviousNodes(){return previous_nodes;}
    public ArrayList<Node> getNextNodes(){return next_nodes;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return
                identifier.equals(node.identifier) &&
                output_depth == node.output_depth &&
                output_width == node.output_width &&
                output_height == node.output_height &&
                input_depth == node.input_depth &&
                input_width == node.input_width &&
                input_height == node.input_height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(output_depth, output_width, output_height, input_depth, input_width, input_height);
    }
}
