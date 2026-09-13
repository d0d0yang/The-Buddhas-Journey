import java.util.ArrayList;

/**
 * The GameState class manages the game's main mechanics,
 * including turn systems, phases, status effects, and game state evaluation.
 */
public class GameState {
    // Phase management
    private boolean newTurn = true;
    private boolean isBattlePhase = false;
    private boolean isPlayerTurn = true;


    // Status effects
    // Player starting phase
    private ArrayList<Runnable> playerStartStatusEffects = new ArrayList<>();
    private ArrayList<Integer> playerStartStatusDurations = new ArrayList<>();
    // Player ending phase
    private ArrayList<Runnable> playerEndStatusEffects = new ArrayList<>();
    private ArrayList<Integer> playerEndStatusDurations = new ArrayList<>();
    // Enemy starting phase
    private ArrayList<Runnable> enemyStartStatusEffects = new ArrayList<>();
    private ArrayList<Integer> enemyStartStatusDurations = new ArrayList<>();
    // Enemy ending phase
    private ArrayList<Runnable> enemyEndStatusEffects = new ArrayList<>();
    private ArrayList<Integer> enemyEndStatusDurations = new ArrayList<>();

    // VFX management
    private int vfxStartTime;
    private int vfxDurationTime;
    private Runnable currentVFXEffect;

    // Turn/phase management
    private int turnNum = 0;
    private int playerCurrentPhase = 0;
    private int enemyCurrentPhase = 0;
    // Phase timing/delay management
    private long phaseStartTime = 0;
    private final long phaseDelay = 1500;

    // Card placement logic
    private long cardPlacementStartTime = 0; // To track the start time for the first card placement
    private int cardIndex = 0;  // To track the current card being placed
    private final long cardPlacementDelay = 1000;

    // Gamestate tracking
    private boolean gameOver = false;
    private boolean playerWon;
    private int bonusCardIndex = -1;

    // Game component external references
    Lanes lanes;
    Hands hands;
    Decks decks;
    Cards cards;
    Sketch sketch;
    Player player;
    Enemy enemy;

    // Setters for dependencies to avoid circular dependencies
    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    public void setLanes(Lanes lanes) {
        this.lanes = lanes;
    }

    public void setHands(Hands hands) {
        this.hands = hands;
    }

    public void setDecks(Decks decks) {
        this.decks = decks;
    }

    public void setCards(Cards cards) {
        this.cards = cards;
    }

    public void setSketch(Sketch sketch) {
        this.sketch = sketch;
    }

    /**
     * Checks whether the game is over and updates the game state accordingly.
     */
    public void checkGameOver() {
        if (!gameOver) {
            // Player win
            if (enemy.getLifeForce() <= 0) {
                // Sets game to over and player won
                gameOver = true;
                playerWon = true;
                // Add scripture and bonus card
                sketch.savemanager.setScriptures(sketch.savemanager.getScriptures() + 1);
                bonusCardIndex = (int) sketch.random(0, 18);
                sketch.savemanager.setBonusCard(bonusCardIndex);
            }
            // Enemy win
            else if (player.getLifeForce() <= 0) {
                // Sets game over and enemy won
                gameOver = true;
                playerWon = false;
            }
        }
    }

    /**
     * Executes the battle phase for the specified user.
     *
     * @param user 0 for player, 1 for enemy used for determining phase.
     */
    public void runBattlePhase (int user) {
        // Player phase
        if (user == 0) {
            // For each lane
            for (int i = 0; i < lanes.getPlayerLane().size(); i++) {
                // Player and enemy card
                Cards playerCard = lanes.getPlayerLane().get(i);
                Cards enemyCard = lanes.getEnemyLane().get(i);

                // If there is player card
                if (playerCard != null) {
                    // If there is enemy card
                    if (enemyCard != null) {
                        // Damage enemy card
                        enemyCard.setLifeForce(enemyCard.getLifeForce() - playerCard.getPower());
                        // Delete enemy card if 0 life force
                        if (enemyCard.getLifeForce() <= 0) {
                            lanes.getEnemyLane().set(i, null);
                            lanes.setEnemyLane(lanes.getEnemyLane());
                            sketch.printToConsole("+ Enemy " + enemyCard.getCardName() + " was defeated");
                        }
                    }
                    // If there is no card damage enemy life force
                    else {
                        enemy.dealDamage(playerCard.getPower());
                        sketch.printToConsole("+ " + playerCard.getCardName() + " drained the enemy of " + playerCard.getPower() + " life force!");
                    }
                }
            }
        }
        // Enemy phase
        else {
            // For each lane
            for (int i = 0; i < lanes.getEnemyLane().size(); i++) {
                // Player and enemy cards
                Cards playerCard = lanes.getPlayerLane().get(i);
                Cards enemyCard = lanes.getEnemyLane().get(i);

                // If there is enemy card
                if (enemyCard != null) {
                    // If there is player card
                    if (playerCard != null) {
                        // Damage player card or player life
                        playerCard.setLifeForce(playerCard.getLifeForce() - enemyCard.getPower());
                        // Delete player card if 0 life force
                        if (playerCard.getLifeForce() <= 0) {
                            lanes.getPlayerLane().set(i, null); // Set the element at index 'i' to null
                            lanes.setPlayerLane(lanes.getPlayerLane()); // Update the player lane in the lanes object
                            sketch.printToConsole("- Your " + playerCard.getCardName() + " was defeated");
                        }
                    }
                    // If there is no player card damage life force
                    else {
                        player.dealDamage(enemyCard.getPower());
                        sketch.printToConsole("- The enemy " + enemyCard.getCardName() + " drained " + enemyCard.getPower() + " life force from you!");
                    }
                }
            }
        }
    }

