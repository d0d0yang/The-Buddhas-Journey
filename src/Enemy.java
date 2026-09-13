import processing.core.PImage;

/**
 * The Enemy class represents an enemy character in the game.
 * It extends the Character class and adds enemy-specific attributes,
 * such as a portrait image and dialogue.
 */

public class Enemy extends Character {
    // Portrait image
    private PImage portraitImage;

    // Dialogue for the enemy
    private String dialogue;

    /**
     * Constructor for the enemy class.
     * Initializes the enemy's life force and portrait image.
     *
     * @param lifeForce The life force value of the enemy.
     * @param portraitImage The portrait image of the enemy.
     */
    public Enemy(int lifeForce, PImage portraitImage) {
        // Call the superclass (Character) constructor to initialize life force
        super("Enemy");
        setLifeForce(50);
        // Set the enemy's portrait image
        this.portraitImage = portraitImage;
    }



    // Getters and setters
    public PImage getPortraitImage() {
        return portraitImage;
    }

    public void setPortraitImage(PImage portraitImage) {
        this.portraitImage = portraitImage;
    }

    public String getDialogue() {
        return dialogue;
    }

    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }
}
