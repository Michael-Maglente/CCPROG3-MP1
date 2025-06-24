import java.util.*;
public class PvZ {
    // CONSTANTS //
    static final int ROWS = 6;
    static final int COLS = 10;
    static final int TIME_LIMIT = 180;
s
    // State of the game //
    static int sun = 50;
    static int currentTime = 0;
    static int sunDrops = 0;

    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random(); // for zombie location control //

    static List<Peashooter> peashooters = new ArrayList<>(); // where peashooters are stored
    static List<Sunflower> sunflowers = new ArrayList<>(); // where sunflowers are stored
    static List<Zombie>[] laneZombies = new ArrayList[ROWS]; // where zombies are stored or initialized //
    static List<int []> peas = new ArrayList<>();
    static String[][] lawn = new String[ROWS][COLS];

    static Map<String, Double> lastPlantedTime = new HashMap<>();
    static String timeFormat(int t){
        return String.format("%02d:%02d", t / 60, t % 60);
    }

    public static void main(String[] args) throws InterruptedException{
        // LANE INITIALIZATION //

        // game start
        // GAME LOOP //
        laneInitialized();
        System.out.println("READY...SET...PLANT!");
        System.out.println("Duration: 03:00 | Current Sun: " + sun);

        while (currentTime <= TIME_LIMIT) {
            System.out.println("\nTime: " + timeFormat(currentTime));
            System.out.println("Current Sun: " + sun);

            // LAWN WORK //
            changeLawn();
            displayLawn();

            // SUN PRODUCTION //
            sunDropFromSky();
            produceSunFromSunflower();
            collectSun();

            // ZOMBIE LOGIC //
            // zombieSpawn();
            // moveZombies();
            // plantVsZombie();

            // PLANT ATTACK LOGIC //
            // peashooterShoot();
            // movePeas();

            // PLANTING WINDOW LOGIC //
            if ((currentTime % 10 > 0 && currentTime % 10 < 10 && currentTime > 30 && currentTime < 80) ||
                    (currentTime % 5 >= 0 && currentTime % 5 <= 5 && currentTime > 81 && currentTime < 140) ||
                    (currentTime % 3 >= 0 && currentTime % 3 <= 3 && currentTime > 141 && currentTime < 170) ||
                    (currentTime > 171 && currentTime < 180)) {
                plantPlacementPrompt();
            }

            // PLANT PLACEMENT AND REMOVAL //
            deadPlantsAndZombies(); //

            currentTime++;
            Thread.sleep(1000);
        }
        System.out.println("Game over! Plants win");
    }

    // LAWN DISPLAY //
    public static void displayLawn() {
        int i, j;
        System.out.println();
        System.out.print("        ");
        for(j = 1; j < COLS; j++){
            System.out.printf("  %2d  ", j);
        }
        System.out.println();

        for(i = 1; i < ROWS; i++){
            System.out.print("        ");
            for(j = 1; j < COLS; j++){
                System.out.print("*-----");
            }
            System.out.println("*");
            // CONTENT ROW //
            System.out.printf("Row %-2d  ", i);
            for(j = 1; j < COLS; j++){
                if(lawn[i][j] == null || lawn[i][j].isBlank()){
                    System.out.print("|     ");
                } else{
                    System.out.printf("| %-2s  ", lawn[i][j]);
                }
            }
            System.out.println("|");
        }

        System.out.print("        ");
        for(j = 1; j < COLS; j++){
            System.out.print("*-----");
        }
        System.out.println("*");
    }
    public static void changeLawn(){
        int i, j;
        for(i = 1; i < ROWS; i++){ // clear lawn for update //
          for(j = 1; j < COLS; j++){
              lawn[i][j] = " ";
          }
        }
        // adding peashooters //
        for(Peashooter p : peashooters){
            if(lawn[p.getX()][p.getY()].equals(" ")){
                lawn[p.getX()][p.getY()] = "P";
            }
            else{
                lawn[p.getX()][p.getY()] += "P";
            }
        }
        // adding sunflowers //
        for(Sunflower sf : sunflowers){
            lawn[sf.getX()][sf.getY()] = "SF";
        }
        // adding zombies (may override when plant is eaten //
        for(i = 1; i < ROWS; i++){
            for(Zombie z : laneZombies[i]){
                if(lawn[i][z.getY()].equals(" ")){
                    lawn[i][z.getY()] = "Z";
                }
                else{
                    lawn[i][z.getY()] += "Z";
                }
            }
        }
        // for peas //
        for(int [] pea : peas){
            int x = pea[0], y = pea[1];
            if(y < COLS){
                if(lawn[x][y].equals(" ")){
                    lawn[x][y] = "O";
                } else{
                    lawn[x][y] += "O";
                }
            }
        }

    }

