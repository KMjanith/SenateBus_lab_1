package org.example;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
    	
        // Create the shared FIFO queues for the riders
        Queue<String> mainQueue = new LinkedList<>();
        Queue<String> tempQueue = new LinkedList<>();
        Semaphore mutex = new Semaphore(1);  // Mutex semaphore

        // Create and start the RiderThread
        RiderThread riderThread = new RiderThread(mainQueue, tempQueue, mutex);
        riderThread.start();

        // Simulate a bus arriving after 60 seconds and departing after 10 seconds
        new Thread(() -> {
            try {
                Thread.sleep(60000);  // Simulate bus arriving after 1 minute
                System.out.println("Bus has arrived.");
                riderThread.busArrived();
                
                Thread.sleep(10000);  // Simulate bus boarding for 10 seconds
                System.out.println("Bus has departed.");
                riderThread.busDeparted();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
