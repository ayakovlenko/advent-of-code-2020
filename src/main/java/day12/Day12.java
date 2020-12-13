package day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Day12 {

    public static void main(String[] args) throws IOException {
        var instructions = Instructions.fromFile(Path.of("./data/day_12.txt"));

        // Part 1
        if (part1(instructions) != 1441) throw new AssertionError();

        // Part 2
        if (part2(instructions) != 61616) throw new AssertionError();
    }

    static int part1(List<Instruction> instructions) {
        var p = new Point(0, 0);
        var fd = CardinalDirection.EAST;
        for (var i : instructions) {
            if (i.direction() instanceof CardinalDirection direction) {
                p = p.moved(direction, i.value());
            } else if (i.direction() instanceof RelativeDirection direction) {
                fd = fd.turned(direction, i.value());
                if (direction == RelativeDirection.FORWARD) {
                    p = p.moved(fd, i.value());
                }
            }
        }
        return p.distance(new Point(0, 0));
    }

    static int part2(List<Instruction> instructions) {
        var s = new Point(0, 0);
        var t = new Translation(10, 1);
        for (var i : instructions) {
            if (i.direction() instanceof CardinalDirection direction) {
                switch (direction) {
                    case NORTH -> t = new Translation(t.x, t.y + i.value);
                    case SOUTH -> t = new Translation(t.x, t.y - i.value);
                    case EAST -> t = new Translation(t.x + i.value, t.y);
                    case WEST -> t = new Translation(t.x - i.value, t.y);
                }
            } else if (i.direction() instanceof RelativeDirection direction) {
                switch (direction) {
                    case FORWARD -> s = new Point(s.x + t.x * i.value, s.y + t.y * i.value);
                    case LEFT -> t = t.rotated(i.value);
                    case RIGHT -> t = t.rotated(-i.value);
                }
            }
        }
        return s.distance(new Point(0, 0));
    }

    static record Translation(int x, int y) {

        @SuppressWarnings("SuspiciousNameCombination")
        Translation rotated(int angdeg) {
            var result = this;
            angdeg = angdeg % 360;
            angdeg = angdeg < 0 ? 360 + angdeg : angdeg;
            for (; angdeg > 0; angdeg -= 90) {
                result = new Translation(-result.y, result.x);
            }
            return result;
        }
    }

    static record Point(int x, int y) {

        int distance(Point that) {
            return Math.abs(that.x - this.x) + Math.abs(that.y - this.y);
        }

        Point moved(CardinalDirection direction, int value) {
            return switch (direction) {
                case NORTH -> new Point(x, y + value);
                case SOUTH -> new Point(x, y - value);
                case EAST -> new Point(x + value, y);
                case WEST -> new Point(x - value, y);
            };
        }
    }

    static final class Instructions {

        static List<Instruction> fromFile(Path p) throws IOException {
            try (var lines = Files.lines(p)) {
                return lines.map(line ->
                        new Instruction(
                                Direction.fromChar(line.charAt(0)),
                                Integer.parseInt(line.substring(1))
                        )
                ).collect(Collectors.toList());
            }
        }
    }

    static record Instruction(Direction direction, int value) {
    }

    sealed interface Direction permits CardinalDirection, RelativeDirection {

        static Direction fromChar(char c) {
            return switch (c) {
                case 'N' -> CardinalDirection.NORTH;
                case 'S' -> CardinalDirection.SOUTH;
                case 'E' -> CardinalDirection.EAST;
                case 'W' -> CardinalDirection.WEST;
                case 'L' -> RelativeDirection.LEFT;
                case 'R' -> RelativeDirection.RIGHT;
                case 'F' -> RelativeDirection.FORWARD;
                default -> throw new IllegalArgumentException();
            };
        }
    }

    enum CardinalDirection implements Direction {

        NORTH, EAST, SOUTH, WEST;

        CardinalDirection turned(RelativeDirection direction, int degrees) {
            int turn = degrees % 360 / 90;
            return switch (direction) {
                case FORWARD -> this;
                case LEFT -> {
                    int idx = (this.ordinal() - turn) % 4;
                    yield CardinalDirection.values()[idx >= 0 ? idx : 4 + idx];
                }
                case RIGHT -> {
                    int idx = (this.ordinal() + turn) % 4;
                    yield CardinalDirection.values()[idx];
                }
            };
        }
    }

    enum RelativeDirection implements Direction {
        LEFT, RIGHT, FORWARD
    }
}
