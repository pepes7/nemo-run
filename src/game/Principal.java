package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class Principal extends JFrame implements ActionListener, KeyListener {

    protected boolean[] controleTecla = new boolean[2]; // vetor para armazenar as teclas pressionadas
    protected boolean jogando; //verifica de o usuario esta jogando
    protected int contAguas;
    protected Menu menu;
    protected Tela tela;
    protected Instrucoes inst;
    protected Opcoes sets;
    protected Musica musica;

    public Principal() {
        musica = new Musica(new File("res/menu.mp3"));
        menu = new Menu(this);
        tela = new Tela(this);
        inst = new Instrucoes(this);
        sets = new Opcoes(this);

        setTitle("Nemo Run");
        setSize(850, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        add(menu);

        tela.btPause.addActionListener(this);
        tela.addKeyListener(this);
        tela.setFocusable(true);

        menu.btStart.addActionListener(this);
        menu.btInst.addActionListener(this);
        menu.btSets.addActionListener(this);

        inst.btNext.addActionListener(this);
        inst.btPrevious.addActionListener(this);
        inst.btBack.addActionListener(this);

        sets.btMusicOn.addActionListener(this);
        sets.btMusicOff.addActionListener(this);
        sets.btSoundOn.addActionListener(this);
        sets.btSoundOff.addActionListener(this);
        sets.btBack.addActionListener(this);

        contAguas = 0;
        jogando = true;
    }

    public static void main(String[] args) {
        Principal p = new Principal();
        p.iniciaAnimacao();
    }

    public void iniciaAnimacao() {
        if (sets.music) {
            musica.start();
        }

        while (jogando) {
            if (tela.reinicia) { //Comando para fazer o nemo ficar parado quando reinicia o jogo
                controleTecla[0] = false;
                controleTecla[1] = false;
                update();
                tela.reinicia = false;
            }
            try {
                if (contAguas > tela.numAguas) {
                    tela.addAguaViva();
                    contAguas = 0;
                }
                update();
                tela.repaint();
                Thread.sleep(45);
                if (Util.colisao(tela.nemo, tela.nadador)) {
                    tela.control = Util.GAME_OVER;
                }
                contAguas++;
            } catch (InterruptedException e) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, e);
            }
            if (tela.control == Util.GAME_OVER) {
                Musica m = new Musica(new File("res/game_over.mp3"));
                m.start();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();
        setKey(tecla, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        setKey(tecla, false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (sets.sound) {
            Musica mu = new Musica(new File("res/click.mp3"));
            mu.start();
        }
        //Botoes do Menu Principal
        if (e.getSource().equals(menu.btStart)) {
            if (sets.music) {
                musica.stop();
                Musica m = new Musica(new File("res/corrida.mp3"));
                m.start();
            }
            menu.setVisible(false);
            this.add(this.tela);
            tela.requestFocus();
            tela.control = Util.PLAYING;
        } else {
            if (e.getSource().equals(menu.btInst)) {
                menu.setVisible(false);
                add(inst);
                inst.setVisible(true);
                inst.requestFocus();
            } else {
                if (e.getSource().equals(menu.btSets)) {
                    menu.setVisible(false);
                    add(sets);
                    sets.setVisible(true);
                    sets.requestFocus();
                }
            }
        }

        //Botoes de Instruções
        if (e.getSource().equals(inst.btNext)) {
            inst.next();
        } else {
            if (e.getSource().equals(inst.btPrevious)) {
                inst.previous();
            } else {
                if (e.getSource().equals(inst.btBack)) {
                    inst.reset();
                    inst.setVisible(false);
                    menu.setVisible(true);
                    menu.requestFocus();
                }
            }
        }

        //Botoes de Opcoes
        sets.switchButton(e);
        if (sets.music) {
            musica.resume();
        } else {
            musica.suspend();
        }

        if (e.getSource().equals(sets.btBack)) {
            sets.setVisible(false);
            menu.setVisible(true);
            menu.requestFocus();
        }

        //Botoes do Jogo
        if (e.getSource().equals(tela.btPause)) {
            //Aqui fica o código de pausar a tela
            System.out.println("pause");
        }
    }

    private void setKey(int tecla, boolean pressionada) { //metodo para saber se a tecla esta pressionada
        switch (tecla) {
            case KeyEvent.VK_ENTER:
                tela.control++;
                if (tela.control >= Util.GAME_OVER) {
                    tela.control = Util.PLAYING;
                    tela.inicializaComponentes();
                }
                break;
            case KeyEvent.VK_UP:
                // Seta para cima
                controleTecla[0] = pressionada;
                break;
            case KeyEvent.VK_DOWN:
                // Seta para baixo
                controleTecla[1] = pressionada;
                break;
        }
    }

    public void update() {
        if (controleTecla[0]) {
            if (tela.control >= Util.PLAYING) {
                tela.nemo.moveUp();
                tela.nadador.moveUp();
            }
        } else {
            tela.nemo.startingPosition();
            tela.nadador.startingPosition();
        }
        if (controleTecla[1]) {
            if (tela.control >= Util.PLAYING) {
                tela.nemo.moveDown();
                tela.nadador.moveDown();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
