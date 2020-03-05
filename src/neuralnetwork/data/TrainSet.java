package neuralnetwork.data;


import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.data.mnist.MnistImageFile;
import neuralnetwork.data.mnist.MnistLabelFile;
import parser.parser.Parser;
import parser.tree.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by finne on 26.01.2018.
 */
public class TrainSet {

    public final int INPUT_DEPTH, INPUT_WIDTH, INPUT_HEIGHT;
    public final int OUTPUT_DEPTH, OUTPUT_WIDTH, OUTPUT_HEIGHT;

    private ArrayList<Tensor3D[]> data = new ArrayList<>();

    public TrainSet(int INPUT_DEPTH, int INPUT_WIDTH, int INPUT_HEIGHT, int OUTPUT_DEPTH, int OUTPUT_WIDTH, int OUTPUT_HEIGHT) {
        this.INPUT_DEPTH = INPUT_DEPTH;
        this.INPUT_WIDTH = INPUT_WIDTH;
        this.INPUT_HEIGHT = INPUT_HEIGHT;
        this.OUTPUT_DEPTH = OUTPUT_DEPTH;
        this.OUTPUT_WIDTH = OUTPUT_WIDTH;
        this.OUTPUT_HEIGHT = OUTPUT_HEIGHT;
    }

    public void addData(Tensor3D in,Tensor3D expected) {
        if (in.getDimension(0) != INPUT_DEPTH ||
                in.getDimension(1) != INPUT_WIDTH ||
                in.getDimension(2) != INPUT_HEIGHT ||
                expected.getDimension(0) != OUTPUT_DEPTH ||
                expected.getDimension(1) != OUTPUT_WIDTH ||
                expected.getDimension(2) != OUTPUT_HEIGHT) {
            return; }
        data.add(new Tensor3D[]{in, expected});
    }

    public TrainSet extractBatch(int size) {
        if (size > 0 && size <= this.size()) {
            TrainSet set = new TrainSet(INPUT_DEPTH, INPUT_WIDTH, INPUT_HEIGHT, OUTPUT_DEPTH, OUTPUT_WIDTH, OUTPUT_HEIGHT);
            Integer[] ids = ArrayTools.randomValues(0, this.size() - 1, size);
            for (Integer i : ids) {
                set.addData(this.getInput(i), this.getOutput(i));
            }
            return set;
        } else return this;
    }

    public TrainSet copy(){
        TrainSet t = new TrainSet(INPUT_DEPTH, INPUT_WIDTH, INPUT_HEIGHT, OUTPUT_DEPTH, OUTPUT_WIDTH, OUTPUT_HEIGHT);
        for(int i = 0; i < this.size(); i++){
            t.data.add(new Tensor3D[]{data.get(i)[0].copy(),data.get(i)[0].copy()});
        }
        return t;
    }

    public void remove(int index) {
        this.data.remove(index);
    }

    public void shuffle(){
        for(int i = 0; i < this.size(); i++){
            int index = (int)(Math.random() * this.size());
            Tensor3D[] a = data.get(i);
            data.set(i, data.get(index));
            data.set(index, a);
        }
    }

    public ArrayList<TrainSet> shuffledParts(int size_each){
        ArrayList<TrainSet> t = new ArrayList<>();
        TrainSet trainSet = this.copy();
        trainSet.shuffle();
        for(int i = 0; i < this.size(); i++){
            if(i % size_each == 0){
                t.add(new TrainSet(INPUT_DEPTH, INPUT_WIDTH, INPUT_HEIGHT, OUTPUT_DEPTH, OUTPUT_WIDTH, OUTPUT_HEIGHT));
            }
            t.get(t.size()-1).data.add(trainSet.data.get(i));
        }
        return t;
    }

    @Override
    public String toString() {
        if(this.INPUT_DEPTH == 1 && this.INPUT_WIDTH == 1 && this.OUTPUT_DEPTH == 1 && this.OUTPUT_WIDTH == 1){
            String s = "TrainSet{" +
                    "INPUT_DEPTH=" + INPUT_DEPTH +
                    ", INPUT_WIDTH=" + INPUT_WIDTH +
                    ", INPUT_HEIGHT=" + INPUT_HEIGHT +
                    ", OUTPUT_DEPTH=" + OUTPUT_DEPTH +
                    ", OUTPUT_WIDTH=" + OUTPUT_WIDTH +
                    ", OUTPUT_HEIGHT=" + OUTPUT_HEIGHT;
            System.out.print("size: " + this.size() + "  " + data.size());
            for(Tensor3D[] ar:data){
                s += "\n    " + Arrays.toString(ar[0].getData()) + "    >    " +Arrays.toString(ar[1].getData());
            }
            return s;
        }
        return "TrainSet{" +
                "INPUT_DEPTH=" + INPUT_DEPTH +
                ", INPUT_WIDTH=" + INPUT_WIDTH +
                ", INPUT_HEIGHT=" + INPUT_HEIGHT +
                ", OUTPUT_DEPTH=" + OUTPUT_DEPTH +
                ", OUTPUT_WIDTH=" + OUTPUT_WIDTH +
                ", OUTPUT_HEIGHT=" + OUTPUT_HEIGHT +
                ", network.network.data=" + data +
                '}';
    }

