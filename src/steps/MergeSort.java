package steps;

import support.Constants;
import support.SublistBuffer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class MergeSort {
    public static final int TUPLE_CAPACITY = 40; // 40 tuples in a single read for a sublist
    private long noTuple;
    private int sublistCount, totalTuples;
    private int noOfReads1, noOfReads2, noOfWrites2;

    /**
     * Initializes the parameters
     * @param noTuple number of tuples to fit
     */
    public MergeSort(long noTuple) {
        this.noTuple = noTuple;
        this.sublistCount = this.totalTuples = this.noOfReads2 = this.noOfWrites2 = this.noOfReads1 = 0;
    }

    public void sort() throws IOException {
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
        // For heapSort argument
        int[] bufferCID = new int[(int) this.noTuple];
        
        Scanner scanner = new Scanner(new FileReader(Query.DATA_FILE));

        while (scanner.hasNext()) {
            for (int i = 0; i < this.noTuple && scanner.hasNext(); i++) {
                buffer[i] = scanner.nextLine().trim();
                // For heapSort
                bufferCID[i] = Integer.parseInt(buffer[i].substring(MergeSort.PRIMARY_KEY_START, MergeSort.PRIMARY_KEY_END));
                this.totalTuples++;
            }
            /*
            Arrays.sort(buffer, Comparator.comparingInt((String o1) -> {
                if (o1 == null)
                    return 99999999;
                return Integer.parseInt(o1.substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END));
            }).thenComparing((String o1) -> {
                if (o1 == null)
                    return 99999999;
                return Integer.parseInt(o1.substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END));
            }));
            */
            
            heapSort(bufferCID,buffer);
            
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
        PrintWriter printWriter = new PrintWriter(new FileWriter(Constants.OUT_FILE, true));

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
    
    // To heapify a subtree rooted with node i which is 
    // an index in arr[]. n is size of heap 
    private void heapify(int[] arr, int n, int i, String[] bufferStr){ 
        int largest = i; // Initialize largest as root 
        int l = 2*i + 1; // left = 2*i + 1 
        int r = 2*i + 2; // right = 2*i + 2 
  
        // If left child is larger than root 
        if (l < n && arr[l] > arr[largest]) 
            largest = l; 
  
        // If right child is larger than largest so far       
        if (r < n && arr[r] > arr[largest])
            largest = r; 
  
        // If largest is not root 
        if (largest != i) 
        { 
        	int swap = arr[i]; 
            arr[i] = arr[largest]; 
            arr[largest] = swap;
            // simultaneously heapifying the original data record 
            String swapStr = bufferStr[i];
            bufferStr[i] = bufferStr[largest]; 
            bufferStr[largest] = swapStr;
            
            // Recursively heapify the affected sub-tree 
            heapify(arr, n, largest,bufferStr); 
        } 
    }
    
    private void heapSort(int[] bufferData, String[] bufferStr){ 
        int n = bufferData.length; 
  
        // Build heap (rearrange array) 
        for (int i = n / 2 - 1; i >= 0; i--) 
            heapify(bufferData, n, i,bufferStr); 
  
        // One by one extract an element from heap 
        for (int i=n-1; i>=0; i--) 
        { 
            // Move current root to end 
        	int temp = bufferData[0]; 
            bufferData[0] = bufferData[i]; 
            bufferData[i] = temp;
            // simultaneously swapping the original data record
            String tempStr = bufferStr[0];
            bufferStr[0] = bufferStr[i]; 
            bufferStr[i] = tempStr;
  
            // call max heapify on the reduced heap 
            heapify(bufferData, i, 0,bufferStr); 
        } 
    } 
}
