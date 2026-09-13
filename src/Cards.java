import processing.core.PImage;

/**
 * The Cards class represents a card in the game, including its properties, effects,
 * and graphics. Cards can have different effects for the player and enemy for balancing
 * and other purposes.
 * <p>
 * Implements cloneable to clone individual cards
 */
public class Cards implements Cloneable {
    // The name of the card
    private final String cardName;
    // Index to identify the card
    private final int cardIndex;
    // Game effect triggered when the player uses the card
    private final Runnable playerCardEffect;
    // Game effect triggered when the enemy uses the card
    private final Runnable enemyCardEffect;
    // Life force value of the card
    private int lifeForce;
    // Power value of the card
    private int power;
    // Description of the card's game effect
    private final String cardEffectLore;
    // File path to the card's image
    private final String cardImagePath;
    // Current lane position
    private int currentLane;
    // Current pos of the card graphically
    private int currentX, currentY;
    // Width of the card in pixels
    private static final int cardWidth = 140;
    // Height of the card in pixels
    private static final int cardHeight = 180;
    // Card PImage
    private PImage cardImage;

    /**
     * Constructs a new Card object with specified properties.
     *
     * @param sketch           Sketch object for graphics.
     * @param cardName         Name of the card.
     * @param cardIndex        Index of the card.
     * @param playerCardEffect The executed game effect when player-played.
     * @param enemyCardEffect  The executed game effect when enemy-played.
     * @param lifeForce        Life force value.
     * @param power            Power value.
     * @param cardEffectLore   Game card effect description.
     * @param cardImagePath    File path to card image.
     */
    public Cards(Sketch sketch, String cardName, int cardIndex, Runnable playerCardEffect, Runnable enemyCardEffect,
                 int lifeForce, int power, String cardEffectLore, String cardImagePath) {
        this.cardName = cardName;
        this.cardIndex = cardIndex;
        this.playerCardEffect = playerCardEffect;
        this.enemyCardEffect = enemyCardEffect;
        this.lifeForce = lifeForce;
        this.power = power;
        this.cardEffectLore = cardEffectLore;
        this.cardImagePath = cardImagePath;
        // Load the image from the given path
        this.cardImage = sketch.loadImage(cardImagePath);
        // Initialize the card's lane position
        this.currentLane = 0;
        // Default off-screen X position
        this.currentX = -9999;
        // Default off-screen Y position
        this.currentY = -9999;
    }

    /**
     * Plays the card by executing its effect for either player or enemy.
     *
     * @param user Indicates who is playing the card (0 for player, 1 for enemy).
     */
    public void playCard(int user) {
        // Player
        if (user == 0) {
            System.out.println("Player runs effects");
            this.playerCardEffect.run();
        }
        // Enemy
        else {
            System.out.println("Enemy runs effects");
            this.enemyCardEffect.run();
        }
    }

    /**
     * Creates a copy of this Cards object to use in game.
     * Idea is to use the original cards as a repository for cards
     * with new cards being clones from these repositories.
     * This system allows for multiple copies of identical cards in-game.
     *
     * @return A new Cards object with the same properties as this one.
     */
    @Override
    protected Cards clone() {
        try {
            // Clone the parent
            Cards clonedCard = (Cards) super.clone();

            // Deep clone mutable fields if necessary
            if (this.cardImage != null) {
                clonedCard.cardImage = this.cardImage.copy();
            }
            return clonedCard;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }

    /**
     * Returns a string representation of the card.
     * Used mostly for debugging.
     *
     * @return A string containing the card's name, index, life force, power, and image path.
     */
    @Override
    public String toString() {
        return "Cards{" +
                "cardName='" + cardName + '\'' +
                ", cardIndex=" + cardIndex +
                ", lifeForce=" + lifeForce +
                ", power=" + power +
                ", cardImagePath='" + cardImagePath + '\'' +
                '}';
    }

    // Getters and setters for card properties

    public String getCardName() {
        return cardName;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public int getLifeForce() {
        return lifeForce;
    }

    public void setLifeForce(int lifeForce) {
        this.lifeForce = lifeForce;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getCardEffectLore() {
        return cardEffectLore;
    }

    public int getCurrentLane() {
        return currentLane;
    }

    public void setCurrentLane(int currentLane) {
        this.currentLane = currentLane;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public int getCardHeight() {
        return cardHeight;
    }

    public PImage getCardImage() {
        return cardImage;
    }
}
