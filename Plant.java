/**
 * The base class for all plants of the game
 * Defines the common attributes of plants like health, damage, speed, position (x, y)
 * Has methods for interacting with plant data
 */
public abstract class Plant {
    protected int cost;
    protected int health;
    protected double regenerateRate;
    protected int damage;
    protected int range;
    protected int directDamage;
    protected double speed;

    protected final int x;
    protected final int y;
    /**
     * This creates a Plant at a given position (x, y)
     * @param x x-coordinate / row index
     * @param y y-coordinate / column index
     */
    public Plant(int x, int y){
        this.x = x;
        this.y = y;
    }
    /**
     * This checks if a plant is dead
     */
    public boolean isDead(){
        return health <= 0;
    }
    /**
     * This checks if a plant is dead
     * @param dmg How much damage a zombie deals
     */
    public void loseHP(int dmg){
        health -= dmg;
    }
    /**
     * This gets the cost of a plant in sun
     * @return how much sun needed to use the plant
     */
    public int getCost() {
        return cost;
    }
    /**
     * This gets the health of a plant
     * @return how much damage a plant can sustain
     */
    public int getHealth() {
        return health;
    }
    /**
     * This gets how much damage a plant deals
     * @return how much damage a plant deals to a zombie
     */
    public int getDamage() {
        return damage;
    }
    /**
     * This gets how much damage a plant can deal at a closer range
     * @return how much damage a plant causes to a zombie at a closer range
     */
    public int getDirectDamage(){
        return directDamage;
    }
    /**
     * This gets the attack speed of a plant
     * @return how fast the next attack of a plant will be
     */
    public double getSpeed(){
        return speed;
    }
    /**
     * This gets the attack range of a plant
     * @return how far the attack reaches
     */
    public int getRange() {
        return range;
    }
    /**
     * This gets the regenerate rate of a plant
     * @return how much time a plant needs to regenerate (when will it be ready to be planted again?)
     */
    public double getRegenerateRate() {
        return regenerateRate;
    }
    /**
     * This gets the x-coordinate / row index of a plant
     * @return x-coordinate / row index
     */
    public int getX() {
        return x;
    }
    /**
     * This gets the y-coordinate / column index of a plant
     * @return y-coordinate / column index
     */
    public int getY() {
        return y;
    }
}
