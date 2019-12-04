package neuralnetwork.functions;

import neuralnetwork.nodes.Node;

public abstract class Function {

    public void apply(Node l){
        for(int i = 0; i < l.getOutputSize(); i++){
            l.getOutputDerivative().getData()[i] = func_val_prime(l.getOutputValue().getData()[i]);
            l.getOutputValue().getData()[i] = func_val(l.getOutputValue().getData()[i]);
        }
    }

    protected abstract double func_val(double x);

    protected abstract double func_val_prime(double x);

}
