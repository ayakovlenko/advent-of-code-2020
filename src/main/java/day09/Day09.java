package day09;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

public class Day09 {

    public static void main(String[] args) throws IOException {
        var p = Path.of("./data/day_09.txt");
        var input = parseInputFile(p);
        int windowSize = p.toString().endsWith("_example.txt") ? 5 : 25;

        var window = new Window(input.subList(0, windowSize));
        var part1 = OptionalLong.empty();
        for (int i = windowSize; i < input.size(); i++) {
            var value = input.get(i);
            if (!window.check(value)) {
                part1 = OptionalLong.of(value);
                break;
            }
            window.add(value);
        }

        if (part1.isEmpty()) throw new AssertionError();
        part1.ifPresent(System.out::println);

        var subarray = subarraySum(input, part1.getAsLong());
        if (subarray == null) throw new AssertionError();
        var smallest = subarray.stream().mapToLong(Long::longValue).min();
        var largest = subarray.stream().mapToLong(Long::longValue).max();
        var part2 = smallest.stream().flatMap(x -> largest.stream().map(y -> x + y)).findFirst();

        if (part2.isEmpty()) throw new AssertionError();
        part2.ifPresent(System.out::println);
    }

    // Part 1

    static class Window {

        private final int size;

        private final Deque<Long> values;

        private final Map<Long, Set<Long>> pairs = new HashMap<>();

        private final Map<Long, Integer> sums = new HashMap<>();

        Window(List<Long> values) {
            this.values = new LinkedList<>(values);
            this.size = values.size();

            // pre-compute pairs
            for (int i = 0; i < values.size(); i++) {
                for (int j = i + 1; j < values.size(); j++) {
                    var k = Math.min(values.get(i), values.get(j));
                    var v = Math.max(values.get(i), values.get(j));
                    pairs.putIfAbsent(k, new HashSet<>());
                    pairs.get(k).add(v);
                    sums.compute(k + v, (__, count) -> count == null ? 1 : count + 1);
                }
            }
        }

        boolean check(long value) {
            var v = sums.get(value);
            return v != null && v > 0;
        }

        void add(long x) {
            remove(x);

            values.addLast(x);
            for (Long y : values) {
                var k = Math.min(x, y);
                var v = Math.max(x, y);
                pairs.putIfAbsent(k, new HashSet<>());
                pairs.get(k).add(v);
                sums.compute(k + v, (__, count) -> count == null ? 1 : count + 1);
            }
        }

        void remove(long value) {
            var x = values.removeFirst();
            var ys = pairs.remove(x);
            for (var y : ys) {
                sums.computeIfPresent(x + y, (__, v) -> v - 1);
            }
        }

        @Override
        public String toString() {
            return "Window[size=" + size + ", values=" + values + ", pairs=" + pairs + ", sums=" + sums + ']';
        }
    }

    static List<Long> parseInputFile(Path p) throws IOException {
        try (var lines = Files.lines(p)) {
            return lines.map(Long::valueOf).collect(Collectors.toList());
        }
    }

    // Part 2

    static Deque<Long> subarraySum(List<Long> xs, long target) {
        var result = new LinkedList<>(xs.subList(0, 2));
        var sum = result.stream().mapToLong(Long::longValue).sum();
        for (int i = 0; i < xs.size(); ) {
            if (sum == target) {
                return result;
            } else if (sum < target) {
                var x = xs.get(i++);
                result.addLast(x);
                sum += x;
            } else {
                while (sum > target && xs.size() > 2) {
                    sum -= result.removeFirst();
                }
            }
        }
        return null;
    }
}
