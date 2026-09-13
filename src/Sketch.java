import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * The Sketch class represents the main game loop and user interface for the card game.
 * It extends the PApplet class from the Processing library to handle drawing and events.
 */
public class Sketch extends PApplet {
    // List of cards for the game
    private ArrayList<Cards> cardList;

    // Player and enemy hands
    private ArrayList<Cards> playerHand;
    private ArrayList<Cards> enemyHand;

    // Track the selected card
    private Cards selectedCard = null;

    // UI dimensions
    private static final int uiWidth = 1200;
    private static final int uiHeight = 600;

    // Scale for selected card
    private float selectedCardScale = 1.25f;

    // Paths to various images
    private final String backgroundImagePath = "Images/boardImage.png";
    private final String enemyPortraitPath = "Images/boardImage.png";
    private final String winImagePath = "Images/Win Image.png";
    private final String loseImagePath = "Images/Lose Image.png";
    private final String enlightenmentImagePath = "Images/Enlightenment Image.png";
    private final String tutorial0ImagePath = "Images/Tutorial 0.png";
    private final String tutorial1ImagePath = "Images/Tutorial 1.png";
    private final String tutorial2ImagePath = "Images/Tutorial 2.png";
    private final String tutorial3ImagePath = "Images/Tutorial 3.png";
    private final String tutorial4ImagePath = "Images/Tutorial 4.png";
    private final String tutorial5ImagePath = "Images/Tutorial 5.png";
    private final String tutorial6ImagePath = "Images/Tutorial 6.png";
    private final String tutorial7ImagePath = "Images/Tutorial 7.png";

    // Array of tutorial image paths
    String[] tutorialImagePaths = {tutorial0ImagePath, tutorial1ImagePath, tutorial2ImagePath,
            tutorial3ImagePath, tutorial4ImagePath, tutorial5ImagePath, tutorial6ImagePath,
            tutorial7ImagePath
    };

    // Button dimensions and position
    private final int buttonWidth = 120;
    private final int buttonHeight = 60;
    private final int buttonX = uiWidth - 200;
    private final int buttonY = 400;

    // Image for background
    PImage img;

    // External references
    Decks decks;
    Hands hands;
    Lanes lanes;
    CardManager cardmanager;
    GameState gameState;
    SaveManager savemanager;
    Player player;
    Enemy enemy;

    // Tutorial number
    private int tutorialNum = 0;

    // Console variables
    private final int maxConsoleLines = 20;
    private final int consoleWidth = 270;
    private final int lineHeight = 20;
    private final int consoleHeight = maxConsoleLines * lineHeight;
    private ArrayList<String> consoleLines = new ArrayList<String>();


    /**
     * Initializes much of the game logic, loads resources, and creates objects.
     */
    public void setup() {
        // Save manager
        savemanager = new SaveManager();
        savemanager.getSave();

        // Characters
        player = new Player(50, savemanager.getScriptures());
        PImage enemyPortrait = loadImage(enemyPortraitPath);
        enemy = new Enemy(50, enemyPortrait);

        // Initialize the lanes object
        lanes = new Lanes(this);

        // Gamestate
        gameState = new GameState();

        // Card manager
        cardmanager = new CardManager(this, player, enemy);
        // Load card data from the file
        String cardData = cardmanager.loadCardData("src\\Card_Data.txt");
        // Parse the card data and create Cards objects
        cardmanager.parseCardData(cardData);
        // Retrieve the list of cards
        cardList = cardmanager.getCardList();

        // Decks
        decks = new Decks();
        decks.setCardList(cardList); // Set cardList before using the Decks object

        // Player
        // Add player cards to the deck after cardList is set
        for (int i = 0; i < 7; i++) {
            decks.addCardToDeck(0, i);
        }
        // Add player bonus cards
        for (int i = 0; i < savemanager.getBonusCards().size();i++) {
            decks.addCardToDeck(0, savemanager.getBonusCards().get(i));
        }

        // Enemy
        // Add enemy cards to the deck after cardList is set
        for (int i = 7; i < 19; i++) {
            decks.addCardToDeck(1, i);
        }

        // Load full deck into current deck
        decks.loadCurrentDeck(0);
        decks.shuffleCurrentDeck(0);
        decks.loadCurrentDeck(1);
        decks.shuffleCurrentDeck(1);

        // Hands
        hands = new Hands(this);
        hands.setDecks(decks); // Set decks before using the Hands object
        playerHand = hands.getPlayerHand();
        enemyHand = hands.getEnemyHand();

        // First draw
        for (int i = 3; i > 0; i--) {
            hands.addCardToHand(0);
            hands.addCardToHand(1);
        }

        // Set dependencies to avoid circular dependencies
        cardmanager.setGameState(gameState);
        gameState.setHands(hands);
        gameState.setDecks(decks);
        gameState.setLanes(lanes);
        gameState.setPlayer(player);
        gameState.setEnemy(enemy);
        gameState.setSketch(this);

        // Load background image
        img = loadImage(backgroundImagePath);
        img.resize(uiWidth, uiHeight);

        // Initialize console with a message
        printToConsole("< Game start");
    }