    // LANE INITIALIZATION //
    /**
     * This method controls how the lanes are supposed to be initialized, essentially
     * preparing the zombies for attack to the house
     *
     */
    public static void laneInitialized(){
        // There should be list of zombies prepared in each 5 lanes //
        int i;
        for(i = 1; i < ROWS; i++){
            laneZombies[i] = new ArrayList<>();
        }
        lastPlantedTime.put("SF", -999.0); // For cooldown //
        lastPlantedTime.put("P", -999.0); // For cooldown //
    }
    // SUN PRODUCTION //

    /**
     * Controls how the sun is supposed to drop from the sky
     *
     */
    public static void sunDropFromSky(){
        if(currentTime != 0 && currentTime % 10 == 0){
            sunDrops++;
            System.out.println("☀️ Sun has dropped from the sky!");
        }
    }
    /**
     * Controls how the sun is supposed to be collected when dropped from the sky
     *
     */
    public static void collectSun(){
        if(sunDrops > 0){
            System.out.print("☀️ You have " + sunDrops + " uncollected sun(s). Collect now (yes/no)");
            String decision = scanner.nextLine().trim().toLowerCase();
            if(decision.equals("yes")){
                int gained = sunDrops * 25;
                sun += gained;
                sunDrops = 0;
                System.out.println("Collected " + gained + " sun. " + " Total Sun: " + sun);
            }
            else{
                System.out.println("Skipped collecting sun.");
            }
        }else if(sunDrops < 0){
            System.out.println("No sun to collect. Try again later.");
        }
    }
    /**
     * Controls how the Sunflower produces sun for placing plants
     *
     */
    public static void produceSunFromSunflower(){
        for(Sunflower sf : sunflowers){
            if(sf.canProduceSun(currentTime) && (currentTime - sf.getLastProdTime()) >= sf.getSunProductionInterval()
                    && currentTime != sf.getLastProdTime()){
                sun += sf.getSunAmount();
                sf.produceSun(currentTime);
                System.out.println("Sunflower at Row" + sf.getX() + ", Column " + sf.getY() + " has produced " + sf.getSunAmount() + " sun.");
            }
        }
    }
    /**
     * Controls how the user places the plants in the tiles of the lawn to defend the house.
     *
     */
    // PLANT PLACEMENT //
    public static void plantPlacementPrompt(){
        if(sun >= 50){
            plantCooldowns();

            System.out.println("Do you want to use: [S] Shovel, [SF] Sunflower, [P] Peashooter to the board, or skip.");
            String plant = scanner.nextLine().trim().toLowerCase();

            if(plant.equals("skip")){
                return;
            }

            System.out.print("Enter row (1-5): ");
            int x = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter column (1-9): ");
            int y = Integer.parseInt(scanner.nextLine());

            // Shovel use //
            if(plant.equalsIgnoreCase("S")){
                if(getPeashooterAt(x, y) != null){
                    peashooters.removeIf(p -> p.getX() == x && p.getY() == y);
                    System.out.println("Peashooter was removed at Row " + x + ", Column " + y);
                } else if(getSunflowerAt(x, y) != null){
                    sunflowers.removeIf(s -> s.getX() == x && s.getY() == y);
                    System.out.println("Sunflower was removed at Row " + x + ", Column " + y);
                } else {
                    System.out.println("No plant found at that location.");
                }
                return;
            }
            // Sunflower placement //
            if (plant.equalsIgnoreCase("SF")) {
                Sunflower temp = new Sunflower(x, y, currentTime);
                temp.setLastProdTime(currentTime);
                double last = lastPlantedTime.get("SF");
                if (sun >= temp.getCost() && (currentTime - last) >= temp.getRegenerateRate()) {
                    sunflowers.add(temp);
                    sun -= temp.getCost();
                    lastPlantedTime.put("SF", (double) currentTime);
                    System.out.println("The sunflower has been planted at Row " + x + ", Column " + y);
                } else if (isTileOccupied(x, y)) {
                    System.out.println("Tile already occupied! Choose another location");
                } else if((currentTime - last) < temp.getRegenerateRate()){
                    System.out.println("Can't plant Sunflower (not enough sun or still on cooldown)");
                }
            }
            // Peashooter placement //
            if (plant.equalsIgnoreCase("P")) {
                Peashooter temp = new Peashooter(x, y);
                double last = lastPlantedTime.get("P");
                if (sun >= temp.getCost() && (currentTime - last) >= temp.getRegenerateRate()) {
                    peashooters.add(temp);
                    sun -= temp.getCost();
                    lastPlantedTime.put("P", (double) currentTime);
                    System.out.println("The peashooter has been planted at Row " + x + ", Column" + y);
                } else if (isTileOccupied(x, y)) {
                    System.out.println("Tile already occupied! Choose another location");
                } else if((currentTime - last) < temp.getRegenerateRate()){
                    System.out.println("Can't plant Peashooter (not enough sun or still on cooldown)");
                }
            }
        } else{
            System.out.println("Can't plant a plant at the moment (no sun / cooldown)");
        }
    }

