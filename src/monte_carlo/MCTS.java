package monte_carlo;


import monte_carlo.expandor.Expandor;
import monte_carlo.selection.Selector;
import monte_carlo.selection.UCT;
import monte_carlo.simulator.Simulator;

import java.util.List;

public class MCTS<T extends NodeData> {

    private int amountOfPlayers = 2;

    private Node root;
    private Selector<T> selector = new UCT();
    private Simulator<T> simulator;
    private Expandor<T> expandor;
    private int maxDepth = 10000;

    public MCTS(Selector selector, Simulator<T> simulator, Expandor<T> expandor) {
        this.selector = selector;
        this.simulator = simulator;
        this.expandor = expandor;
    }

    public MCTS(int amountOfPlayers, Selector<T> selector, Simulator<T> simulator, Expandor<T> expandor) {
        this.amountOfPlayers = amountOfPlayers;
        this.selector = selector;
        this.simulator = simulator;
        this.expandor = expandor;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Node<T> run(Node<T> root, int times){
        this.root = root;
        for(int i = 0; i < times; i++){
            iteration();
        }
        Node<T> best = root.getChilds().get(0);
        for(Node<T> n:root.getChilds()){
            if(n.getTotal_score() / (double)n.getNumber_of_simulations() > best.getTotal_score() / (double)best.getNumber_of_simulations()){
                best = n;
            }
        }
//        System.err.println(root.getChilds());
//        System.err.println(best);
//        System.err.println();
        return best;
        //return selector.selectChild(root);
    }


    public String toString(){
        StringBuilder builder = new StringBuilder();
        toString(builder, 1, root, 100000);
        return builder.toString();
    }

    public String toString(int depth){
        StringBuilder builder = new StringBuilder();
        toString(builder, 1, root, depth);
        return builder.toString();
    }

    public static void toString(StringBuilder builder, int spaces, Node root, int depthleft){
        builder.append(String.format("%-" + spaces + "s [%-3s;%-3.5f]\n", "", root.getNumber_of_simulations(), root.getTotal_score()));
        if(depthleft == 0) return;
        if(root.getChilds() != null){
            for(Object n:root.getChilds()){
                toString(builder, spaces + 4, (Node)n, depthleft-1);
            }
        }
    }

    public void iteration(){
        Node leaf = selection();
        if(leaf.getDepth() < maxDepth){
            List<Node<T>> subLeafs = expansion(leaf);
            if(subLeafs == null || subLeafs.size() == 0) {
                return;
            }
            for(Node<T> n:subLeafs){
                double eval = simulation(n);
                backpropagation(n, eval);
            }
        }else{
            double eval = simulation(leaf);
            backpropagation(leaf, eval);
        }

    }

    public Node selection(){
        return selector.selectLeaf(root);
    }

    public List<Node<T>> expansion(Node leaf){
        List<Node<T>> newLeafs = expandor.expand(leaf);
        if(newLeafs.size() == 0) return null;

        leaf.setChilds(newLeafs);
        for(Node<T> l:newLeafs){
            l.setParent(leaf);
            //leaf.getChilds().add(l);
            l.setPlayerIndex((leaf.getPlayerIndex()+1)%amountOfPlayers);
        }
        return newLeafs;
    }

    public double simulation(Node<T> leaf){
        return simulator.simulate(leaf);
    }

    public void backpropagation(Node selected, double score){
        Node cur = selected;
        cur.setNumber_of_simulations(cur.getNumber_of_simulations()+1);
        cur.setTotal_score(cur.getTotal_score() + score);
        while(cur != root){
            cur = cur.getParent();
            cur.setNumber_of_simulations(cur.getNumber_of_simulations()+1);
            if(cur.getPlayerIndex() == selected.getPlayerIndex()){
                cur.setTotal_score(cur.getTotal_score() + score);
            }
        }
    }

}
