package day13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

record Input(int earlierDeparture, List<Integer> busIds) {

    static Input fromFile(Path p) throws IOException {
        try (var br = Files.newBufferedReader(p)) {
            int earlierDeparture = Integer.parseInt(br.readLine());
            var busIds = new ArrayList<Integer>();
            for (var busId : br.readLine().split(",")) {
                if (!busId.equals("x")) busIds.add(Integer.parseInt(busId));
            }
            return new Input(earlierDeparture, busIds);
        }
    }
}