    /**
     * Sets the settings and canvas size for the game UI.
     */
    public void settings() {
        size(uiWidth, uiHeight);  // Set canvas size
    }

    /**
     * The main game loop that draws the GUI and updates every frame.
     */
    public void draw() {
        // Enlightenment ending
        if (player.getScriptures() >= 81){
            drawEnlightenmentEnding();
        }

        // Has not played tutorial
        else if (!savemanager.getPlayedTutorial()) {
            drawTutorial();
        }
        else if (gameState.getGameOver()) {
            drawGameOver();
        }

        // Game GUI draw
        else {
            background(255);  // Set background to white
            image(img, 0, 0);  // Display the background image

            // Draw lanes on GUI
            drawLanes();

            // Draw player cards in lanes
            drawCardsInLanes();

            // Draw player cards in hand
            hands.updateHandPos();
            drawHand();

            // Draw selected card on top
            drawSelectedCard();

            textAlign(LEFT, TOP + 30);  // Reset to left-top alignment after drawing card info


            // Apply card VFX
            drawCardVFX();

            // Display stats UI (highest priority)
            drawStatGUI();

            // Draw the Battle button beneath the enemy HP
            drawBattleButton();

            // Draw the console at the bottom of the screen
            drawConsole();

            gameState.turnSystem();
        }
    }


    /**
     * Stores the data and draws the frame of the enlightenment ending.
     */
    private void drawEnlightenmentEnding() {
        savemanager.resetSave();
        PImage enlightenmentImage = loadImage(enlightenmentImagePath);
        enlightenmentImage.resize(uiWidth, uiHeight);
        image(enlightenmentImage, 0, 0);

        // Display the enlightenment message with a translucent black background
        textSize(32);
        textAlign(CENTER, CENTER);
        fill(0, 0, 0, 150);  // Black with some transparency for the background
        rect(100, 72, uiWidth - 200, 250);  // Rectangle behind the text

        fill(255);  // White text color
        text("You collected 81 scriptures and completed your " +
                        "Journey\n to the West! The Buddha recognized your dedication and\n" +
                        "granted you enlightenment.\n\n\nThank you for playing!",
                uiWidth / 2, 190);
    }

    /**
     * Stores the data and draws the frame of the tutorial frames.
     */
    private void drawTutorial() {
        String tutorialImagePath = tutorialImagePaths[min(tutorialNum, tutorialImagePaths.length - 1)];
        PImage tutorialImage = loadImage(tutorialImagePath);
        tutorialImage.resize(uiWidth, uiHeight);
        image(tutorialImage, 0, 0);
    }

