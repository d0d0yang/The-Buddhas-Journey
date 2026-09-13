import processing.core.PApplet;

/**
 * Main class to launch the application and initialize the sketch.
 */
class Main {
    /**
     * Main entry point to run the application.
     *
     * @param args Command-line arguments (not used here).
     */
    public static void main(String[] args) {
        // Create an instance of PApplet to use its color() method
        PApplet app = new PApplet();

        // Initialize the sketch with the cards
        Sketch sketch = new Sketch();

        // Start the sketch
        PApplet.runSketch(new String[]{"Sketch"}, sketch);
    }
}
