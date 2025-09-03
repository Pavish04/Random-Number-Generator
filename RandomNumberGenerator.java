// File: RandomNumberGenerator.java
// Console app: generate random integers in [low, high], with options.

import java.util.*;

public class RandomNumberGenerator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("--- Random Number Generator ---");
        int low = readInt(sc, "Enter lower bound (integer): ");
        int high = readInt(sc, "Enter upper bound (integer): ");

        while (high < low) {
            System.out.println("Upper bound must be >= lower bound. Try again.");
            low  = readInt(sc, "Enter lower bound (integer): ");
            high = readInt(sc, "Enter upper bound (integer): ");
        }

        int count = readInt(sc, "How many numbers do you want to generate? (>=1): ");
        while (count < 1) {
            System.out.println("Count must be at least 1.");
            count = readInt(sc, "How many numbers do you want to generate? (>=1): ");
        }

        System.out.print("Require uniqueness? (y/N): ");
        boolean unique = sc.next().trim().equalsIgnoreCase("y");

        Long seed = null;
        System.out.print("Use a seed for reproducibility? (enter number or leave blank): ");
        sc.nextLine(); // consume leftover newline
        String seedLine = sc.nextLine().trim();
        if (!seedLine.isEmpty()) {
            try {
                seed = Long.parseLong(seedLine);
            } catch (NumberFormatException e) {
                System.out.println("Invalid seed. Proceeding without a seed.");
            }
        }

        // Validate uniqueness vs range size
        long rangeSize = (long) high - (long) low + 1L;
        if (unique && rangeSize < count) {
            System.out.println("Error: uniqueness requested but range size (" + rangeSize + ") < count (" + count + ").");
            return;
        }

        Random rng = (seed == null) ? new Random() : new Random(seed);
        List<Integer> results = generate(rng, low, high, count, unique);

        System.out.println("\nGenerated " + (unique ? "unique " : "") + "numbers in [" + low + ", " + high + "]"
                + (seed != null ? " using seed " + seed : "") + ":");
        for (int x : results) System.out.print(x + " ");
        System.out.println();
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) return sc.nextInt();
            System.out.println("Please enter a valid integer.");
            sc.next(); // discard invalid token
        }
    }

    // Generates 'count' integers in [low, high], optionally unique.
    private static List<Integer> generate(Random rng, int low, int high, int count, boolean unique) {
        List<Integer> out = new ArrayList<>(count);
        if (!unique) {
            for (int i = 0; i < count; i++) {
                int val = low + rng.nextInt(high - low + 1);
                out.add(val);
            }
            return out;
        }

        // Unique: use Fisher–Yates on a window if range is small, or a set if large
        long rangeSize = (long) high - (long) low + 1L;
        if (rangeSize <= 200_000) {
            // Build array then shuffle first 'count' elements
            int n = (int) rangeSize;
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) arr[i] = low + i;
            // Partial Fisher–Yates
            for (int i = 0; i < count; i++) {
                int j = i + rng.nextInt(n - i);
                int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
                out.add(arr[i]);
            }
        } else {
            // Large range: sample with a HashSet
            HashSet<Integer> set = new HashSet<>(count * 2);
            while (set.size() < count) {
                int val = low + rng.nextInt(high - low + 1);
                set.add(val);
            }
            out.addAll(set);
        }
        return out;
    }
}
