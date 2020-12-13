package day13;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Day13 {

    public static void main(String[] args) throws IOException {
        var input = Input.fromFile(Path.of("./data/day_13.txt"));

        System.out.println(part1(input)); // > 379
    }

    static int part1(Input input) {
        var m = new HashMap<Integer, Integer>();
        var pq = new PriorityQueue<Integer>();
        for (int busId : input.busIds()) {
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
}
