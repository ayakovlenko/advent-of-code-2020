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
        var part1 =
                new Ship(new Point(0, 0), CardinalDirection.EAST)
                        .move(instructions)
                        .position.distance(new Point(0, 0));
        System.out.println(part1); // 1441

        System.out.println(part2(instructions)); // < 75306
    }

    static int part2(List<Instruction> instructions) {
        var s = new Point(0, 0);
        var t = new Translation(10, 1);
        var w = new Point(s.x + t.x, s.y + t.y);
        for (var i : instructions) {
            if (i.direction() instanceof CardinalDirection direction) {
                switch (direction) {
                    case NORTH -> t = new Translation(t.x, t.y + i.value);
                    case EAST -> t = new Translation(t.x + i.value, t.y);
                    case SOUTH -> t = new Translation(t.x, t.y - i.value);
                    case WEST -> t = new Translation(t.x - i.value, t.y);
                }
            } else if (i.direction() instanceof RelativeDirection direction) {
                switch (direction) {
                    case FORWARD -> s = new Point(s.x + t.x * i.value, s.y + t.y * i.value);
                    case LEFT -> t = t.rotated(i.value % 360);
                    case RIGHT -> t = t.rotated(-(i.value % 360));
                }
            }
            w = new Point(s.x + t.x, s.y + t.y);
        }

        System.out.println(s);
        System.out.println(w);
        return s.distance(new Point(0, 0));
    }

    static record Translation(int x, int y) {

        @SuppressWarnings("SuspiciousNameCombination")
        Translation rotated(int angdeg) {
            return switch (angdeg) {
                case 0 -> this;
                case 90, -270 -> new Translation(-y, x);
                case 180, -180 -> new Translation(-x, y);
                case 270, -90 -> new Translation(y, -x);
                default -> throw new IllegalArgumentException("" + angdeg);
            };
        }
    }

    static record Ship(Point position, CardinalDirection facingDirection) {

        Ship move(List<Instruction> instructions) {
            var newPosition = position;
            var fd = facingDirection;
            for (var instruction : instructions) {
                if (instruction.direction() instanceof CardinalDirection direction) {
                    newPosition = newPosition.moved(direction, instruction.value());
                } else if (instruction.direction() instanceof RelativeDirection direction) {
                    fd = fd.turned(direction, instruction.value());
                    if (direction == RelativeDirection.FORWARD) {
                        newPosition = newPosition.moved(fd, instruction.value());
                    }
                }
            }
            return new Ship(newPosition, fd);
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
