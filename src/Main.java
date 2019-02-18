import steps.MergeSort;
import steps.Sum;

import java.io.IOException;

public class Main {

    /**
     * We divide the free memory by the size of 1 tuple to get total number of tuples we can fit into the memory.
     * Then we adjust the number of tuples in the memory with respect to the additional usage during the process.
     */
    public static void main(String[] args) throws IOException {
        System.gc();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long noTuple = freeMemory / 250;
        noTuple /= 1.619;

        System.out.println("Free memory (bytes): " + freeMemory);
        System.out.println("Number of tuples(s) to fit: " + noTuple);

        MergeSort mergeSort = new MergeSort(noTuple);
        long sortTIme = mergeSort.sort();

        mergeSort = null;
        System.gc();

        Sum sum = new Sum(noTuple);
        long sumTIme = sum.calculateSum();

        System.out.println("Overall time: " + (sortTIme + sumTIme) + "ms");
    }
}
