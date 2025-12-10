import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Font;

public class GamePanel extends JPanel implements ActionListener {
    // ================ ATTRIBUTS ==============================================================
    // Fréquence de rafraîchissement (Update rate)
    private final int DELAY = 10; // 10 millisecondes (100 fois par seconde)
    private Timer timer;

    // Objets du jeu
    private Ball ball;
    private Paddle paddle;
    private ArrayList<Brick> bricks;

    private final int MAX_LEVELS = 3; // Nombre total de niveaux
    private int currentLevel = 1;      // Niveau actuel

    private int score = 0;
    private boolean gameWon = false;
    private boolean gameOver = false;
    private boolean gameStarted = false; // Le jeu est en pause au démarrage

    private boolean devMode = false; // Mode DEV

    // ================================ CONSTRUCTEUR =====================================
    public GamePanel() {
        // Initialisation des objets de jeu (Balle, Raquette, Briques)
        // La taille de la fenêtre dans Main.java est 800x600
        ball = new Ball(400, 300, 3, 3);
        paddle = new Paddle(400 - 50, 550);

        bricks = new ArrayList<>();
        createBricks();

        // Initialisation du Timer
        timer = new Timer(DELAY, this);
        // timer.start(); <--- commenté pour démarrer le jeu en 'pause'

        // Initialisation de la gestion du clavier pour bouger la raquette
        this.addKeyListener(new TAdapter());
        this.setFocusable(true);
        this.setBackground(Color.BLACK);

        // S'assurer que le panneau a le focus au démarrage
        requestFocusInWindow();
    }

    // ================= METHODES ========================================================================================
    // REINITIALISE la balle et la raquette sans réinitialiser le score et les compteurs de victoire/défaite
    private void resetLevel() {
        // Réinitialisation de la balle au centre
        ball = new Ball(400, 300, 3, 3);

        // Réinitialisation de la raquette au centre
        paddle = new Paddle(400 - 50, 550);

        // Créer le nouveau mur de briques pour le niveau actuel
        createBricks();

        // C'est cette partie qui fait la pause pour le jeu normal
        gameStarted = false;
        timer.stop();

        requestFocusInWindow();
    }

