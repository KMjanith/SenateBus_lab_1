package org.example.SenateBus;

import java.util.LinkedList;
import java.util.Queue;

public class Riders {

    // FIFO queue for riders at the stop
    public static Queue<String> riderQueue = new LinkedList<>();

    // FIFO queue for riders arriving while the bus is boarding
    public static Queue<String> waitingQueue = new LinkedList<>();

}
