package day04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
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

        int valid = 0;
        for (var passport : passports) {
            if (passport.looksLegit()) {
                valid++;
            }
        }
        System.out.println(valid);
    }


    record Passport(Map<String, String> fields) {

        final static Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

        final static Pattern HEIGHT_PATTERN = Pattern.compile("(0|[1-9][0-9]*)(cm|in)");

        final static Pattern HAIR_COLOR_PATTERN = Pattern.compile("#[0-9a-f]{6}");

        final static Set<String> EYE_COLORS = Set.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth");

        final static Set<String> REQUIRED_FIELDS = Set.of("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid");

        boolean looksLegit() {
            return fields.keySet().containsAll(REQUIRED_FIELDS)
                    && isYearInRange("byr", 1920, 2002)
                    && isYearInRange("iyr", 2010, 2020)
                    && isYearInRange("eyr", 2020, 2030)
                    && isValidHeight()
                    && isValidHairColor()
                    && isValidEyeColor()
                    && isValidPassportId();
        }

        private boolean isYearInRange(String fieldName, int min, int max) {
            var yr = fields.get(fieldName);
            if (!YEAR_PATTERN.matcher(yr).matches()) return false;
            int i = Integer.parseInt(yr);
            return i >= min && i <= max;
        }

        private boolean isValidHeight() {
            var m = HEIGHT_PATTERN.matcher(fields.get("hgt"));
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

        private boolean isValidHairColor() {
            return HAIR_COLOR_PATTERN.matcher(fields.get("hcl")).matches();
        }

        private boolean isValidEyeColor() {
            return EYE_COLORS.contains(fields.get("ecl"));
        }

        private boolean isValidPassportId() {
            var pid = fields.get("pid");
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
