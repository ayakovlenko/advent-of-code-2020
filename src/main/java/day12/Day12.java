package day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Day12 {

    public static void main(String[] args) throws IOException {
        var instructions = Instructions.fromFile(Path.of("./data/day_12.txt"));

        var ship1 = new Ship(new Point(0, 0), CardinalDirection.EAST);
        var ship2 = ship1.move(instructions);

        System.out.println(ship2);

        System.out.println(ship1.position.distance(ship2.position)); // < 5013
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
