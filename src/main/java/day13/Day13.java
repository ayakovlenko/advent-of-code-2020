package day13;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Day13 {

    public static void main(String[] args) throws IOException {
        var input = Input.fromFile(Path.of("./data/day_13.txt"));

        System.out.println(part1(input)); // 1895

        System.out.println(part2(input)); // 840493039281088
    }

    static int part1(Input input) {
        var m = new HashMap<Integer, Integer>();
        var pq = new PriorityQueue<Integer>();
        var busIds = input.busIds().stream().filter(Objects::nonNull).collect(Collectors.toList());
        for (int busId : busIds) {
            var ref = input.earlierDeparture() / busId * busId;
            var nextRoute = ref < input.earlierDeparture() ? ref + busId : ref;
            pq.add(nextRoute);
            m.put(nextRoute, busId);
        }
        //noinspection ConstantConditions
        int minutesToWait = pq.peek() - input.earlierDeparture();
        int earliestBus = m.get(pq.peek());
        return earliestBus * minutesToWait;
    }

    static String part2(Input input) {
        BiFunction<Collection<Integer>, String, String> join =
                (c, sep) -> c.stream().map(String::valueOf).collect(Collectors.joining(sep));

        var offsets = new LinkedHashMap<Integer, Integer>();
        for (int i = 0; i < input.busIds().size(); i++) {
            var busId = input.busIds().get(i);
            if (busId != null) offsets.put(busId, busId - i);
        }
        long t = 0;
        var mods = join.apply(offsets.keySet(), ", ");
        var rems = join.apply(offsets.values(), ", ");
        var crt = String.format("ChineseRemainder[{%s}, {%s}]", rems, mods);
        return "https://www.wolframalpha.com/input/?i=" +
                URLEncoder.encode(crt, StandardCharsets.UTF_8);
    }
}
