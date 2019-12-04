package luecx.ai.genetic_algorithm.neat.mario;


import luecx.ai.genetic_algorithm.neat.neat.Client;

public class Bot extends Mario {

    public Bot(World w, double x, double y, Client client) {
        super(w, x, y);
        this.client = client;
    }

    private Client client;

    @Override
    protected boolean[] getControl() {


        int l = 3;
        double[] in = new double[(l + 1) * (l + 2)];

        int c = 0;

        for(int i = (int)x;i <= (int) x + l; i++){
            for(int n = (int)y - l; n <= (int) y + 1; n++){
                in[c] = world.getBlock(i,n) != -1 ? world.getBlock(i,n): 0;
                c++;
            }
        }
        double[] o = client.calculate(in);


        return new boolean[]{o[0] > 0.5,o[1] > 0.5,o[2] > 0.5, false};
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
