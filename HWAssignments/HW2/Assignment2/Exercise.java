package Assignment2;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Semaphore;
import Assignment2.WeightPlateSize;
import Assignment2.ApparatusType;

public class Exercise{
    private ApparatusType at;
    private HashMap<WeightPlateSize,Integer> weight;
    private int duration;

    Exercise(ApparatusType at, HashMap<WeightPlateSize,Integer> weight, int duration){
        this.at = at;
        this.weight = weight;
        this.duration = duration;
    }

    private static ApparatusType pickRandomApparatus(){
        int apparatusNumber = new Random().nextInt(ApparatusType.values().length);
        return ApparatusType.values()[apparatusNumber];
    }

    private static HashMap<WeightPlateSize,Integer> pickRandomWeights(){
        HashMap<WeightPlateSize,Integer> excerciseWeights = new HashMap<WeightPlateSize,Integer>();
        WeightPlateSize[] weightPlates = WeightPlateSize.values();
        Random randomNumberGenerator = new Random();
        for(int i = 0; i < weightPlates.length; ++i){
            int amountOfWeights = randomNumberGenerator.nextInt(10);
            excerciseWeights.put(weightPlates[i],amountOfWeights);
        }
        return excerciseWeights;
    } 
    private static int pickRandomDuration(){
        int randomDuration = new Random().nextInt(9)+1;
        return randomDuration;
    }
    public static Exercise generateRandom(){
        ApparatusType randomApparatus = pickRandomApparatus();
        HashMap<WeightPlateSize,Integer> weights = pickRandomWeights(); 
        int duration = pickRandomDuration();
        return new Exercise(randomApparatus,weights,duration);
    }
    private void grabWeights(
        HashMap<WeightPlateSize,Semaphore> availableWeights
    ){
        for(WeightPlateSize weightSize : WeightPlateSize.values()){
            int amountOfWeightsToGrab = weight.get(weightSize);
            for(int amountOfGrabbedWeights = 0; 
                amountOfGrabbedWeights < amountOfWeightsToGrab; 
                amountOfGrabbedWeights++
            ){
                try{
                    availableWeights.get(weightSize).acquire();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    private void releaseWeights(
        HashMap<WeightPlateSize,Semaphore> availableWeights
    ){
        for(WeightPlateSize weightSize : WeightPlateSize.values()){
            int amountOfWeightsToGrab = weight.get(weightSize);
            for(int amountOfGrabbedWeights = 0; 
                amountOfGrabbedWeights < amountOfWeightsToGrab; 
                amountOfGrabbedWeights++
            ){
                    availableWeights.get(weightSize).release();
            }
        }
    }
     

    public void performExercise(
        HashMap<ApparatusType,Semaphore> availableApparatuses, 
        HashMap<WeightPlateSize,Semaphore> availableWeights, 
        Semaphore tryToGrabWeights
    ){
        try{
            availableApparatuses.get(at).acquire();
            tryToGrabWeights.acquire();
            this.grabWeights(availableWeights);
            tryToGrabWeights.release();
            Thread.sleep(duration);
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally{
            this.releaseWeights(availableWeights);
            availableApparatuses.get(at).release();
        }
    }
}
