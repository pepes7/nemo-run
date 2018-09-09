package game;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import javazoom.jl.player.Player;

public class Musica extends Thread {

    private File mp3;
    private Player player;

    public Musica(File mp3){
        this.mp3 = mp3;
    }
    
    @Override
    public void run() {
        try {
            FileInputStream fis = new FileInputStream(mp3);
            BufferedInputStream bis = new BufferedInputStream(fis);

            this.player = new Player(bis);
            this.player.play();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
