package support;

import steps.MergeSort;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class SublistBuffer {
    private Scanner scanner;
    private String[] buffer;
    private int pointer, noOfReads, tuplesLoaded;

    public SublistBuffer(String name) throws IOException {
        this.scanner = new Scanner(new FileReader(name));
        this.buffer = new String[MergeSort.TUPLE_CAPACITY];
        this.pointer = 0;
        this.noOfReads = 0;
    }

    public void load() {
        this.pointer = 0;
        this.tuplesLoaded = 0;
        this.noOfReads++;
        for (int i = 0; i < MergeSort.TUPLE_CAPACITY && scanner.hasNext(); i++) {
            this.buffer[i] = scanner.nextLine().trim();
            this.tuplesLoaded++;
        }
    }

    public int getNoOfReads() {
        return this.noOfReads;
    }

    public int getFirstValue() {
        return Integer.parseInt(this.buffer[this.pointer]
            .substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END));
    }

    public String getFirst() {
        return this.buffer[this.pointer];
    }

    public void movePointer() {
        this.pointer++;

        if (this.pointer == this.tuplesLoaded) {
            if (this.scanner.hasNext())
                this.load();
            else
                this.pointer = -1;
        }
    }

    public boolean isFileCompleted() {
        return this.pointer == -1;
    }
}
