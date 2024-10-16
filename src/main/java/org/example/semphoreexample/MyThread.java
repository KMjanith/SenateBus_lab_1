package org.example.semphoreexample;

import java.util.concurrent.Semaphore;

public class MyThread extends Thread {

    Semaphore sem;
    String threadName;

    public MyThread(Semaphore sem, String threadName) {
        super(threadName);
        this.sem = sem;
        this.threadName = threadName;
    }

    @Override
    public void run() {

        if(this.getName().equals("A")){
            System.out.println("Starting " + threadName);

            try {

                //get the permit
                System.out.println(threadName + " is waiting for a permit.");

                //acquire a permit
                sem.acquire();
                System.out.println(threadName + " gets a permit.");

                //accessing the shred variabale while other threads are waiting
                for(int i = 0 ; i<5; i++){
                    Shared.count ++;
                    System.out.println(threadName + ": " + Shared.count);

                    Thread.sleep(10);
                }


            }catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //release the permit
            sem.release();

        }else {

                System.out.println("starting " + threadName);
                try{
                //get the permit
                System.out.println(threadName + " is waiting for a permit.");

                //accuire a permit
                sem.acquire();
                System.out.println(threadName + " gets a permit.");

                //accessing the shared variable
                for(int i=0 ; i<5 ; i++){
                    Shared.count --;
                    System.out.println(threadName + ": " + Shared.count);

                    Thread.sleep(10);
                }


            }catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            //release the permit
            System.out.println(threadName + " releases the permit.");
            sem.release();

        }

    }
}
