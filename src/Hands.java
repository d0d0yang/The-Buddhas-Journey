import java.util.ArrayList;


/**
 * Manages the cards held by the player and the enemy.
 * Handles drawing cards, updating positions, interacting with cards via mouse clicks,
 * and maintaining the layout of cards in the hands.
 */
public class Hands {
    // Player and enemy hands
    private ArrayList<Cards> playerHand = new ArrayList<>();;
    private ArrayList<Cards> enemyHand = new ArrayList<>();;

    // Position of the cards within the hand
    private int handY = 400; // Fixed Y position for player's hand
    private int maxHandWidth = 800; // Maximum width the hand can occupy
    private int screenWidth = 1200; // Screen width for centering cards

    // External references
    Sketch sketch;
    Decks decks;

    /**
     * Constructor for Hands class with the Sketch instance.
     *
     * @param sketch The Sketch instance for graphics.
     */
    public Hands(Sketch sketch) {
        this.sketch = sketch;
    }

    public void setDecks(Decks decks) {
        this.decks = decks;
    }

    /**
     * Handles mouse clicks on the player's hand to toggle card selection.
     *
     * @param selectedCard The currently selected card (can be null).
     * @param mouseX The X-coordinate of the mouse click.
     * @param mouseY The Y-coordinate of the mouse click.
     * @param index The index of the card to check.
     * @return The newly selected card, or null if deselected.
     */
    public Cards clickHands(Cards selectedCard, int mouseX, int mouseY, int index) {
        // Base case: if the index is out of bounds, return the current selectedCard
        if (index < 0) {
            return selectedCard;
        }

        // Get the card at the current index
        Cards card = playerHand.get(index);

        // Check if the mouse is over the card
        if (mouseX >= card.getCurrentX() && mouseX <= card.getCurrentX() + card.getCardWidth() &&
                mouseY >= card.getCurrentY() && mouseY <= card.getCurrentY() + card.getCardHeight()) {
            // Toggle selection
            return selectedCard == card ? null : card;
        }

        // Recursive call with the next index
        return clickHands(selectedCard, mouseX, mouseY, index - 1);
    }


    /**
     * Adds a card to the hand based on the specified hand type (player or enemy).
     *
     * @param hand 0 for the player's hand, 1 for the enemy's hand.
     */
    public void addCardToHand(int hand) {
        if (hand == 0) {
            // Check if the player's deck is empty, reload and shuffle it if needed
            if (decks.getPlayerCurrentDeck().isEmpty()) {
                decks.loadCurrentDeck(0);
                decks.shuffleCurrentDeck(0);
            }

            // Get the top card from the player's deck
            Cards card = decks.getPlayerCurrentDeck().get(0);

            // Add the card to the player's hand
            playerHand.add(card);

            // Log the card draw to the console
            sketch.printToConsole("+ You draw: " + card.getCardName());

            // Remove the drawn card from the player's deck
            decks.setPlayerCurrentDeck(new ArrayList<>(decks.getPlayerCurrentDeck().subList(1, decks.getPlayerCurrentDeck().size())));
        } else {
            // Check if the enemy's deck is empty, reload and shuffle it if needed
            if (decks.getEnemyCurrentDeck().isEmpty()) {
                decks.loadCurrentDeck(1);
                decks.shuffleCurrentDeck(1);
            }

            // Log the card draw for the enemy
            sketch.printToConsole("- The enemy draws a card");

            // Add the top card to the enemy's hand
            enemyHand.add(decks.getEnemyCurrentDeck().get(0));

            // Remove the drawn card from the enemy's deck
            decks.setEnemyCurrentDeck(new ArrayList<>(decks.getEnemyCurrentDeck().subList(1, decks.getEnemyCurrentDeck().size())));
        }
    }

    /**
     * Removes the first card from the specified hand.
     *
     * @param hand 0 for the player's hand, 1 for the enemy's hand.
     */
    public void removeCardFromHand(int hand) {
        // Player hand
        if (hand == 0) {
            playerHand.remove(0);
        }
        // Enemy hand
        else {
            enemyHand.remove(0);
        }
    }


    /**
     * Removes a specific card from the specified hand.
     *
     * @param hand 0 for the player's hand, 1 for the enemy's hand.
     * @param card The card to remove.
     */
    public void removeCardFromHand(int hand, Cards card) {
        if (hand == 0) {
            // Remove the card from the player's hand
            playerHand.remove(card);
        } else {
            // Remove the card from the enemy's hand
            enemyHand.remove(card);
        }
    }

    /**
     * Updates the positions of the cards in the player's hand when placed in a lane.
     */
    public void updateHandPos () {
        int cardWidth = playerHand.isEmpty() ? 0 : playerHand.get(0).getCardWidth(); // Assume all cards have the same width
        int totalHandWidth = Math.min(maxHandWidth, playerHand.size() * cardWidth); // Hand width limited by maxHandWidth
        int overlapSpacing = playerHand.size() > 1 ? (totalHandWidth - cardWidth) / (playerHand.size() - 1) : 0;
        overlapSpacing = Math.max(overlapSpacing, 10); // Minimum overlap spacing to prevent complete overlap

        int startX = (screenWidth - totalHandWidth) / 2; // Calculate starting X position

        // For each card in the hand
        for (int i = 0; i < playerHand.size(); i++) {
            Cards card = playerHand.get(i);
            card.setCurrentX(startX + i * overlapSpacing);
            card.setCurrentY(handY);
        }
    }

    // Getters
    public ArrayList<Cards> getPlayerHand() {
        return playerHand;
    }

    public ArrayList<Cards> getEnemyHand() {
        return enemyHand;
    }
}
