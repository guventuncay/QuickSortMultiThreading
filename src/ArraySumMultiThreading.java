import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ArraySumMultiThreading extends RecursiveTask<Integer> {

    int start, end;
    int[] arr;

    public ArraySumMultiThreading(int start, int end, int[] arr) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {

        if (end - start <= 1) {
            return arr[start] + arr[end];
        }

        int mid = (start + end) / 2;
        ArraySumMultiThreading left = new ArraySumMultiThreading(start, mid, arr);
        ArraySumMultiThreading right = new ArraySumMultiThreading(mid, end, arr);
        left.fork();
        int rightResult = right.compute();
        int leftResult = left.join();

        return leftResult + rightResult;
    }

    public static void main(String[] args) {
        int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        ForkJoinPool pool = ForkJoinPool.commonPool();

        System.out.println(pool.invoke(new ArraySumMultiThreading(0, arr.length - 1, arr)));

    }
}
