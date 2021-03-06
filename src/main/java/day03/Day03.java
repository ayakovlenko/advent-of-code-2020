package day03;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Day03 {

    public static void main(String[] args) {
        var toboggan = Toboggan.fromFile(Path.of("./data/day_03_part_1.txt"));

        // Part 1
        var part1 = toboggan.countTrees(new Slope(1, 3));
        System.out.println(part1); // 211

        // Part 2
        var part2 = product(
                toboggan.countTrees(new Slope(1, 1)),
                toboggan.countTrees(new Slope(1, 3)),
                toboggan.countTrees(new Slope(1, 5)),
                toboggan.countTrees(new Slope(1, 7)),
                toboggan.countTrees(new Slope(2, 1))
        );
        System.out.println(part2); // 3584591857
    }

    static long product(int... args) {
        long result = 1;
        for (int arg : args) {
            result *= arg;
        }
        return result;
    }

    enum SquareType {
        OPEN, TREE
    }

    record Slope(int x, int y) {
    }

    static class Toboggan {

        final int height;

        final int width;

        final List<List<SquareType>> territory;

        Toboggan(List<List<SquareType>> territory) {
            this.territory = territory;
            this.height = territory.size();
            this.width = territory.get(0).size();
        }

        static Toboggan fromFile(Path p) {
            try (var lines = Files.lines(p)) {
                var territory =
                        lines.map(Toboggan::stringToSquareTypes).collect(Collectors.toList());
                return new Toboggan(territory);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        int countTrees(Slope slope) {
            int trees = 0;
            for (var kind : new TobogganIterable(this, slope)) {
                if (SquareType.TREE == kind) {
                    trees++;
                }
            }
            return trees;
        }

        // ---

        private static List<SquareType> stringToSquareTypes(String s) {
            return s.chars()
                    .mapToObj(c -> switch (c) {
                        case '.' -> SquareType.OPEN;
                        case '#' -> SquareType.TREE;
                        default -> throw new RuntimeException("unknown square type: " + c);
                    })
                    .collect(Collectors.toList());
        }

        private static class TobogganIterable implements Iterable<SquareType> {

            final Toboggan toboggan;

            final Slope slope;

            TobogganIterable(Toboggan toboggan, Slope slope) {
                this.toboggan = toboggan;
                this.slope = slope;
            }

            @Override
            public Iterator<SquareType> iterator() {
                return new Iterator<>() {

                    int row = slope.x();

                    int col = slope.y();

                    @Override
                    public boolean hasNext() {
                        return row < toboggan.height;
                    }

                    @Override
                    public SquareType next() {
                        var kind = toboggan.territory.get(row).get(col);
                        row += slope.x();
                        col = (col + slope.y()) % toboggan.width;
                        return kind;
                    }
                };
            }
        }
    }
}
