package day09;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day09 {

    static int WINDOW_SIZE = 25;

    public static void main(String[] args) throws IOException {
        var input = parseInputFile(Path.of("./data/day_09.txt"));

        var window = new Window(input.subList(0, WINDOW_SIZE));

        for (int i = WINDOW_SIZE; i < input.size(); i++) {
            var value = input.get(i);
            if (!window.check(value)) {
                System.out.println(value);
                break;
            }
            window.add(value);
        }
    }

    static class Window {

        private final int size;

        private final LinkedList<Long> values;

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
}
