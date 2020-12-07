package day07;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Day07 {

    public static void main(String[] args) {
        var ruleGraph = parseInputFile(Path.of("./data/day_07.txt"));

        var myColor = "shiny gold";

        var colors = findColors(ruleGraph, myColor);

        System.out.println(colors.size());

        System.out.println(countBags(ruleGraph, myColor) - 1);
    }

    static Set<String> findColors(Map<String, Rule> ruleGraph, String myColor) {
        var result = new HashSet<String>();
        var seen = new HashSet<>(List.of(myColor));
        for (var color : ruleGraph.keySet()) {
            findColorsBacktrack(ruleGraph, myColor, new LinkedList<>(List.of(color)), seen, result);
        }
        return result;
    }

    static int countBags(Map<String, Rule> ruleGraph, String myColor) {
        var rule = ruleGraph.get(myColor);
        if (rule.capacity().isEmpty()) {
            return 1;
        }
        int result = 1;
        for (var cap : rule.capacity().entrySet()) {
            result += cap.getValue() * countBags(ruleGraph, cap.getKey());
        }
        return result;
    }

    static void findColorsBacktrack(Map<String, Rule> ruleGraph,
                                    String myColor,
                                    LinkedList<String> path,
                                    Set<String> seen,
                                    Set<String> result) {

        var color = path.getLast();

        if (seen.contains(color)) return;
        seen.add(color);

        var rule = ruleGraph.get(color);
        if (rule.capacity().containsKey(myColor) || rule.capacity().keySet().stream().anyMatch(result::contains)) {
            result.addAll(path);
        }

        for (var c : rule.capacity.keySet()) {
            path.addLast(c);
            findColorsBacktrack(ruleGraph, myColor, path, seen, result);
            path.removeLast();
        }
    }

    static record Rule(String color, Map<String, Integer> capacity) {
        private final static Pattern PATTERN =
                Pattern.compile("(\\d+) (.*)");

        static Rule fromString(String s) {
            var tokens = s.replaceAll(" bags?\\.?", "").split(" contains? ");
            var capacity = new HashMap<String, Integer>();
            for (var token : tokens[1].split(", ")) {
                var m = PATTERN.matcher(token);
                if (m.find()) {
                    var color = m.group(2);
                    var cap = Integer.parseInt(m.group(1));
                    capacity.put(color, cap);
                }
            }
            return new Rule(tokens[0], capacity);
        }
    }

    // ---

    static Map<String, Rule> parseInputFile(Path p) {
        var g = new HashMap<String, Rule>();
        try (var lines = Files.lines(p)) {
            lines.map(Rule::fromString).forEach(r -> g.put(r.color(), r));
            return g;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
