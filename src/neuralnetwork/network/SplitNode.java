package neuralnetwork.network;

import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.builder.Builder;
import neuralnetwork.builder.Network;
import neuralnetwork.functions.Sigmoid;
import neuralnetwork.nodes.Node;
import neuralnetwork.nodes.Node1ToM;

public class SplitNode extends Node1ToM {

    @Override
    protected void abs_calcOutputDim() {
        this.setOutputDepth(get_previous_node().getOutputDepth());
        this.setOutputWidth(get_previous_node().getOutputWidth());
        this.setOutputHeight(get_previous_node().getOutputHeight());
    }

    @Override
    public void abs_genArrays() {

    }

    @Override
    public void abs_feedForward() {
        for(int i = 0; i < getInputSize(); i++){
            this.output_value.getData()[i] = this.input_value.getData()[i];
            this.output_derivative.getData()[i] = this.input_derivative.getData()[i];
        }
    }

    @Override
    public void abs_feedBackward() {
        for(int i = 0; i < get_previous_node().getOutputSize(); i++){
            this.input_loss.getData()[i] = 0;
            for(Node n:getNextNodes()){
                this.input_loss.getData()[i] += n.getInputLoss().getData()[i];
            }
        }
    }

    @Override
    public void abs_updateWeights(double eta) {

    }

    public static void main(String[] args) {
        Builder b = new Builder();
        InputNode inputNode = new InputNode(1,2,2);
        Node denseNode = new ConvolutionNode(1,2,1,0).setActivationFunction(new Sigmoid());
        b.addNode("input_node_1", inputNode);
        b.addNode("dense", denseNode, "input_node_1");
        b.addNode("split",new DenseNode(2).setActivationFunction(new Sigmoid()), "dense");
        b.addNode("split2",new SplitNode(), "split");
        b.addNode("denseiei",new DenseNode(3), "split2");
        b.addNode("out1",new OutputNode(), "denseiei");

        Network net = b.build_network();

        net.print_overview();

        net.write("test.net");
        Network loaded = Network.load("test.net");

        Tensor3D in = new Tensor3D(1,2,2);
        in.randomizeRegular(0,1);

        System.out.println(net.calculate(in)[0].get(0,0,0));
        System.out.println(loaded.calculate(in)[0].get(0,0,0));

        net.print_outputs();

    }
}
