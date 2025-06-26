import java.util.*;
/**
 * Welcome to Plants Vs Zombies. This is the main driver for simulating the game in console.
 * This handles the entire game loop, input handling, game states, and entity interactions:
 *  1. Managing the timings and game loop
 *  2. Zombie spawning over time
 *  3. Plant placements
 *  4. Sun production and collection handling
 *  5. Moving zombies and combat engagements
 *  6. Lawn state display and action prompting.
 */
public class PvZ {
    /** Number of playable rows*/
    public static final int ROWS = 6;
    /** Number of playable columns*/
    public static final int COLS = 10;
    /** Time limit of entire game */
    public static final int TIME_LIMIT = 180;

    // State of the game //
    /** Sun available for player */
    public static int sun = 50;
    /** Game time in seconds */
    public static int currentTime = 0;
    /** Uncollected sun as it falls from the sky */
    public static int sunDrops = 0;
    /** State of entire game running */
    public static boolean gameRunning = true;

    /** Scanner object for handling input of user*/
    public static Scanner scanner = new Scanner(System.in);
    /** Random object for zombie placement*/
    public static Random random = new Random(); // for zombie location control //
    /** List of plants placed by the player */
    public static List<Plant> plants = new ArrayList<>(); // where plants are stored now //
    /** List of zombies in each row/lane */
    public static List<List<Zombie>> laneZombies = new ArrayList<>();
    /** Queue of peas (projectiles) on the lawn */
    public static List<int []> peas = new ArrayList<>();
    /** 2D grid representing the lawn */
    public static String[][] lawn = new String[ROWS][COLS];