    /**
     * Stores the data and draws the frame of the game over ending.
     */
    private void drawGameOver() {
        // Set the text size for the victory/defeat message
        textSize(32);
        textAlign(CENTER, CENTER);

        // Player won
        if (gameState.getPlayerWon()) {
            PImage winImage = loadImage(winImagePath);
            winImage.resize(uiWidth, uiHeight);
            image(winImage, 0, 0);

            // Get bonus card name
            String bonusCardName = "";
            for (int i = 0; i < cardList.size(); i++) {
                if (cardList.get(i).getCardIndex() == gameState.getBonusCardIndex()) {
                    bonusCardName = cardList.get(i).getCardName();
                    break;
                }
            }

            // Display the victory message with a translucent black background
            fill(0, 0, 0, 150);  // Black with some transparency for the background
            rect(100, 72, uiWidth - 200, 250);  // Rectangle behind the text

            fill(255);  // White text color
            text("You were victorious! You were granted a scripture and " +
                            "another\ncompanion, " + bonusCardName + ", who will " +
                            "permanently travel\nwith you (extra card). \n\n\nRestart game " +
                            "to start a new journey.",
                    uiWidth / 2, 190);
        }
        // Enemy won
        else {
            PImage winImage = loadImage(loseImagePath);
            winImage.resize(uiWidth, uiHeight);
            image(winImage, 0, 0);

            // Display the defeat message with a translucent black background
            fill(0, 0, 0, 150);  // Black with some transparency for the background
            rect(100, 86, uiWidth - 130, 240);  // Rectangle behind the text

            fill(255);  // White text color
            text("You were defeated! You were not granted a scripture and \ndid not recruit " +
                            "any companions.\n\n\nRestart game to start a new journey.",
                    uiWidth / 2, 200);
        }
    }

    /**
     * Stores the data and draws the hands for the player in the GUI.
     */
    private void drawHand() {
        for (int i = 0; i < playerHand.size(); i++) {
            Cards card = playerHand.get(i);

            // Only render the card if it's not null or selected card
            if (card != null && card != selectedCard) {
                PImage cardImage = card.getCardImage();
                cardImage.resize(card.getCardWidth(), card.getCardHeight());  // Resize the image for display

                image(cardImage, card.getCurrentX(), card.getCurrentY());  // Draw the card (shifted to center)

                // Draw the card information (Name, Power, Life Force, Effect Lore)
                drawCardInfo(card, card.getCurrentX(), card.getCurrentY(), false);
            }
        }
    }

    /**
     * Stores the data and draws the lanes for both the opponent and player in the GUI.
     */
    private void drawLanes() {
        // Draw the player lanes
        for (int i = 0; i < lanes.getNumLanes(); i++) {
            int x = lanes.getStartX() + i * (lanes.getLaneWidth() + lanes.getLaneSpacing());
            int y = height - 330; // Player's lane at the bottom
            fill(0, 170);
            stroke(255);
            rect(x, y, lanes.getLaneWidth(), lanes.getLaneHeight()); // Draw the player's lane
        }

        // Draw the opponent lanes (top of the screen)
        for (int i = 0; i < lanes.getNumLanes(); i++) {
            int x = lanes.getStartX() + i * (lanes.getLaneWidth() + lanes.getLaneSpacing());
            int y = 30; // Enemy's lane at the top
            fill(0, 170);
            stroke(255);
            rect(x, y, lanes.getLaneWidth(), lanes.getLaneHeight()); // Draw the opponent's lane
        }
    }

    /**
     * Stores the data and draws each individual card for the player and enemy in the GUI.
     */
    private void drawCardsInLanes() {
        for (int i = 0; i < lanes.getPlayerLane().size(); i++) {
            Cards card = lanes.getPlayerLane().get(i); // Get the card from the player's lane

            // Only render the card if it's not null or selected card
            if (card != null && card != selectedCard) {
                PImage cardImage = card.getCardImage(); // Load the card image
                cardImage.resize(lanes.getLaneWidth(), lanes.getLaneHeight());  // Resize the image for display

                // Draw the card
                image(cardImage, card.getCurrentX(), card.getCurrentY());  // Draw the card (shifted to center)

                // Draw the card information (Name, Power, Life Force, Effect Lore)
                drawCardInfo(card, card.getCurrentX(), card.getCurrentY(), false);
            }
        }

        // Draw enemy cards in lanes
        for (int i = 0; i < lanes.getEnemyLane().size(); i++) {
            Cards card = lanes.getEnemyLane().get(i); // Get the card from the enemy's lane

            // Only render the card if it's not null
            if (card != null) {
                PImage cardImage = card.getCardImage(); // Load the card image
                cardImage.resize(lanes.getLaneWidth(), lanes.getLaneHeight());  // Resize the image for display

                // Draw the card
                image(cardImage, card.getCurrentX(), card.getCurrentY());  // Draw the card (shifted to center)

                // Draw the card information (Name, Power, Life Force, Effect Lore)
                drawCardInfo(card, card.getCurrentX(), card.getCurrentY(), false);
            }
        }
    }