    /**
     * Runs status effects for the specified phase and user.
     *
     * @param phase 0 for start phase, 1 for end phase.
     * @param user  0 for player, 1 for enemy.
     */
    public void runStatusEffects (int phase, int user) {
        // Array for status effects and their durations
        ArrayList <Runnable> statusEffects = null;
        ArrayList <Integer> statusDurations = null;
        // Start phase
        if (phase == 0) {
            // Player
            if (user == 0) {
                statusEffects = playerStartStatusEffects;
                statusDurations = playerStartStatusDurations;
            }
            // Enemy
            else {
                statusEffects = enemyStartStatusEffects;
                statusDurations = enemyStartStatusDurations;
            }
        }
        // End phase
        else if (phase == 1) {
            // Player
            if (user == 0) {
                statusEffects = playerEndStatusEffects;
                statusDurations = playerEndStatusDurations;
            }
            // Enemy
            else {
                statusEffects = enemyEndStatusEffects;
                statusDurations = enemyEndStatusDurations;
            }
        }
        // If there is a status effect
        if (statusEffects != null) {
            // For each status effect
            for (int i = 0; i < statusEffects.size(); i++) {
                // Current status effect
                Runnable effect = statusEffects.get(i);
                // If status is active
                if (statusDurations.get(i) > 0) {
                    // Decrease duration and run
                    statusDurations.set(i, statusDurations.get(i) - 1);
                    effect.run();
                }
                // Delete status if duration ended
                if (statusDurations.get(i) <= 0) {
                    // Remove from effects and durations lists
                    statusEffects.remove(i);
                    statusDurations.remove(i);
                    // Adjust index
                    i--;
                }
            }
        }
    }

    /**
     * Manages the game's turn system.
     * Alternates between player and enemy turns.
     * Also manages win/lose variables to determine endings
     */
    public void turnSystem () {
        // Check game over
        checkGameOver();

        // Turn increase
        if (newTurn) {
            turnNum += 1;
            newTurn = false;
        }

        // Player turn
        if (turnNum % 2 != 0) {
            // Start phase
            if (playerCurrentPhase == 0) {
                isPlayerTurn = true;
                sketch.printToConsole("< Your turn");

                // Run starting status effects
                runStatusEffects(0, 0);

                // Draw a card
                hands.addCardToHand(0);

                // Next phase
                playerCurrentPhase = 1;
            }

            // Place phase
            if (playerCurrentPhase == 1) {
                // Placing handled by mouseclick
                // Battle phase
                if (isBattlePhase) {
                    // Time delay
                    if (sketch.millis() - phaseStartTime >= phaseDelay) {
                        // Run battle phase
                        runBattlePhase(0);
                        isBattlePhase = false;
                        // Next phase
                        playerCurrentPhase = 2;
                        phaseStartTime = sketch.millis();
                    }
                }
            }

            // End phase
            if (playerCurrentPhase == 2) {
                // End phase
                runStatusEffects(1, 0);

                // Reset phase
                playerCurrentPhase = 0;
                newTurn = true;
            }
        }

        // Enemy turn
        else if (turnNum % 2 == 0) {
            // Delay
            if (enemyCurrentPhase == 0 && sketch.millis() - phaseStartTime >= phaseDelay * 4) {
                sketch.printToConsole("< Enemy turn");
                // Status effects
                runStatusEffects(0, 1);
                phaseStartTime = sketch.millis();

                // Next phase
                enemyCurrentPhase = 1;
            }

            // Start phase
            if (enemyCurrentPhase == 1 && sketch.millis() - phaseStartTime >= phaseDelay) {
                // Draw card
                hands.addCardToHand(1);
                phaseStartTime = sketch.millis();

                // Next phase
                enemyCurrentPhase = 2;
            }

            // Place phase
            if (enemyCurrentPhase == 2 && sketch.millis() - phaseStartTime >= phaseDelay * 2) {
                // Start placing cards in each lane
                if (cardIndex < lanes.getEnemyLane().size() && !hands.getEnemyHand().isEmpty()) {
                    // Delay
                    if (sketch.millis() - cardPlacementStartTime >= cardPlacementDelay) {
                        // If there isn't already a card
                        if (lanes.getEnemyLane().get(cardIndex) == null) {
                            // Place the card
                            lanes.placeCardInLane(hands.getEnemyHand().get(0), cardIndex + 4);
                            // Remove card from hand
                            hands.removeCardFromHand(1);

                            // Update the card placement start time
                            cardPlacementStartTime = sketch.millis();
                        }

                        // Move to the next card index
                        cardIndex++;
                    }
                }

                // All lanes are full or hand is empty
                else if (cardIndex >= lanes.getEnemyLane().size() || hands.getEnemyHand().isEmpty()) {
                    // Reset index
                    cardIndex = 0;
                    phaseStartTime = sketch.millis();

                    // Next phase (move to battle phase)
                    sketch.printToConsole("< Enemy entered into battle!");
                    enemyCurrentPhase = 3;
                }
            }


            // After another delay, move to the next phase (Battle phase)
            if (enemyCurrentPhase == 3 && sketch.millis() - phaseStartTime >= phaseDelay * 2) {
                // Enemy battle phase
                runBattlePhase(1);
                phaseStartTime = sketch.millis();

                // Next phase
                enemyCurrentPhase = 4;
            }

            // After another delay, move to the next phase (End phase)
            if (enemyCurrentPhase == 4 && sketch.millis() - phaseStartTime >= phaseDelay * 3) {
                // End status effects
                runStatusEffects(1, 1);
                newTurn = true;

                // Reset phase
                enemyCurrentPhase = 0;
                isPlayerTurn = true;
            }
        }
    }



