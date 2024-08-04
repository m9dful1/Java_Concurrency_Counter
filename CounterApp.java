package java_concurrency_counter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CounterApp {
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static boolean isReady = false;

    public static void main(String[] args) {
        Thread countUpThread = new Thread(new CountUp());
        Thread countDownThread = new Thread(new CountDown());

        countUpThread.start();
        countDownThread.start();

        try {
            countUpThread.join();
            countDownThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static class CountUp implements Runnable {
        @Override
        public void run() {
            lock.lock();
            try {
                for (int i = 0; i <= 20; i++) {
                    System.out.println("Count Up: " + i);
                    Thread.sleep(100); // Simulate work
                }
                isReady = true;
                condition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    static class CountDown implements Runnable {
        @Override
        public void run() {
            lock.lock();
            try {
                while (!isReady) {
                    condition.await();
                }
                for (int i = 20; i >= 0; i--) {
                    System.out.println("Count Down: " + i);
                    Thread.sleep(100); // Simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }
}
