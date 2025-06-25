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

    public Plant(int x, int y){
        this.x = x;
        this.y = y;
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

    public int getDirectDamage(){
        return directDamage;
    }

    public double getSpeed(){
        return speed;
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
