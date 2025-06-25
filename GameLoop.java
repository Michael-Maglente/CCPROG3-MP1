public class GameLoop implements Runnable{
    @Override
    public void run(){
        try{
            while(PvZ.currentTime <= PvZ.TIME_LIMIT){
                synchronized(PvZ.lock){
                    System.out.println("\033[H\033[2J");
                    System.out.flush();
                    PvZ.gameTick();
                    PvZ.showPlantPlacementPrompt = PvZ.shouldShowPlantingPrompt();
                }
                Thread.sleep(1000);
                PvZ.currentTime++;
            }
        } catch (InterruptedException e){
            System.out.println("Game loop interrupted!");
        }
    }
}

