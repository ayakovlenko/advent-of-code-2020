package day08;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Day08 {

    public static void main(String[] args) throws IOException {
        var console = Console.load(Path.of("./data/day_08.txt"));

        System.out.println(console.run());
        console.reset();

        InstructionMender.findPatch(console.instructions).forEach(console::applyPatch);
        System.out.println(console.run());
    }

    static class Console {

        private final ArrayList<Instruction> instructions;

        private final int[] execCount;

        private int ip = 0; // instruction pointer

        private int acc = 0;

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
            while (ip < instructions.size()) {
                if (++execCount[ip] > 1) {
                    // debug();
                    break;
                }
                var inst = instructions.get(ip);
                switch (inst.op) {
                    case ACC -> {
                        ip++;
                        acc += inst.arg;
                    }
                    case JMP -> ip += inst.arg;
                    case NOP -> ip++;
                }
            }
            return acc;
        }

        void applyPatch(InstructionPatch patch) {
            instructions.set(patch.ip, patch.instruction);
        }

        void reset() {
            Arrays.fill(this.execCount, 0);
            this.ip = 0;
            this.acc = 0;
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

        static Instruction nop(int arg) {
            return new Instruction(Operation.NOP, arg);
        }

        static Instruction jmp(int arg) {
            return new Instruction(Operation.JMP, arg);
        }
    }

    enum Operation {
        NOP, ACC, JMP
    }

    static record InstructionPatch(int ip, Instruction instruction) {
    }

    static class InstructionMender {

        static List<InstructionPatch> findPatch(ArrayList<Instruction> instructions) {
            var result = new LinkedList<InstructionPatch>();
            backtrack(instructions, 0, new int[instructions.size()], null, result);
            return result;
        }

        private static void backtrack(ArrayList<Instruction> instructions,
                                      int ip,
                                      int[] execCount,
                                      InstructionPatch candidate,
                                      LinkedList<InstructionPatch> patch) {
            if (!patch.isEmpty()) {
                return;
            }

            if (ip == instructions.size() && candidate != null) {
                patch.add(candidate);
                return;
            }

            if (ip >= instructions.size()) {
                return;
            }

            if (++execCount[ip] > 1) {
                execCount[ip]--;
                return;
            }

            var inst = instructions.get(ip);
            switch (inst.op) {
                case NOP, ACC -> backtrack(instructions, ip + 1, execCount, candidate, patch);
                case JMP -> backtrack(instructions, ip + inst.arg, execCount, candidate, patch);
            }
            if (candidate == null) {
                switch (inst.op) {
                    case NOP -> {
                        var newCandidate = new InstructionPatch(ip, Instruction.jmp(inst.arg));
                        backtrack(instructions, ip + inst.arg, execCount, newCandidate, patch);
                    }
                    case ACC -> backtrack(instructions, ip + 1, execCount, candidate, patch);
                    case JMP -> {
                        var newCandidate = new InstructionPatch(ip, Instruction.nop(inst.arg));
                        backtrack(instructions, ip + 1, execCount, newCandidate, patch);
                    }
                }
            }
        }
    }
}
