import java.util.Scanner;

public class LRU {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("==================================");
        System.out.println("  LRU PAGE REPLACEMENT ALGORITHM");
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

        // -1 represents an empty frame.
        int[] frames = new int[numberOfFrames];
        int[] lastUsed = new int[numberOfFrames];

        for (int i = 0; i < numberOfFrames; i++) {
            frames[i] = -1;
        }

        int hits = 0;
        int faults = 0;

        System.out.println("\n================ LRU SIMULATION TABLE ================");
        System.out.printf("%-12s", "Reference");

        for (int i = 0; i < numberOfFrames; i++) {
            System.out.printf("%-10s", "Frame " + (i + 1));
        }
        System.out.printf("%-12s%n", "Result");

        // Process each page in the reference string.
        for (int i = 0; i < numberOfPages; i++) {
            int currentPage = referenceString[i];
            int pagePosition = -1;

            // Check whether the page is already in memory.
            for (int j = 0; j < numberOfFrames; j++) {
                if (frames[j] == currentPage) {
                    pagePosition = j;
                    break;
                }
            }

            String result;

            if (pagePosition != -1) {
                // Page hit: update its most recent use.
                hits++;
                result = "HIT";
                lastUsed[pagePosition] = i;
            } else {
                // Page fault: use an empty frame if one is available.
                faults++;
                result = "FAULT";

                int replacementIndex = -1;

                for (int j = 0; j < numberOfFrames; j++) {
                    if (frames[j] == -1) {
                        replacementIndex = j;
                        break;
                    }
                }

                // If all frames are full, replace the least recently used page.
                if (replacementIndex == -1) {
                    int leastRecentUse = Integer.MAX_VALUE;

                    for (int j = 0; j < numberOfFrames; j++) {
                        if (lastUsed[j] < leastRecentUse) {
                            leastRecentUse = lastUsed[j];
                            replacementIndex = j;
                        }
                    }
                }

                frames[replacementIndex] = currentPage;
                lastUsed[replacementIndex] = i;
            }

            // Print the state of all frames after this reference is processed.
            System.out.printf("%-12d", currentPage);

            for (int j = 0; j < numberOfFrames; j++) {
                if (frames[j] == -1) {
                    System.out.printf("%-10s", "-");
                } else {
                    System.out.printf("%-10d", frames[j]);
                }
            }

            System.out.printf("%-12s%n", result);
        }

        double hitPercentage = (double) hits / numberOfPages * 100.0;
        double faultPercentage = (double) faults / numberOfPages * 100.0;

        System.out.println("\n==================== FINAL RESULTS ====================");
        System.out.print("Final Frames: ");

        for (int i = 0; i < numberOfFrames; i++) {
            System.out.print((frames[i] == -1 ? "-" : frames[i]) + " ");
        }

        System.out.println();
        System.out.println("Total Hits: " + hits);
        System.out.println("Total Faults: " + faults);
        System.out.printf("Hit Percentage: %.2f%%%n", hitPercentage);
        System.out.printf("Fault Percentage: %.2f%%%n", faultPercentage);
        System.out.println("=======================================================");

        input.close();
    }
}
