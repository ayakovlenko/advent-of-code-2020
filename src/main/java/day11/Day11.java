package day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Day11 {

    public static void main(String[] args) throws IOException {
        var layout = Layout.fromFile(Path.of("./data/day_11.txt"));

        var state = new State(layout, SeatChoiceStrategy.PART1);
        while (state.hasNext()) {
            state = state.next();
        }
        System.out.println(state.layout.countOccupied()); // 2265
    }

    static class Layout {

        private final List<List<CellType>> grid;

        private final int width;

        private final int height;

        Layout(List<List<CellType>> grid) {
            this.grid = grid;
            this.height = grid.size();
            this.width = grid.get(0).size();
        }

        static Layout fromFile(Path p) throws IOException {
            try (var lines = Files.lines(p)) {
                var layout = lines.map(line ->
                        line.chars().mapToObj(CellType::fromChar).collect(Collectors.toList())
                ).collect(Collectors.toList());
                return new Layout(layout);
            }
        }

        private static final int[] DIRECTION = {-1, 0, 1};

        List<CellType> adjacent(int i, int j) {
            var cells = new LinkedList<CellType>();
            for (int iDelta : DIRECTION) {
                for (int jDelta : DIRECTION) {
                    if (iDelta == 0 && jDelta == 0) continue;
                    int i1 = i + iDelta;
                    int j1 = j + jDelta;
                    if (0 <= i1 && i1 < height && 0 <= j1 && j1 < width) {
                        cells.add(grid.get(i1).get(j1));
                    }
                }
            }
            return cells;
        }

        long countOccupied() {
            return grid.stream().flatMap(List::stream).filter(CellType.OCCUPIED::equals).count();
        }

        @Override
        public String toString() {
            var sj = new StringJoiner("\n");
            grid.stream()
                    .map(row -> row.stream().map(CellType::toString).collect(Collectors.joining()))
                    .forEach(sj::add);
            return sj.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Layout layout = (Layout) o;
            return grid.equals(layout.grid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(grid);
        }
    }

    static class State implements Iterator<State> {

        private int prev;

        private Layout layout;

        private final SeatChoiceStrategy strategy;

        State(Layout layout, SeatChoiceStrategy strategy) {
            this.layout = layout;
            this.prev = -1;
            this.strategy = strategy;
        }

        @Override
        public String toString() {
            return layout.toString();
        }

        @Override
        public boolean hasNext() {
            return prev != layout.hashCode();
        }

        @Override
        public State next() {
            var nextLayout = strategy.nextLayout(layout);
            prev = layout.hashCode();
            layout = nextLayout;
            return this;
        }
    }

    enum CellType {

        FLOOR('.'), EMPTY('L'), OCCUPIED('#');

        private final char c;

        CellType(char c) {
            this.c = c;
        }

        static CellType fromChar(int c) {
            return switch (c) {
                case '.' -> CellType.FLOOR;
                case 'L' -> CellType.EMPTY;
                case '#' -> CellType.OCCUPIED;
                default -> throw new IllegalArgumentException("" + c);
            };
        }

        @Override
        public String toString() {
            return "" + c;
        }
    }

    interface SeatChoiceStrategy {

        SeatChoiceStrategy PART1 = layout -> {
            var grid = new ArrayList<List<CellType>>(layout.height);
            for (int i = 0; i < layout.height; i++) {
                grid.add(new ArrayList<>());
                for (int j = 0; j < layout.width; j++) {
                    var currentCell = layout.grid.get(i).get(j);
                    var adjacent = layout.adjacent(i, j);
                    var count = adjacent.stream().filter(CellType.OCCUPIED::equals).count();
                    if (currentCell == CellType.EMPTY && count == 0) {
                        grid.get(i).add(CellType.OCCUPIED);
                    } else if (currentCell == CellType.OCCUPIED && count >= 4) {
                        grid.get(i).add(CellType.EMPTY);
                    } else {
                        grid.get(i).add(currentCell);
                    }
                }
            }
            return new Layout(grid);
        };

        Layout nextLayout(Layout layout);
    }
}
