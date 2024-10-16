package org.example.SenateBus;

import java.util.concurrent.Semaphore;

public class SenateThreads extends Thread {
    Semaphore busArrived;
    Semaphore tempFiller;
    Semaphore riders;
    String threadName;
    static int riderID = 1;  // To give unique IDs to each rider
    int MAX = 50;

    public SenateThreads(Semaphore busArrived, Semaphore tempFiller, Semaphore riders, String threadName) {
        super(threadName);
        this.busArrived = busArrived;
        this.tempFiller = tempFiller;
        this.riders = riders;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        try {
            while (true) {  // Loop indefinitely for continuous execution
                if (threadName.equals("Bus")) {
                    busArrived.acquire();
                    tempFiller.release();  //now the temporary filler can start filling the waiting queue
                    System.out.println("🚌 " + threadName + " ARRIVED and TempFiller RELEASED 🚦");

                    riders.acquire();  // Stop new riders from being added to the main queue while boarding
                    System.out.println("🚌 BOARDING RIDERS");

                    if (Riders.riderQueue.isEmpty()) {
                        busArrived.release();  // Bus departs if there are no riders
                        System.out.println("🚌 Bus Departed as there are NO riders 🏁");
                        riders.release();  // Allow riders to start adding to the queue again
                        tempFiller.acquire();  // Wait for the bus to arrive again
                        Thread.sleep(4000);
                    } else {
                        int boardedRiders = 0;

                        // Board up to MAX riders from the queue
                        while (boardedRiders < MAX && !Riders.riderQueue.isEmpty()) {
                            String rider = Riders.riderQueue.poll();  // Remove from the queue (FIFO)
                            System.out.println("🚌 Boarding Rider: " + rider);
                            boardedRiders++;
                            Thread.sleep(200);  // Simulate boarding time
                        }

                        System.out.println("🚌 Bus DEPARTED with " + boardedRiders + " riders 🎟️ | Remaining Riders at stop: " + Riders.riderQueue.size());

                        busArrived.release(); // Bus is gone
                        tempFiller.acquire(); // Temporary riders waiting are moved to the main queue

                        // Move temporary riders to the main queue
                        while (!Riders.waitingQueue.isEmpty()) {
                            String tempRider = Riders.waitingQueue.poll();
                            Riders.riderQueue.add(tempRider);
                        }

                        System.out.println("----------- New Riders at stop: " + Riders.riderQueue.size() + " -----------");
                        riders.release();
                        Thread.sleep(4000);
                    }

                } else if (threadName.equals("Rider")) {
                    riders.acquire();  // Get permit to add to the rider queue
                    String riderName = "Rider-" + riderID++;
                    Riders.riderQueue.add(riderName);  // Add rider to the queue
                    System.out.println("🚶‍♂️ " + threadName + " added " + riderName + " to the queue. Total Riders at stop: " + Riders.riderQueue.size());
                    riders.release();  // Allow the bus to board riders at any time

                    Thread.sleep(100);  // Simulate random arrival times

                } else {  // TempRider
                    tempFiller.acquire();  // Get permit to add to the waiting queue
                    String tempRiderName = "TempRider-" + riderID++;
                    Riders.waitingQueue.add(tempRiderName);  // Add to temporary waiting queue
                    System.out.println("🚶 " + threadName + " added " + tempRiderName + " to the waiting queue. Total Waiting Riders: " + Riders.waitingQueue.size());
                    tempFiller.release();  // Allow the bus to board riders when it arrives

                    Thread.sleep(100);  // Simulate random arrival times
                }
            }
        } catch (InterruptedException e) {
            System.out.println(threadName + " interrupted.");
        }
    }
}