    public int size() {
        return data.size();
    }

    public Tensor3D getInput(int index) {
        if (index >= 0 && index < size())
            return data.get(index)[0];
        else return null;
    }

    public Tensor3D getOutput(int index) {
        if (index >= 0 && index < size())
            return data.get(index)[1];
        else return null;
    }

    public void write(String file){
        try {
            Parser p = new Parser();
            p.create(file);

            Node root = new Node("TrainSet");

            root.addAttribute("input dim", INPUT_DEPTH + " " + INPUT_WIDTH + " " + INPUT_HEIGHT);
            root.addAttribute("output dim", OUTPUT_DEPTH + " " + OUTPUT_WIDTH + " " + OUTPUT_HEIGHT);

            for(int i = 0; i < data.size(); i++){
                Node node = new Node(""+i);

                StringBuilder inputBuilder = new StringBuilder();
                for (double value : data.get(i)[0].getData()) {
                    inputBuilder.append(value);
                    inputBuilder.append(" ");
                }
                inputBuilder.deleteCharAt(inputBuilder.length()-1);

                StringBuilder outputBuilder = new StringBuilder();
                for (double value : data.get(i)[1].getData()) {
                    outputBuilder.append(value);
                    outputBuilder.append(" ");
                }
                outputBuilder.deleteCharAt(outputBuilder.length()-1);

                node.addAttribute("in", inputBuilder.toString());
                node.addAttribute("out", outputBuilder.toString());

                root.addChild(node);
            }

            p.getContent().addChild(root);

            p.write();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TrainSet load(String file){
        try {
            Parser p = new Parser();
            p.load(file);

            int in_d, in_w, in_h;
            int out_d, out_w, out_h;


            String[] inSplit = p.getValue(new String[]{"TrainSet"},"input dim").split(" ");
            String[] outSplit = p.getValue(new String[]{"TrainSet"},"output dim").split(" ");

            in_d = Integer.parseInt(inSplit[0]);
            in_w = Integer.parseInt(inSplit[1]);
            in_h = Integer.parseInt(inSplit[2]);
            out_d = Integer.parseInt(outSplit[0]);
            out_w = Integer.parseInt(outSplit[1]);
            out_h = Integer.parseInt(outSplit[2]);

            TrainSet trainSet = new TrainSet(in_d, in_w, in_h, out_d, out_w, out_h);

            for(Node n:p.getContent().getChilds().get(0).getChilds()){
                inSplit = n.getAttribute("in").getValue().split(" ");
                outSplit = n.getAttribute("out").getValue().split(" ");

                double[] in = new double[inSplit.length];
                double[] out = new double[outSplit.length];

                for(int i = 0; i < inSplit.length; i++){
                    in[i] = Double.parseDouble(inSplit[i]);
                }for(int i = 0; i < outSplit.length; i++){
                    out[i] = Double.parseDouble(outSplit[i]);
                }

                trainSet.addData(
                        new Tensor3D(in, in_d, in_w, in_h),
                        new Tensor3D(out, out_d, out_w, out_h));

            }

            return trainSet;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getINPUT_DEPTH() {
        return INPUT_DEPTH;
    }

    public int getINPUT_WIDTH() {
        return INPUT_WIDTH;
    }

    public int getINPUT_HEIGHT() {
        return INPUT_HEIGHT;
    }

    public int getOUTPUT_DEPTH() {
        return OUTPUT_DEPTH;
    }

    public int getOUTPUT_WIDTH() {
        return OUTPUT_WIDTH;
    }

    public int getOUTPUT_HEIGHT() {
        return OUTPUT_HEIGHT;
    }


    public static TrainSet fromMnist(String images, String labels, int start, int end) {
        TrainSet set = new TrainSet(1, 28, 28, 1, 1, 10);

        try {

            MnistImageFile m = new MnistImageFile(images, "r");
            MnistLabelFile l = new MnistLabelFile(labels, "r");

            for(int i = 0; i < start; i++){
                m.next();
                l.next();
            }

            for (int i = start; i < end; i++) {
                if (i % 100 == 0) {
                    System.out.println("prepared: " + i);
                }
                Tensor3D input = new Tensor3D(1,28,28);
                Tensor3D output = new Tensor3D(1,1,10);

                output.set(1d,0,0,l.readLabel());
                for (int j = 0; j < 28 * 28; j++) {
                    input.set((double) m.read() / (double) 256,0, j / 28, j % 28);
                }

                set.addData(input, output);
                m.next();
                l.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return set;
    }

    public static void main(String[] args) {
        TrainSet set = new TrainSet(1,2,3,1,2,3);

        Tensor3D s1 = new Tensor3D(1,2,3);
        s1.randomizeRegular(0,1);

        set.addData(s1, new Tensor3D(1,2,3));
        set.addData(new Tensor3D(1,2,3), new Tensor3D(1,2,3));
        set.write("test.out");


        TrainSet loaded = TrainSet.load("test.out");
        System.out.println(loaded);
    }
}


