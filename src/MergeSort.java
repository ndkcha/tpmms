import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class MergeSort {
    private final int primaryKeyLength = 8;
    private final int blockTupleCapacity = 40; // 40 blocks
    private long noTuple;
    private int sublistCount = 0;
    private int noOfReads1 = 0, noOfReads2 = 0, noOfWrites2 = 0;

    /**
     * Initializes the parameters
     * We divide the free memory by the size of 1 tuple to get total number of tuples we can fit into the memory.
     * Then we adjust the number of tuples in the memory with respect to the additional usage during the process.
     * @param freeMemory the available main memory for the process
     */
    MergeSort(long freeMemory) {
        this.noTuple = freeMemory / 250;
        this.noTuple /= 1.6;

        System.out.println("Free memory (bytes): " + freeMemory);
        System.out.println("Number of tuples(s) to fit: " + this.noTuple);
    }

    public void sort(String inFile, String outFile) throws IOException {
        long startTime = System.currentTimeMillis();
        long phase1Time, phase2Time;

        phase1(inFile);
        phase1Time = System.currentTimeMillis() - startTime;
        System.out.println("phase1: " + phase1Time + "ms | no of IO: " + (this.noOfReads1 * 2));
        System.out.println("No of sub-lists: " + this.sublistCount );
    }

    private void phase1(String inFile) throws IOException {
        this.sublistCount = 0;
        this.noOfReads1 = 0;
        String[] buffer = new String[(int) this.noTuple];

        Scanner scanner = new Scanner(new FileReader(inFile));

        while (scanner.hasNext()) {
            for (int i = 0; i < this.noTuple && scanner.hasNext(); i++) {
                buffer[i] = scanner.nextLine().trim();
            }

            Arrays.sort(buffer, Comparator.comparingInt((String o1) -> {
                if (o1 == null)
                    return 99999999;
                return Integer.parseInt(o1.substring(0, this.primaryKeyLength));
            }).thenComparing((String o1) -> {
                if (o1 == null)
                    return 99999999;
                return Integer.parseInt(o1.substring(0, this.primaryKeyLength));
            }));

            this.sublistCount = writePhase1(buffer, this.sublistCount);

            buffer = new String[(int) this.noTuple];
            this.noOfReads1++;
        }

        scanner.close();
    }

    private int writePhase1(String[] buffer, int sublistCount) throws IOException {
        String name = "sub-" + (++sublistCount) + ".txt";
        PrintWriter printWriter = new PrintWriter(new FileWriter(name));

        for (String item : buffer) {
            if (item != null)
                printWriter.println(item);
        }

        printWriter.close();
        return sublistCount;
    }
}
