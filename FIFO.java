
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("==================================");
        System.out.println("  FIFO PAGE REPLACEMENT ALGORITHM");
        System.out.println("==================================");

        System.out.print("Enter number of frames: ");
        int numberOfFrames = input.nextInt();

        if (numberOfFrames <= 0) {
            System.out.println("Number of frames must be greater than zero.");
            input.close();
            return;
        }

        System.out.print("Enter number of pages: ");
        int numberOfPages = input.nextInt();

        if (numberOfPages <= 0) {
            System.out.println("Number of pages must be greater than zero.");
            input.close();
            return;
        }

        int[] referenceString = new int[numberOfPages];

        System.out.println("\nEnter the reference string:");

        for (int i = 0; i < numberOfPages; i++) {
            System.out.print("Page " + (i + 1) + ": ");
            referenceString[i] = input.nextInt();
        }

        int[] frames = new int[numberOfFrames];

        for (int i = 0; i < numberOfFrames; i++) {
            frames[i] = -1;
        }

        int hits = 0;
        int faults = 0;
        int pointer = 0;

        // TABLE HEADER
        System.out.println("\nFIFO SIMULATION TABLE");

        printBorder(numberOfFrames);

        System.out.printf("| %-9s |", "Reference");

        for (int i = 0; i < numberOfFrames; i++) {
            System.out.printf(" %-9s |", "Frame " + (i + 1));
        }

        System.out.printf(" %-6s |%n", "Result");

        printBorder(numberOfFrames);

        // PROCESS REFERENCE STRING
        for (int i = 0; i < numberOfPages; i++) {

            int currentPage = referenceString[i];
            boolean pageFound = false;

            // CHECK IF PAGE EXISTS
            for (int j = 0; j < numberOfFrames; j++) {
                if (frames[j] == currentPage) {
                    pageFound = true;
                    break;
                }
            }

            String result;

            if (pageFound) {

                hits++;
                result = "HIT";

            } else {

                faults++;
                result = "FAULT";

                frames[pointer] = currentPage;

                pointer = (pointer + 1) % numberOfFrames;
            }

            // DISPLAY TABLE ROW
            System.out.printf("| %-9d |", currentPage);

            for (int j = 0; j < numberOfFrames; j++) {

                if (frames[j] == -1) {
                    System.out.printf(" %-9s |", "-");
                } else {
                    System.out.printf(" %-9d |", frames[j]);
                }
            }

            System.out.printf(" %-6s |%n", result);
        }

        // TABLE FOOTER
        printBorder(numberOfFrames);

        // FINAL RESULTS
        double hitPercentage =
                (double) hits / numberOfPages * 100.0;

        double faultPercentage =
                (double) faults / numberOfPages * 100.0;

        System.out.println("\n================ FINAL RESULTS ================");

        System.out.print("Final Frames: ");

        for (int i = 0; i < numberOfFrames; i++) {
            System.out.print(
                (frames[i] == -1 ? "-" : frames[i]) + " "
            );
        }

        System.out.println();
        System.out.println("Total Hits: " + hits);
        System.out.println("Total Faults: " + faults);

        System.out.printf("Hit Percentage: %.2f%%%n", hitPercentage);
        System.out.printf("Fault Percentage: %.2f%%%n", faultPercentage);

        System.out.println("================================================");

        input.close();
    }

    // METHOD FOR TABLE BORDERS
    public static void printBorder(int numberOfFrames) {

        System.out.print("+-----------+");

        for (int i = 0; i < numberOfFrames; i++) {
            System.out.print("-----------+");
        }

        System.out.println("--------+");
    }
}
