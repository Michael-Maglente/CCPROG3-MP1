import java.util.Scanner;

public class InputHandler implements Runnable{
    private final Scanner scanner = new Scanner(System.in);


    @Override
    public void run(){
        while(!Thread.currentThread().isInterrupted() && PvZ.currentTime <= PvZ.TIME_LIMIT){
            synchronized(PvZ.lock){
                if(PvZ.shouldShowPlantingPrompt()){
                    PvZ.plantPlacementPrompt(scanner);
                    PvZ.showPlantPlacementPrompt = false;
                }
            }

            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                break;
            }
        }
    }

}
