public class Peashooter {
    private final int cost;
    private int health;
    private final int damage;
    private final int directDamage;
    private final int range;
    private final double regenerateRate;
    private int lastShot;
    private static final int SPEED = 2;
    private final int x;
    private final int y;
    // private int lastProdTime; // last time a peashooter was planted

    Peashooter(int x, int y){
        this.cost = 100;
        this.health = 6;
        this.damage = 20;
        this.regenerateRate = 7.5;
        this.range = 8;
        this.x = x;
        this.y = y;
        this.lastShot = -SPEED;
        this.directDamage = 20;
    }

    public boolean canShootPea(int currentTime){
        return (currentTime - lastShot) >= SPEED;
    }

    public void updateShootPeaTime(int currentTime){
        this.lastShot = currentTime;
    }
    public boolean isDead(){
        return health <= 0;
    }

    public int loseHP(int dmg){
        return health -= dmg;
    }

    public int getCost() {
        return cost;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getRange() {
        return range;
    }

    public double getRegenerateRate() {
        return regenerateRate;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
