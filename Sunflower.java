public class Sunflower extends Plant{
    private final int amountSun;
    private final int sunProductionInterval;
    private int lastProdTime; // last time a sunflower was planted

    Sunflower(int x, int y, int currentTime){
        super(x, y);
        this.cost = 50;
        this.health = 6;
        this.regenerateRate = 7.5;
        this.damage = 0;
        this.directDamage = 0;
        this.range = 0;

        this.sunProductionInterval = 24;
        this.amountSun = 25;
        this.lastProdTime = currentTime;
    }

    public boolean canProduceSun(int currentTime){
        return (currentTime - lastProdTime) >= sunProductionInterval;
    }

    public void produceSun(int currentTime){
        lastProdTime = currentTime;
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
}
