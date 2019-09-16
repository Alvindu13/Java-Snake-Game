package com.alvindu13;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;

    /*
    // get the screen size as a java dimension
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private final int B_HEIGHT = screenSize.height * 2 / 3;
    private final int B_WIDTH = screenSize.width * 2 / 3;
    */


    //taille d'un anneau en pixel
    private final int DOT_SIZE = 10;
    //300 * 300 / 10 = ALL DOTS (c'est la résolution divisé par 10)
    private final int ALL_DOTS = 900;

    private final int RAND_POS = 29;
    private final int DELAY = 30;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        
        initBoard();
    }
    
    private void initBoard() {



        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        /*
        // get the screen size as a java dimension
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // get 2/3 of the height, and 2/3 of the width
        int height = screenSize.height * 2 / 3;
        int width = screenSize.width * 2 / 3;

        // set the jframe height and width
        setPreferredSize(new Dimension(width, height));

        */

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
    }

    private void initGame() {

        //initialise le corps du serpent à 3 anneaux
        dots = 3;

        // Z inférieur au corps du serpent, alors faire Z++
        // 50 - 1 * 10
        // 50 - 2 * 10
        /*
        intialise la position des anneaux
         */
        /*for (int z = 0; z < dots; z++) {
            //x[z] = 50 - z * 10;
            //x[z] = 50;
            //y[z] = 50;
        }*/

        // assigne des coordonnées aléatoirement aux pommes
        locateApple();

        //timer de l'action, toutes les DELAY ms répéter l'action définie par this
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        
        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }        
    }

    private void gameOver(Graphics g) {
        
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    //Vérifier les coordonnées de la pomme, si la tête du serpent touche les coordonnées
    //On ajoute un anneau au corps
    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            dots++;
            locateApple();
        }
    }

    /*
    method de déplacement
     */
    private void move() {


        //Définit la position des anneaux du serpents (initaux et ceux ajoutés)
        // Comme x(0) est rédéfinit durant le déplacement, toutes les coordonnées sont rédéfinis
        for (int z = dots; z > 0; z--) {
            System.out.println("z" + z);
            System.out.println("x(z) = " + x[z]);

            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];

            System.out.println("x(z-1) = " + x[z-1]);


        }

        //x(0) = x(0) - 10
        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        System.out.println("source" + source);

        //A chaque timer (l'ActionEvent) faire ceci :
        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    /*
    Permet de déplacer le serpent avec les flèches
    Par défaut, le serpent se déplace à droite au lancement du jeu
     */
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            //avec le bolleean après && on empêche l'utilisateur d'aller dans le sens inverse du serpent (le snake ne peut pas faire demi tour)
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
