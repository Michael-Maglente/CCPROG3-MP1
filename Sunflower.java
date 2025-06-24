public class Sunflower {
    private final int cost;
    private int health;
    private final double regenerateRate;
    private final int amountSun;
    private final int sunProductionInterval;
    private int lastProdTime; // last time a sunflower was planted
    private final int damage;
    private final int range;
    private final int directDamage;

    private final int x;
    private final int y;


    Sunflower(int x, int y, int currentTime){
        this.x = x;
        this.y = y;
        this.cost = 50;
        this.health = 6;
        this.regenerateRate = 7.5;
        this.sunProductionInterval = 24;
        this.amountSun = 25;
        this.lastProdTime = currentTime;
        this.damage = 0;
        this.directDamage = 0;
        this.range = 0;
    }

    public boolean canProduceSun(int currentTime){
        return (currentTime - lastProdTime) >= sunProductionInterval;
    }

    public void produceSun(int currentTime){
        lastProdTime = currentTime;
    }

    public boolean isDead(){
        return health <= 0;
    }

    public void loseHP(int dmg){
        health -= dmg;
    }

    public double getRegenerateRate() {
        return regenerateRate;
    }

    public int getCost() {
        return cost;
    }

    public int getHealth() {
        return health;
    }

    public int getSunAmount() {
        return amountSun;
    }

    public int getSunProductionInterval() {
        return sunProductionInterval;
    }

    public int getLastProdTime() {
        return lastProdTime;
    }

    public void setLastProdTime(int time){
        this.lastProdTime = time;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
