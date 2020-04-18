package algebra.model;


import algebra.nodes.Node;
import algebra.nodes.basic.Variable;
import core.tensor.Tensor;

import java.util.ArrayList;

public class Model {


    private ArrayList<Variable> inputs;
    private ArrayList<Node> outputs;

    private ArrayList<Node> calculationOrder;

    public Model(ArrayList<Variable> inputs, ArrayList<Node> outputs, ArrayList<Node> calculationOrder) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.calculationOrder = calculationOrder;
    }

    /**
     * feeds the data forward.
     * the input is expected to alternate between strings identifying the variables and their inputs respectively
     * For a list of the input values, see @link{getInputIdentifier()}
     * For a list of the output values, see @link{getOutputIdentifier()}
     */
    public Tensor[] calc(Object... in){
        setInputValues(in);

        calcFeed();

        return getOutputValues();
    }

    /**
     * feeds the data forward.
     * the input will be matches to the input nodes.
     * For a list of the input values, see @link{getInputIdentifier()}
     * For a list of the output values, see @link{getOutputIdentifier()}
     */
    public Tensor[] calc(Tensor... in){
        setInputValues(in);

        calcFeed();

        return getOutputValues();
    }

    /**
     * feeds the output data through the model
     */
    private void calcFeed() {
        for(Node n:calculationOrder){
            n.calc();
        }
    }

    /**
     * feeds the gradients backwards
     */
    private void autoDiffFeed(){
        for(int i = calculationOrder.size()-1; i>=0; i--){
            calculationOrder.get(i).autoDiff();
        }
    }

    /**
     * feeds the gradients backward.
     * For a list of the input values, see @link{getInputIdentifier()}
     * For a list of the output values, see @link{getOutputIdentifier()}
     */
    public Tensor[] autoDiff(Object... in){
        resetGradients();
        setOutputGradients(in);

        autoDiffFeed();

        return getInputGradients();
    }

    /**
     * feeds the gradients backward.
     * For a list of the input values, see @link{getInputIdentifier()}
     * For a list of the output values, see @link{getOutputIdentifier()}
     */
    public Tensor[] autoDiff(Tensor... in){
        resetGradients();
        setOutputGradients(in);

        autoDiffFeed();

        return getInputGradients();
    }



    /**
     * sets the input values
     * @param in
     */
    public void setInputValues(Tensor... in){
        for(int i = 0; i < Math.min(in.length, inputs.size()); i+=1){
            Variable n = inputs.get(i);
            Tensor t = in[i];
            n.setValue(t);
        }
    }

    /**
     * sets the input values
     * @param in
     */
    public void setInputValues(Object... in){
        for(int i = 0; i < in.length; i+=2){
            Variable n = getVariable((String)in[i]);
            Tensor t = (Tensor) in[i+1];
            n.setValue(t);
        }
    }

    /**
     * sets the output gradients
     * @param out
     */
    public void setOutputGradients(Tensor... out){
        for(int i = 0; i < Math.min(out.length, outputs.size()); i+=1){
            Node n = outputs.get(i);
            Tensor t = out[i];
            n.setOutputGradient(t);
        }
    }

    /**
     * sets the output gradients
     * @param out
     */
    public void setOutputGradients(Object... out){
        for(int i = 0; i < out.length; i+=2){
            Node n = getOutput((String)out[i]);
            Tensor t = (Tensor) out[i+1];
            n.setOutputGradient(t);
        }
    }


    public void resetGradients() {
        for(Node n:calculationOrder){
            n.resetGrad();
        }
    }

    public String[] getInputIdentifier(){
        String[] t = new String[inputs.size()];
        for(int i = 0; i < t.length; i++){
            t[i] = inputs.get(i).getIdentifier();
        }
        return t;
    }

    public String[] getOutputIdentifier(){
        String[] t = new String[outputs.size()];
        for(int i = 0; i < t.length; i++){
            t[i] = outputs.get(i).getIdentifier();
        }
        return t;
    }

    public Tensor[] getOutputValues() {
        Tensor[] t = new Tensor[outputs.size()];
        for(int i = 0; i < t.length; i++){
            t[i] = outputs.get(i).getOutputValue().copy();
        }
        return t;
    }

    public Tensor[] getInputGradients() {
        Tensor[] t = new Tensor[inputs.size()];
        for(int i = 0; i < t.length; i++){
            t[i] = inputs.get(i).getOutputGradient().copy();
        }
        return t;
    }

    public Node getOutput(String string){
        for(Node n:outputs){
            if(n.getIdentifier().equals(string)){
                return n;
            }
        }
        return null;
    }

    public Variable getVariable(String string){
        for(Variable n:inputs){
            if(n.getIdentifier().equals(string)){
                return n;
            }
        }
        return null;
    }
}
