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
                    System.out.println("======================================");
                    System.out.println("🚌 [BUS THREAD] Starting " + threadName);
                    System.out.println("======================================");

                    System.out.println("🚌 " + threadName + " Pending to Arrive...");
                    busArrived.acquire();
                    System.out.println("🚌 " + threadName + " ARRIVED and TempFiller RELEASED 🚦");
                    tempFiller.release();

                    System.out.println("🚌 " + threadName + " waiting for riders to board...");
                    riders.acquire();
                    System.out.println("🚌 BOARDING RIDERS");

                    if (Riders.riders == 0) {
                        busArrived.release();
                        System.out.println("🚌 Bus Departed as there are NO riders 🏃‍♂️");
                        riders.release();
                        tempFiller.acquire();
                        Thread.sleep(400);
                    } else {
                        int availableRiders = Math.min(Riders.riders, MAX);
                        Riders.riders -= availableRiders;
                        System.out.println("🚌 " + threadName + " BOARDED with " + availableRiders + " Riders 🎟️");
                        busArrived.release();
                        System.out.println("🚌 Bus DEPARTED with " + availableRiders + " riders 🏁 | Remaining Riders: " + Riders.riders + " | Waiting Riders: " + Riders.waitingWhenBusArrived);

                        tempFiller.acquire();
                        Riders.riders += Riders.waitingWhenBusArrived;
                        Riders.waitingWhenBusArrived = 0;

                        System.out.println("----------- New Riders: " + Riders.riders + " | Waiting Riders cleared: " + Riders.waitingWhenBusArrived + " -----------");
                        riders.release();
                        Thread.sleep(400);
                    }

                } else if (threadName.equals("Rider")) {
                    System.out.println("======================================");
                    System.out.println("🚶‍♂️ [RIDER THREAD] Starting " + threadName);
                    System.out.println("======================================");

                    System.out.println("🚶‍♂️ " + threadName + " is waiting for a permit to board...");
                    riders.acquire();
                    System.out.println("🚶‍♂️ " + threadName + " gets a permit and joins the queue.");

                    Riders.riders++;
                    System.out.println("🚶‍♂️ Total Riders at stop: " + Riders.riders);
                    riders.release();

                    Thread.sleep(10);

                } else {
                    System.out.println("======================================");
                    System.out.println("🚶‍♂️ [TEMP RIDER THREAD] Starting " + threadName);
                    System.out.println("======================================");

                    System.out.println("🚶‍♂️ " + threadName + " is waiting for a permit to join temp queue...");
                    tempFiller.acquire();
                    System.out.println("🚶‍♂️ " + threadName + " gets a permit and waits at stop.");

                    Riders.waitingWhenBusArrived++;
                    System.out.println("🚶‍♂️ " + threadName + " added to waiting queue. Total Waiting: " + Riders.waitingWhenBusArrived);
                    tempFiller.release();
                    System.out.println("🚶‍♂️ TempRider finishes waiting temporarily.");

                    Thread.sleep(10);
                }
            }
        } catch (InterruptedException e) {
            System.out.println(threadName + " interrupted.");
        }
    }
}
