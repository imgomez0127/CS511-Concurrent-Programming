/**
* Assignment 2: Modeling a gym using Semaphores
* "I pledge my honor that I have abided by the Stevens honor system" - igomez1 Ian Gomez
* Partner: Gary Ung
*/

package Assignment2;

import java.util.HashMap; 
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.*;
import java.util.Random;
import Assignment2.Client;
import Assignment2.WeightPlateSize;
import Assignment2.ApparatusType;

public class Gym implements Runnable{
    public static final int GYM_SIZE = 30;
    public static final int GYM_REGISTERED_CLIENTS = 10000;
    private HashMap<WeightPlateSize,Integer> noOfWeightPlates;
    private HashSet<Integer> clients;
    private HashMap<WeightPlateSize,Semaphore> availableWeights;
    private HashMap<ApparatusType,Semaphore> availableApparatuses;
    private Semaphore tryToGrabWeights = new Semaphore(1);

    private void populateClientsSet(){
        this.clients = new HashSet<Integer>();
        Random idGenerator = new Random();
        while(this.clients.size() < Gym.GYM_REGISTERED_CLIENTS){
            this.clients.add(idGenerator.nextInt(Integer.MAX_VALUE));
        }
    }


    private void populateNoOfWeightsPlates(){
        this.noOfWeightPlates = new HashMap<WeightPlateSize,Integer>();
        this.noOfWeightPlates.put(WeightPlateSize.SMALL_3KG, 110);
        this.noOfWeightPlates.put(WeightPlateSize.MEDIUM_5KG, 90);
        this.noOfWeightPlates.put(WeightPlateSize.LARGE_10KG, 75);
    }
   
     private void populateAvailableWeights(){
        this.availableWeights = new HashMap<WeightPlateSize,Semaphore>();
        for(Map.Entry<WeightPlateSize,Integer> weightAndAmount : this.noOfWeightPlates.entrySet()){
            this.availableWeights.put(
                weightAndAmount.getKey(),
                new Semaphore(weightAndAmount.getValue())
            );
        }
    }

    private void populateAvailableApparatuses(){
        this.availableApparatuses = new HashMap<ApparatusType,Semaphore>();
        for(ApparatusType apparatus:ApparatusType.values()){
            this.availableApparatuses.put(apparatus,new Semaphore(5));
        }
    }

   Gym(){

        populateNoOfWeightsPlates();
        populateAvailableApparatuses();    
        populateAvailableWeights();
        populateClientsSet();
    }     

    public void run(){
        ExecutorService executorService = Executors.newFixedThreadPool(Gym.GYM_SIZE);
        for(int id : this.clients){
            Client client = Client.generateRandom(id);
            System.out.println("Client " + id + " Routine:" + client.getRoutine().toString());
            executorService.execute(new Runnable() {
                public void run(){
                    client.executeRoutine(availableApparatuses,availableWeights,tryToGrabWeights);
                }
            });
        }
        executorService.shutdown();
    }
}
