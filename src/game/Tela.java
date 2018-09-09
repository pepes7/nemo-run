package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Tela extends JPanel {

    protected int xP, xA; // xPrincipal e xAuxiliar
    protected int x, y, control;
    protected int velocX, velocY;
    protected int largura, altura;
    protected int score;
    protected int bestScore;
    protected int fTime;
    protected int delay;
    protected int vidas, xp;
    protected int numAguas;
    protected boolean reinicia;

    protected ImageIcon background;
    protected ImageIcon imgPause, imgPausePressed;
    protected ImageIcon life;
    protected ImageIcon game_over;
    protected ImageIcon[] xpBar;
    protected JButton btPause;

    protected Nemo nemo;
    protected Nadador nadador;
    protected AguaViva agua_viva;
    protected ArrayList<AguaViva> aguas_vivas;
    protected Opcoes sets;

    public Tela(Principal principal) {

        // Inicializacao de objetos
        nemo = new Nemo(50, 200, 105, 65, 3, 5, true);
        nadador = new Nadador(-500, 130, 428, 200, 3, 5, true);
        background = new ImageIcon(getClass().getResource("/imagens/loop_background.png"));
        life = new ImageIcon(getClass().getResource("/imagens/life_icon.png"));
        game_over = new ImageIcon(getClass().getResource("/imagens/game_over.gif"));
        imgPause = new ImageIcon(getClass().getResource("/imagens/button_pause.png"));
        imgPausePressed = new ImageIcon(getClass().getResource("/imagens/button_pause_pressed.png"));

        //Inicializacao do botao Pause
        btPause = new JButton();
        btPause.setBounds(200, 600, imgPause.getIconWidth(), imgPause.getIconHeight());
        add(btPause);
        btPause.setText(null);
        btPause.setIcon(imgPause);
        btPause.setPressedIcon(imgPausePressed);
        btPause.setBorderPainted(false); //Tira a borda do botao
        btPause.setContentAreaFilled(false); //Tira o fundo branco do botao

        aguas_vivas = new ArrayList<>();
        bestScore = 0;
        control = Util.MAIN; // Controla o numero de vezes que o enter foi pressionado
        inicializaComponentes();
    }

    public void inicializaComponentes() {
        // Inicializacao de posicoes
        this.x = 0;
        this.y = 0;
        this.velocX = 4;
        this.nemo.x = 50;
        this.nemo.y = 200;
        this.nadador.x = -500;
        this.nadador.y = 130;

        // Inicializacao de contadores
        score = 0;
        xP = 0; // Posicao x inicial do primeiro fundo
        xA = background.getIconWidth(); // Posicao x inicial do segundo fundo
        fTime = 0;
        delay = 10;
        vidas = 4;
        xp = 0;
        reinicia = true;
        numAguas = 20;

        //Inicializacao da barra de nivel
        xpBar = new ImageIcon[13];
        for (int i = 0; i < 13; i++) {
            xpBar[i] = new ImageIcon(getClass().getResource("/progress_bar/xp_" + i + ".png"));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image img, img2, img3, img4;
        img = background.getImage();
        img2 = nadador.nadador.getImage();
        img4 = life.getImage();

        g.drawImage(img, xP, 0, null);
        g.drawImage(img, xA, 0, null);

        if (control >= Util.PLAYING && control < Util.GAME_OVER) {
            img3 = nemo.nadando.getImage();
            // Verifica a posicao do nemo e do nadador
            if (fTime == 0) {
                if (nemo.x == 395) {
                    fTime = 1;
                }
                if (nemo.x < 395) {
                    nemo.moveRight();
                }
                if (nadador.x < -200) {
                    nadador.x += nadador.velocX;
                }
            }

            // Verifica se um dos backgrounds chegou ao inicio da tela
            if (xA == 0) {
                xP = background.getIconWidth();
            }
            if (xP == 0) {
                xA = background.getIconWidth();
            }

            // Decrementa as posicoes
            xA -= velocX;
            xP -= velocX;

            for (int i = 0; i < aguas_vivas.size(); i++) {
                aguas_vivas.get(i).movimenta();
                if (aguas_vivas.get(i).x < -agua_viva.largura) {
                    aguas_vivas.get(i).ativo = false;
                }
                score++;

                if (Util.colisao(nemo, aguas_vivas.get(i))) {
                    Musica mu = new Musica(new File("res/choque.mp3"));
                    mu.start();
                    delay = 0;
                    aguas_vivas.get(i).ativo = false;
                    nemo.moveLeft();
                    vidas--;
                    if (score > 1500) {
                        score -= 1000;
                    } else {
                        if (score > 150) {
                            score -= 100;
                        } else {
                            score -= 50;
                        }
                    }
                }

                if (delay < 10) {
                    img3 = nemo.choque.getImage();
                    delay++;
                } else {
                    img3 = nemo.nadando.getImage();
                    delay = 10;
                }
            }

            for (int i = 0; i < aguas_vivas.size(); i++) {
                if (aguas_vivas.get(i).ativo == false) {
                    aguas_vivas.remove(i);
                }
            }

            // Desenha na ordem os backgrounds e o nadador
            g.drawImage(img, xP, y
                    - 15, null);
            g.drawImage(img, xA, y
                    - 15, null);
            g.drawImage(img2, nadador.x, nadador.y,
                    this);
        } else {

            img3 = nemo.parado.getImage();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Gamer", Font.PLAIN, 40));
            g2d.drawString("PRESS ENTER TO START", 260, 240);
        }
        for (AguaViva agua : aguas_vivas) {
            agua.draw(g);
        }
        progressBar(g);
        g.drawImage(img3, nemo.x, nemo.y, this);

        if (control == Util.GAME_OVER) {
            gameOver(g);
        }
        setScore(g);
        int posVidaX = -15;
        for (int i = 0; i < vidas; i++) {
            posVidaX += img4.getWidth(this) - 4;
            g.drawImage(img4, posVidaX, 35, null);
        }
    }

    public void addAguaViva() {
        int[] position = {80, 100, 125, 150, 200, 225, 250, 300, 325, 350, 400};
        Random px = new Random();
        agua_viva = new AguaViva(1200, position[px.nextInt(11)], 61, 65, 4, 4, true);
        aguas_vivas.add(agua_viva);
    }

    public void setScore(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Gamer", Font.PLAIN, 30));
        g2d.drawString("SCORE: " + score, 17, 30);
    }

    public void gameOver(Graphics g) {
        Image img = game_over.getImage();
        nemo.x = 200;
        nemo.y = 200;
        g.drawImage(img, 190, 150, null);
        for (int i = 0; i < aguas_vivas.size(); i++) {
            aguas_vivas.remove(i);
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(13, 59, 81));
        g2d.setFont(new Font("Gamer", Font.PLAIN, 40));
        g2d.drawString("SCORE", 268, 267);
        g2d.drawString("BEST", 460, 267);

        if (score > bestScore) {
            bestScore = score;
        }

        g2d.setColor(new Color(10, 44, 61));
        g2d.setFont(new Font("Gamer", Font.PLAIN, 35));
        g2d.drawString("" + score, 268, 290);
        g2d.drawString("" + bestScore, 460, 290);
    }

    public void progressBar(Graphics g) {
        int xp = (int) score / 500;
        int atualXp = 0;
        Image img;
        switch (xp) {
            case 1:
                atualXp = 1;
                break;
            case 2:
                atualXp = 1;
                break;
            case 3:
                atualXp = 2;
                break;
            case 4:
                atualXp = 3;
                break;
            case 5:
                atualXp = 4;
                break;
            case 6:
                atualXp = 5;
                break;
            case 7:
                atualXp = 6;
                break;
            case 8:
                atualXp = 7;
                break;
            case 9:
                atualXp = 8;
                break;
            case 10:
                atualXp = 9;
                break;
            case 11:
                atualXp = 10;
                break;
            case 12:
                atualXp = 11;
                break;
            case 13:
                atualXp = 12;
                break;
        }
        if (atualXp > 4) {
            agua_viva.velocX = atualXp;
            nemo.velocY = atualXp;
            nadador.velocY = atualXp;
            if (numAguas > 15) {
                numAguas--;
            }
        }

        if (xp < 3 && atualXp == 5) {
            xp++;
            for (int i = 1; i - 1 < xp; i++) {
                img = (new ImageIcon(getClass().getResource("/imagens/star_icon.png"))).getImage();
                g.drawImage(img, x - img.getWidth(this), 8, this);
                System.out.println(i + " " + xp);
            }
        }

        img = xpBar[atualXp].getImage();
        int x = getWidth() - img.getWidth(this);
        g.drawImage(img, x, 8, null);
        
    }
}
