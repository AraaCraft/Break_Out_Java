import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Paddle {
    private int x;
    private final int y; // Position Y fixe (verticale)
    private final int WIDTH = 100;
    private final int HEIGHT = 10;
    private int dx = 0; // Déplacement sur l'axe X (vitesse)
    private final int SPEED = 4; // Vitesse de déplacement

    // Constructeur : place la raquette au centre en bas
    public Paddle(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    // --- SETTER ---
    public int getX() {
        return x;
    }
    public int getWIDTH() {
        return WIDTH;
    }

    public void move(int gameWidth) {
        // Applique le déplacement
        x += dx;

        // Limite les bords pour ne pas sortir de l'écran
        if (x < 0) {
            x = 0;
        }
        if (x > gameWidth - WIDTH) {
            x = gameWidth - WIDTH;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }

    // Pour les collisions
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // Gestion des entrées clavier (appelées par le TAdapter de GamePanel)
    public void keyPressed(int keyCode) {
        if (keyCode == 37) { // 37 est le code pour la flèche Gauche (KeyEvent.VK_LEFT)
            dx = -SPEED;
        } else if (keyCode == 39) { // 39 est le code pour la flèche Droite (KeyEvent.VK_RIGHT)
            dx = SPEED;
        }
    }

    public void keyReleased(int keyCode) {
        if (keyCode == 37 || keyCode == 39) {
            dx = 0; // Arrête le mouvement
        }
    }
}