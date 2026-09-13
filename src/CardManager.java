import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The CardManager class handles loading, parsing, and managing card data for the game.
 * Also defines effects for specific cards, which include visual effects (VFX) and gameplay effects
 * such as applying status effects or modifying game state.
 */
public class CardManager {
    // External references
    Sketch sketch;
    ArrayList<Cards> cardList;
    GameState gameState;
    Player player;
    Enemy enemy;

    /**
     * CardManager constructor with the given references.
     *
     * @param sketch Reference to the main sketch object.
     * @param player Reference to the Player object.
     * @param enemy Reference to the Enemy object.
     */
    public CardManager(Sketch sketch, Player player, Enemy enemy) {
        this.sketch = sketch;
        this.player = player;
        this.enemy = enemy;
        // Cardlist init
        this.cardList = new ArrayList<>();
    }

    /**
     * Sets the GameState reference for the CardManager to help avoid circular dependency.
     *
     * @param gameState Reference to the GameState object.
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Loads card data from a file and returns it as a string.
     *
     * @param fileName The name of the file containing card data.
     * @return A string containing the raw card data.
     */
    public String loadCardData(String fileName) {
        // Stringbuilder init
        StringBuilder cardData = new StringBuilder();
        try {
            // Reads and stores the file
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            // For each line
            while ((line = reader.readLine()) != null) {
                // Appends to list
                cardData.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Returns the data
        return cardData.toString();
    }

    /**
     * Parses card data from a string and generates Card objects, adding them to the cardList.
     *
     * @param cardData The raw card data loaded from a file.
     */
    public void parseCardData(String cardData) {
        String[] cardEntries = cardData.split("\n\n");  // Separate cards by double newline
        // For each entry
        for (String cardEntry : cardEntries) {
            String[] lines = cardEntry.split("\n");  // Split the card entry into lines

            // Init card attributes
            String cardName = "";
            int cardIndex = 0;
            int lifeForce = 0;
            int power = 0;
            String cardEffectLore = "";
            String cardImagePath = "";

            // Loop through each line in the card entry and extract values
            for (String line : lines) {
                String[] keyValue = line.split(":");  // Split the line into key and value
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    // Assign each attribute of card
                    switch (key) {
                        case "cardName":
                            cardName = value;
                            break;
                        case "cardIndex":
                            cardIndex = Integer.parseInt(value);
                            break;
                        case "lifeForce":
                            lifeForce = Integer.parseInt(value);
                            break;
                        case "power":
                            power = Integer.parseInt(value);
                            break;
                        case "cardEffectLore":
                            cardEffectLore = value;
                            break;
                        case "cardImagePath":
                            cardImagePath = value;
                            break;
                    }
                }
            }

            // Create the card object
            Cards card = new Cards(sketch, cardName, cardIndex, runnableEffects(cardIndex)[0],
                    runnableEffects(cardIndex)[1], lifeForce, power, cardEffectLore, cardImagePath);
            // Add the card to the list
            cardList.add(card);
        }
    }



    /**
     * Defines the effects (both for player and enemy) associated with a specific card index.
     * Some cards without the associated enemy index are player-specific and cannot be controlled
     * by the enemy.
     *
     * @param cardIndex The index of the card to determine effects for.
     * @return An array of Runnable objects where the first element is the player effect,
     *         and the second element is the enemy effect.
     */
    public Runnable [] runnableEffects (int cardIndex){
        // Following rules for declarations
        // Runnable effect[0] is effect for player
        // Runnable effect[1] is effect for enemy
        // VFX time data is the data relevant to the time the effect is played for
        // VFX effect is the effect the card plays on the GUI
        // Card effect is the effect the card executes on the game
        Runnable[] runnableEffects = new Runnable[2];

        // Sun Wukong
        if (cardIndex == 0) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Sun Wukong");
                gameState.setVFXStartTime(sketch.millis());
                gameState.setVFXDurationTime(2000);
                sketch.printToConsole("*** SUN WUKONG'S GOLDEN HEADBAND SHAKES THE EARTH ***");

                // VFX effect
                gameState.setCurrentVFXEffect(() -> {
                    sketch.noStroke();
                    sketch.fill(255, 255, 0, 55);  // Yellow with transparency
                    sketch.rect(0, 0, sketch.width, sketch.height);  // Draw full screen overlay
                });

                // Status effects
                ArrayList<Runnable> statusEffect = gameState.getPlayerEndStatusEffects();
                statusEffect.add(() -> {
                    sketch.printToConsole("+ SUN WUKONG EFFECT - Drain enemy for 2 life force");
                    enemy.dealDamage(2);
                });
                gameState.setPlayerEndStatusEffects(statusEffect);

                ArrayList<Integer> statusDuration = gameState.getPlayerEndStatusDurations();
                statusDuration.add(3);
                gameState.setPlayerEndStatusDurations(statusDuration);


            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Sun Wukong");

                // VFX effect
                // None

                // Status effects
                // None
            };
        }

