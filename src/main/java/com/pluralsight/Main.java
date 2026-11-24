package com.pluralsight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    // A record stores product data and automatically provides accessor methods.
    record BedFrame(int id, String name, String material, String size,
                    double price, int stock, double rating) {
        @Override
        public String toString() {
            return String.format("%02d | %-22s | %-8s | %-6s | €%7.2f | Stock: %2d | Rating: %.1f",
                    id, name, material, size, price, stock, rating);
        }
    }

    private static final List<BedFrame> BED_FRAMES = List.of(
            new BedFrame(1, "Oak Platform", "Wood", "Queen", 249.99, 5, 4.7),
            new BedFrame(2, "Metal Classic", "Metal", "Twin", 149.99, 8, 4.2),
            new BedFrame(3, "Velvet Luxe", "Fabric", "King", 599.99, 2, 4.9),
            new BedFrame(4, "Walnut Modern", "Wood", "Queen", 449.99, 0, 4.8),
            new BedFrame(5, "Industrial Steel", "Metal", "King", 349.99, 4, 4.5),
            new BedFrame(6, "Pine Minimalist", "Wood", "Full", 199.99, 6, 4.3),
            new BedFrame(7, "Cloud Upholstered", "Fabric", "Queen", 499.99, 0, 4.9),
            new BedFrame(8, "Bamboo Zen", "Bamboo", "King", 399.99, 3, 4.8),
            new BedFrame(9, "Nordic Birch", "Wood", "Twin", 179.99, 0, 4.1),
            new BedFrame(10, "Linen Retreat", "Fabric", "Full", 289.99, 7, 4.9),
            new BedFrame(11, "Urban Loft", "Metal", "Queen", 299.99, 2, 4.6),
            new BedFrame(12, "Rattan Haven", "Rattan", "Queen", 459.99, 3, 4.7),
            new BedFrame(13, "Bamboo Compact", "Bamboo", "Twin", 219.99, 4, 4.4),
            new BedFrame(14, "Leather Grand", "Leather", "King", 799.99, 1, 4.9),
            new BedFrame(15, "Maple Storage", "Wood", "Queen", 500.00, 5, 5.0)
    );

    public static void main(String[] args) {
        System.out.println("Welcome to the Bed Frame Stream Lab!");
        System.out.println("Each stream starts from the same original product list.");
        System.out.println("Intermediate operations are lazy: they run when a terminal operation needs results.");
        System.out.println("These pipelines do not modify the original collection.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printMenu();
                if (!scanner.hasNextLine()) return;
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> displayAllBedFrames();
                    case "2" -> filterAffordableInStock();
                    case "3" -> mapProductNames();
                    case "4" -> sortByPrice();
                    case "5" -> findUniqueMaterials();
                    case "6" -> countInStock();
                    case "7" -> findCheapestAndMostExpensive();
                    case "8" -> calculateInventoryValue();
                    case "9" -> groupByMaterial();
                    case "10" -> findTopRatedAffordable();
                    case "11" -> runAllExamples();
                    case "0" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Enter a number from 0 to 11.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.print("""

                ===== BED FRAME STREAM LAB =====
                1.  forEach - Display all products
                2.  filter - In stock and under €300
                3.  map - Product names
                4.  sorted - Price low to high
                5.  distinct - Unique materials
                6.  count - Products in stock
                7.  min / max - Cheapest and most expensive
                8.  reduce - Total inventory value
                9.  collect / groupingBy - Group by material
                10. Combined operations - Top 3 rated under €500
                11. Run all examples
                0.  Exit
                Enter your choice: """);
    }

    // 1. forEach is terminal: it performs an action for each element.
    private static void displayAllBedFrames() {
        printHeading("1. forEach()", "Display the webshop catalog, including zero-stock listings.",
                "forEach (terminal): prints every product.");

        System.out.println("Traditional loop:");
        for (BedFrame bedFrame : BED_FRAMES) {
            System.out.println(bedFrame);
        }

        System.out.println("Stream result:");
        // System.out::println is shorthand for b -> System.out.println(b).
        BED_FRAMES.stream()
                .forEach(System.out::println);
    }

    // 2. filter is intermediate: only elements matching a condition continue.
    private static void filterAffordableInStock() {
        printHeading("2. filter()", "Find purchasable products costing less than €300.",
                "filter (intermediate) keeps matches; forEach (terminal) prints them.");

        System.out.println("Traditional loop:");
        for (BedFrame bedFrame : BED_FRAMES) {
            if (bedFrame.price() < 300 && bedFrame.stock() > 0) {
                System.out.println(bedFrame);
            }
        }

        System.out.println("Stream result:");
        // b -> ... is a lambda: b represents the current BedFrame.
        // The filters are lazy until forEach requests elements.
        BED_FRAMES.stream()
                .filter(b -> b.price() < 300)
                .filter(b -> b.stock() > 0)
                .forEach(System.out::println);
    }

    // 3. map is intermediate: it transforms each BedFrame into a String.
    private static void mapProductNames() {
        printHeading("3. map()", "Create a list containing only product names.",
                "map (intermediate) transforms elements; toList (terminal) gathers them.");

        List<String> loopNames = new ArrayList<>();
        for (BedFrame bedFrame : BED_FRAMES) {
            loopNames.add(bedFrame.name());
        }
        System.out.println("Traditional loop: " + loopNames);

        // BedFrame::name is equivalent to b -> b.name().
        List<String> names = BED_FRAMES.stream()
                .map(BedFrame::name)
                .toList();

        System.out.println("Stream result: " + names);
    }

    // 4. sorted is intermediate: it orders the stream, not the source list.
    private static void sortByPrice() {
        printHeading("4. sorted()", "Show the least expensive products first.",
                "sorted (intermediate) orders by price; forEach (terminal) prints results.");

        BED_FRAMES.stream()
                .sorted(Comparator.comparingDouble(BedFrame::price))
                .forEach(System.out::println);
    }

    // 5. distinct is intermediate: it removes duplicate values from the stream.
    private static void findUniqueMaterials() {
        printHeading("5. distinct()", "Find which materials the webshop offers.",
                "map extracts materials; distinct removes duplicates (both intermediate); forEach prints (terminal).");

        BED_FRAMES.stream()
                .map(BedFrame::material)
                .distinct()
                .forEach(System.out::println);
    }

    // 6. count is terminal: it returns the number of remaining elements as a long.
    private static void countInStock() {
        printHeading("6. count()", "Count product listings with at least one unit in stock.",
                "filter (intermediate) keeps in-stock products; count (terminal) counts them.");

        long count = BED_FRAMES.stream()
                .filter(b -> b.stock() > 0)
                .count();

        System.out.println("In-stock products: " + count);
    }

    // 7. min and max are terminal: each returns an Optional<BedFrame>.
    private static void findCheapestAndMostExpensive() {
        printHeading("7. min() and max()", "Find the lowest- and highest-priced listings.",
                "min and max (terminal) compare prices and return Optional results.");

        // An Optional contains a result or is empty if the stream has no elements.
        // Each terminal operation consumes its stream, so create two streams.
        Optional<BedFrame> cheapest = BED_FRAMES.stream()
                .min(Comparator.comparingDouble(BedFrame::price));

        Optional<BedFrame> mostExpensive = BED_FRAMES.stream()
                .max(Comparator.comparingDouble(BedFrame::price));

        cheapest.ifPresentOrElse(
                b -> System.out.println("Cheapest: " + b),
                () -> System.out.println("No cheapest product found."));

        mostExpensive.ifPresentOrElse(
                b -> System.out.println("Most expensive: " + b),
                () -> System.out.println("No most expensive product found."));
    }

    // 8. reduce is terminal: it combines elements into one value.
    private static void calculateInventoryValue() {
        printHeading("8. reduce()", "Calculate the value of every unit currently in stock.",
                "map (intermediate) calculates price times stock; reduce (terminal) adds the values.");

        // 0.0 is the starting value; Double::sum is equivalent to (a, b) -> a + b.
        double totalValue = BED_FRAMES.stream()
                .map(b -> b.price() * b.stock())
                .reduce(0.0, Double::sum);

        System.out.printf("Total inventory value: €%.2f%n", totalValue);
    }

    // 9. collect is terminal: groupingBy builds a map of categories to products.
    private static void groupByMaterial() {
        printHeading("9. collect() and groupingBy()", "Organize the catalog by material.",
                "collect (terminal) uses the groupingBy collector to build a Map.");

        Map<String, List<BedFrame>> grouped = BED_FRAMES.stream()
                .collect(Collectors.groupingBy(BedFrame::material));

        // This forEach belongs to Map; the inner forEach belongs to List.
        grouped.forEach((material, products) -> {
            System.out.println("\n" + material + ":");
            products.forEach(System.out::println);
        });
    }

    // 10. Combine filters, sorting and limiting into a single pipeline.
    private static void findTopRatedAffordable() {
        printHeading("10. Combining Stream operations", "Recommend the top three in-stock products under €500.",
                "filter, sorted and limit (intermediate); forEach (terminal).");

        BED_FRAMES.stream()
                .filter(b -> b.stock() > 0)
                .filter(b -> b.price() < 500)
                .sorted(Comparator.comparingDouble(BedFrame::rating).reversed())
                .limit(3)
                .forEach(System.out::println);
    }

    private static void runAllExamples() {
        displayAllBedFrames();
        filterAffordableInStock();
        mapProductNames();
        sortByPrice();
        findUniqueMaterials();
        countInStock();
        findCheapestAndMostExpensive();
        calculateInventoryValue();
        groupByMaterial();
        findTopRatedAffordable();
    }

    private static void printHeading(String title, String problem, String operations) {
        System.out.println("\n===== " + title + " =====");
        System.out.println("Business problem: " + problem);
        System.out.println("Stream operations: " + operations);
        System.out.println("Results:");
    }
    private static void findWoodenQueenBedFrames() {
        // TODO 1: Display only queen-size bed frames made of wood.

        BED_FRAMES.stream()
                .filter(b -> b.material().equalsIgnoreCase("Wood") && b.size().equalsIgnoreCase("Queen"))
                .forEach(System.out::println);
    }
    private static void uppercaseProductNames() {
        // TODO 2: Create a list of product names in uppercase.
        List<String> names = BED_FRAMES.stream()
                .map(b -> b.name())
                .map(name -> name.toUpperCase())
                .toList();

        names.forEach(System.out::println);
    }




    // TODO 3: Calculate the average price of in-stock products.
    // TODO 4: Group products by size instead of material.
    // TODO 5: Find the two cheapest products with a rating of at least 4.5.
}
