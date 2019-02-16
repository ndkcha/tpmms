package steps;

import support.Constants;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Sum {
    private int inputTuple, outputTuple;

    public Sum(long noTuple) {
        this.inputTuple = (int) (((double) noTuple) * 0.7);
        this.outputTuple = (int) (((double) noTuple) * 0.3);
    }

    public long calculateSum() throws IOException {
        long startTime = System.currentTimeMillis();
        Scanner scanner = new Scanner(new FileReader(Constants.SORTED_FILE));
        String inputBuffer[] = new String[this.inputTuple];
        String outputBuffer[] = new String[this.outputTuple];
        String topTen[] = new String[10];
        int topTenPointer = 0;
        String headClientId = null;
        double sum = 0.0;
        int localTotal;
        int outPointer = 0;

        while (scanner.hasNext()) {
            localTotal = 0;
            for (int i = 0; i < this.inputTuple && scanner.hasNext(); i++) {
                inputBuffer[i] = scanner.nextLine();
                localTotal++;
            }

            for (int i = 0; i < localTotal; i++) {
                if (headClientId == null) {
                    headClientId = inputBuffer[i].substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END);
                    sum = Double.parseDouble(inputBuffer[i].substring(Constants.SECONDARY_KEY_START));
                    continue;
                }

                String clientId = inputBuffer[i].substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END);

                if (headClientId.equalsIgnoreCase(clientId)) {
                    double amountPaid = Double.parseDouble(inputBuffer[i].substring(Constants.SECONDARY_KEY_START));
                    sum += amountPaid;
                } else {
                    String tup = headClientId.concat(String.valueOf(sum));
                    outputBuffer[outPointer] = tup;
                    outPointer++;
                    headClientId = inputBuffer[i].substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END);
                    sum = Double.parseDouble(inputBuffer[i].substring(Constants.SECONDARY_KEY_START));

                    if (topTenPointer != 10) {
                        topTen[topTenPointer] = tup;
                        topTenPointer++;
                    } else {
                        Arrays.sort(topTen, (String o1, String o2) -> {
                            double l = Double.parseDouble(o2.substring(Constants.PRIMARY_KEY_LENGTH));
                            double r = Double.parseDouble(o1.substring(Constants.PRIMARY_KEY_LENGTH));
                            return (int) (l - r);
                        });

                        double lastSum = Double.parseDouble(topTen[9].substring(Constants.PRIMARY_KEY_LENGTH));
                        if (sum > lastSum)
                            topTen[9] = tup;
                    }

                    if (outPointer == this.outputTuple) {
                        this.writeBuffer(outputBuffer);
                        outputBuffer = new String[this.outputTuple];
                        outPointer = 0;
                    }
                }
            }
        }

        if (outPointer > 0)
            this.writeBuffer(outputBuffer);

        Arrays.sort(topTen, (String o1, String o2) -> {
                double l = Double.parseDouble(o2.substring(Constants.PRIMARY_KEY_LENGTH));
                double r = Double.parseDouble(o1.substring(Constants.PRIMARY_KEY_LENGTH));
                return (int) (l - r);
        });

        this.writeTopTen(topTen);

        long difference = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken for calculating sum: " + difference + "ms");

        return difference;
    }

    private void writeTopTen(String[] buffer) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(Constants.TOP_TEN_FILE));

        for (String b : buffer) {
            if (b != null)
                printWriter.println(b);
        }

        printWriter.close();
    }

    private void writeBuffer(String[] buffer) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(Constants.SUM_FILE, true));

        for (String b : buffer) {
            if (b != null)
                printWriter.println(b);
        }

        printWriter.close();
    }
}