    /**
     * Stores the data and draws the card that the player has selected
     */
    private void drawSelectedCard() {
        if (selectedCard != null) {
            PImage cardImage = selectedCard.getCardImage(); // Load the card image

            pushMatrix();  // Save the current transformation matrix
            if (selectedCard.getCurrentLane() >= 1 && selectedCard.getCurrentLane() <= 8) {
                cardImage.resize(lanes.getLaneWidth(), lanes.getLaneHeight());  // Resize the image for display
                translate(selectedCard.getCurrentX() + lanes.getLaneWidth() / 2, selectedCard.getCurrentY() + lanes.getLaneHeight() / 2);  // Move to the center of the card for scaling
                scale(selectedCardScale);  // Apply scaling
                image(cardImage, - lanes.getLaneWidth() / 2, - lanes.getLaneHeight() / 2);  // Draw the card (shifted to center)
            } else {
                cardImage.resize(selectedCard.getCardWidth(), selectedCard.getCardHeight());  // Resize the image for display
                translate(selectedCard.getCurrentX() + selectedCard.getCardWidth() / 2, selectedCard.getCurrentY() + selectedCard.getCardHeight() / 2);  // Move to the center of the card for scaling
                scale(selectedCardScale);  // Apply scaling
                image(cardImage, - selectedCard.getCardWidth() / 2, - selectedCard.getCardHeight() / 2);  // Draw the card (shifted to center)
            }
            popMatrix();  // Restore the transformation matrix

            // Draw the card information (Name, Power, Life Force, Effect Lore)
            drawCardInfo(selectedCard, selectedCard.getCurrentX(), selectedCard.getCurrentY(), true);
        }
    }

    /**
     * Draws the current card VFX that is applied to the GUI.
     */
    private void drawCardVFX() {
        if (gameState.getVFXDurationTime() > 0) {
            if (millis() - gameState.getVFXStartTime() < gameState.getVFXDurationTime()) {
                gameState.getCurrentVFXEffect().run();
            } else {
                gameState.setVFXDurationTime(0);
            }
        }
    }

    /**
     * Draws the battle button which initiates the battle phase
     */
    private void drawBattleButton() {
        // Draw the button (black transparent rectangle)
        if (gameState.getIsBattlePhase()) {
            fill(255, 0, 30, 230);
        } else {
            fill(0, 0, 0, 170);
        }
        rect(buttonX, buttonY, buttonWidth, buttonHeight, 10);  // Draw rounded rectangle

        // Draw button text ("Battle")
        fill(255);  // White text color
        textSize(32);
        textAlign(CENTER, CENTER);
        text("Battle", buttonX + buttonWidth / 2, buttonY + buttonHeight / 2);  // Center the text in the button
    }

    /**
     * Draws each of the status UIs for the player
     * UIs include for player hp, enemy hp, turn number, and scripture count.
     */
    private void drawStatGUI() {
        // Player HP box
        fill(255, 150);
        rect(20, 20, 230, 40);
        textSize(26);
        textAlign(LEFT, TOP);
        fill(0);
        text(player.getName() + " Life Force: " + player.getLifeForce(), 30, 30);

        // Enemy HP box
        fill(255, 215, 0, 150);
        rect(width - 260, 20, 235, 40);  // Draw a rounded rectangle for Enemy HP
        textAlign(RIGHT, TOP);
        fill(0);
        text(enemy.getName() + " Life Force: " + enemy.getLifeForce(), width - 30, 30);

        // Turn number box
        fill(0, 0, 0, 150);
        rect(width - 195, 350, 110, 40, 10);  // Draw a rounded rectangle for Turn number
        textAlign(RIGHT, TOP);
        fill(255);
        text("Turn: " + gameState.getTurnNum(), width - 97, 360);

        // Scriptures
        fill(255, 150);
        rect(20, 520, 160, 40);
        textSize(26);
        textAlign(LEFT, TOP);
        fill(0);
        text("Scriptures: " + player.getScriptures(), 30, 530);
    }

