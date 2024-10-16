package org.example;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class RiderThread extends Thread {
    private final Queue<String> mainQueue;  // Main FIFO queue
    private final Queue<String> tempQueue;  // Temporary FIFO queue for riders during bus boarding
    private final Semaphore mutex;  // Semaphore for synchronizing access to the queues
    private boolean busArrived;  // Flag to check if a bus has arrived and is boarding

    public RiderThread(Queue<String> mainQueue, Queue<String> tempQueue, Semaphore mutex) {
        this.mainQueue = mainQueue;
        this.tempQueue = tempQueue;
        this.mutex = mutex;
        this.busArrived = false;  // Initially, no bus has arrived
    }

    // This method will be called when a bus arrives to start boarding
    public void busArrived() {
        busArrived = true;  // Set bus arrival flag to true
    }

    // This method will be called when the bus leaves after boarding
    public void busDeparted() {
        busArrived = false;  // Set bus arrival flag to false
        moveTempToMainQueue();  // Move temporary riders to the main queue
    }

    // Move riders from tempQueue to mainQueue after the bus departs
    private void moveTempToMainQueue() {
        try {
            mutex.acquire();  // Acquire semaphore before modifying the queues
            while (!tempQueue.isEmpty()) {
                mainQueue.add(tempQueue.poll());  // Transfer riders from temp to main queue
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mutex.release();  // Release semaphore after the operation
        }
    }

    @Override
    public void run() {
        int riderId = 1;
        while (true) {
            try {
                // Sleep for 3 seconds to simulate rider arrival time
                Thread.sleep(3000);

                // Simulate rider generation
                String rider = "Rider-" + riderId++;

                mutex.acquire();  // Acquire semaphore to safely modify the queues

                if (busArrived) {
                    // If bus is boarding, add the rider to the tempQueue
                    System.out.println(rider + " added to temp queue (bus is boarding).");
                    tempQueue.add(rider);
                } else {
                    // Otherwise, add the rider to the main queue
                    System.out.println(rider + " added to main queue.");
                    mainQueue.add(rider);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mutex.release();  // Release semaphore after adding rider
            }
        }
    }
}
