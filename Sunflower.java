/**
 * This class stores the attributes and actions of the Sunflower, derived from the Plant class
 */
public class Sunflower extends Plant{
    private final int amountSun;
    private final int sunProductionInterval;
    private int lastProdTime; // last time a sunflower was planted
    /**
     * This creates a Sunflower
     * @param x Which row Sunflower is located
     * @param y Which column Sunflower is located
     * @param currentTime Current time at the moment (for sun production handling)
     */
    Sunflower(int x, int y, int currentTime){
        super(x, y);
        this.cost = 50;
        this.health = 300;
        this.regenerateRate = 7.5;
        this.damage = 0;
        this.directDamage = 0;
        this.range = 0;

        this.sunProductionInterval = 24;
        this.amountSun = 25;
        this.lastProdTime = currentTime;
    }
    /**
     * This checks if the Sunflower can produce sun
     * @param currentTime Current time at the moment
     */
    public boolean canProduceSun(int currentTime){
        return (currentTime - lastProdTime) >= sunProductionInterval;
    }
    /**
     * This lets the Sunflower produce the sun
     * @param currentTime Current time at the moment
     */
    public void produceSun(int currentTime){
        lastProdTime = currentTime;
    }
    /**
     * This gets the amount of sun that the Sunflower produces
     * @return the amount of sun a Sunflower produces
     */
    public int getSunAmount() {
        return amountSun;
    }
    /**
     * This gets how long it takes for the Sunflower to produce the sun
     *  @return the interval for sun production of Sunflower
     */
    public int getSunProductionInterval() {
        return sunProductionInterval;
    }
    /**
     * This gets the timestamp of when the Sunflower was planted last.
     * @return the last time a Sunflower has been planted
     */
    public int getLastProdTime() {
        return lastProdTime;
    }
    /**
     * This sets or updates the timestamp of when the Sunflower was planted last.
     * @param time This becomes the new timestamp for the last time a Sunflower was planted
     */
    public void setLastProdTime(int time){
        this.lastProdTime = time;
    }
}
