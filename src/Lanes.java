import java.util.ArrayList;

/**
 * Manages the lanes for both the player and the enemy.
 * Handles updating the lanes, checking mouse clicks on lanes,
 * and placing cards in the appropriate lanes.
 */
public class Lanes {
    Sketch sketch;
    private ArrayList<Cards> playerLane;
    private ArrayList<Cards> enemyLane;

    private static int laneWidth = 140;
    private static int laneHeight = 180;
    private static int numLanes = 4;
    private static int laneSpacing = 10; // Space between lanes
    private static int totalWidth = numLanes * laneWidth + (numLanes - 1) * laneSpacing; // Total width occupied by lanes
    private static int startX = (1200 - totalWidth) / 2; // Calculate starting X position to center the lanes

    /**
     * Initializes the Lanes class with the Sketch instance.
     * Initializes the player and enemy lanes.
     *
     * @param sketch The Sketch instance for rendering and logging.
     */
    public Lanes(Sketch sketch) {
        this.sketch = sketch;
        playerLane = new ArrayList<>(numLanes);  // Create a fixed size list of 4 lanes
        enemyLane = new ArrayList<>(numLanes);
        updateLanes();
    }

    /**
     * Updates the player and enemy lanes to ensure they have the appropriate number of slots.
     */
    public void updateLanes() {
        // If the playerLane does not have enough slots for the selected lane, expand it
        while (playerLane.size() < numLanes) {
            playerLane.add(null);  // Add null placeholders for empty lanes
        }
        // If the enemyLane does not have enough slots for the selected lane, expand it
        while (enemyLane.size() < numLanes) {
            enemyLane.add(null);  // Add null placeholders for empty lanes
        }
    }

    /**
     * Checks if a mouse click is within a specific lane, either for the player or the enemy.
     *
     * @param mouseX The X-coordinate of the mouse click.
     * @param mouseY The Y-coordinate of the mouse click.
     * @param isEnemyLane True if checking for the enemy's lane, false for the player's lane.
     * @return The index of the lane clicked, or -1 if no lane was clicked.
     */
    public int checkLaneClick(int mouseX, int mouseY, boolean isEnemyLane) {
        // Check if the mouse click is within a lane (either player or enemy)
        for (int i = 0; i < numLanes; i++) {
            int x = startX + i * (laneWidth + laneSpacing);
            int y = isEnemyLane ? 30 : sketch.height - 330;  // Enemy's lane is at the top, player's lane is at the bottom
            if (mouseX >= x && mouseX <= x + laneWidth && mouseY >= y && mouseY <= y + laneHeight) {
                // Adjust for enemy lanes by adding the number of player lanes to the index
                if (isEnemyLane) {
                    i += numLanes;
                }
                System.out.println("Lane: " + i + "       X: " + x + "       Y: " + y);
                return i;  // Return the index of the clicked lane
            }
        }
        return -1;  // No lane clicked
    }

    /**
     * Places a card in the specified lane.
     *
     * @param card The card to place in the lane.
     * @param laneIndex The index of the lane where the card should be placed.
     */
    public void placeCardInLane(Cards card, int laneIndex) {
        // Ensure that there is space in the lane by checking the size
        if (laneIndex >= 0 && laneIndex < numLanes) {
            // Place the card in the selected lane
            card.setCurrentLane(laneIndex);
            card.setCurrentX(startX + laneIndex * (laneWidth + laneSpacing));
            card.setCurrentY(600 - 330);
            card.playCard(0); // Play the card for the player
            playerLane.set(laneIndex, card); // Add the card to the player's lane
            sketch.printToConsole("+ You placed " + card.getCardName() + " in lane " + (card.getCurrentLane() + 1) + ".");
            System.out.println("Placed: " + card + "\nIn lane: " + laneIndex);
        }
        else if (laneIndex >= numLanes && laneIndex < 8) {
            // Place the card in the selected lane for the enemy
            card.setCurrentLane(laneIndex);
            card.setCurrentX(startX + (laneIndex - numLanes) * (laneWidth + laneSpacing));
            card.setCurrentY(30);
            card.playCard(1); // Play the card for the enemy
            enemyLane.set(laneIndex - numLanes, card); // Add the card to the enemy's lane
            sketch.printToConsole("- Enemy placed " + card.getCardName() + " in lane " + (card.getCurrentLane() - 3) + ".");
            System.out.println("Placed: " + card + "\nIn lane: " + laneIndex);
        }
    }

    // Getters and Setters
    public ArrayList<Cards> getPlayerLane() {
        return playerLane;
    }

    public void setPlayerLane(ArrayList<Cards> playerLane) {
        this.playerLane = playerLane;
    }

    public ArrayList<Cards> getEnemyLane() {
        return enemyLane;
    }

    public void setEnemyLane(ArrayList<Cards> enemyLane) {
        this.enemyLane = enemyLane;
    }

    public int getLaneWidth() {
        return laneWidth;
    }

    public int getLaneHeight() {
        return laneHeight;
    }

    public int getNumLanes() {
        return numLanes;
    }

    public int getLaneSpacing() {
        return laneSpacing;
    }

    public int getStartX() {
        return startX;
    }
}
