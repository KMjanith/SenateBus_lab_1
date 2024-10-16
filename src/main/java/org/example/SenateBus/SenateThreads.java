package org.example.SenateBus;

import java.util.concurrent.Semaphore;

public class SenateThreads extends Thread {
    Semaphore busArrived;
    Semaphore tempFiller;
    Semaphore riders;
    String threadName;

    public SenateThreads(Semaphore busArrived, Semaphore tempFiller, Semaphore riders, String threadName) {
        super(threadName);
        this.busArrived = busArrived;
        this.tempFiller = tempFiller;
        this.riders = riders;
        this.threadName = threadName;
    }

    int MAX = 50;

    @Override
    public void run() {

        try {
            while (true) {  // Loop indefinitely for continuous execution
                if (threadName.equals("Bus")) {

                    busArrived.acquire();
                    tempFiller.release(); //now the temporary filler can start filling the waitingWhenBusArrived queue
                    System.out.println("🚌 " + threadName + " ARRIVED and TempFiller RELEASED 🚦");


                    riders.acquire(); //now rider thread cannot add more riders to the riders(original) queue
                    System.out.println("🚌 BOARDING RIDERS");

                    if (Riders.riders == 0) {
                        busArrived.release();  //bus gone as there are no riders
                        System.out.println("🚌 Bus Departed as there are NO riders 🏁");
                        riders.release(); //release the riders semaphore so that riders can start adding riders again
                        tempFiller.acquire();  //tempFiller will wait for the bus to arrive again
                        Thread.sleep(4000);
                    } else {
                        int availableRiders = Math.min(Riders.riders, MAX);
                        Riders.riders -= availableRiders;
                        Thread.sleep(1200);  //boarding time, this waiting time helps to simulate the temporary rider adding process clearly.
                        System.out.println("🚌 " + threadName + " BOARDED with " + availableRiders + " Riders 🎟️");
                        busArrived.release(); //bus is gone
                        System.out.println("🚌 Bus DEPARTED with " + availableRiders + " riders 🎟️ | Remaining Riders: " + Riders.riders + " | Waiting Riders: " + Riders.waitingWhenBusArrived);

                        tempFiller.acquire();
                        Riders.riders += Riders.waitingWhenBusArrived;
                        Riders.waitingWhenBusArrived = 0;

                        System.out.println("----------- New Riders: " + Riders.riders + " | Waiting Riders cleared: " +  " -----------");
                        riders.release();
                        Thread.sleep(4000);
                    }

                } else if (threadName.equals("Rider")) {


                    riders.acquire();
                    System.out.println("🚶‍♂️ " + threadName + " start to add a rider to the queue.");

                    Riders.riders++;
                    System.out.println(" ------------ Total Riders at stop: " + Riders.riders);
                    riders.release();  //release the riders semaphore so that bus can start boarding riders aby time it arrived

                    Thread.sleep(100);

                } else {


                    tempFiller.acquire(); // getting the permit to add riders to the waitingWhenBusArrived queue
                    Riders.waitingWhenBusArrived++; // adding rider to the waitingWhenBusArrived queue
                    System.out.println("🚶 " + threadName + " added a rider to the waiting queue. Total Waiting Riders: " + Riders.waitingWhenBusArrived);
                    tempFiller.release(); // releasing the permit so that bus can start boarding riders ad depart when boarding finished

                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            System.out.println(threadName + " interrupted.");
        }
    }
}
