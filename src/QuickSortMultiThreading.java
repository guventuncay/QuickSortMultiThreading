import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

public class QuickSortMultiThreading extends RecursiveAction { // unlike RecursiveTask, RecursiveAction does not return any value

    int start, end;
    int[] arr;

    public QuickSortMultiThreading(int start, int end, int[] arr) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    /**
     * Finding random pivoted and partition array on a pivot.
     *
     * @param start start index of the array
     * @param end   end index of the array
     * @param arr   array to be partitioned
     * @return index of the pivot
     */
    private int partition(int start, int end, int[] arr) {

        int i = start, j = end;

        // Decide random pivot
        int pivoted = new Random().nextInt(j - i) + i;

        // Swap the pivoted with end
        // element of array;
        int t = arr[j];
        arr[j] = arr[pivoted];
        arr[pivoted] = t;
        j--;

        // Start partitioning
        while (i <= j) {

            if (arr[i] <= arr[end]) {
                i++;
                continue;
            }

            if (arr[j] >= arr[end]) {
                j--;
                continue;
            }

            t = arr[j];
            arr[j] = arr[i];
            arr[i] = t;
            j--;
            i++;
        }

        // Swap pivoted to its
        // correct position
        t = arr[j + 1];
        arr[j + 1] = arr[end];
        arr[end] = t;
        return j + 1;
    }

    @Override
    protected void compute() {
        // Base case
        if (start >= end)
            return;

        // Else divide the array into two parts
        // Find partition
        int p = partition(start, end, arr);

        // Divide array
        QuickSortMultiThreading left = new QuickSortMultiThreading(start, p - 1, arr);
        QuickSortMultiThreading right = new QuickSortMultiThreading(p + 1, end, arr);

        // Left subproblem as separate thread
        left.fork();
        right.compute();

        // Wait until left thread complete
        left.join();
    }

    // Driver Code
    public static void main(String[] args) {

        // Array size
        int n = 10_000_000;

        // Generate random array
        int[] arr = new Random().ints(n, 0, n).toArray();

        System.out.println("Unsorted array: " +
                Arrays.stream(arr).boxed().map(String::valueOf).collect(Collectors.joining(" ")));

        // Fork-join ThreadPool to keep thread creation as per resources
        // According to Oracle’s documentation, using the predefined common pool reduces resource consumption,
        // since this discourages the creation of a separate thread pool per task.
        ForkJoinPool pool = ForkJoinPool.commonPool();

        // ForkJoinPool with the indicated parallelism level (how many worker threads to use)
//        ForkJoinPool pool = new ForkJoinPool(2);

        long startTime = System.currentTimeMillis();

        // Start the first thread in fork
        // join pool for range 0, n-1
        pool.invoke(new QuickSortMultiThreading(0, n - 1, arr));

        long endTime = System.currentTimeMillis();

        System.out.println("Sorted array:   " +
                Arrays.stream(arr).boxed().map(String::valueOf).collect(Collectors.joining(" ")));

        System.out.println("Time taken: " + (endTime - startTime) + " ms");

    }
}
