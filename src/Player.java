/**
 * Represents a player character that extends the basic functionality of a Character.
 * Includes a field for the number of scriptures, which may be used for specific gameplay mechanics.
 */
public class Player extends Character {
    private int scriptures;

    /**
     * Constructor for Player class, initializes the player's health and scriptures.
     *
     * @param health The health value of the player.
     * @param scriptures The number of scriptures the player has.
     */
    public Player(int health, int scriptures) {
        super("Player", health);  // Call the superclass (Character) constructor
        this.scriptures = scriptures;
    }

    // Getters and setters
    public int getScriptures() {
        return scriptures;
    }

    public void setScriptures(int scriptures) {
        this.scriptures = scriptures;
    }
}