    // Création des briques
    private void createBricks() {
        int rows = 5;
        int cols = 10;
        int topMargin = 50;
        int horizontalSpacing = 5;
        int verticalSpacing = 5;
        int brickWidth = 60;
        int brickHeight = 20;

        // Vider la liste pour le nouveau niveau
        bricks.clear();

        // Calculer le point de départ X pour centrer les briques
        int totalBrickRowWidth = (cols * brickWidth) + ((cols - 1) * horizontalSpacing);
        int startX = (800 - totalBrickRowWidth) / 2;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = startX + j * (brickWidth + horizontalSpacing);
                int y = topMargin + i * (brickHeight + verticalSpacing);

                // Logique de résistance basée sur le niveau et la position
                int resistance = 0;

                if (currentLevel == 1) {
                    // Niveau 1 : Toutes les briques sont fragiles (résistance 1)
                    resistance = 1;
                } else if (currentLevel == 2) {
                    // Niveau 2 : Briques fortes (résistance 2) au centre
                    resistance = (j >= 3 && j <= 6) ? 2 : 1;
                } else if (currentLevel == 3) {
                    // Niveau 3 : Briques très fortes (résistance 3) et en quinconce
                    if (i % 2 == 0) {
                        // Lignes paires : résistance 3 (centre) ou 1 (bords)
                        resistance = (j >= 4 && j <= 5) ? 3 : 1;
                    } else {
                        // Lignes impaires : résistance 2
                        resistance = 2;
                    }
                }

                if (resistance > 0) {
                    // Créer la brique avec la résistance calculée
                    bricks.add(new Brick(x, y, resistance)); // <--- MODIFIÉ
                }
            }
        }
    }

    // ================= SYSTEME DE COLLISIONS ============================================ //
    private void checkCollisions() {
        // --- Dimensions du panneau de jeu ---
        int width = getWidth();
        int height = getHeight();

        // --- Collision avec les MURS Gauche et Droite ---
        if (ball.getX() <= 0 || ball.getX() + ball.getSIZE() >= width) {
            ball.setVx(-ball.getVx());
        }

        // --- Collision avec le MUR du Haut ---
        if (ball.getY() <= 0) {
            ball.setVy(-ball.getVy());
        }

        // --- Collision avec le MUR du Bas (Défaite) ---
        if (ball.getY() + ball.getSIZE() >= height) {
            if (!devMode) { // Vérifie si le mode dev est OFF
                gameOver = true;
            } else {
                // Si devMode est ON : la balle rebondit au lieu de perdre
                ball.setVy(-ball.getVy());
            }
        }

        // --- Collision BALLE / RAQUETTE ---
        if (ball.getBounds().intersects(paddle.getBounds())) {

            // 1. Calculer le point de contact central de la balle sur la raquette
            // L'impactZone va de -1 (extrême gauche) à +1 (extrême droite)

            // Centre de la raquette
            double paddleCenter = paddle.getX() + (paddle.getWIDTH() / 2.0);
            // Centre de la balle
            double ballCenter = ball.getX() + (ball.getSIZE() / 2.0);

            // Différence entre les centres (de -paddle.WIDTH/2 à +paddle.WIDTH/2)
            double distance = ballCenter - paddleCenter;

            // Normalisation de la distance sur une échelle de -1.0 à 1.0
            double impactZone = (distance / (paddle.getWIDTH() / 2.0));

            // 2. Inverser la direction verticale (VY)
            // Toujours inverser la direction Y, mais s'assurer qu'elle monte
            if (ball.getVy() > 0) {
                ball.setVy(-ball.getVy());
            }

            // 3. Appliquer l'angle (VX)
            // Vitesse totale actuelle (norme) : V = sqrt(vx^2 + vy^2)
            double currentSpeed = Math.sqrt(Math.pow(ball.getVx(), 2) + Math.pow(ball.getVy(), 2));

            // L'impactZone est utilisé comme un angle (simulé)
            // Multiplier la norme par l'impactZone pour obtenir la nouvelle VX
            // On limite l'impact à un facteur max (ex: 0.8) pour ne pas aller trop horizontalement
            double newVx = currentSpeed * impactZone * 0.8;

            // Mettre à jour les vitesses (on réutilise l'ancienne VY mais inversée)
            ball.setVx((int) newVx);

            // Assurez-vous que la vitesse Y reste d'une magnitude respectable (au moins 2)
            if (Math.abs(ball.getVy()) < 2) {
                ball.setVy(ball.getVy() < 0 ? -2 : 2);
            }
        }

        // --- Collision BALLE / BRIQUES ---
        int destroyedCount = 0;
        boolean collisionOccurred = false;

        for (Brick brick : bricks) {
            if (brick.isDestroyed()) {
                destroyedCount++;
                continue;
            }

            // Vérification de la collision avec une brique non détruite
            if (ball.getBounds().intersects(brick.getBounds())) {

                // 1. Frapper la brique (décrémente la résistance)
                brick.hit();

                // 2. Inverser la direction Y de la balle
                ball.setVy(-ball.getVy());

                // 3. Ajouter du score SEULEMENT si la brique est maintenant détruite
                if (brick.isDestroyed()) {
                    score += 10;
                    destroyedCount++;
                }

                // Sortir de la boucle des briques après le premier impact
                break;
            }
        }

        // Vérification de la Victoire ou de l'Avancement de Niveau (Logique de fin de boucle)
        // Cette logique DOIT être à la fin après le calcul de destroyedCount
        if (destroyedCount == bricks.size() && bricks.size() > 0) {
            if (currentLevel < MAX_LEVELS) {
                // Avancer au niveau suivant
                currentLevel++;
                resetLevel(); // Passe au niveau suivant
            } else {
                // Dernier niveau terminé -> Victoire finale
                gameWon = true;
            }
        }
    }

    // --- Méthode utilitaire pour AFFICHER les MESSAGES au centre ---
    private void displayMessage(Graphics g, String message, boolean includeRestartPrompt) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 36));

        // Centrer le texte
        int x = (getWidth() - g.getFontMetrics().stringWidth(message)) / 2;
        int y = getHeight() / 2;

        g.drawString(message, x, y);

        if (includeRestartPrompt) { // <-- Affichage conditionnel
            // Indiquer comment redémarrer
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.ITALIC, 20));
            String restartMessage = "Appuyez sur ENTRÉE pour rejouer !";
            int restartX = (getWidth() - g.getFontMetrics().stringWidth(restartMessage)) / 2;
            g.drawString(restartMessage, restartX, y + 40);
        }
    }

    // --- Méthode pour RELANCER LE JEU (Redémarrage Complet) ---
    private void resetGame() {
        // Réinitialisation des attributs de jeu
        gameOver = false;
        gameWon = false;
        score = 0;

        // Force le redémarrage au Niveau 1
        currentLevel = 1;

        // Réinitialisation des objets
        ball = new Ball(400, 300, 3, 3);
        paddle = new Paddle(400 - 50, 550);

        // 3. Recréer le mur de briques (crée le niveau 1)
        bricks.clear();
        createBricks();

        // Redémarrer la boucle de jeu
        gameStarted = true;
        timer.start();

        requestFocusInWindow();
    }


    // --- Méthode pour REDEMARRER le NIVEAU ACTUEL (Mode Dev) ---
    private void restartCurrentLevel() {
        // Réinitialisation des attributs de jeu
        gameOver = false;
        gameWon = false;
        score = 0; // Remettre à 0 est plus standard pour un redémarrage de partie.

        // currentLevel N'EST PAS réinitialisé

        // Réinitialisation des objets
        ball = new Ball(400, 300, 3, 3);
        paddle = new Paddle(400 - 50, 550);

        // 3. Recréer le mur de briques (crée le niveau actuel)
        bricks.clear();
        createBricks();

        // Redémarrer la boucle de jeu
        gameStarted = true;
        timer.start();

        requestFocusInWindow();
    }

    // --- Méthode : même travail que resetLevel(), mais SANS TOUCHER à l'ETAT gameStarted ni au timer ---
    private void resetLevelQuickDev() {
        // Réinitialisation de la balle au centre
        ball = new Ball(400, 300, 3, 3);

        // Réinitialisation de la raquette au centre
        paddle = new Paddle(400 - 50, 550);

        // Créer le nouveau mur de briques pour le niveau actuel
        createBricks();

        // NE PAS faire : timer.stop();
        // NE PAS faire : gameStarted = false;

        // Le jeu continue immédiatement
        requestFocusInWindow();
    }

    // ============== @OVERRIDE ============================================================= //

    // La méthode de mise à jour (La "boucle de jeu")
    @Override
    public void actionPerformed(ActionEvent e) {
        // Le jeu ne se met à jour que s'il n'est ni gagné ni perdu
        if (!gameOver && !gameWon && gameStarted) {
            ball.move();
            paddle.move(getWidth());
            checkCollisions();
        }

        // Redessiner l'écran
        repaint();
    }

    // La méthode de dessin (RENDER)
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessin des objets du jeu (balle, raquette, briques)
        ball.draw(g);
        paddle.draw(g);
        for (Brick brick : bricks) {
            brick.draw(g);
        }

        // Affichage du score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 20);

        // Affichage du Niveau Actuel
        g.drawString("Niveau: " + currentLevel, 10, 40);

        // Affichage du Mode DEVELOPPEUR (Divisé sur deux lignes)
        if (devMode) {
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 14));

            // Ligne 1 : Statut du mode
            String line1 = "MODE DEV (D) ON - [N] Niv. Suiv - [R] Niv. Actuel";
            g.drawString(line1, getWidth() - g.getFontMetrics().stringWidth(line1) - 10, 20); // Alignement à droite, marge de 10px

            // Ligne 2 : Fonction K
            String line2 = "[K] Detruire tout";
            g.drawString(line2, getWidth() - g.getFontMetrics().stringWidth(line2) - 10, 35); // Décalé de 15px vers le bas
        }

        // Affichage des messages de fin de partie
        if (gameOver) {
            // Le jeu est terminé : affiche le message de fin ET l'instruction de redémarrage.
            displayMessage(g, "GAME OVER - Score final : " + score, true); // <--- MODIFIÉ : true
            timer.stop();
        } else if (gameWon) {
            // Le jeu est terminé : affiche le message de victoire ET l'instruction de redémarrage.
            displayMessage(g, "VICTOIRE ! Bravo ! Score : " + score, true); // <--- MODIFIÉ : true
            timer.stop();
        } else if (!gameStarted) {
            // Le jeu est en pause de démarrage : n'affiche QUE l'instruction de lancement.
            displayMessage(g, "Appuyez sur ENTRÉE pour lancer la balle !", false); // <--- MODIFIÉ : false
        }
    }

    // Classe interne TAdapter
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            // --- DEV : Activation/Désactivation du mode développeur (touche D)
            if (keyCode == KeyEvent.VK_D) {
                devMode = !devMode; // Inverse l'état (true devient false, et inversement)
                System.out.println("Mode Développeur : " + (devMode ? "ACTIVÉ" : "DÉSACTIVÉ"));
                return;
            }

            // --- DEV : Fonction de Saut de Niveau (touche N)
            if (devMode && keyCode == KeyEvent.VK_N) {
                if (currentLevel < MAX_LEVELS) {
                    currentLevel++;

                    // Appeler une nouvelle méthode qui ne met PAS en pause le timer et garde l'état 'gameStarted' à true.
                    resetLevelQuickDev();

                    System.out.println("Niveau sauté. Niveau actuel : " + currentLevel);
                } else {
                    // Si déjà au dernier niveau, marquer comme gagné pour tester la victoire
                    gameWon = true;
                }
                return;
            }

            // --- DEV - Redémarrage au Niveau Actuel (touche R)
            if (devMode && (gameOver || gameWon) && keyCode == KeyEvent.VK_R) {
                restartCurrentLevel();
                System.out.println("Partie redémarrée au Niveau " + currentLevel + " (Mode Dev)");
                return;
            }

            // --- DEV : Destruction Instantanée des Briques (touche K)
            if (devMode && keyCode == KeyEvent.VK_K && !gameOver && !gameWon) {
                // Marquer toutes les briques comme détruites
                for (Brick brick : bricks) {
                    if (!brick.isDestroyed()) {
                        score += 10; // Ajouter le score (comme si elle avait été détruite normalement)
                        brick.kill(); // DÉTRUIRE
                    }
                }

                // Déclencher la logique de transition de niveau
                if (currentLevel < MAX_LEVELS) {
                    currentLevel++;
                    // On utilise la réinitialisation rapide pour continuer immédiatement
                    resetLevelQuickDev();
                    System.out.println("Triche activée (K). Niveau suivant : " + currentLevel);
                } else {
                    gameWon = true; // Victoire finale
                    System.out.println("Triche activée (K). VICTOIRE !");
                    // timer.stop(); // Arrêter le jeu // <--- Il sera finalement stoppé par paintComponent
                }
                repaint(); // Force un rafraîchissement immédiat pour voir la transition
                return;
            }

            // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
            // 1. Gérer le lancement (si le jeu n'a pas encore été démarré)
            if (!gameStarted && keyCode == KeyEvent.VK_ENTER) {
                gameStarted = true; // Le jeu est marqué comme démarré
                timer.start();      // Le timer est lancé
                return;
            }

            // 2. Gérer le redémarrage (si la partie est finie)
            if ((gameOver || gameWon) && keyCode == KeyEvent.VK_ENTER) {
                resetGame();
                return;
            }

            // 3. Gérer le déplacement de la raquette
            if (gameStarted && !gameOver && !gameWon) { // <-- Seul le mouvement est géré si le jeu est actif
                paddle.keyPressed(keyCode);
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {
            // Gérer l'arrêt du déplacement de la raquette si le jeu est en cours
            if (gameStarted && !gameOver && !gameWon) {
                paddle.keyReleased(e.getKeyCode());
            }
        }
    }
}
