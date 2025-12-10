import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Brick {
    // ================== ATTRIBUTS ================================================================= //
    private int x, y;
    private final int WIDTH = 60;
    private final int HEIGHT = 20;
    // private boolean destroyed; // Est-ce que cette brique a été touchée et doit disparaître

    // Résistance de la brique (1 = fragile, 3 = très solide)
    private int resistance;

    // ================== CONSTRUCTEUR ============================================================= //
    public Brick(int x, int y, int initialResistance) {
        this.x = x;
        this.y = y;
        this.resistance = initialResistance; // Initialisation correcte
    }

    // ==================== METHODES ================================================================ //
    // Méthode appelée lorsque la balle FRAPPE la brique
    public void hit() {
        if (resistance > 0) {
            resistance--;
        }
    }

    // DEV : Méthode pour la destruction instantanée
    public void kill() {
        // Met la résistance à 0, marquant la brique comme détruite
        resistance = 0;
    }

    // Détermine la couleur en fonction de la résistance
    private Color getColor() {
        switch (resistance) {
            case 3:
                return Color.RED;      // La plus forte
            case 2:
                return Color.ORANGE;   // Résistance moyenne
            case 1:
                return Color.YELLOW;   // La plus faible
            default:
                return Color.BLACK;    // Brique invisible (détruite)
        }
    }

    public void draw(Graphics g) {
        if (resistance > 0) { // On dessine si la résistance est supérieure à 0
            g.setColor(getColor()); // <--- Utilise la nouvelle méthode
            g.fillRect(x, y, WIDTH, HEIGHT);

            // Dessiner un contour pour mieux séparer les briques
            g.setColor(Color.BLACK);
            g.drawRect(x, y, WIDTH, HEIGHT);
        }
    }

    // Vérifie si la brique est détruite (résistance <= 0)
    public boolean isDestroyed() {
        return resistance <= 0;
    }

    // Pour la détection de collision
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // Getter pour afficher afficher la vie de la brique
    public int getResistance() {
        return resistance;
    }
}