    /** Last planted timestamps for cooldown tracking */
    public static Map<String, Double> lastPlantedTime = new HashMap<>();
    /**
     * Displays the time format of the game (MM:SS)
     * */
    public static String timeFormat(int t){
        return String.format("%02d:%02d", t / 60, t % 60);
    }
    /**
     * The main method of the game, which starts and runs the game for 3 minutes
     * Initializes game state, loops every second, and performs timed events
     * @param args command-line arguments (not used)
     * */
    public static void main(String[] args) throws InterruptedException{
        // LANE INITIALIZATION //
        laneInitialized();
        // MENU //
        do{
            System.out.println("=== Welcome to Plants Vs Zombies ===");
            System.out.println("1 - Play");
            System.out.println("2 - Exit");
            System.out.print("Choose from these options: ");
            int selection = scanner.nextInt();
            scanner.nextLine();

            switch(selection){
                case 1:
                    // GAME LOOP //
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
                        zombieSpawn();
                        moveZombies();
                        plantVsZombie();

                        // PLANT ATTACK LOGIC //
                        peashooterShoot();
                        movePeas();
                        // PLANTING WINDOW LOGIC //
                        if(shouldShowPlantingPrompt()) {
                           plantPlacementPrompt();
                        }
                        // PLANT PLACEMENT AND REMOVAL //
                        deadPlantsAndZombies(); //

                        currentTime++;
                        Thread.sleep(1000);
                    }
                    System.out.println("Game over! Plants win!");
                    break;

                case 2:
                    System.out.println("Exiting Plants vs Zombies...");
                    System.out.println("Thank you!");
                    gameRunning = false;
                    break;
                default:
                    System.out.println("Invalid option! Try again...");
            }
        } while(gameRunning);
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
        for(Plant p : plants){
            if(p instanceof Peashooter){
                lawn[p.getX()][p.getY()] = "P";
            }
            else if(p instanceof Sunflower){
                lawn[p.getX()][p.getY()] = "S";
            }
        }
        // adding zombies (may override when plant is eaten //
        for(i = 1; i < ROWS; i++){
            for(Zombie z : laneZombies.get(i)){
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
        for(i = 0; i < ROWS; i++){
            laneZombies.add(new ArrayList<>());
        }
        lastPlantedTime.put("S", -999.0); // For cooldown //
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
            System.out.println(" Sun has dropped from the sky!");
        }
    }
    /**
     * Controls how the sun is supposed to be collected when dropped from the sky
     *
     */
    public static void collectSun(){
        if(sunDrops > 0){
            System.out.print(" You have " + sunDrops + " uncollected sun(s). Collect now (yes/no)");
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
        } else {
            System.out.println("No sun to collect.");
        }
    }
    /**
     * Controls how the Sunflower produces sun for placing plants
     *
     */
    public static void produceSunFromSunflower(){
        for(Plant p : plants){
            if(p instanceof Sunflower){
                Sunflower sf = (Sunflower) p;
                if(sf.canProduceSun(currentTime)){
                    sun += sf.getSunAmount();
                    sf.produceSun(currentTime);
                    System.out.println("Sunflower at Row " + p.getX() + ", Column " + p.getY() + " produced " + sf.getSunAmount() + " sun.");
                }
            }
        }
    }
    /**
     * Controls how the user places the plants in the tiles of the lawn to defend the house.
     *
     */
    // PLANT PLACEMENT //
    public static void plantPlacementPrompt(){
            plantCooldowns();

            while(sun >= 50){
                if(!plants.isEmpty()){
                    System.out.println("\nPlants on Lawn: ");
                    for(Plant p : plants){
                        String type = (p instanceof Sunflower) ? "Sunflower" : (p instanceof Peashooter) ? "Peashooter" : "Plant";
                        System.out.println("- " + type + " at Row " + p.getX() + ", Column " + p.getY() + " (" + p.getHealth() + " HP)");
                    }
                    System.out.println();
                }
                System.out.println("Do you want to use: [X] Shovel, [S] Sunflower, [P] Peashooter to the board, or skip.");
                String plant = scanner.nextLine().trim().toLowerCase();

                if(plant.equalsIgnoreCase("skip")){
                    return;
                }

                System.out.print("Enter row (1-5): ");
                int x = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter column (1-9): ");
                int y = Integer.parseInt(scanner.nextLine());

                // Shovel use //
                if(plant.equalsIgnoreCase("X")){
                    plants.removeIf(p -> p.getX() == x && p.getY() == y);
                    System.out.println("Plant at Row " + x + ", Column " + y + " has been removed.");
                }

                // If tile is already occupied by plants //
                if(isTileOccupied(x, y)){
                    System.out.println("Tile already occupied!");
                    return;
                }

                // Sunflower placement //
                if (plant.equalsIgnoreCase("S")) {
                    Sunflower sf = new Sunflower(x, y, currentTime);
                    boolean canPlant = sun >= sf.getCost();
                    boolean cooldownLoad = (currentTime - lastPlantedTime.get("S")) >= sf.getRegenerateRate();
                    if (canPlant && cooldownLoad) {
                        plants.add(sf);
                        sun -= sf.getCost();
                        lastPlantedTime.put("S", (double) currentTime);
                        System.out.println("The Sunflower (" + sf.getHealth() + " HP)" + " has been planted at Row " + x + ", Column " + y);
                    } else {
                        System.out.print("Can't plant Sunflower ( ");
                        if(!canPlant) System.out.print("not enough sun");
                        if(!canPlant && !cooldownLoad) System.out.print(" and ");
                        if(!cooldownLoad) System.out.print("still on cooldown");
                        System.out.print(").");
                    }
                } else if (plant.equalsIgnoreCase("P")) {
                    Peashooter ps = new Peashooter(x, y);
                    boolean canPlant = sun >= ps.getCost();
                    boolean cooldownLoad = (currentTime - lastPlantedTime.get("P")) >= ps.getRegenerateRate();
                    if (canPlant && cooldownLoad) {
                        plants.add(ps);
                        sun -= ps.getCost();
                        lastPlantedTime.put("P", (double) currentTime);
                        System.out.println("The Peashooter (" + ps.getHealth() + " HP)" + " has been planted at Row " + x + ", Column " + y);
                    } else {
                        System.out.print("Can't plant Peashooter ( ");
                        if(!canPlant) System.out.print("not enough sun");
                        if(!canPlant && !cooldownLoad) System.out.print(" and ");
                        if(!cooldownLoad) System.out.print("still on cooldown");
                        System.out.println(").");
                    }
                }
            }
    }
    /**
     * Controls the display of plant cooldowns when dealing with plant placement
     */
    public static void plantCooldowns(){
        double sfCooldown = Math.max(0, lastPlantedTime.get("S")) + new Sunflower(0,0, currentTime).getRegenerateRate() - currentTime;
        double pCooldown = Math.max(0, lastPlantedTime.get("P")) + new Peashooter(0,0).getRegenerateRate() - currentTime;
        System.out.println("Plant Cooldowns: ");

        if(sfCooldown > 0){
            System.out.printf("   - [S] Sunflower: %.1f s\n",sfCooldown);
        } else{
            System.out.println("   - [S] Sunflower: Ready");
        }
        if(pCooldown > 0){
            System.out.printf("   - [P] Peashooter: %.1f s\n", pCooldown);
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
        for(Plant plant : plants){
            if(plant instanceof Peashooter){
                Peashooter ps = (Peashooter) plant;
                List<Zombie> zombiesInLanes = laneZombies.get(ps.getX());
                Zombie enemy = null;
                if(ps.canShootPea(currentTime)){
                    for(Zombie z : zombiesInLanes){
                        if(z.getY() == ps.getY()){
                            enemy = z;
                            break;
                        }
                        if(z.getY() >= ps.getY() && z.getY() <= ps.getY() + ps.getRange()){
                            if(enemy == null || z.getY() < enemy.getY()){
                                enemy = z;
                            }
                        }
                    }
                    if(enemy != null){
                        if(ps.canShootPea(currentTime)){
                            peas.add(new int[]{ps.getX(), ps.getY() + 1, ps.getDamage()});
                            ps.updateShootPeaTime(currentTime);
                            System.out.println("Peashooter at Row " + ps.getX() + ", Column " + ps.getY() + " fired a pea!");
                        }
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
            List<Zombie> lane  = laneZombies.get(pea[0]);

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
        /*
        if(currentTime >= 30 && currentTime <= 80 && currentTime % 10 == 0)
        {
            int lane = 1 + random.nextInt(ROWS - 1);
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
        */
        int spawnInterval = 0; // no. of seconds, it takes zombies to spawn
        int spawnCount = 1; // no. of zombies to spawn

        if(currentTime >= 30 && currentTime <= 80 && currentTime % 10 == 0){
            spawnInterval = 10;
        } else if(currentTime >= 81 && currentTime <= 140 && currentTime % 5 == 0){
            spawnInterval = 5;
        } else if(currentTime >= 141 && currentTime <= 170 && currentTime % 3 == 0){
            spawnInterval = 3;
        } else if (currentTime >= 171 && currentTime <= 180){
            System.out.println("A huge wave of zombies is approaching!");
            spawnCount = 5;
            spawnInterval = 1;
        }

        if(spawnInterval > 0) {
            int i;

            for (i = 0; i < spawnCount; i++) {
                int lane = 1 + random.nextInt(ROWS - 1);
                Zombie newZombie = new Zombie(lane, COLS - 1, currentTime);

                // Overcrowding prevention //
                boolean occupiedTile = false;
                for (Zombie z : laneZombies.get(lane)) {
                    if (z.getY() == COLS - 1) {
                        occupiedTile = true;
                        break;
                    }
                }

                if (!occupiedTile) { // If tile is not occupied //
                    laneZombies.get(lane).add(newZombie);
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
            List<Zombie> zombiesInLanes = laneZombies.get(x);
            Iterator<Zombie> iterator = zombiesInLanes.iterator();

            while(iterator.hasNext()){
                Zombie z = iterator.next();
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
        int i;
        for(i = 1; i < ROWS; i++){
            for(Zombie z : laneZombies.get(i)){
                boolean plantFound = false;
                for(Plant p : plants){
                    if(p.getX() == i && p.getY() == z.getY()){
                        plantFound = true;
                        if(!p.isDead()){
                            p.loseHP(z.getDamage());
                            z.attack();
                            String type = (p instanceof Sunflower) ? "Sunflower" : (p instanceof Peashooter) ? "Peashooter" : "Plant";
                            System.out.println("Zombie at Row " + i + ", Column " + z.getY() + " attacked " + type + " (-" + z.getDamage() + " HP)");
                        }
                        break;
                    }
                }

                if(!plantFound){
                    z.stopAttack();
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
        plants.removeIf(p ->{
            if(p.isDead()){
                System.out.println((p instanceof Sunflower ? "Sunflower" : "Peashooter") + " at Row " + p.getX() + ", Column " + p.getY() + " has been eaten.");
                return true;
            }
            return false;
        });

        int i;
        for(i = 1; i < ROWS; i++){
            final int currentRow = i;
            laneZombies.get(i).removeIf(z ->{
                if(z.isDead()){
                    System.out.println("Zombie at Row " + currentRow + ", Column " + z.getY() + " has been defeated.");
                    return true;
                }
                return false;
            });
        }
    }
    // LOCATIONS //
    /**
     * Controls if a plant tile is occupied by a plant
     * @param x Row
     * @param y Column
     */
    public static boolean isTileOccupied(int x, int y){
        for(Plant p : plants){
            if(p.getX() == x && p.getY() == y){
                return true;
            }
        }
        return false;
    }
    /**
     * Controls when planting prompt should actually appear (work in progress)
     *
     */
    public static boolean shouldShowPlantingPrompt(){
        // Check if the user can afford either Sunflower or Peashooter
        int sunflowerCost = new Sunflower(0, 0, currentTime).getCost();
        int peashooterCost = new Peashooter(0, 0).getCost();

        return sun >= sunflowerCost || sun >= peashooterCost;
    }
    /*
        Sources:
            https://www.youtube.com/watch?v=mcXc8Mva2bA
            https://www.geeksforgeeks.org/java/arraylist-in-java/
            https://www.reddit.com/r/java/comments/4v2uf9/made_a_textbased_open_world_pokemon_game_and_i/
            https://www.youtube.com/watch?v=GTP5lVEKXaU
            https://www.w3schools.com/java/java_inheritance.asp
            https://www.youtube.com/watch?v=taI7G6U29L8
            https://stackoverflow.com/questions/2979383/how-to-clear-the-console-using-java
    */
}
