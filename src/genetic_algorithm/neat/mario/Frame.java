package luecx.ai.genetic_algorithm.neat.mario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Frame extends JFrame implements KeyListener {



    private World world;
    private JPanel panel;
    private Player player;

    static int update_timer = 10;
    static long last_update = System.nanoTime();

    static boolean hightlight_best = true;
    static boolean follow_best = true;

    public Frame(World w){

        this.world = w;
        this.world.addSpectator(4,5);
        this.world.startBotTraining(4,5,400);
        this.player = (Player)world.getPlayer(0);
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout());
        this.setTitle("-.-");
        this.setMinimumSize(new Dimension(700,500));
        this.setPreferredSize(new Dimension(700,500));
        this.addKeyListener(this);


        this.panel = new JPanel(){

            final double w = 26;
            final double h = 18;

            private int[] transform(double x, double y){
                double dx = x - world.getPlayer(0).getX();
                double dy = y - world.getPlayer(0).getY();

                if(follow_best && world.best_bot_this_gen != null){
                    dx = x - world.best_bot_this_gen.getX();
                    dy = y - 4;
                }

                double px = dx / w + 0.5;
                double py = dy / h + 0.5;

                return new int[]{(int)(this.getWidth() * px), (int)(this.getHeight() * (1-py))};
            }

            private int[] len(double x, double y){
                return new int[]{(int) (this.getWidth() * x / w),(int) (this.getHeight() * y / h)};
            }

            @Override
            protected void paintComponent(Graphics g) {
                g.clearRect(0,0, 10000,100000);

                ((Graphics2D)(g)).setStroke(new BasicStroke(5));

                int[] pos;
                int[] sze;

                for(World.BlockPosition p: world.getBlock_positions()){
                    pos = transform(p.getX(), p.getY());
                    sze = len(1,1);

                    g.drawRect(pos[0] - sze[0] / 2,
                            pos[1] - sze[1] / 2,
                            sze[0],
                            sze[1]);
                }


                for(Mario io:world.getMarios()){

                    if(io instanceof Bot){
                        ((Graphics2D)(g)).setColor(new Color(50,50,50,10));
                        pos = transform(io.getX(),io.getY());
                        sze = len(1, 1);
                        g.fillRect(pos[0] - sze[0] / 2,
                                pos[1] - sze[1] / 2,
                                sze[0],
                                sze[1]);
                    }else{
                        if(!io.isSpectator()){
                            ((Graphics2D)(g)).setColor(new Color(220,220,50,140));
                            pos = transform(io.getX(),io.getY());
                            sze = len(1, 1);
                            g.fillRect(pos[0] - sze[0] / 2,
                                    pos[1] - sze[1] / 2,
                                    sze[0],
                                    sze[1]);
                        }
                    }
                    g.setColor(Color.red);
                }

                if(hightlight_best && world.best_bot_this_gen != null){
                    ((Graphics2D)(g)).setColor(Color.red);
                    pos = transform(world.best_bot_this_gen.getX(),world.best_bot_this_gen.getY());
                    sze = len(1, 1);
                    g.fillRect(pos[0] - sze[0] / 2,
                            pos[1] - sze[1] / 2,
                            sze[0],
                            sze[1]);
                }

                g.drawString(world.getPlayer(0).getX() + " ", 10, 10);
                g.drawString(world.getPlayer(0).getY() + " ", 10, 20);
                if(world.best_bot != null)g.drawString("best: " + world.best_bot.getClient().getScore() + " ", 10, 40);

            }
        };
        this.panel.addKeyListener(this);
        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
        new Updater(this);

    }

    public void revalidate() {
        super.revalidate();
        this.world.update(0.01);
        this.panel.revalidate();
        this.panel.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyChar()){
            case 'd': player.setMoveRight(true); break;
            case 'a': player.setMoveLeft(true); break;
            case 'w': player.setMoveUp(true); break;
            case 's': player.setMoveDown(true); break;
            case 'q': if(System.nanoTime() - last_update > 300){
                update_timer --;
                update_timer = (update_timer < 0 ? 0: update_timer);
                last_update = System.nanoTime();
            }
            case 'e': if(System.nanoTime() - last_update > 300){
                update_timer ++;
                update_timer = (update_timer > 10 ? 10: update_timer);
                last_update = System.nanoTime();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyChar()){
            case 'd': player.setMoveRight(false); break;
            case 'a': player.setMoveLeft(false); break;
            case 'w': player.setMoveUp(false); break;
            case 's': player.setMoveDown(false); break;

        }
    }


    public static void main(String[] args){
        World w = new World(128, 16);

        w.create_random_world();

//        for(int i = 0; i < 128; i++){
////            if(i != 14 && i != 15 && i != 16)
////                w.placeBlock(i,2);
//            if(Math.random() > 0.6) w.placeBlock(i,2);


//        w.placeBlock(11,3);
//        w.placeBlock(15,3);
//        w.placeBlock(15,6);


        new Frame(w);
    }

    private class Updater extends Thread{
        Frame frame;

        public Updater(Frame frame) {
            this.frame = frame;
            this.start();
        }

        public void run(){
            while(!this.isInterrupted()){
                try{
                    frame.revalidate();
                    Thread.sleep(update_timer);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.interrupt();
                }
            }
        }
    }
}
