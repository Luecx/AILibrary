package neuralnetwork.data;

import core.tensor.Tensor3D;
import neuralnetwork.builder.Network;

public class Measure {


    public static double classificationAccuracy(Network network, TrainSet trainSet){
        int hits = 0;
        for(int i = 0; i < trainSet.size(); i++){
            Tensor3D out = network.calculate(trainSet.getInput(i))[0];
            Tensor3D exp = trainSet.getOutput(i);

            int outMaxIndex = 0;
            int expMaxIndex = 0;
            for(int k = 0; k < out.size(); k++){
                if(out.getData()[k] > out.getData()[outMaxIndex]) outMaxIndex = k;
                if(exp.getData()[k] > exp.getData()[expMaxIndex]) expMaxIndex = k;
            }

            if(outMaxIndex == expMaxIndex) hits++;
        }
        return hits/(double)trainSet.size();
    }

}
