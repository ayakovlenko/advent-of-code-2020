package day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
        System.out.println(part1);
    }

    static List<Integer> parseInputFile(Path p) throws IOException {
        try (var lines = Files.lines(p)) {
            return lines.map(Integer::parseInt).collect(Collectors.toList());
        }
    }
}
