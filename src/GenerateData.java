import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class GenerateData {
    public static void main(String[] args) throws IOException  {
        Random random = new Random();
        PrintWriter printWriter = new PrintWriter(new FileWriter("input.txt"));
        Scanner scanner = new Scanner(System.in);

        System.out.println("If the batch size is m and no of batches are n, it will generate m * n values.");
        System.out.println("Keep the batch size around 10,000 to get stable results");
        System.out.print("[input] Batch size: ");
        long batchSize = Long.parseLong(scanner.nextLine());
        System.out.print("[input] Total Batches: ");
        long totalBatches = Long.parseLong(scanner.nextLine());
        long claimId = 0;
        String date = "2010-05-21";
        String name = "Name Is So Long Because "; // add one ch$ last
        String address = "I have a long address. But do you know how long. Even I don't know how long it is." +
            " I guess it is 150 letters. But will it end. Let me add some spaces.";
        String email = "ihave28characterss@email.com";
        String insuredItem = "01";

        for (int j = 0; j < totalBatches; j++) {
            for (int i = 0; i < batchSize; i++) {
                claimId++;
                String cId = String.format("%08d", claimId);
                String clientId = String.format("%09d", random.nextInt(99999));
                String nameIt = clientId.substring(0, 1);
                String newName = name.concat(nameIt);
                String damageL = String.format("%06d", random.nextInt(99999));
                String damageT = String.format("%02d", random.nextInt(99));
                String damage = damageL + "." + damageT;

                String finalString = cId + date + clientId + newName + address +
                    email + insuredItem + damage + damage;

                printWriter.println(finalString);

                String data = "\r" + claimId;
                System.out.write(data.getBytes());
            }
        }

        printWriter.close();
    }
}