        // Tang Sanzang
        else if (cardIndex == 1) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Tang Sanzang");
                gameState.setVFXStartTime(sketch.millis());
                gameState.setVFXDurationTime(2000);
                sketch.printToConsole("*** HE WHO SEARCHES FOR ENLIGHTENMENT ***");

                // VFX effect
                gameState.setCurrentVFXEffect(() -> {
                    sketch.noStroke();
                    sketch.fill(255, 255, 255, 55);
                    sketch.rect(0, 0, sketch.width, sketch.height);  // Draw full screen overlay
                });

                // Status effects
                ArrayList<Runnable> statusEffect = gameState.getPlayerStartStatusEffects();
                statusEffect.add(() -> {
                    sketch.printToConsole("+ TANG SANZANG EFFECT - Draw 2 cards");
                    gameState.hands.addCardToHand(0);
                    gameState.hands.addCardToHand(0);
                });

                gameState.setPlayerStartStatusEffects(statusEffect);

                ArrayList<Integer> statusDuration = gameState.getPlayerStartStatusDurations();
                statusDuration.add(1);
                gameState.setPlayerStartStatusDurations(statusDuration);
            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Tang Sanzang");

                // VFX effect
                // None

                // Status effects
                // None
            };
        }

        // Zhu Bajie
        else if (cardIndex == 2) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Zhu Bajie");

                // VFX effect
                // None

                // Effects
                Cards card1 = gameState.lanes.getPlayerLane().get(0);
                Cards card2 = gameState.lanes.getPlayerLane().get(3);
                if (card1 != null) {
                    card1.setPower(card1.getPower() + 1);
                    sketch.printToConsole("+ ZHU BAJIE EFFECT - Granted " + card1.getCardName() + " 1 power");
                }
                if (card2 != null) {
                    card2.setPower(card2.getPower() + 1);
                    sketch.printToConsole("+ ZHU BAJIE EFFECT - Granted " + card2.getCardName() + " 1 power");
                }
            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Zhu Bajie");

                // VFX effect
                // None

                // Status effects
                // None
            };
        }

        // Sha Wujing
        else if (cardIndex == 3) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Sha Wujing");

                // VFX effect
                // None

                // Status effects
                for (int i = 0;i < gameState.lanes.getPlayerLane().size();i++) {
                    Cards card = gameState.lanes.getPlayerLane().get(i);
                    if (card != null) {
                        if (card.getCardName().equals("Tang Sanzang")||card.getCardName().
                                equals("Zhu Bajie")||card.getCardName().equals("Sun Wukong")) {
                            sketch.printToConsole("+ SHA WUJING EFFECT - Grants 3 life force to " + card.getCardName());
                            card.setLifeForce(card.getLifeForce() + 3);
                        }
                    }
                }
            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Sha Wujing");

                // VFX effect
                // None

                // Status effects
                // None
            };
        }

        // White dragon horse
        else if (cardIndex == 4) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: White Dragon Horse");

                // VFX effect
                // None

                // Status effects
                for (int i = 0;i < gameState.lanes.getPlayerLane().size();i++) {
                    Cards card = gameState.lanes.getPlayerLane().get(i);
                    if (card != null) {
                        if (card.getCardName().equals("Tang Sanzang")) {
                            sketch.printToConsole("+ WHITE DRAGON HORSE EFFECT - Grants 2 power to all Tang Sanzang cards");
                            card.setPower(card.getPower() + 2);
                        }
                    }
                }

            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: White Dragon Horse");

                // VFX effect
                // None

                // Status effects
                // None
            };
        }

        // Princess iron fan
        else if (cardIndex == 5) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Princess Iron Fan");

                // VFX effect
                // None

                // Status effects
                Cards card = gameState.lanes.getPlayerLane().get(3);
                if (card != null) {
                    card.setPower(card.getPower() + 2);
                    sketch.printToConsole("+ PRINCESS IRON FAN EFFECT - Granted " +
                            card.getCardName() + " 2 power");
                }

            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Princess Iron Fan");

                // VFX effect
                // None

                // Status effects
                // None
            };
        }

        // Golden hooped staff
        else if (cardIndex == 6) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Golden Hooped Staff");

                // VFX effect
                // None

                // Status effects
                for (int i = 0;i < gameState.lanes.getPlayerLane().size();i++) {
                    Cards card = gameState.lanes.getPlayerLane().get(i);
                    if (card != null) {
                        if (card.getCardName().equals("Sun Wukong")) {
                            sketch.printToConsole("+ GOLDEN HOOPED STAFF EFFECT - Grants 3 power to all Sun Wukong cards");
                            card.setPower(card.getPower() + 3);
                        }
                    }
                }
            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Golden Hooped Staff");

                // VFX effect
                // None

                // Status effects
                // None
            };
        }

        // Three-eyed demon
        else if (cardIndex == 11) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Three-Eyed Demon");

                // VFX effect
                // None

                // Status effects
                ArrayList<Runnable> statusEffect = gameState.getPlayerEndStatusEffects();
                statusEffect.add(() -> {
                    sketch.printToConsole("+ THREE-EYED DEMON EFFECT - Grants 1 life force to all lane cards");
                    for (int i = 0;i < gameState.lanes.getPlayerLane().size();i++) {
                        Cards card = gameState.lanes.getPlayerLane().get(i);
                        if (card != null) {
                            card.setLifeForce(card.getLifeForce() + 1);
                        }
                    }
                });
                gameState.setPlayerEndStatusEffects(statusEffect);

                // Status effect turn duration
                ArrayList<Integer> statusDuration = gameState.getPlayerEndStatusDurations();
                statusDuration.add(3);
                gameState.setPlayerEndStatusDurations(statusDuration);

            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Three-Eyed Demon");

                // VFX effect
                // None

                // Status effects
                ArrayList<Runnable> statusEffect = gameState.getEnemyEndStatusEffects();
                statusEffect.add(() -> {
                    sketch.printToConsole("- THREE-EYED DEMON EFFECT - Grants 1 life force to all lane cards");
                    for (int i = 0;i < gameState.lanes.getEnemyLane().size();i++) {
                        Cards card = gameState.lanes.getEnemyLane().get(i);
                        if (card != null) {
                            card.setLifeForce(card.getLifeForce() + 1);
                        }
                    }
                });
                gameState.setEnemyEndStatusEffects(statusEffect);

                // Status effect turn duration
                ArrayList<Integer> statusDuration = gameState.getEnemyEndStatusDurations();
                statusDuration.add(3);
                gameState.setEnemyEndStatusDurations(statusDuration);
            };
        }

        // Yellow Robed Demon
        else if (cardIndex == 12) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Yellow Robed Demon");

                // VFX effect
                // None

                // Status effects
                sketch.printToConsole("+ YELLOW ROBED DEMON EFFECT - Draw 2 cards");
                gameState.hands.addCardToHand(0);
                gameState.hands.addCardToHand(0);
            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Yellow Robed Demon");

                // VFX effect
                // None

                // Status effects
                sketch.printToConsole("- YELLOW ROBED DEMON EFFECT - Draw 2 cards");
                gameState.hands.addCardToHand(1);
                gameState.hands.addCardToHand(1);
            };
        }

        // Pig Yaoguai
        else if (cardIndex == 13) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Pig Yaoguai");

                // VFX effect
                // None

                // Status effects
                sketch.printToConsole("+ PIG YAOGUAI EFFECT - Drains 2 life force from the enemy");
                enemy.dealDamage(2);
            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Pig Yaoguai");

                // VFX effect
                // None

                // Status effects
                sketch.printToConsole("- PIG YAOGUAI EFFECT - Drains 2 life force from the enemy");
                player.dealDamage(2);
            };
        }

        // Ao Guang
        else if (cardIndex == 16) {
            runnableEffects[0] = () -> {
                // VFX time data
                System.out.println("Player VFX: Ao Guang");
                gameState.setVFXStartTime(sketch.millis());
                gameState.setVFXDurationTime(2000);
                sketch.printToConsole("*** AO GUANG, THE KING OF THE EASTERN SEAS ARRIVES ***");

                // VFX effect
                gameState.setCurrentVFXEffect(() -> {
                    sketch.noStroke();
                    sketch.fill(80, 190, 190, 55);
                    sketch.rect(0, 0, sketch.width, sketch.height);  // Draw full screen overlay
                });

                // Status effects
                sketch.printToConsole("+ AO GUANG EFFECT - Drains 3 life force from the enemy");
                enemy.dealDamage(3);
            };
            runnableEffects[1] = () -> {
                // VFX time data
                System.out.println("Enemy VFX: Ao Guang");

                // VFX effect
                // None

                // Status effects
                sketch.printToConsole("- AO GUANG EFFECT - Drains 3 life force from the enemy");
                player.dealDamage(3);
            };
        }

        // No effect cards (default value)
        else {
            runnableEffects[0] = () -> System.out.println("Player VFX: No Effect");
            runnableEffects[1] = () -> System.out.println("Enemy Effect: No Effect");
        }

        return runnableEffects;
    }

    // Getters and setters
    public ArrayList<Cards> getCardList() {
        return cardList;
    }
}

