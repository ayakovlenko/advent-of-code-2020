package day04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Day04 {

    public static void main(String[] args) throws IOException {
        var passports = new LinkedList<Passport>();
        try (var br = Files.newBufferedReader(Path.of("./data/day_04_part_1.txt"))) {
            var fields = new HashMap<String, String>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    passports.add(new Passport(Map.copyOf(fields)));
                    fields.clear();
                } else {
                    for (String field : line.split(" ")) {
                        var kv = field.split(":");
                        fields.put(kv[0], kv[1]);
                    }
                }
            }
            if (!fields.isEmpty()) {
                passports.add(new Passport(Map.copyOf(fields)));
            }
        }

        // Part 1
        System.out.println(countValidPassports(passports, new Part1Strategy())); // 230

        // Part 2
        System.out.println(countValidPassports(passports, new Part2Strategy())); // 156
    }

    static long countValidPassports(List<Passport> passports, Predicate<Passport> p) {
        return passports.stream().filter(p).count();
    }

    static record Passport(Map<String, String> fields) {

        final static Set<String> REQUIRED_FIELDS =
                Set.of("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid");
    }

    static class Part1Strategy implements Predicate<Passport> {

        @Override
        public boolean test(Passport passport) {
            return passport.fields.keySet().containsAll(Passport.REQUIRED_FIELDS);
        }
    }

    static class Part2Strategy implements Predicate<Passport> {

        private final static Pattern HEIGHT_PATTERN =
                Pattern.compile("(0|[1-9][0-9]*)(cm|in)");

        private final static Pattern HAIR_COLOR_PATTERN =
                Pattern.compile("#[0-9a-f]{6}");

        private final static Set<String> EYE_COLORS =
                Set.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth");

        @Override
        public boolean test(Passport passport) {
            return passport.fields.keySet().containsAll(Passport.REQUIRED_FIELDS)
                    && isYearInRange(passport.fields.get("byr"), 1920, 2002)
                    && isYearInRange(passport.fields.get("iyr"), 2010, 2020)
                    && isYearInRange(passport.fields.get("eyr"), 2020, 2030)
                    && isValidHeight(passport.fields.get("hgt"))
                    && isValidHairColor(passport.fields.get("hcl"))
                    && isValidEyeColor(passport.fields.get("ecl"))
                    && isValidPassportId(passport.fields.get("pid"));
        }

        private boolean isYearInRange(String yr, int min, int max) {
            if (yr.length() != 4 && !allDigits(yr)) return false;
            int i = Integer.parseInt(yr);
            return i >= min && i <= max;
        }

        private boolean isValidHeight(String hgt) {
            var m = HEIGHT_PATTERN.matcher(hgt);
            if (m.find()) {
                var value = Integer.parseInt(m.group(1));
                return switch (m.group(2)) {
                    case "cm" -> 150 <= value && value <= 193;
                    case "in" -> 59 <= value && value <= 76;
                    default -> throw new RuntimeException("improbable case");
                };
            }
            return false;
        }

        private boolean isValidHairColor(String hcl) {
            return HAIR_COLOR_PATTERN.matcher(hcl).matches();
        }

        private boolean isValidEyeColor(String ecl) {
            return EYE_COLORS.contains(ecl);
        }

        private boolean isValidPassportId(String pid) {
            return pid.length() == 9 && allDigits(pid);
        }

        private boolean allDigits(String s) {
            for (int i = 0; i < s.length(); i++) {
                if (!Character.isDigit(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
