import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Optimal Page Replacement Simulator.
 *
 * Rules:
 *  - Frames start empty; a reference to a page already in a frame is a HIT,
 *    otherwise it is a FAULT.
 *  - On a FAULT with a free frame, the page goes into the first free frame.
 *  - On a FAULT with all frames full, ALL frames are checked at once against the
 *    rest of the reference string:
 *      * If every page in the frames will be used again, replace the page
 *        that will be used LAST (farthest in the future).
 *      * If some pages will never be used again, replace one of those. If
 *        several are never used again, replace the one in the lowest-numbered
 *        frame (Frame 1 before Frame 2, and so on).
 *
 * Usage:  java OptimalPageReplacement
 */
public class OptimalPageReplacement {

    // ---------------------------------------------------------------- model

    static final class Step {
        final int page;
        final int[] frames;   // -1 = empty
        final boolean fault;

        Step(int page, int[] frames, boolean fault) {
            this.page = page;
            this.frames = frames;
            this.fault = fault;
        }
    }

    static List<Step> simulate(List<Integer> refs, int frameCount) {
        validate(refs, frameCount);
        List<Step> steps = new ArrayList<>();
        int[] frames = new int[frameCount];
        Arrays.fill(frames, -1);

        for (int t = 0; t < refs.size(); t++) {
            int page = refs.get(t);
            int slot = -1;
            for (int i = 0; i < frameCount; i++) if (frames[i] == page) slot = i;

            boolean fault = (slot < 0);
            if (fault) {
                int target = -1;
                for (int i = 0; i < frameCount; i++) {
                    if (frames[i] == -1) { target = i; break; }      // free frame first
                }
                if (target < 0) {                                     // full: look at all frames
                    target = 0;
                    int farthest = -1;
                    for (int i = 0; i < frameCount; i++) {
                        int next = Integer.MAX_VALUE;                  // never used again
                        for (int k = t + 1; k < refs.size(); k++) {
                            if (refs.get(k) == frames[i]) { next = k; break; }
                        }
                        // strict '>' keeps the lowest-numbered frame on ties (never-used-again pages)
                        if (next > farthest) { farthest = next; target = i; }
                    }
                }
                frames[target] = page;
            }
            steps.add(new Step(page, frames.clone(), fault));
        }
        return steps;
    }

    static void validate(List<Integer> refs, int frames) {
        if (frames < 1 || frames > 10) {
            throw new IllegalArgumentException("Number of frames must be between 1 and 10 (got " + frames + ").");
        }
        if (refs == null || refs.isEmpty()) {
            throw new IllegalArgumentException("Reference string is empty.");
        }
        for (int p : refs) {
            if (p < 0) throw new IllegalArgumentException("Page numbers must be >= 0 (found " + p + ").");
        }
    }

    // --------------------------------------------------------------- output

    static String center(String text, int width) {
        int total = width - text.length();
        int left = total / 2;
        int right = total - left;
        return " ".repeat(Math.max(0, left)) + text + " ".repeat(Math.max(0, right));
    }

    static String border(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) sb.append("-".repeat(w)).append('+');
        return sb.toString();
    }

    static String row(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++) sb.append(center(cells[i], widths[i])).append('|');
        return sb.toString();
    }

    static void printTable(List<Step> steps, int frameCount) {
        int cols = frameCount + 2;                      // Reference + frames + Result
        String[] header = new String[cols];
        header[0] = "Reference";
        for (int i = 0; i < frameCount; i++) header[i + 1] = "Frame " + (i + 1);
        header[cols - 1] = "Result";

        int[] widths = new int[cols];
        for (int i = 0; i < cols; i++) widths[i] = header[i].length() + 2;
        for (Step s : steps) {                          // widen for long page numbers
            widths[0] = Math.max(widths[0], String.valueOf(s.page).length() + 2);
            for (int i = 0; i < frameCount; i++) {
                widths[i + 1] = Math.max(widths[i + 1], String.valueOf(s.frames[i]).length() + 2);
            }
        }
        widths[cols - 1] = Math.max(widths[cols - 1], "FAULT".length() + 2);

        System.out.println(border(widths));
        System.out.println(row(header, widths));
        System.out.println(border(widths));
        for (Step s : steps) {
            String[] cells = new String[cols];
            cells[0] = String.valueOf(s.page);
            for (int i = 0; i < frameCount; i++) {
                cells[i + 1] = s.frames[i] < 0 ? "-" : String.valueOf(s.frames[i]);
            }
            cells[cols - 1] = s.fault ? "FAULT" : "HIT";
            System.out.println(row(cells, widths));
        }
        System.out.println(border(widths));
    }

    static void printSummary(List<Step> steps) {
        int total = steps.size();
        int faults = 0;
        for (Step s : steps) if (s.fault) faults++;
        int hits = total - faults;
        int[] last = steps.get(total - 1).frames;

        StringBuilder fin = new StringBuilder();
        for (int i = 0; i < last.length; i++) {
            if (i > 0) fin.append(", ");
            fin.append("Frame ").append(i + 1).append(" = ").append(last[i] < 0 ? "-" : String.valueOf(last[i]));
        }

        System.out.println();
        System.out.println("Final frames      : " + fin);
        System.out.println("Total references  : " + total);
        System.out.println("Total hits        : " + hits);
        System.out.println("Total faults      : " + faults);
        System.out.printf("Hit ratio         : %d/%d = %.2f%%%n", hits, total, 100.0 * hits / total);
        System.out.printf("Fault ratio       : %d/%d = %.2f%%%n", faults, total, 100.0 * faults / total);
    }

    static void run(List<Integer> refs, int frames) {
        List<Step> steps = simulate(refs, frames);
        System.out.println();
        System.out.println("OPTIMAL PAGE REPLACEMENT");
        System.out.println();
        printTable(steps, frames);
        printSummary(steps);
    }

    // ---------------------------------------------------------------- input

    static int readInt(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  ! Please enter a whole number.");
            }
        }
    }

    static List<Integer> readRefs(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println("  ! Reference string cannot be empty.");
                continue;
            }
            try {
                List<Integer> refs = new ArrayList<>();
                for (String tok : s.split("[\\s,]+")) refs.add(Integer.parseInt(tok));
                return refs;
            } catch (NumberFormatException e) {
                System.out.println("  ! Use whole numbers separated by spaces or commas.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try {
            List<Integer> refs;
            int frames;
            while (true) {
                refs = readRefs(in, "Page reference string (space/comma separated): ");
                frames = readInt(in, "Number of page frames (1-10): ");
                try {
                    validate(refs, frames);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("  ! Invalid input: " + e.getMessage() + " Please re-enter.");
                    System.out.println();
                }
            }
            run(refs, frames);
        } catch (NoSuchElementException eof) {
            System.out.println("\n(end of input)");
        }
    }
}