    /**
     * Controls the display of plant cooldowns when dealing with plant placement
     */
    public static void plantCooldowns(){
        double sfCooldown = Math.max(0, lastPlantedTime.get("SF")) + new Sunflower(0,0, currentTime).getRegenerateRate() - currentTime;
        double pCooldown = Math.max(0, lastPlantedTime.get("P")) + new Sunflower(0,0, currentTime).getRegenerateRate() - currentTime;
        System.out.println("Plant Cooldowns: ");

        if(sfCooldown > 0){
            System.out.printf("   - [SF] Sunflower: %.1f s\n", sfCooldown);
        } else{
            System.out.println("   - [SF] Sunflower: Ready");
        }
        if(pCooldown > 0){
            System.out.printf("   - [P] Peashooter: %.1f s\n", sfCooldown);
        } else{
            System.out.println("   - [P] Peashooter: Ready");
        }
    }
    // PLANT ATTACK //
    /**
     * Controls how the Peashooters are going to attack the zombies
     *
     */
    public static void peashooterShoot(){
        for(Peashooter p : peashooters){
            List<Zombie> zombiesInLanes = laneZombies[p.getX()];
            Zombie enemy = null;
            if(p.canShootPea(currentTime)){
                for(Zombie z : zombiesInLanes){
                    if(z.getY() > p.getY() && z.getY() <= p.getY() + p.getRange()){
                        if(enemy == null || z.getY() < enemy.getY()){
                            enemy = z;
                        }
                    }
                }
                if(enemy != null){
                    if(p.canShootPea(currentTime)){
                        peas.add(new int[]{p.getX(), p.getY() + 1, p.getDamage()});
                        p.updateShootPeaTime(currentTime);
                        System.out.println("Peashooter at Row " + p.getX() + ", Column " + p.getY() + " fired a pea!");
                    }
                }
            }
        }
    }
    /**
     * Controls how the peas of the Peashooter shoot the zombies (projectile)
     *
     */
    public static void movePeas(){
        List<int[]> toRemove = new ArrayList<>(); // to remove peas //

        for(int[] pea : peas){
            pea[1]++; // move to the right

            if(pea[1] >= COLS){ // if the pea goes out of bounds
                toRemove.add(pea);
            }
            // Check collision with zombie //
            List<Zombie> lane  = laneZombies[pea[0]];
            for(Zombie z : lane){
                if(z.getY() == pea[1]){
                    z.loseHP(pea[2]);
                    System.out.println("Pea hit Zombie at Row " + pea[0] + ", Column " + z.getY() + "(-" + pea[2] + " HP)");
                    toRemove.add(pea);
                    break;
                }
            }
        }
        peas.removeAll(toRemove);
    }
    // ZOMBIE LOGIC //
    /**
     * Controls the spawn logic of the zombies and how they are going to spawn per interval
     *
     */
    public static void zombieSpawn(){
        if(currentTime >= 30 && currentTime <= 80 && currentTime % 10 == 0)
        {
            int lane = random.nextInt(ROWS);
            Zombie newZombie = new Zombie(lane, COLS - 1, currentTime);

            // Overcrowding prevention //
            boolean occupiedTile = false;
            for (Zombie z : laneZombies[lane]) {
                if (z.getY() == COLS - 1) {
                    occupiedTile = true;
                    break;
                }
            }

            if (!occupiedTile) { // If tile is not occupied //
                laneZombies[lane].add(newZombie);
                System.out.println("Zombie spawned at Row " + lane + ", Column " + (COLS - 1) + " (Speed: " + newZombie.getSpeed() + ", Damage: "
                        + newZombie.getDamage() + ", Health: " + newZombie.getHealth() + ")");
            } else {
                System.out.println("Zombie spawn skipped, Tile occupied at Row " + lane);
            }
        }
        if(currentTime >= 81 && currentTime <= 140 && currentTime % 5 == 0){
            int lane = random.nextInt(ROWS);
            Zombie newZombie = new Zombie(lane, COLS - 1, currentTime);

            // Overcrowding prevention //
            boolean occupiedTile = false;
            for(Zombie z : laneZombies[lane]){
                if(z.getY() == COLS - 1){
                    occupiedTile = true;
                    break;
                }
            }

            if(!occupiedTile){ // If tile is not occupied //
                laneZombies[lane].add(newZombie);
                System.out.println("Zombie spawned at Row " + lane + ", Column " + (COLS - 1) + " (Speed: " + newZombie.getSpeed() + ", Damage: "
                        + newZombie.getDamage() + ", Health: " + newZombie.getHealth() + ")");
            } else {
                System.out.println("Zombie spawn skipped, Tile occupied at Row " + lane);
            }
        }

        if(currentTime >= 141 && currentTime <= 170 && currentTime % 3 == 0){
            int lane = random.nextInt(ROWS);
            Zombie newZombie = new Zombie(lane, COLS - 1, currentTime);

            // Overcrowding prevention //
            boolean occupiedTile = false;
            for(Zombie z : laneZombies[lane]){
                if(z.getY() == COLS - 1){
                    occupiedTile = true;
                    break;
                }
            }

            if(!occupiedTile){ // If tile is not occupied //
                laneZombies[lane].add(newZombie);
                System.out.println("Zombie spawned at Row " + lane + ", Column " + (COLS - 1) + " (Speed: " + newZombie.getSpeed() + ", Damage: "
                        + newZombie.getDamage() + ", Health: " + newZombie.getHealth() + ")");
            } else {
                System.out.println("Zombie spawn skipped, Tile occupied at Row " + lane);
            }
        }

        if(currentTime >= 171 && currentTime <= 180){
            System.out.println("A huge wave of zombies is approaching!");

            int i;
            for(i = 0; i < 5; i++){
                int lane = random.nextInt(ROWS);
                Zombie newZombie = new Zombie(lane, COLS - 1, currentTime);

                // Overcrowding prevention //
                boolean occupiedTile = false;
                for(Zombie z : laneZombies[lane]){
                    if(z.getY() == COLS - 1){
                        occupiedTile = true;
                        break;
                    }
                }

                if(!occupiedTile){ // If tile is not occupied //
                    laneZombies[lane].add(newZombie);
                    System.out.println("Zombie spawned at Row " + lane + ", Column " + (COLS - 1) + " (Speed: " + newZombie.getSpeed() + ", Damage: "
                            + newZombie.getDamage() + ", Health: " + newZombie.getHealth() + ")");
                } else {
                    System.out.println("Zombie spawn skipped, Tile occupied at Row " + lane);
                }
            }
        }
    }

