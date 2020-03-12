package neuralnetwork.visualize;

import core.tensor.Tensor;
import core.tensor.Tensor2D;
import core.tensor.Tensor3D;
import core.tensor.Tensor4D;
import neuralnetwork.builder.Builder;
import neuralnetwork.builder.Network;
import neuralnetwork.data.TrainSet;
import neuralnetwork.functions.LeakyReLU;
import neuralnetwork.functions.None;
import neuralnetwork.functions.Sigmoid;
import neuralnetwork.loss.MSE;
import neuralnetwork.network.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ConvVisual {

    public static Tensor3D visualize(
            Network network,
            ConvolutionNode convolutionNode,
            int filterIndex,
            int iterations,
            double eta) {

        MSE mse = new MSE();


        Tensor3D input = new Tensor3D(
                network.getInputNode(0).getOutputDepth(),
                network.getInputNode(0).getOutputWidth(),
                network.getInputNode(0).getOutputHeight());
        input.randomizeRegular(0,0);


        network.train(input, network.calculate(input), 0);


        for(int iter = 0; iter < iterations; iter++){
            network.calculate(input);
            Tensor3D expected = convolutionNode.getOutputValue().copy();
            for(int i = 0; i < convolutionNode.getOutputWidth(); i++){
                for(int n = 0; n < convolutionNode.getOutputHeight(); n++){
                    expected.set(1,filterIndex, i,n);
                }
            }
            double loss = mse.calculate_loss(convolutionNode, expected, 1);
            expected.self_sub(convolutionNode.getOutputValue());

            network.backpropagateError(convolutionNode);
//            network.print_outputs();
//            network.print_outputLoss();

            Tensor3D inputLoss = new Tensor3D(network.getInputNode(0).getOutputLoss());
            inputLoss.self_scale(eta);
            input.self_sub(inputLoss);



            System.out.println("iteration: "  + (iter+1)+ "       loss: " + loss);
        }

        return input;
        //network.tr
    }


    public static BufferedImage toImage(Tensor3D tensor3D) {
        tensor3D.self_normalise();
        BufferedImage out = new BufferedImage(tensor3D.getDimension(1), tensor3D.getDimension(2), BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < out.getWidth(); i++){
            for(int n = 0; n < out.getHeight(); n++){
                if(tensor3D.getDimension(0) == 1){
                    out.setRGB(i,n, new Color(
                            (float)tensor3D.get(0,i,n),
                            (float)tensor3D.get(0,i,n),
                            (float)tensor3D.get(0,i,n)).getRGB());
                }else{
                    out.setRGB(i,n, new Color(
                            (float)tensor3D.get(0,i,n),
                            (float)tensor3D.get(1,i,n),
                            (float)tensor3D.get(2,i,n)).getRGB());
                }

            }
        }

        return out;
    }

    public static void displayImage(BufferedImage image){

        JFrame frame = new JFrame();
        frame.setSize(700,700);
        frame.setPreferredSize(new Dimension(700,700));
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(3);
        frame.getContentPane().add(new JLabel(){
            @Override
            public void paintComponent(Graphics g) {
                g.clearRect(0,0,10000,10000);
                g.drawImage(image, 0,0,this.getWidth(), this.getHeight(), this);
            }
        }, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {

        Builder builder = new Builder(3,100,100);
        builder.addNode(new ConvolutionNode(4, 3,1,1).setActivationFunction(new LeakyReLU()));
        builder.addNode(new PoolingNode(2));
        builder.addNode(new ConvolutionNode(8, 3,1,1).setActivationFunction(new LeakyReLU()));
        builder.addNode(new PoolingNode(2));
        builder.addNode(new ConvolutionNode(8, 3,1,1).setActivationFunction(new LeakyReLU()));
        builder.addNode(new PoolingNode(2));
        builder.addNode(new ConvolutionNode(16, 3,1,1).setActivationFunction(new LeakyReLU()));
        builder.addNode(new PoolingNode(2));
        builder.addNode(new ConvolutionNode(4, 3,1,1).setActivationFunction(new LeakyReLU()));
        builder.addNode(new FlattenNode());
        //builder.addNode(new DenseNode(100).setActivationFunction(new LeakyReLU()));
        builder.addNode(new DenseNode(10).setActivationFunction(new Sigmoid()));


        Network network = builder.build_network();

        //network = Network.load("res/test.net");

//        TrainSet trainSet = TrainSet.fromMnist("res/train-images.idx3-ubyte", "res/train-labels.idx1-ubyte",0,1);
//        network.train(trainSet, 1000, 0.01);

        //network.write("res/test.net");


        for(int i = 0; i < 5; i++){


            ConvolutionNode convNode = (ConvolutionNode) network.getNodes()[9];
            Tensor3D input = visualize(network, convNode, i,200,1E5);
            displayImage(toImage(input));
        }


    }

}
