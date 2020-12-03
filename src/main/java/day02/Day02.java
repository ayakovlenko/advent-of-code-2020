package day02;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.Integer.parseInt;

public class Day02 {

    public static void main(String[] args) {
        System.out.println(countValidPasswords(RangeRule.class)); // 416
        System.out.println(countValidPasswords(PositionRule.class)); // 688
    }

    public static long countValidPasswords(Class<? extends Rule> kind) {
        record Pair(Rule rule, String password) {
        }

        try (var input = Files.lines(Path.of("./data/day_02_part_1.txt"))) {
            return input
                    .map(line -> {
                        var tokens = line.split(": ");
                        return new Pair(Rule.parse(tokens[0], kind), tokens[1]);
                    })
                    .filter(__ -> __.rule().validate(__.password()))
                    .count();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    sealed interface Rule permits RangeRule, PositionRule {

        boolean validate(String password);

        static Rule parse(String s, Class<? extends Rule> kind) {
            var token = s.split("[ -]");
            var c = token[2].charAt(0);
            if (kind == RangeRule.class) {
                return new RangeRule(c, parseInt(token[0]), parseInt(token[1]));
            } else {
                return new PositionRule(c, parseInt(token[0]) - 1, parseInt(token[1]) - 1);
            }
        }
    }

    record RangeRule(char symbol, int min, int max) implements Rule {

        @Override
        public boolean validate(String password) {
            int n = 0;
            for (int i = 0; i < password.length(); i++) {
                if (password.charAt(i) == this.symbol) {
                    if (++n > this.max) {
                        return false;
                    }
                }
            }
            return n >= this.min;
        }
    }

    record PositionRule(char symbol, int pos1, int pos2) implements Rule {

        @Override
        public boolean validate(String password) {
            return password.charAt(pos1) == symbol ^ password.charAt(pos2) == symbol;
        }
    }
}