    /**
     * Controls how the zombies move towards the house
     *
     */
    public static void moveZombies(){
        int x;
        for(x = 1; x < ROWS; x++){
            List<Zombie> zombiesInLanes = laneZombies[x];
            for(Zombie z : zombiesInLanes){
                if(z.canMove(currentTime)){
                    z.walk(currentTime);
                    System.out.println("Zombie moved to Row " + z.getX() + ", Column " + z.getY());

                    if(z.getY() == 0){
                        System.out.println("THE ZOMBIES HAVE EATEN YOUR BRAINS! Game over!");
                        System.exit(0);
                    }
                }
            }
        }
    }
    /**
     * Controls the movement of the zombies toward the house
     *
     */
    public static void plantVsZombie(){
        int x;
        for(x = 1; x < ROWS; x++){
            for(Zombie z : laneZombies[x]){
                for(Peashooter p : peashooters){
                    if(p.getX() == x && p.getY() == z.getY()){
                        if(p.getHealth() > 0){
                            p.loseHP(z.getDamage());
                            System.out.println("Zombie at Row " + x + ", Column " + z.getY() + " attacked Peashooter (-" + z.getDamage() + " HP)");
                        }
                    }
                }
                for(Sunflower s : sunflowers){
                    if(s.getX() == x && s.getY() == z.getY()){
                        if(s.getHealth() > 0){
                            s.loseHP(z.getDamage());
                            System.out.println("Zombie at Row " + x + ", Column " + z.getY() + " attacked Sunflower (-" + z.getDamage() + " HP)");
                        }
                    }
                }
            }
        }
    }
    // REMOVAL //
    /**
     * Controls the removal of plants and zombies if they are dead
     *
     */
    public static void deadPlantsAndZombies(){
        peashooters.removeIf(p ->{
            if(p.isDead()){
                System.out.println("Peashooter at Row " + p.getX() + ", Column " + p.getY() + " has been eaten.");
                return true;
            }
            return false;
        });

        sunflowers.removeIf(sf ->{
            if(sf.isDead()){
                System.out.println("Sunflower at Row " + sf.getX() + ", Column " + sf.getY() + " has been eaten.");
                return true;
            }
            return false;
        });
        int x;
        for(x = 1; x < ROWS; x++){
            final int currentX = x;
            laneZombies[x].removeIf(z ->{
                if(z.isDead()){
                    System.out.println("Zombie at Row " + currentX + ", Column " + z.getY() + " has been defeated.");
                    return true;
                }
                return false;
            });
        }
    }
    // LOCATIONS //
    /**
     * Controls where Peashooter is located
     *
     */
    public static Peashooter getPeashooterAt(int x, int y){
        for(Peashooter p : peashooters){
            if(p.getX() == x && p.getY() == y){
                return p;
            }
        }
        return null;
    }
    /**
     * Controls where Sunflower is located
     *
     */
    public static Sunflower getSunflowerAt(int x, int y){
        for(Sunflower s : sunflowers){
            if(s.getX() == x && s.getY() == y){
                return s;
            }
        }
        return null;
    }
    /**
     * Controls if a plant tile is occupied by a Peashooter or a Sunflower
     *
     */
    static boolean isTileOccupied(int x, int y){
        return getSunflowerAt(x, y) != null || getPeashooterAt(x, y) != null;
    }
    /*
        Sources:
            https://www.youtube.com/watch?v=mcXc8Mva2bA
            https://www.geeksforgeeks.org/java/arraylist-in-java/
            https://www.reddit.com/r/java/comments/4v2uf9/made_a_textbased_open_world_pokemon_game_and_i/

    */
}
