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
    private PrintWriter sortedPrintWriter;

    /**
     * Initializes the parameters
     * @param noTuple number of tuples to fit
     */
    public MergeSort(long noTuple) throws IOException {
        this.noTuple = noTuple;
        this.sublistCount = this.totalTuples = this.noOfReads2 = this.noOfWrites2 = this.noOfReads1 = 0;
        this.sortedPrintWriter = new PrintWriter(new FileWriter(Constants.SORTED_FILE, true));
    }

    public long sort() throws IOException {
        long startTime = System.currentTimeMillis();
        long phase1Time, phase2Time;

        phase1();
        phase1Time = System.currentTimeMillis() - startTime;

        System.out.println("phase1: " + phase1Time + "ms | no of IO: " + (this.noOfReads1 * 2));
        System.out.println("No of sub-lists: " + this.sublistCount);

        System.gc();

        startTime = System.currentTimeMillis();
        phase2();
        phase2Time = System.currentTimeMillis() - startTime;

        System.out.println("phase2: " + phase2Time + "ms | no of reads: " + this.noOfReads2 + " | no of writes: "
            + this.noOfWrites2);
        System.out.println("Total number of tuples: " + this.totalTuples);

        System.out.println("\nTotal sorting time: " + (phase1Time + phase2Time) + "ms");

        sortedPrintWriter.close();
        return phase1Time + phase2Time;
    }

    private void phase1() throws IOException {
        this.sublistCount = 0;
        this.noOfReads1 = 0;

        Scanner scanner = new Scanner(new FileReader(Constants.DATA_FILE));

        while (scanner.hasNext()) {
            int i;
            String[] buffer = new String[(int) this.noTuple];
            for (i = 0; i < this.noTuple && scanner.hasNext(); i++) {
                buffer[i] = scanner.nextLine().trim();
                this.totalTuples++;
            }
            /*
            Arrays.sort(buffer, 0, i, Comparator.comparingInt((String o1) ->
                Integer.parseInt(o1.substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END)))
                .thenComparing((String o1) ->
                    Integer.parseInt(o1.substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END))));
            */
            
            quickSort(bufferCID,0,((int)this.noTuple-1),buffer);

            this.sublistCount = writePhase(buffer, this.sublistCount);

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

    private void writePhase(String[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != null)
                this.sortedPrintWriter.println(buffer[i]);
        }
        this.noOfWrites2++;
    }

    private int writePhase(String[] buffer, int sublistCount) throws IOException {
        String name = "sub-" + (sublistCount++) + ".txt";
        PrintWriter printWriter = new PrintWriter(new FileWriter(name));

        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] != null)
                printWriter.println(buffer[i]);
        }

        printWriter.close();
        return sublistCount;
    }
        /* low  --> Starting index,  high  --> Ending index */
    
    private void quickSort(int arr[], int low, int high, String[] bufferStr)
    {
    	if (low < high) 
        { 
            // pi is dividing index, arr[pi] is  
            //  now at right place 
            int pi = dividing(arr, low, high,bufferStr); 
  
            // Recursively sort elements before 
            // dividing and after dividing 
            quickSort(arr, low, pi-1,bufferStr); 
            quickSort(arr, pi+1, high,bufferStr); 
        } 
    }

    private int dividing(int arr[], int low, int high, String[] bufferStr) 
    { 
        int pivot = arr[high];  
        int i = (low-1); // index of smaller element 
        for (int j=low; j<high; j++) 
        { 
            // If current element is smaller than or 
            // equal to pivot 
            if (arr[j] <= pivot) 
            { 
                i++; 
  
                // simultaneously swapping the original data record!  
                int temp = arr[i]; 
                arr[i] = arr[j]; 
                arr[j] = temp;
                
                String tempStr = bufferStr[i];
                bufferStr[i] = bufferStr[j]; 
                bufferStr[j] = tempStr;
            } 
        } 
  
        // simultaneously swapping the original data record!  
        int temp = arr[i+1]; 
        arr[i+1] = arr[high]; 
        arr[high] = temp;
        String tempStr = bufferStr[i+1];
        bufferStr[i+1] = bufferStr[high]; 
        bufferStr[high] = tempStr;
  
        return i+1; 
    } 
    
    
}
