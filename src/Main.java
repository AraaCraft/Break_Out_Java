import javax.swing.JFrame;
import java.awt.EventQueue; // Pour s'assurer que l'interface graphique est gérée correctement

public class Main {

    public static void main(String[] args) {

        // Utiliser EventQueue.invokeLater pour garantir que la création de l'interface
        // se fait sur le Thread de répartition des événements de Swing (EDT).
        EventQueue.invokeLater(() -> {

            // 1. Créer le panneau de jeu
            GamePanel gamePanel = new GamePanel();

            // 2. Créer la fenêtre principale
            JFrame frame = new JFrame("Casse-Brique -- Break Out");

            // 3. Ajouter le panneau de jeu à la fenêtre
            frame.add(gamePanel);

            // 4. Configurer la fenêtre
            frame.setSize(800, 600); // Définissez la taille de votre jeu
            frame.setResizable(false); // Empêcher le redimensionnement de la fenêtre
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Quitte l'application quand on ferme la fenêtre
            frame.setLocationRelativeTo(null); // Centre la fenêtre à l'écran

            // 5. Afficher la fenêtre
            frame.setVisible(true);

            // Très important pour que le GamePanel puisse recevoir les événements clavier
            gamePanel.requestFocusInWindow();
        });
    }
}