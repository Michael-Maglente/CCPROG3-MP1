/**
 * This class stores the attributes and actions of a Zombie
 */
public class Zombie {
    private int health;
    private final int damage;
    private final double speed;
    private final int x;
    private int y;
    private int lastMoveTime; // last time a zombie moved //
    private boolean attacking;
    /**
     * This creates a Zombie
     * @param x Which row Peashooter is located
     * @param y Which column Peashooter is located
     * @param currentTime time when Zombie is spawned
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
    /**
     * This checks if a Zombie can move
     * @param currentTime current time at the moment
     */
    public boolean canMove(int currentTime){
        return !attacking && (currentTime - lastMoveTime >= speed);
    }
    /**
     * This allows a Zombie to walk or move
     * @param currentTime current time at the moment
     */
    public void walk(int currentTime){
        if(y > 0){
            y--;
            lastMoveTime = currentTime;
        }
    }
    /**
     * This checks if a Zombie is dead
     */
    public boolean isDead(){
        return health <= 0;
    }
    /**
     * This lets a Zombie attack a plant
     */
    public void attack(){
        attacking = true;
    }
    /**
     * This lets a Zombie stop attacking (for continuing movement)
     */
    public void stopAttack(){
        attacking = false;
    }
    /**
     * This checks if a Zombie is attacking a plant
     */
    public boolean isAttacking(){
        return attacking;
    }
    /**
     * This makes a Zombie lose health points (take damage)
     * @param dmg how much damage a plant deals
     */
    public void loseHP(int dmg){
        health -= dmg;
    }
    /**
     * This gets the health of a Zombie
     * @return how much damage it can sustain
     */
    public int getHealth() {
        return health;
    }
    /**
     * This gets the speed of a Zombie
     * @return how fast a Zombie moves
     */
    public double getSpeed() {
        return speed;
    }
    /**
     * This gets the damage of a Zombie
     * @return how much damage a Zombie deals to a plant
     */
    public int getDamage() {
        return damage;
    }
    /**
     * This gets the x-coordinates of a Zombie
     * @return x-coordinate / row index
     */
    public int getX() {
        return x;
    }
    /**
     * This gets the y-coordinates of a Zombie
     * @return y-coordinate / column index
     */
    public int getY(){
        return y;
    }
    /**
     * This gets the last time a Zombie has moved
     * @return last time a Zombie moved
     */
    public int getLastMoveTime() {
        return lastMoveTime;
    }
}
