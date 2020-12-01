package day01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Day01 {

    public static void main(String[] args) throws IOException {
        // Same input for both parts
        List<Integer> nums;
        try (var input = Files.lines(Path.of("./data/day_01_part_1.txt"))) {
            nums = input.map(Integer::parseInt).collect(Collectors.toList());
        }

        // Part 1
        twoSum(nums, 2020)
                .flatMap(result -> result.stream().reduce((x, y) -> x * y))
                .ifPresent(System.out::println);

        // Part 2
        threeSum(nums, 2020)
                .flatMap(result -> result.stream().reduce((x, y) -> x * y))
                .ifPresent(System.out::println);

        // 805731
        // 192684960
    }

    @SuppressWarnings("SameParameterValue")
    static Optional<List<Integer>> twoSum(List<Integer> nums, int target) {
        var cache = new HashMap<Integer, Integer>();
        for (int num : nums) {
            int diff = target - num;
            if (cache.containsKey(num)) {
                return Optional.of(List.of(cache.get(num), num));
            }
            cache.put(diff, num);
        }
        return Optional.empty();
    }

    @SuppressWarnings("SameParameterValue")
    static Optional<List<Integer>> threeSum(List<Integer> nums, int target) {
        for (int i = 0; i < nums.size(); i++) {
            var nums1 = new ArrayList<>(nums);
            var elem = nums1.remove(i);
            var result = twoSum(nums1, target - elem).map(LinkedList::new);
            if (result.isPresent()) {
                return result.map(r -> {
                    r.addFirst(elem);
                    return r;
                });
            }
        }
        return Optional.empty();
    }
}
