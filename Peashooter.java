/**
 * This class stores the attributes and actions of the Peashooter, derived from the Plant class
 */
public class Peashooter extends Plant{
    private double lastShot; // timestamp that stores the last time the peashooter shoots a pea
    // private int lastProdTime; // last time a peashooter was planted
    /**
     * This creates a Peashooter
     * @param x Which row Peashooter is located
     * @param y Which column Peashooter is located
     */
    Peashooter(int x, int y){
        super(x, y);
        this.cost = 100;
        this.health = 60;
        this.damage = 10;
        this.regenerateRate = 7.5;
        this.range = 8;
        this.speed = 1.4;
        this.lastShot = -speed;
        this.directDamage = 20;
    }
    /**
     * This checks if the Peashooter can shoot a pea, checks if enough time has passed
     * since the last time a Peashooter shot a pea, based on its attack speed
     * @param currentTime Current time at the moment
     */
    public boolean canShootPea(double currentTime){
        return (currentTime - lastShot) >= speed;
    }
    /**
     * This updates the time a Peashooter shoots a pea
     * @param currentTime Current time at the moment
     */
    public void updateShootPeaTime(int currentTime){
        this.lastShot = currentTime;
    }
}
