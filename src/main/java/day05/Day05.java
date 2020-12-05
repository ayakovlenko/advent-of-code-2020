package day05;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class Day05 {

    public static void main(String[] args) {

        {
            var id = Seat.fromString("BFFFBBFRRR").id();
            assert id == 567 : id;
        }
        {
            var id = Seat.fromString("FFFBBBFRRR").id();
            assert id == 119 : id;
        }
        {
            var id = Seat.fromString("BBFFBBFRLL").id();
            assert id == 820 : id;
        }

        try (var lines = Files.lines(Path.of("./data/day_05_part_1.txt"))) {
            var seats = lines.map(Seat::fromString).collect(Collectors.toList());

            var maxId = seats.stream().mapToInt(Seat::id).max();

            // Part 1
            maxId.ifPresent(System.out::println); // 878

            // Part 2
            findSeat(seats).ifPresent(System.out::println); // 504
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static OptionalInt findSeat(List<Seat> seats) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        var seen = new HashSet<Integer>();
        for (var seat : seats) {
            var id = seat.id();
            if (id < min) min = id;
            if (max < id) max = id;
            seen.add(id);
        }
        seen.remove(min);
        seen.remove(max);
        for (int id = min + 1; id <= max - 1; id++) {
            if (!seen.contains(id)) return OptionalInt.of(id);
        }
        return OptionalInt.empty();
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
                    case 'F' -> hi -= (hi - lo) / 2;
                    case 'B' -> lo += (hi - lo) / 2;
                }
            }
            return lo;
        }

        private static int parseCol(String s) {
            int lo = 0;
            int hi = 8;
            for (int i = 7; i < 10; i++) {
                switch (s.charAt(i)) {
                    case 'R' -> lo += (hi - lo) / 2;
                    case 'L' -> hi -= (hi - lo) / 2;
                }
            }
            return lo;
        }
    }
}
