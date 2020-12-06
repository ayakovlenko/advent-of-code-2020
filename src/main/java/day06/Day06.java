package day06;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day06 {

    public static void main(String[] args) {
        var groups = parseInputFile(Path.of("./data/day_06_part_1.txt"));

        // Part 1
        var anyYesCount = groups.stream().mapToLong(DeclarationGroup::anyYesCount).sum();
        System.out.println(anyYesCount); // 6387

        // Part 2
        var allYesCount = groups.stream().mapToLong(DeclarationGroup::allYesCount).sum();
        System.out.println(allYesCount); // 3039
    }

    static record DeclarationGroup(List<Declaration> declarations) {

        static DeclarationGroup empty() {
            return new DeclarationGroup(new LinkedList<>());
        }

        long anyYesCount() {
            return declarations.stream()
                    .flatMap(decl -> decl.answers.stream())
                    .collect(Collectors.toSet())
                    .size();
        }

        long allYesCount() {
            return declarations.stream()
                    .map(Declaration::answers)
                    .reduce((s1, s2) -> {
                        s1.retainAll(s2);
                        return s1;
                    })
                    .map(Set::size)
                    .orElse(-1);
        }
    }

    static record Declaration(Set<Character> answers) {
        static Declaration empty() {
            return new Declaration(new HashSet<>());
        }
    }

    // ---

    static List<DeclarationGroup> parseInputFile(Path p) {
        try (var br = Files.newBufferedReader(p)) {
            var groups = new LinkedList<DeclarationGroup>();
            var group = DeclarationGroup.empty();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    groups.add(group);
                    group = DeclarationGroup.empty();
                } else {
                    var declaration = Declaration.empty();
                    for (int i = 0; i < line.length(); i++) {
                        declaration.answers.add(line.charAt(i));
                    }
                    group.declarations.add(declaration);
                }
            }
            groups.add(group);
            return groups;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