    /**
     * Draws the card info on top of the card, both when it is selected and not.
     */
    private void drawCardInfo(Cards card, float x, float y, boolean isSelected) {
        // Selected cards
        if (isSelected) {
            // Adjust text size
            textSize(16);

            // **1. Card Name**
            // Draw background rectangle for card name (top, left-centered)
            fill(0, 0, 0, 170);  // Almost opaque black for the rectangle
            rect(x + card.getCardWidth() / 2 - 70, y - 35, 140, 30);  // Rectangle background for name area
            fill(255);  // White text for visibility
            textAlign(CENTER, TOP);
            text(card.getCardName(), x + card.getCardWidth() / 2, y - 27);  // Move the name text slightly upwards

            // **2. Power (bottom-left corner)**
            // Draw background rectangle for Power
            fill(0, 0, 0, 170);  // Fully opaque black for the rectangle
            rect(x - 5, y + card.getCardHeight() - 22, 68, 30);  // Rectangle background for power area
            textAlign(LEFT, BOTTOM);
            fill(255);  // White text for visibility
            text("PW: " + card.getPower(), x + 11, y + card.getCardHeight() + 2);  // Move the power text slightly upward

            // **3. Life Force (bottom-right corner)**
            // Draw background rectangle for Life Force
            fill(0, 0, 0, 170);  // Fully opaque black for the rectangle
            rect(x + card.getCardWidth() - 56, y + card.getCardHeight() - 22, 61, 30);  // Rectangle background for life force area
            textAlign(RIGHT, BOTTOM);
            fill(255);  // White text for visibility
            text("LF: " + card.getLifeForce(), x + card.getCardWidth() - 10, y + card.getCardHeight() + 3);  // Move the life force text slightly upward

            // **4. Effect Lore (bottom-center area)**
            // Draw background rectangle for Effect Lore
            fill(0, 0, 0, 170);  // Fully opaque black for the rectangle
            rect(x + card.getCardWidth() / 2 - 80, y + card.getCardHeight() - 75, 160, 50);  // Increased height for lore area
            textAlign(CENTER, BOTTOM);
            fill(255);  // White text for visibility

            // Call wrapText to split the lore text
            String loreText = card.getCardEffectLore();
            String[] wrappedLines = wrapText(loreText, 160);

            // Reverse the order of the lines
            Collections.reverse(Arrays.asList(wrappedLines));

            // Draw each wrapped line, adjusting vertical spacing
            float yOffset = y + card.getCardHeight() - 50;  // Starting y position for the first line
            for (String line : wrappedLines) {
                text(line, x + card.getCardWidth() / 2, yOffset);  // Draw each line
                yOffset += 18;  // Move down for the next line
            }
        }
        // Unselected cards
        else {
            // Adjust text size
            textSize(12);

            // **1. Card Name**
            // Draw background rectangle for card name (top, left-centered)
            fill(0, 0, 0, 170);  // Almost opaque black for the rectangle
            rect(x + card.getCardWidth() / 2 - 60, y - 30, 120, 20);  // Rectangle background for name area
            fill(255);  // White text for visibility
            textAlign(CENTER, TOP);
            text(card.getCardName(), x + card.getCardWidth() / 2, y - 25);  // Move the name text slightly upwards

            // **2. Power (bottom-left corner)**
            // Draw background rectangle for Power
            fill(0, 0, 0, 170);  // Fully opaque black for the rectangle
            rect(x + 5, y + card.getCardHeight() - 25, 58, 20);  // Rectangle background for power area
            textAlign(LEFT, BOTTOM);
            fill(255);  // White text for visibility
            text("Power: " + card.getPower(), x + 12, y + card.getCardHeight() - 8);  // Move the power text slightly upward

            // **3. Life Force (bottom-right corner)**
            // Draw background rectangle for Life Force
            fill(0, 0, 0, 170);  // Fully opaque black for the rectangle
            rect(x + card.getCardWidth() - 76, y + card.getCardHeight() - 25, 72, 20);  // Rectangle background for life force area
            textAlign(RIGHT, BOTTOM);
            fill(255);  // White text for visibility
            text("Life Force: " + card.getLifeForce(), x + card.getCardWidth() - 8, y + card.getCardHeight() - 8);  // Move the life force text slightly upward

            // **4. Effect Lore (bottom-center area)**
            // Draw background rectangle for Effect Lore
            fill(0, 0, 0, 170);  // Fully opaque black for the rectangle
            rect(x + card.getCardWidth() / 2 - 60, y + card.getCardHeight() - 72, 120, 40);  // Increased height for lore area
            textAlign(CENTER, BOTTOM);
            fill(255);  // White text for visibility

            // Call wrapText to split the lore text
            String loreText = card.getCardEffectLore();
            String[] wrappedLines = wrapText(loreText, 120);  // Max width is 120 for the lore box

            // Reverse the order of the lines
            Collections.reverse(Arrays.asList(wrappedLines));

            // Draw each wrapped line, adjusting vertical spacing
            float yOffset = y + card.getCardHeight() - 53;  // Starting y position for the first line
            for (String line : wrappedLines) {
                text(line, x + card.getCardWidth() / 2, yOffset);  // Draw each line
                yOffset += 15;  // Move down for the next line
            }
        }
    }

