import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

class MergeSort {
    static final int PRIMARY_KEY_START = 0;
    static final int PRIMARY_KEY_END = 8;
    static final int TUPLE_CAPACITY = 40; // 40 tuples in a single read for a sublist
    private long noTuple;
    private int sublistCount, totalTuples;
    private int noOfReads1, noOfReads2, noOfWrites2;

    /**
     * Initializes the parameters
     * We divide the free memory by the size of 1 tuple to get total number of tuples we can fit into the memory.
     * Then we adjust the number of tuples in the memory with respect to the additional usage during the process.
     * @param freeMemory the available main memory for the process
     */
    MergeSort(long freeMemory) {
        this.noTuple = freeMemory / 250;
        this.noTuple /= 1.6;
        this.sublistCount = this.totalTuples = this.noOfReads2 = this.noOfWrites2 = this.noOfReads1 = 0;

        System.out.println("Free memory (bytes): " + freeMemory);
        System.out.println("Number of tuples(s) to fit: " + this.noTuple);
    }

    void sort() throws IOException {
        long startTime = System.currentTimeMillis();
        long phase1Time, phase2Time;

        phase1();
        phase1Time = System.currentTimeMillis() - startTime;

        System.out.println("phase1: " + phase1Time + "ms | no of IO: " + (this.noOfReads1 * 2));
        System.out.println("No of sub-lists: " + this.sublistCount);

        startTime = System.currentTimeMillis();
        phase2();
        phase2Time = System.currentTimeMillis() - startTime;

        System.out.println("phase2: " + phase2Time + "ms | no of reads: " + this.noOfReads2 + " | no of writes: "
            + this.noOfWrites2);
        System.out.println("Total number of tuples: " + this.totalTuples);

        System.out.println("\nTotal time: " + (phase1Time + phase2Time) + "ms");
    }

    private void phase1() throws IOException {
        this.sublistCount = 0;
        this.noOfReads1 = 0;
        String[] buffer = new String[(int) this.noTuple];

        Scanner scanner = new Scanner(new FileReader(Query.DATA_FILE));

        while (scanner.hasNext()) {
            for (int i = 0; i < this.noTuple && scanner.hasNext(); i++) {
                buffer[i] = scanner.nextLine().trim();
                this.totalTuples++;
            }

            Arrays.sort(buffer, Comparator.comparingInt((String o1) -> {
                if (o1 == null)
                    return 99999999;
                return Integer.parseInt(o1.substring(MergeSort.PRIMARY_KEY_START, MergeSort.PRIMARY_KEY_END));
            }).thenComparing((String o1) -> {
                if (o1 == null)
                    return 99999999;
                return Integer.parseInt(o1.substring(MergeSort.PRIMARY_KEY_START, MergeSort.PRIMARY_KEY_END));
            }));

            this.sublistCount = writePhase(buffer, this.sublistCount);

            buffer = new String[(int) this.noTuple];
            this.noOfReads1++;
        }

        scanner.close();
    }

    private void phase2() throws IOException {
        SublistBuffer[] sublistBuffers = new SublistBuffer[this.sublistCount];
        String[] outputBuffer = new String[MergeSort.TUPLE_CAPACITY];
        int indexOut = 0;

        for (int i = 0; i < this.sublistCount; i++) {
            sublistBuffers[i] = new SublistBuffer("sub-" + i + ".txt");
            sublistBuffers[i].load();
        }

        for (int i = 0; i < this.totalTuples; i++) {
            int min = 999999999, minIndex = 0;

            for (int j = 0; j < this.sublistCount; j++) {
                if (!sublistBuffers[j].isFileCompleted()) {
                    if (sublistBuffers[j].getFirstValue() < min) {
                        minIndex = j;
                        min = sublistBuffers[j].getFirstValue();
                    }
                }
            }

            outputBuffer[indexOut++] = sublistBuffers[minIndex].getFirst();
            sublistBuffers[minIndex].movePointer();

            if (indexOut == MergeSort.TUPLE_CAPACITY) {
                this.writePhase(outputBuffer);
                indexOut = 0;
                outputBuffer = new String[MergeSort.TUPLE_CAPACITY];
            }
        }

        this.writePhase(outputBuffer);

        for (SublistBuffer buffer : sublistBuffers) {
            this.noOfReads2 += buffer.getNoOfReads();
        }
    }

    private void writePhase(String[] buffer) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(Query.OUT_FILE, true));

        for (String item : buffer) {
            if (item != null)
                printWriter.println(item);
        }
        this.noOfWrites2++;

        printWriter.close();
    }

    private int writePhase(String[] buffer, int sublistCount) throws IOException {
        String name = "sub-" + (sublistCount++) + ".txt";
        PrintWriter printWriter = new PrintWriter(new FileWriter(name));

        for (String item : buffer) {
            if (item != null)
                printWriter.println(item);
        }

        printWriter.close();
        return sublistCount;
    }
}