    // Getters and setters
    public boolean getGameOver() {
        return gameOver;
    }

    public int getBonusCardIndex() {
        return bonusCardIndex;
    }

    public boolean getPlayerWon(){
        return playerWon;
    }

    public void setPhaseStartTime(int phaseStartTime) {
        this.phaseStartTime = phaseStartTime;
    }

    public boolean getIsBattlePhase() {
        return isBattlePhase;
    }

    public void setIsBattlePhase(boolean isBattlePhase) {
        this.isBattlePhase = isBattlePhase;
    }

    public int getVFXStartTime() {
        return vfxStartTime;
    }

    public void setVFXStartTime(int vfxStartTime) {
        this.vfxStartTime = vfxStartTime;
    }

    public int getVFXDurationTime() {
        return vfxDurationTime;
    }

    public void setVFXDurationTime(int vfxDurationTime) {
        this.vfxDurationTime = vfxDurationTime;
    }

    public Runnable getCurrentVFXEffect() {
        return currentVFXEffect;
    }

    public void setCurrentVFXEffect(Runnable currentVFXEffect) {
        this.currentVFXEffect = currentVFXEffect;
    }

    public int getTurnNum() {
        return turnNum;
    }

    public ArrayList<Runnable> getPlayerStartStatusEffects() {
        return playerStartStatusEffects;
    }

    public void setPlayerStartStatusEffects(ArrayList<Runnable> playerStartStatusEffects) {
        this.playerStartStatusEffects = playerStartStatusEffects;
    }

    public ArrayList<Integer> getPlayerStartStatusDurations() {
        return playerStartStatusDurations;
    }

    public void setPlayerStartStatusDurations(ArrayList<Integer> playerStartStatusDurations) {
        this.playerStartStatusDurations = playerStartStatusDurations;
    }

    public ArrayList<Runnable> getPlayerEndStatusEffects() {
        return playerEndStatusEffects;
    }

    public void setPlayerEndStatusEffects(ArrayList<Runnable> playerEndStatusEffects) {
        this.playerEndStatusEffects = playerEndStatusEffects;
    }

    public ArrayList<Integer> getPlayerEndStatusDurations() {
        return playerEndStatusDurations;
    }

    public void setPlayerEndStatusDurations(ArrayList<Integer> playerEndStatusDurations) {
        this.playerEndStatusDurations = playerEndStatusDurations;
    }

    public ArrayList<Runnable> getEnemyStartStatusEffects() {
        return enemyStartStatusEffects;
    }

    public void setEnemyStartStatusEffects(ArrayList<Runnable> enemyStartStatusEffects) {
        this.enemyStartStatusEffects = enemyStartStatusEffects;
    }

    public ArrayList<Integer> getEnemyStartStatusDurations() {
        return enemyStartStatusDurations;
    }

    public void setEnemyStartStatusDurations(ArrayList<Integer> enemyStartStatusDurations) {
        this.enemyStartStatusDurations = enemyStartStatusDurations;
    }

    public ArrayList<Runnable> getEnemyEndStatusEffects() {
        return enemyEndStatusEffects;
    }

    public void setEnemyEndStatusEffects(ArrayList<Runnable> enemyEndStatusEffects) {
        this.enemyEndStatusEffects = enemyEndStatusEffects;
    }

    public ArrayList<Integer> getEnemyEndStatusDurations() {
        return enemyEndStatusDurations;
    }

    public void setEnemyEndStatusDurations(ArrayList<Integer> enemyEndStatusDurations) {
        this.enemyEndStatusDurations = enemyEndStatusDurations;
    }
}
