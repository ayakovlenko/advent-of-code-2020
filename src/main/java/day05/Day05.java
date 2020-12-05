package day05;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Day05 {

    public static void main(String[] args) {
        try (var lines = Files.lines(Path.of("./data/day_05_part_1.txt"))) {
            var seats = lines.map(Seat::fromString).collect(Collectors.toList());

            var maxId = seats.stream().mapToInt(Seat::id).max();

            maxId.ifPresent(System.out::println); // 878

            seats.sort(Comparator.comparing(Seat::id));
            for (int i = 1; i < seats.size() - 1; i++) {
                var leftSeat = seats.get(i - 1);
                var rightSeat = seats.get(i);
                if (leftSeat.id() + 1 == rightSeat.id() - 1) {
                    System.out.println(leftSeat.id() + 1); // 504
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static record Seat(int row, int col) {

        int id() {
            return row * 8 + col;
        }

        static Seat fromString(String s) {
            return new Seat(parseRow(s), parseCol(s));
        }

        private static int parseRow(String s) {
            int lo = 0;
            int hi = 128;
            for (int i = 0; i < 7; i++) {
                switch (s.charAt(i)) {
                    case 'F' -> lo -= (hi - lo) / 2;
                    case 'B' -> hi += (hi - lo) / 2;
                }
            }
            return lo;
        }

        private static int parseCol(String s) {
            int lo = 0;
            int hi = 8;
            for (int i = 7; i < 10; i++) {
                switch (s.charAt(i)) {
                    case 'L' -> hi -= (hi - lo) / 2;
                    case 'R' -> lo += (hi - lo) / 2;
                }
            }
            return lo;
        }
    }
}
