import java.io.IOException;
import java.io.*;
import java.util.Random;

public class Script {

    public static void main(String[] arg) throws IOException {
        try {
            if (arg.length < 1) {
                System.out.println("Please specify the number of days to schedule for.");
                System.exit(1);
            }
            // Random rn = new Random();
            int num = Integer.parseInt(arg[0]);
            // System.out.println(num);
            String result1 = "#index, number\n";
            for (int i = 1; i<=num; i++) {
                    result1 += i + "," + i;
                    if (!(i == num)) result1 += "\n";
            }
            BufferedWriter writer1 = new BufferedWriter(new FileWriter("out.txt", false));
            writer1.write(result1);
            writer1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}