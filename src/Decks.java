import java.util.ArrayList;

/**
 * The Decks class manages the player's and enemy's decks in the game.
 * Decks are all the card available to play throughout the entire game.
 * It allows for operations such as adding cards to the deck, reloading decks, and shuffling.
 */
public class Decks {
    // Card list repository
    private ArrayList<Cards> cardList;
    // 2D array for storing full decks (player's deck at index 0, enemy's deck at index 1)
    private Cards[][] fullDecks;
    // Current decks represent the remaining cards in a player's deck.
    // For example, a player draws 5 cards from a full deck of 20. The current deck is 15.
    // An arraylist storing the player's current deck
    private ArrayList<Cards> playerCurrentDeck;
    // An arraylist storing the enemy's current deck
    private ArrayList<Cards> enemyCurrentDeck;

    /**
     * Constructor for initializing the Decks class.
     * Sets up empty current decks and a 2D array for the full decks.
     */
    public Decks() {
        this.playerCurrentDeck = new ArrayList<>();
        this.enemyCurrentDeck = new ArrayList<>();

        // Initialize the 2D array (2 rows for player and enemy)
        fullDecks = new Cards[2][];

        // Initialize player and enemy decks with empty arrays
        fullDecks[0] = new Cards[0];
        fullDecks[1] = new Cards[0];
    }

    /**
     * Sets the card list to be used in the decks.
     *
     * @param cardList The list of available cards repository.
     */
    public void setCardList(ArrayList<Cards> cardList) {
        this.cardList = cardList;
    }

    /**
     * Adds a card to the specified deck (player or enemy) using the card's index.
     *
     * @param deck The deck to which the card should be added (0 for player, 1 for enemy).
     * @param cardIndex The index of the card to be added.
     */
    // Add card to the full deck (player or enemy) using cardIndex
    public void addCardToDeck(int deck, int cardIndex) {
        // Search for the card in the cardList using the cardIndex
        for (Cards card : cardList) {
            if (card.getCardIndex() == cardIndex) {
                try {
                    // Clone the card
                    Cards clonedCard = card.clone();

                    // Resize the fullDecks array to add the new card
                    Cards[] newDeck = new Cards[fullDecks[deck].length + 1];
                    System.arraycopy(fullDecks[deck], 0, newDeck, 0, fullDecks[deck].length);
                    newDeck[fullDecks[deck].length] = clonedCard;
                    // Replace the old deck with the new one
                    fullDecks[deck] = newDeck;
                    // Exit after adding the cloned card
                    return;
                } catch (Exception e) {
                    System.out.println("Error cloning card: " + e.getMessage());
                }
            }
        }
        // If no card is found, print an error message for debug
        System.out.println("Card with index " + cardIndex + " not found in cardList.");
    }

    /**
     * Reloads the current deck for the specified player or enemy from the full deck.
     * Effectively replenishes the current deck when the player has drawn them all
     *
     * @param deck The deck to reload (0 for player, 1 for enemy).
     */
    // Reload current deck for player or enemy from the full deck
    public void loadCurrentDeck(int deck) {
        // Choose the appropriate current deck (player or enemy) based on the deck parameter
        ArrayList<Cards> currentDeck = (deck == 0) ? playerCurrentDeck : enemyCurrentDeck;
        Cards[] fullDeck = fullDecks[deck];

        // Clear the current deck before reloading
        currentDeck.clear();

        // Clone each card from the full deck and add it to the current deck
        for (Cards card : fullDeck) {
            try {
                Cards clonedCard = card.clone(); // Clone the card
                currentDeck.add(clonedCard); // Add the cloned card to the current deck
            } catch (Exception e) {
                System.out.println("Error cloning card: " + e.getMessage());
            }
        }
    }

    /**
     * Shuffles/randomizes the current deck for the specified player or enemy.
     *
     * @param deck The deck to shuffle (0 for player, 1 for enemy).
     */
    // Shuffle deck for player or enemy
    public void shuffleCurrentDeck(int deck) {
        // Player
        if (deck == 0) {
            java.util.Collections.shuffle(playerCurrentDeck);
        }
        // Enemy
        else {
            java.util.Collections.shuffle(enemyCurrentDeck);
        }
    }

    // Getters and setters
    public ArrayList<Cards> getPlayerCurrentDeck() {
        return playerCurrentDeck;
    }

    public void setPlayerCurrentDeck(ArrayList<Cards> playerCurrentDeck) {
        this.playerCurrentDeck = playerCurrentDeck;
    }

    public ArrayList<Cards> getEnemyCurrentDeck() {
        return enemyCurrentDeck;
    }

    public void setEnemyCurrentDeck(ArrayList<Cards> enemyCurrentDeck) {
        this.enemyCurrentDeck = enemyCurrentDeck;
    }
}
