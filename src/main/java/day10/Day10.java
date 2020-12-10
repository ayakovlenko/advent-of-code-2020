package day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day10 {

    public static void main(String[] args) throws IOException {
        var input = parseInputFile(Path.of("./data/day_10.txt"));

        input.add(0);
        input.sort(Integer::compareTo);
        input.add(input.get(input.size() - 1) + 3);

        var diffs = new ArrayList<Integer>();
        for (int i = 1; i < input.size(); i++) {
            diffs.add(input.get(i) - input.get(i - 1));
        }

        var diffCount = diffs.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        var part1 = diffCount.get(1) * diffCount.get(3);
        System.out.println(part1); // 2414

        var part2 = countCombs(input);
        System.out.println(part2); // 21156911906816
    }

    static long countCombs(List<Integer> jolts) {
        var dp = new HashMap<>(Map.of(jolts.get(jolts.size() - 1), 1L));
        for (int i = jolts.size() - 2; i >= 0; i--) {
            var canReach = 0L;
            var maxJolts = jolts.get(i) + 3;
            for (int j = i + 1; j < jolts.size() && jolts.get(j) <= maxJolts; j++) {
                canReach += dp.get(jolts.get(j));
            }
            dp.put(jolts.get(i), canReach);
        }
        return dp.get(0);
    }

    static List<Integer> parseInputFile(Path p) throws IOException {
        try (var lines = Files.lines(p)) {
            return lines.map(Integer::parseInt).collect(Collectors.toList());
        }
    }
}
