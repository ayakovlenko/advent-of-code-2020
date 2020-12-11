package day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Day11 {

    public static void main(String[] args) throws IOException {
        var state = State.fromFile(Path.of("./data/day_11.txt"));

        while (state.hasNext()) {
            state = state.next();
        }
        System.out.println(state.layout.countOccupied());
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

        private final Set<Integer> seen;

        private Layout layout;

        State(Layout layout) {
            this.layout = layout;
            this.seen = new HashSet<>();
        }

        static State fromFile(Path p) throws IOException {
            try (var lines = Files.lines(p)) {
                var layout = lines.map(line ->
                        line.chars().mapToObj(CellType::fromChar).collect(Collectors.toList())
                ).collect(Collectors.toList());
                return new State(new Layout(layout));
            }
        }

        @Override
        public String toString() {
            return layout.toString();
        }

        @Override
        public boolean hasNext() {
            return !seen.contains(layout.hashCode());
        }

        @Override
        public State next() {
            var nextGridState = new ArrayList<List<CellType>>(layout.height);
            for (int i = 0; i < layout.height; i++) {
                nextGridState.add(new ArrayList<>());
                for (int j = 0; j < layout.width; j++) {
                    var currentCell = layout.grid.get(i).get(j);
                    var adjacent = layout.adjacent(i, j);
                    var count = adjacent.stream().filter(CellType.OCCUPIED::equals).count();
                    if (currentCell == CellType.EMPTY && count == 0) {
                        nextGridState.get(i).add(CellType.OCCUPIED);
                    } else if (currentCell == CellType.OCCUPIED && count >= 4) {
                        nextGridState.get(i).add(CellType.EMPTY);
                    } else {
                        nextGridState.get(i).add(currentCell);
                    }
                }
            }
            seen.add(layout.hashCode());
            layout = new Layout(nextGridState);
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
}
