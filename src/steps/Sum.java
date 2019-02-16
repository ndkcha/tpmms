package steps;

import support.Constants;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Sum {
    private int inputTuple, outputTuple;

    public Sum(long noTuple) {
        this.inputTuple = (int) (((double) noTuple) * 0.7);
        this.outputTuple = (int) (((double) noTuple) * 0.3);
    }

    public void calculateSum() throws IOException {
        long startTime = System.currentTimeMillis();
        Scanner scanner = new Scanner(new FileReader(Constants.SORTED_FILE));
        String inputBuffer[] = new String[this.inputTuple];
        String outputBuffer[] = new String[this.outputTuple];
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
                    outputBuffer[outPointer] = headClientId.concat(String.valueOf(sum));
                    outPointer++;
                    headClientId = inputBuffer[i].substring(Constants.PRIMARY_KEY_START, Constants.PRIMARY_KEY_END);
                    sum = Double.parseDouble(inputBuffer[i].substring(Constants.SECONDARY_KEY_START));

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

        long difference = System.currentTimeMillis() - startTime;
        System.out.println("\nTime taken for calculating sum: " + difference + "ms");
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
