# 🧱 Casse-Brique / Breakout - Java Swing

Ce projet est une implémentation classique du jeu d'arcade Casse-Brique (Breakout/Arkanoid), développé en Java en utilisant la librairie graphique Swing. Il a été réalisé dans le cadre de ma formation en **DEUST Informatique Organisation des Systèmes d'Information** à titre d'entraînement personnel.

## 🚀 Fonctionnalités Implémentées

Ce projet met en application des concepts fondamentaux de la programmation orientée objet et de la logique de jeu 2D :

* **Jeu à Niveaux (Levels):** Mise en place d'une transition entre **3 niveaux** avec des patterns de briques différents.
* **Briques à Résistance Variable:** Les briques nécessitent jusqu'à **3 coups** pour être détruites, avec une couleur changeante pour indiquer leur état (Jaune, Orange, Rouge).
* **Physique Avancée:** Gestion précise des collisions et implémentation du **rebond angulaire** sur la raquette pour permettre le contrôle de la balle par le joueur.
* **Architecture POO:** Séparation claire des responsabilités entre les objets (`Ball`, `Paddle`, `Brick`, `GamePanel`).
* **Contrôle d'État:** Gestion des états de jeu (Pause au démarrage, En jeu, Victoire, Défaite) via le `javax.swing.Timer`.

## 🛠 Mode Développeur (Mode Dev)

Un mode développeur a été inclus pour faciliter les tests et démontrer la capacité à construire des outils de débogage :

| Touche | Fonction | Description |
| :---: | :--- | :--- |
| **D** | Activation/Désactivation | Active ou désactive le mode développeur. |
| **N** | Niveau Suivant | Saute instantanément au niveau suivant (Niveau max : Victoire). |
| **K** | Destruction Totale | Détruit instantanément toutes les briques du niveau pour tester la transition. |
| **R** | Redémarrage Rapide | Redémarre la partie au niveau actuel (uniquement après une défaite/victoire en mode Dev). |

## 💻 Comment Lancer le Jeu

### Pré-requis

* JDK 21 ou supérieur (par exemple : **Azul Zulu 25** que j'ai utilisé).
* Un environnement de développement (IDE) comme IntelliJ IDEA ou Eclipse.

### Étapes de Lancement

1.  **Cloner le dépôt :**
    ```bash
    git clone [https://www.wordreference.com/fren/d%C3%A9p%C3%B4t](https://www.wordreference.com/fren/d%C3%A9p%C3%B4t)
    ```
2.  **Ouvrir le projet** dans votre IDE.
3.  Exécuter la classe principale **`Main.java`**.

---

## 👤 Auteur

**Margaux Brun**
* Étudiante en 2ème année de **DEUST IOSI (Informatique)** en alternance.
* [ https://www.linkedin.com/in/margaux-brun-63452830b/ ]
