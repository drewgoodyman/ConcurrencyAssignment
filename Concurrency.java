import java.util.Random;

class Concurrency extends Thread {

    private final int[] array;
    private final int low;
    private int partial;
    private final int high;

    public Concurrency(int[] array, int low, int high) {
        this.array = array;
        this.low = low;
        this.high = Math.min(high, array.length);
    }

    public int getPartialSum() {
        return partial;
    }

    public void run() {
        partial = sum(array, low, high);
    }

    public static int sum(int[] array) {
        return sum(array, 0, array.length);
    }

    public static int sum(int[] array, int low, int high) {
        int total = 0;
        for (int i = low; i < high; i++) {
            total += array[i];
        }
        return total;
    }

    public static int parallelSum(int[] array) {
        return parallelSum(array, Runtime.getRuntime().availableProcessors());
    }

    public static int parallelSum(int[] array, int threads) {
        int size = (int) Math.ceil(array.length * 1.0 / threads);
        Concurrency[] parallelSums = new Concurrency[threads];
        for (int i = 0; i < threads; i++) {
            parallelSums[i] = new Concurrency(array, i * size, (i + 1) * size);
            parallelSums[i].start();
        }
        try {
            for (Concurrency singleSum : parallelSums) {
                singleSum.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int total = 0;
        for (Concurrency sum : parallelSums) {
            total += sum.getPartialSum();
        }
        return total;
    }

    static class Main {

        public static void main(String[] args) {
            Random random = new Random();
            int[] array = new int[200000000];
            for (int i = 0; i < array.length; i++) {
                array[i] = random.nextInt(10) + 1;
            }
            long start = System.currentTimeMillis();
            System.out.println("Sum of array: " + Concurrency.parallelSum(array));
            System.out.println("Parallel threads: " + (System.currentTimeMillis() - start) + " ms.");
            start = System.currentTimeMillis();
            System.out.println("Sum of array: " + Concurrency.sum(array));
            System.out.println("Single thread: " + (System.currentTimeMillis() - start) + " ms.");
        }
    }
}

