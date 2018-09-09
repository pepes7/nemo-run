package game;

public class Util {

    public final static int MAIN = 1;
    public final static int PLAYING = 2;
    public final static int GAME_OVER = 3;
    public final static int LEVELS = 3;
    
    public static boolean colisao(Objeto a, Objeto b) {
        // Plano de colisao X
        int aColisionPanelWidth = a.x + a.largura;
        int bColisionPanelWidth = b.x + b.largura;

        // Plano de colisao Y
        int aColisionPanelHeight = a.y + a.altura;
        int bColisionPanelHeight = b.y + b.altura;

        // verifica se houve colisão
        if ((a.x >= b.x && a.x <= bColisionPanelWidth
                || b.x >= a.x && b.x <= aColisionPanelWidth)
                && (a.y >= b.y && a.y <= bColisionPanelHeight
                || b.y >= a.y && b.y <= aColisionPanelHeight)) {
            return true;
        }
        return false;
    }
}