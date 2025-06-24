public class Zombie {
    private int health;
    private final int damage;
    private final double speed;
    private final int x;
    private int y;
    private int lastMoveTime; // last time a zombie moved //
    private boolean attacking;
    
    /**
     * Holds the stats of Zombies and generates zombies per lane
     */
    Zombie(int x, int y, int currentTime){
        this.health = 200;
        this.damage = 50;
        this.speed = 5;
        this.x = x;
        this.y = y;
        this.lastMoveTime = currentTime; // last time a zombie moved //
        this.attacking = false;
    }

    public boolean canMove(int currentTime){
        return !attacking && (currentTime - lastMoveTime >= speed);
    }

    public void walk(int currentTime){
        if(y > 0){
            y--;
            lastMoveTime = currentTime;
        }
    }

    public boolean isDead(){
        return health <= 0;
    }

    public void attack(){
        attacking = true;
    }

    public void stopAttack(){
        attacking = false;
    }

    public boolean isAttacking(){
        return attacking;
    }

    public int loseHP(int dmg){
        return health -= dmg;
    }

    public int getHealth() {
        return health;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public int getX() {
        return x;
    }

    public int getY(){
        return y;
    }

    public int getLastMoveTime() {
        return lastMoveTime;
    }
}