    /**
     * Stores the data and draws the game console.
     */
    private void drawConsole() {
        textAlign(LEFT, BOTTOM);
        int yOffset = 500;

        // Draw the console background (black inside)
        fill(0, 170); // Set the fill color to black
        noStroke(); // No border for the inside of the console
        rect(20, yOffset - consoleHeight - 20, consoleWidth, consoleHeight + 5, 10);

        // Draw the console border (white outline)
        stroke(255); // Set the stroke color to white
        strokeWeight(2); // Set the border thickness
        noFill(); // Don't fill the rectangle, just draw the border
        rect(20, yOffset - consoleHeight - 20, consoleWidth, consoleHeight + 5, 10);

        // Draw the console text
        fill(255); // White text color
        textSize(14);

        // We need to keep track of how many lines we've drawn
        int totalLines = 0;

        // Reverse the drawing order: start from the bottom
        for (int i = consoleLines.size() - 1; i >= 0; i--) {
            String message = consoleLines.get(i);

            // Split the message into lines that fit within the console width
            String[] wrappedLines = wrapText(message, consoleWidth - 40); // 40 is the padding for the console's margins

            // Draw each wrapped line
            for (String line : wrappedLines) {
                if (totalLines >= maxConsoleLines) {
                    break; // Stop drawing if we've reached the maximum number of lines
                }
                text(line, 30, yOffset - 25); // Draw each line at the current Y position
                yOffset -= lineHeight; // Move down by lineHeight for the next line
                totalLines++; // Increment the line count
            }

            // Add space between messages (1 line height)
            if (totalLines < maxConsoleLines) {
                yOffset -= lineHeight; // Add space between messages
                totalLines++; // Count this space as a line
            }

            // Break out of the loop if we’ve reached the max allowed lines
            if (totalLines >= maxConsoleLines) {
                break;
            }
        }
    }

