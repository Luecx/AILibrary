package monte_carlo.selection;


import monte_carlo.Node;

public class UCT implements Selector {

    private double C = 2 * Math.sqrt(2);

    @Override
    public Node selectLeaf(Node root) {
        Node current = root;
        while(!current.isLeaf()){
            current = selectChild(current);
        }
        return current;
    }

    public Node selectChild(Node root){
        Node best = (Node) root.getChilds().get(0);
        double eval = evaluation(root, best);
        for(Object c:root.getChilds()){
            double e = evaluation(root, (Node) c);
            if(e > eval){
                best = (Node) c;
                eval = e;
            }
        }
        return best;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public double evaluation(Node parent, Node child){
        return child.getTotal_score() / child.getNumber_of_simulations() + C *
                                                                           Math.sqrt(Math.log(parent.getNumber_of_simulations()) / child.getNumber_of_simulations());
    }
}
