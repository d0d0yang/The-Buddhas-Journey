import java.io.*;
import java.util.*;

/**
 * Manages saving and loading game data, such as scriptures, bonus cards, and tutorial progress.
 */
public class SaveManager {

    // Variables to store the save data
    private int scriptures;
    private List<Integer> bonusCards; // A List to store an arbitrary number of bonus cards
    private boolean playedTutorial;

    private static final String saveFile = "src\\Save_Data.txt";

    /**
     * Constructor for SaveManager, initializes the bonus cards list and loads the save data.
     */
    public SaveManager() {
        // Initialize the list for bonus cards
        bonusCards = new ArrayList<>();
        // Load the save data when the SaveManager is created
        getSave();
    }

    /**
     * Loads the save data from a file.
     * Parses the file line by line and updates the internal variables.
     */
    public void getSave() {
        try {
            File file = new File(saveFile);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                // Parse each line and set the corresponding variables
                String[] parts = line.split(": ");
                String key = parts[0].trim();
                String value = parts[1].trim();

                // For each data point
                switch (key) {
                    case "scriptures":
                        scriptures = Integer.parseInt(value);
                        break;
                    case "bonusCards":
                        bonusCards.clear(); // Clear any previous values in the list
                        String[] bonusValues = value.split(",");
                        for (String bonusValue : bonusValues) {
                            bonusCards.add(Integer.parseInt(bonusValue.trim())); // Add each bonus card value to the list
                        }
                        break;
                    case "playedTutorial":
                        playedTutorial = Boolean.parseBoolean(value);
                        break;
                    default:
                        System.out.println("Unknown key in save file: " + key);
                        break;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error reading the save file: " + e.getMessage());
        }
    }

    /**
     * Saves the current game data to the file.
     * Writes scriptures, bonus cards, and tutorial progress.
     */
    public void setSave() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));

            // Write the variables to the file
            bw.write("scriptures: " + scriptures + "\n");

            // Write bonus cards to the file as a comma-separated string
            bw.write("bonusCards: " + String.join(", ", bonusCards.stream().map(String::valueOf).toArray(String[]::new)) + "\n");

            bw.write("playedTutorial: " + playedTutorial + "\n");

            bw.close();
        } catch (IOException e) {
            System.out.println("Error writing to the save file: " + e.getMessage());
        }
    }

    /**
     * Resets the save data to default values.
     * Overwrites the save file with default values.
     */
    public void resetSave() {
        try {
            // Open the file for writing (this will overwrite the existing file)
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile, false)); // false ensures overwrite

            // Write default values to the file
            bw.write("scriptures: 0\n");
            bw.write("bonusCards: -1\n");  // Single default bonus card value, can be adjusted
            bw.write("playedTutorial: false\n");

            bw.close();
        } catch (IOException e) {
            System.out.println("Error creating the save file: " + e.getMessage());
        }
    }

    // Getters and setters
    public int getScriptures() {
        return scriptures;
    }

    public void setScriptures(int scriptures) {
        this.scriptures = scriptures;
        setSave();  // Automatically save after changing the value
    }

    public List<Integer> getBonusCards() {
        return bonusCards;
    }

    public void setBonusCard(int bonusCard) {
        bonusCards.add(bonusCard);  // Add the new bonus card to the list
        setSave();  // Automatically save after adding the card
    }

    public boolean getPlayedTutorial() {
        return playedTutorial;
    }

    public void setPlayedTutorial(boolean playedTutorial) {
        this.playedTutorial = playedTutorial;
        setSave();  // Automatically save after changing the value
    }
}
