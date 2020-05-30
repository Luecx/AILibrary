package newalgebra.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.tensor.Tensor;
import newalgebra.builder.CellBuilder;
import newalgebra.cells.Cell;
import newalgebra.cells.Dimension;
import newalgebra.cells.Output;
import newalgebra.cells.Variable;
import newalgebra.element_operators.functions.Sigmoid;
import newalgebra.network.loss.Loss;
import newalgebra.network.loss.MSE;
import newalgebra.network.nodes.Dense;
import newalgebra.network.optimiser.Adam;
import newalgebra.network.optimiser.Optimiser;
import newalgebra.network.weights.Weight;

public class GAN implements Serializable {
	private Cell gen, adv;
	
	private ArrayList<Output> genWeights, genOutputs, advWeights, advOutputs; //All of the data on the output types
	private ArrayList<Variable> genVariables, advVariables;                   //The variables, inputs
	private List<Cell> genAllCells, advAllCells;                              //All cells
	private Loss loss;                                                        //The loss function, associated with adv
	private Optimiser genOptimiser, advOptimiser;                             //We actually need two different optimisers, for each network
	
	/**
	 * @param ONE : This is a constant tensor of 1
	 * @param ZERO : This is a constant tensor of 0
	 * */
	private static final Tensor ONE, ZERO;
	
	static {
		ONE = new Tensor(1);
		ONE.set(1, 0);
		ZERO = new Tensor(1); //Here we set the tensors from above
		ZERO.set(0, 0);
	}
	
	//Output of 1 means this is ai generated, output of 0 means it is not
	
	public GAN(Cell generative, Cell adversarial, Loss loss, Optimiser genOptimiser, Optimiser advOptimiser) {
		this.gen = generative;
		this.adv = adversarial;
		this.genOptimiser = genOptimiser;
		this.advOptimiser = advOptimiser;
		this.loss = loss;
		
		Cell.connectCells(adv, loss);
		
		loss.build();
		
		getDataOnCells();
		
		this.genOptimiser.prepare(genWeights);
		this.advOptimiser.prepare(advWeights);
		
		System.out.println(gen);
		System.out.println(adv);
		System.out.println(loss);
	}
	
	
	/**
	 * Basically this uses the inputs and generates what will be the generative network's response, hopefully well faked
	 * @param inputs The inputs of the generative network
	 * @return the output of the generative network given inputs
	 * */
	public Tensor[] calcGenerative(Tensor... inputs) {
		for(int i = 0; i < inputs.length; i++) {
			genVariables.get(i).setValue(inputs[i]);
		}
		gen.calc();
		return getOutputs(gen);
	}
	
	/**
	 * Given the inputs the adversarial determines whether or not this is ai generated or the real deal :)
	 * @param inputs The inputs of the adversarial network
	 * @return the output of the adversarial network given inputs
	 * */
	public Tensor[] calcAdversarial(Tensor... inputs) {
		for(int i = 0; i < inputs.length; i++) {
			advVariables.get(i).setValue(inputs[i]);
		}
		adv.calc();
		return getOutputs(adv);
	}
	/**
	 * Gives the generatives outputs to the adversarial
	 * */
	private void giveGenToAdv() {
		for(int i = 0; i < genOutputs.size(); i++) {
			advVariables.get(i).setValue(genOutputs.get(i).getValue());
		}
	}
	/**
	 * This output should be a value of one, because this the generated
	 * @param genInputs The inputs to the generative, and the output the adversarial gives, which should be 1
	 * */
	public Tensor[] calcAdvWithGen(Tensor... genInputs) {
		calcGenerative(genInputs);
		giveGenToAdv();
		adv.calc();
		return getOutputs(adv);
	}
	
	public void trainGen(Tensor... inputs) {
		
		calcGenerative(inputs);
		giveGenToAdv();
		adv.calc();
		
		loss.setTarget(ZERO, 0); //set it to the opposite, so in this case, we pretend the adv found a non ai
		
		loss.calc();
		
		loss.resetGrad(true);
        adv.resetGrad(true);
		
        loss.getOutput(0).getGradient().getData()[0] = 1;
        loss.autoDiff();
        adv.autoDiff();
        
        trainGenAfterAdv();
	}
	
