package genetic_algorithm.neat.mario;



import genetic_algorithm.neat.neat.Neat;

import java.util.ArrayList;
import java.util.Comparator;

public class World {

    public final int width;
    public final int height;

    private boolean[][] blocks;
    private ArrayList<BlockPosition> block_positions = new ArrayList<>();

    private Neat neat;
    private ArrayList<Mario> marios = new ArrayList<>();

    public World(int width, int height){
        this.width = width;
        this.height = height;
        this.blocks = new boolean[width][height];
    }

    public World() {
        this(64, 16);
    }

    public void placeBlock(int x,int y){
        blocks[x][y] = true;
        block_positions.add(new BlockPosition(x,y));
        block_positions.sort(new Comparator<BlockPosition>() {
            @Override
            public int compare(BlockPosition o1, BlockPosition o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public int getBlock(int x, int y){
        return (x >= 0 && x < width && y>= 0 && y < height) ? (blocks[x][y] ? 1: 0) : -1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean[][] getBlocks() {
        return blocks;
    }

    public void setBlocks(boolean[][] blocks) {
        this.blocks = blocks;
    }

    public ArrayList<BlockPosition> getBlock_positions() {
        return block_positions;
    }

    public void setBlock_positions(ArrayList<BlockPosition> block_positions) {
        this.block_positions = block_positions;
    }

    public ArrayList<Mario> getMarios() {
        return marios;
    }

    public void setMarios(ArrayList<Mario> marios) {
        this.marios = marios;
    }

    public void startBotTraining(int x, int y, int amount){
        neat = new Neat(20,3,amount);
        this.bot_x = x;
        this.bot_y = y;
        for(int i = 0; i < amount; i++){
            this.marios.add(new Bot(this, x, y, this.neat.getClient(i)));
        }
    }

    public void addPlayer(int x, int y){
        this.marios.add(0, new Player(this, x,y));
    }

    public void addSpectator(int x, int y){
        this.marios.add(0, new Player(this, x,y, true));
    }

    private double currentTimer = 0;
    private int generation = 1;
    private int bot_x;
    private int bot_y;
    public Bot best_bot = null;
    public Bot best_bot_this_gen = null;

    public void update(double t){
        currentTimer += t;
        for(Mario p: marios){
            if(p instanceof Bot){
                if(getBlock((int)p.getX(), (int)p.getY()) != -1){
                    p.process(t, this);
                    if(best_bot_this_gen != null){
                        if(p.getX() > best_bot_this_gen.getX()){
                            best_bot_this_gen = (Bot)p;
                        }
                    }else{
                        best_bot_this_gen = (Bot)p;
                    }
                }
            }else{

                p.process(t, this);
            }

        }
        boolean gameover = true;
        for(Mario p:marios){
            if(p instanceof Bot){
                if(getBlock((int)p.getX(), (int) p.getY()) != -1){
                    gameover = false;
                    break;
                }
            }
        }
        if(currentTimer > generation){
            gameover = true;
            generation ++;
        }
        if(gameover){
            for(Mario p:marios){
                if(p instanceof Bot){
                    ((Bot) p).getClient().setScore(p.getX());
                }
            }

            best_bot = null;
            for(Mario p: marios){
                if(p instanceof Bot){
                    if(best_bot != null){
                        if(best_bot.getClient().getScore() < ((Bot) p).getClient().getScore()){
                            best_bot = (Bot)p;
                        }
                    }else{
                        best_bot = (Bot) p;
                    }
                }
            }

            currentTimer = 0;
            if(marios.size() >= 2){
                neat.evolve();
                //create_random_world();
            }
            for(Mario p:marios){
                if(p instanceof Bot){
                    p.setSpeed_y(0);
                    p.setX(bot_x);
                    p.setY(bot_y);
                }
            }
        }
    }

    public void create_random_world(){
        for(boolean[] ar: blocks){
            for(int i = 0; i < ar.length; i++){
                ar[i] = false;
            }
        }
        block_positions.clear();
        int x = 0;
        int[] ranges = new int[]{2,3,3};
        while(x < 120){
            x += ranges[(int)(Math.random() * ranges.length)];
            placeBlock(x, 2 - (int) (Math.random() * 2));
        }
    }

    public Mario getPlayer(int index){
        return marios.get(index);
    }

    public static void main(String[] args){

    }

    class BlockPosition implements Comparable<BlockPosition> {
        int x;
        int y;

        public BlockPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public int compareTo(BlockPosition o) {
            if(o.x < this.x) return 1;
            if(o.x > this.x) return -1;
            return 0;
        }
    }
}
