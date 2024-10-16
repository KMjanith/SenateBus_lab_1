package org.example;

import org.example.SenateBus.SenateThreads;
import org.example.semphoreexample.MyThread;
import org.example.semphoreexample.Shared;

import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {

//        //creating a semaphore
//        Semaphore sem = new Semaphore(1);
//
//        //creating two threads A will increment the count B will decrement the count
//        Thread A = new MyThread(sem, "A");
//        Thread B = new MyThread(sem, "B");
//
//        //starting threads
//        A.start();
//        B.start();
//
//        //waiting for threads
//        try
//        {
//            A.join();
//            B.join();
//        }catch (InterruptedException e){
//            throw new RuntimeException(e);
//        }
//
//        //count should be 0
//        System.out.println("count: " + Shared.count);


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
        Rider.start();
        RiderTempo.start();

        System.out.println("🚌 [BUS THREAD] Starting " + Bus.getName());
        System.out.println("======================================");
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