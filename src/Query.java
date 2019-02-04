import java.io.IOException;

public class Query {
    public static void main(String[] args) throws IOException {
        long freeMemory = Runtime.getRuntime().freeMemory();
        MergeSort mergeSort = new MergeSort(freeMemory);

        mergeSort.sort("data/input.txt", "");
    }
}
