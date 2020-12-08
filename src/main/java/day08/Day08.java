package day08;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Day08 {

    public static void main(String[] args) throws IOException {
        var console = Console.load(Path.of("./data/day_08_example.txt"));

        int acc = console.run();
        System.out.println(acc);
    }

    static class Console {

        final ArrayList<Instruction> instructions;

        final int[] execCount;

        int acc = 0;

        Console(ArrayList<Instruction> instructions) {
            this.instructions = instructions;
            this.execCount = new int[instructions.size()];
        }

        static Console load(Path p) throws IOException {
            try (var lines = Files.lines(p)) {
                var instructions = lines.map(Instruction::parse).collect(Collectors.toCollection(ArrayList::new));
                return new Console(instructions);
            }
        }

        int run() {
            int ip = 0; // instruction pointer
            while (ip < instructions.size()) {
                if (++execCount[ip] > 1) {
                    debug();
                    break;
                }
                var inst = instructions.get(ip);
                switch (inst.op) {
                    case ACC:
                        acc += inst.arg;
                        break;
                    case JMP:
                        ip += inst.arg;
                        continue;
                    case NOP:
                }
                ip++;
            }
            return acc;
        }

        void debug() {
            System.out.println(Arrays.toString(execCount));
        }
    }

    static record Instruction(Operation op, int arg) {
        static Instruction parse(String s) {
            var tokens = s.split(" ");
            return new Instruction(Operation.valueOf(tokens[0].toUpperCase()), Integer.parseInt(tokens[1]));
        }
    }

    enum Operation {
        NOP, ACC, JMP
    }
}
