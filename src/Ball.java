import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Ball {
    // Attributs de position et de taille
    private int x;
    private int y;
    private final int SIZE = 15; // Taille (diamètre) de la balle

    // Attributs de vitesse (vecteur de déplacement)
    private int vx; // Vitesse sur l'axe X
    private int vy; // Vitesse sur l'axe Y

    // Constructeur
    public Ball(int startX, int startY, int speedX, int speedY) {
        this.x = startX;
        this.y = startY;
        this.vx = speedX;
        this.vy = speedY;
    }

    // Méthode de mise à jour de la position (appelée par la boucle de jeu)
    public void move() {
        x += vx; // x = x + vx
        y += vy; // y = y + vy
    }

    // Méthode de dessin (appelée par paintComponent)
    public void draw(Graphics g) {
        g.setColor(Color.orange); // Couleur de la balle
        g.fillOval(x, y, SIZE, SIZE);
    }

    // Méthode utile pour les collisions : retourne la boîte de collision
    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSIZE() { return SIZE; }


    // Getters et Setters pour la vitesse (nécessaire pour inverser la direction lors des rebonds)
    public int getVx() { return vx; }
    public int getVy() { return vy; }
    public void setVx(int vx) { this.vx = vx; }
    public void setVy(int vy) { this.vy = vy; }
}