	public void trainGenAfterAdv() {
		Tensor[] advError = new Tensor[advVariables.size()];
		for(int i = 0; i < advError.length; i++) {
			advError[i] = advVariables.get(i).getGradient();
		}
		for(int i = 0; i < advError.length; i++) {
			gen.getOutput(i).getGradient().setData(advError[i].getData());
		}
		gen.autoDiff();
		genOptimiser.update();
	}
	
	public double trainAdvGivenGen(Tensor... inputs) {
		
		calcGenerative(inputs);
		giveGenToAdv();
		adv.calc();
		
		loss.setTarget(ONE, 0); //set it to FOUND ai, and train the adversarial
		
		loss.calc();
		
		double l = loss.getLoss();
		
		loss.resetGrad(true);
        adv.resetGrad(true);
		
        loss.getOutput(0).getGradient().getData()[0] = 1;
        loss.autoDiff();
        adv.autoDiff();
        advOptimiser.update();
        
        return l;
	}
	
	public double trainAdversarial(Tensor... inputs) {
		
		calcAdversarial(inputs);
		
		loss.setTarget(ZERO, 0); //We know we have found an AI in this case
		
		loss.calc();
		
		double l = loss.getLoss();
		
		loss.resetGrad(true);
        adv.resetGrad(true);
		
        loss.getOutput(0).getGradient().getData()[0] = 1;
        loss.autoDiff();
        adv.autoDiff();
		advOptimiser.update();
		
		return l;
	}
	
	private void getDataOnCells() {
		
		genWeights = new ArrayList<>();
		advWeights = new ArrayList<>();
		
		genOutputs = new ArrayList<>();
		advOutputs = new ArrayList<>();
		
		genVariables = new ArrayList<>();
		advVariables = new ArrayList<>();
		
		genAllCells = gen.listAllChildsDeep();
		advAllCells = adv.listAllChildsDeep();
		
		//generative here
		
        if(!Cell.isEnclosed(genAllCells.toArray(new Cell[0]))){
            throw new RuntimeException();
        }
		
		for(Cell<?> c: genAllCells) {
            if(c instanceof Variable) {
                if(c instanceof Weight) {
                    genWeights.add(((Weight) c).getOutput());
                }
                else {
                    genVariables.add((Variable) c);
                }
            }
            for(Output o:c.getUnconnectedOutputs()) {
                genOutputs.add(o);
            }
        }
		
		//Adversarial data here
		
		
//		if(!Cell.isEnclosed(advAllCells.toArray(new Cell[0]))){
//            throw new RuntimeException();
//        }
		
		for(Cell<?> c: advAllCells) {
            if(c instanceof Variable) {
                if(c instanceof Weight) {
                    advWeights.add(((Weight) c).getOutput());
                }
                else {
                    advVariables.add((Variable) c);
                }
            }
            for(Output o:c.getUnconnectedOutputs()) {
                advOutputs.add(o);
            }
        }
	}
	
	private Tensor[] getOutputs(Cell cell) {
		List<Output> out = cell.getOutputs();
		Tensor[] arr = new Tensor[out.size()];
		for(int i = 0; i < arr.length; i++) {
			arr[i] = out.get(i).getValue();
		}
		return arr;
	}
	
	public static void main(String[] args) {
		CellBuilder builder = new CellBuilder();
        builder.add(new Variable(new Dimension(1)));
        builder.add(new Dense(4));
        builder.add(new Sigmoid());

        Cell gen = builder.build();
        
        builder = new CellBuilder();
        builder.add(new Variable(new Dimension(4)));
        builder.add(new Dense(1));
        builder.add(new Sigmoid());

        Cell adv = builder.build();
        
        GAN gan = new GAN(gen, adv, new MSE(), new Adam(), new Adam());
        Tensor in = new Tensor(1);
        in.set(1.0, 0);
//        System.out.println(gan.calcAdvWithGen(in));
//        System.out.println();
        
	}

}
