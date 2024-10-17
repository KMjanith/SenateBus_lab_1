package org.example;

import org.example.SenateBus.SenateThreads;

import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        //creating a semaphore
        Semaphore busArrived = new Semaphore(1);
        Semaphore tempoFiller = new Semaphore(0);
        Semaphore riders = new Semaphore(1);

        //creating two threads A will increment the count B will decrement the count
        Thread Bus =  new SenateThreads(busArrived, tempoFiller, riders,"Bus");
        Thread Rider = new SenateThreads(busArrived, tempoFiller, riders, "Rider");
        Thread RiderTempo = new SenateThreads(busArrived, tempoFiller, riders, "TempRider");

        //starting threads
        Bus.start();
        System.out.println("🚌 [BUS THREAD] Starting " + Bus.getName());
        System.out.println("======================================");
        // adding a small delay before starting the rider thread
        try {
            Thread.sleep(100); // Delay of 1000 milliseconds (1 second)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Rider.start();
        RiderTempo.start();


        System.out.println("🚶‍ [RIDER THREAD] Starting " + Rider.getName());
        System.out.println("======================================");
        System.out.println("🚶‍ [TEMP RIDER THREAD] Starting " + RiderTempo.getName());
        System.out.println("======================================");

        //waiting for threads
        try
        {
            Bus.join();
            Rider.join();
            RiderTempo.join();
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }




}