    /**
     * A general helper method used to wrap text and fit within a certain width
     * Additionally reverses the order in which the text is printed
     */
    private String[] wrapText(String text, float maxWidth) {
        ArrayList<String> lines = new ArrayList<>();
        String[] words = text.split(" ");  // Split the text into words

        StringBuilder currentLine = new StringBuilder();  // To build the current line
        for (String word : words) {
            // Add the word and check if it exceeds the max width
            if (textWidth(currentLine + " " + word) <= maxWidth) {
                currentLine.append(" ").append(word);  // Add word to current line
            } else {
                // If it exceeds, add the current line to the list and start a new line
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder(word);  // Start new line with the current word
            }
        }

        // Add the last line if there's any content in the currentLine
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        // Reverse the order of the lines
        Collections.reverse(lines);

        // Convert the list of lines to a String array and return
        return lines.toArray(new String[0]);
    }

    /**
     * Adds a message to the game console to be displayed
     *
     * @param message The String message to be printed out in the console
     */
    public void printToConsole(String message) {
        // Add the new message to the list of console lines
        consoleLines.add(message);

        // If the number of lines exceeds the max allowed, remove the oldest one
        if (consoleLines.size() > maxConsoleLines) {
            consoleLines.remove(0);
        }
    }


    /**
     * PApplet method that runs whenever a mouse is pressed. Used for interaction
     * with the user and the game.
     */
    public void mousePressed() {
        // Has not played tutorial
        if (!savemanager.getPlayedTutorial()) {
            tutorialNum++;
            if (tutorialNum == 8) {
                savemanager.setPlayedTutorial(true);
            }
        }
        else if (gameState.getGameOver() || player.getScriptures() >= 81) {
            System.exit(0);
        }

        // By single action and order of priority
        Cards hasInteracted = selectedCard; // Effective boolean

        // Card in player hand is clicked
        selectedCard = hands.clickHands(selectedCard, mouseX, mouseY, hands.getPlayerHand().size() - 1);
        if (selectedCard != hasInteracted) {
            // If there is no selected card
            if (selectedCard != null) {
                // Selected card into
                printToConsole("+ " + selectedCard.getCardName() + "   Life Force: " + selectedCard.getLifeForce() + "   Power: " + selectedCard.getPower() + "   Card Effect: " + selectedCard.getCardEffectLore());
            }
            return;
        }

        // Card in player lane is clicked
        for (int i = 0; i < lanes.getPlayerLane().size(); i++) {
            Cards card = lanes.getPlayerLane().get(i);
            int laneX = lanes.getStartX() + i * (lanes.getLaneWidth() + lanes.getLaneSpacing());
            int laneY = uiHeight - 330;
            if (card != null && mouseX >= laneX && mouseX <= laneX + lanes.getLaneWidth() &&
                    mouseY >= laneY && mouseY <= laneY + lanes.getLaneHeight()) {
                selectedCard = selectedCard == card ? null : card; // Toggle selection
                if (selectedCard != null) {
                    // Selected card into
                    printToConsole(selectedCard.getCardName() + "   Life Force: " + selectedCard.getLifeForce() + "   Power: " + selectedCard.getPower() + "   Card Effect: " + selectedCard.getCardEffectLore());
                }
                return;
            }
        }
        // If there has not been an interaction
        if (selectedCard != hasInteracted) {
            return;
        }

        // If it's the player's turn and not already in the battle phase
        if (gameState.getTurnNum() % 2 != 0 && !gameState.getIsBattlePhase()) {
            // Click (place card) in player lane
            int playerLaneIndex = lanes.checkLaneClick(mouseX, mouseY, false); // Check player's lanes
            if (playerLaneIndex != -1 && selectedCard != null && selectedCard.getCurrentLane() == 0) {
                // Place the card in the player's lane
                lanes.placeCardInLane(selectedCard, playerLaneIndex);
                // Remove card from player's hand
                hands.removeCardFromHand(0, selectedCard);
                System.out.println("Card placed in lane: " + selectedCard);
                selectedCard = null; // Deselect after placing
                return;
            }
            // If there has not been an interaction
            if (selectedCard != hasInteracted) {
                return;
            }

            // Check if the mouse click is inside the battle button
            if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                    mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
                // Deselect selected card
                selectedCard = null;
                // Update phase
                printToConsole("< You entered into battle!");
                gameState.setPhaseStartTime(millis());
                gameState.setIsBattlePhase(true);
            }
        }
    }
}