package genetic_algorithm.qlearning.games;

import genetic_algorithm.qlearning.QController;
import genetic_algorithm.qlearning.QLearner;
import genetic_algorithm.qlearning.statetypes.QIndex;
import genetic_algorithm.qlearning.statetypes.QState;
import genetic_algorithm.qlearning.storage.QNetwork;
import genetic_algorithm.qlearning.storage.QTable;
import newalgebra.builder.CellBuilder;
import newalgebra.cells.Variable;


public class MouseTable extends QController<QIndex> {


    private final int W, H;
    private int agentX, agentY;
    private int cheeseX, cheeseY;


    public MouseTable(int W, int H) {

        this.W = W;
        this.H = H;

        randCheesePos();

    }

    private void randCheesePos() {
        this.cheeseX = (int) (Math.random() * (W-2))+1;
        this.cheeseY = (int) (Math.random() * (H-2))+1;
    }

    private void randAgentPos() {
        this.agentX = (int) (Math.random() * (W-2))+1;
        this.agentY = (int) (Math.random() * (H-2))+1;

        while(agentX == cheeseX && agentY == cheeseY){
            this.agentX = (int) (Math.random() * (W-2))+1;
            this.agentY = (int) (Math.random() * (H-2))+1;
        }
    }

//    public void run() {
//        randAgentPos();
//        boolean gameover = false;
//        boolean win = false;
//        int max = 0;
//        while (!false || max > W * H) { //the second lart to prevent infinite loops
//            max++;
//            int action = max(qtable[agentX][agentY]);
//            System.out.println(this);
//            if (action == 0) {//down
//                agentY++;
//            } else if (action == 1) {//up
//                agentY--;
//            } else if (action == 2) {//right
//                agentX++;
//            } else {//left
//                agentX--;
//            }
//            if (agentX < 0 || agentX >= W) {
//                gameover = true;
//            } else if (agentY < 0 || agentY >= H) {
//                gameover = true;
//            } else if (agentX == cheeseX && agentY == cheeseY) {
//                gameover = true;
//                win = true;
//            }
//        }
//        System.out.println("Win: " + win);
//    }






    public void move(int action){
        switch (action){
            case 0: agentX++; break;
            case 1: agentX--; break;
            case 2: agentY++; break;
            case 3: agentY--; break;
        }
    }

    /**
     * checks if the given point is inside
     * @param x
     * @param y
     * @return
     */
    public boolean isInside(int x, int y) {
        if (x <= 0 || x >= W-1) {
            return false;
        } else if (y <= 0 || y >= H-1) {
            return false;
        }
        return true;
    }


    public String toString(){
        String out = "";
        for (int i = 0; i < W; i++) {
            for (int j = H-1; j >= 0; j--) {
                if (i == cheeseX && j == cheeseY) {
                    out += "C";
                } else if (i == agentX && j == agentY) {
                    out += "M";
                } else if (isInside(i,j)){
                    out += ".";
                } else{
                    out += "#";
                }
            }
            out += "\n";
        }
        return out;
    }

    public void print(){
        String out = "";
        for (int i = 0; i < W; i++) {
            for (int j = H-1; j >= 0; j--) {
                if (i == cheeseX && j == cheeseY) {
                    out += "C";
                } else if (i == agentX && j == agentY) {
                    out += "M";
                } else if (isInside(i,j)){
                    out += ".";
                } else{
                    out += "#";
                }
            }
            out += "\n";
        }
        System.out.println(out);
    }

    public void printDirections(){
        String out = "";
        for (int j = H-1; j >= 0; j--) {
            for (int i = 0; i < W; i++) {
                if (i == cheeseX && j == cheeseY) {
                    out += "C";
                } else if (i == agentX && j == agentY) {
                    out += "M";
                } else if (isInside(i,j)){
                    //get the direction
                    //int dir = tableAction(i,j);


//                    switch(dir) {
//                        case 0 : out+="\u2b9e";
//                            break;
//                        case 1 : out+="\u2b9c";
//                            break;
//                        case 2 : out+="\u2b9d";
//                            break;
//                        case 3 : out+="\u2b9f";
//                            break;
//                    }

                    //out += dir;
                } else{
                    out += "#";
                }
            }
            out += "\n";
        }
        System.out.println(out);
    }

    public static void main(String args[]) {

        MouseTable ql = new MouseTable(10,10);
        QTable table = new QTable(10 * 10, 4);

        QLearner<QIndex> learner = new QLearner<>(ql, table);

        learner.train(100000);
        learner.play(30);




    }

    @Override
    public QIndex getState() {
        return new QIndex(agentX * H + agentY);
    }

    @Override
    public int actionCount(QState state) {
        return 4;
    }

    /**
     * 0 = move right
     * 1 = move left
     * 2 = move up
     * 3 = move down
     * @param action
     */
    @Override
    public double perform(int action) {
        switch (action){
            case 0: agentX++; break;
            case 1: agentX--; break;
            case 2: agentY++; break;
            case 3: agentY--; break;
        }

        int reward = 0;
        if(agentX == cheeseX && agentY == cheeseY){
            reward = 1;
            randAgentPos();
        }else if(!isInside(agentX, agentY)){
            reward = -1;
            randAgentPos();
        }
        return reward;
    }
}
