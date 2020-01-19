package genetic_algorithm.neat.flappy_bird.gui;



import genetic_algorithm.neat.flappy_bird.Game;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private class Updater extends Thread{
        int c;
        GamePanel panel;

        public Updater(int c, GamePanel panel) {
            this.c = c;
            this.panel = panel;
            this.start();
        }

        public void run(){
            while(!this.isInterrupted()){
                try{
                    Thread.sleep(c);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                panel.repaint();
            }
            this.interrupt();
        }
    }

    private Game game;
    private JSlider slider;

    public GamePanel(JSlider slider){
        new Updater(25, this);
        this.game = new Game(500);
        this.slider = slider;
    }

    public Game getGame() {
        return game;
    }

    public void paintComponent(Graphics g){
        for(int i = 0; i < slider.getValue(); i++){
            game.process(0.025);
        }
        game.draw((Graphics2D) g, this.getWidth(), this.getHeight());

    }

}
