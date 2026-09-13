/**
 * The Character class represents a character in the game.
 * Serves as base class for the player and enemy.
 */
public class Character {
    // Character life force
    private int lifeForce;
    private String name;

    /**
     * Constructs a Character object with no specified initial life force.
     *
     */
    public Character(String name) {
        this.name = name;
    }

    /**
     * Constructs a Character object with the specified initial life force and name.
     *
     * @param lifeForce The initial life force of the character.
     */
    public Character(String name, int lifeForce) {
        this.name = name;
        this.lifeForce = lifeForce;
    }

    /**
     * Applies damage to the character, reducing their life force by the specified amount.
     * Ensures that life force does not fall below zero.
     *
     * @param damage The amount of damage to deal to the character.
     */
    public void dealDamage(int damage) {
        // Reduce life force by damage
        this.lifeForce -= damage;
        // Prevent negative life force
        if (this.lifeForce < 0) {
            this.lifeForce = 0;
        }
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public int getLifeForce() {
        return lifeForce;
    }

    public void setLifeForce(int lifeForce) {
        this.lifeForce = lifeForce;
    }
}
