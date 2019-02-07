import java.io.IOException;

public class Query {
    static final String DATA_FILE = "data/input.txt";
    static final String OUT_FILE = "out.txt";

    public static void main(String[] args) throws IOException {
        long freeMemory = Runtime.getRuntime().freeMemory();
        MergeSort mergeSort = new MergeSort(freeMemory);

        mergeSort.sort();
    }
}
