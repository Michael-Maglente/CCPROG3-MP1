public class Peashooter extends Plant{
    private double lastShot;
    // private int lastProdTime; // last time a peashooter was planted

    Peashooter(int x, int y){
        super(x, y);
        this.cost = 100;
        this.health = 6;
        this.damage = 20;
        this.regenerateRate = 7.5;
        this.range = 8;
        this.speed = 2;
        this.lastShot = -speed;
        this.directDamage = 20;
    }

    public boolean canShootPea(int currentTime){
        return (currentTime - lastShot) >= speed;
    }

    public void updateShootPeaTime(int currentTime){
        this.lastShot = currentTime;
    }
}
