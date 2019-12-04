package luecx.ai.genetic_algorithm.neat.flappy_bird.gui;



import luecx.ai.genetic_algorithm.neat.visual.NEATFrame;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    private GamePanel gamePanel;
    private JSlider jSlider1;
    private JPanel jPanel1;

    public Frame(){
        this.setDefaultCloseOperation(3);

        jSlider1 = new JSlider();
        this.gamePanel = new GamePanel(jSlider1);
        this.jPanel1 = new JPanel();

        this.setPreferredSize(new Dimension(1200,900));
        this.setMaximumSize(new Dimension(1200,900));
        this.setMinimumSize(new Dimension(1200,900));

        jSlider1.setMajorTickSpacing(10);
        jSlider1.setMaximum(500);
        jSlider1.setMinorTickSpacing(1);
        jSlider1.setPaintLabels(true);
        jSlider1.setPaintTicks(true);
        jSlider1.setSnapToTicks(true);
        jSlider1.setToolTipText("");
        jSlider1.setValue(1);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSlider1, GroupLayout.DEFAULT_SIZE, 942, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSlider1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(63, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, BorderLayout.PAGE_END);

        GroupLayout jPanel2Layout = new GroupLayout(gamePanel);
        gamePanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 962, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 437, Short.MAX_VALUE)
        );

        getContentPane().add(gamePanel, BorderLayout.CENTER);

        pack();

        this.setVisible(true);
    }


    public static void main(String[] args){
        Frame g = new Frame();
        NEATFrame f = new NEATFrame();
        f.setNetwork(g.gamePanel.getGame().getBirds().get(0).getClient());
    }
}
