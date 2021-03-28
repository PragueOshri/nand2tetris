import java.util.*;
import java.io.*;

public class CodeWriter {

    FileWriter asmFile;
    BufferedWriter writer;
    Parser codeLine;
    List<String> segments;
    String currentCommand;
    Map<String, String> arithmeticCommands;
    String generalStartOfArithmeticCommand;
    String generalEndOfPushCommand;
    String generalEndOfPopCommand;
    int counterOfJmp;


    CodeWriter(String asmFileName, String vmFileName) throws IOException {
        asmFile = new FileWriter(asmFileName);
        writer = new BufferedWriter(asmFile);
        codeLine = new Parser(vmFileName);
        codeLine.fillCodeLines();
        counterOfJmp = 0;

        segments = new ArrayList<>();
        segments.add("constant");
        segments.add("static");
        segments.add("temp");
        segments.add("pointer");

        generalEndOfPushCommand =
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n";

        generalEndOfPopCommand =
                "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n";

        generalStartOfArithmeticCommand =
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "A=A-1\n";

        this.initArithmeticCommands();
    }

    /* update the arithmetic commands map because if there is
    * more then one operation like eq, gt, or lt consecutively
    * we need to count them according their order with counterOfJump
    * so every jump get to the correct place */
    private void initArithmeticCommands() {
        arithmeticCommands = new HashMap<>();
        arithmeticCommands.put("add", generalStartOfArithmeticCommand + "M=M+D\n");
        arithmeticCommands.put("sub", generalStartOfArithmeticCommand + "M=M-D\n");
        arithmeticCommands.put("and", generalStartOfArithmeticCommand + "M=M&D\n");
        arithmeticCommands.put("or", generalStartOfArithmeticCommand + "M=M|D\n");
        arithmeticCommands.put("not", "@SP\n" +
                "A=M-1\n" +
                "M=!M\n");
        arithmeticCommands.put("neg", "@SP\n" +
                "A=M-1\n" +
                "M=-M\n");
        arithmeticCommands.put("eq", generalStartOfArithmeticCommand + "D=M-D\n" +
                "@isEQ" + counterOfJmp + "\n" +
                "D;JEQ\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@notEQ" + counterOfJmp + "\n" +
                "0;JMP\n" +
                "(isEQ" + counterOfJmp + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(notEQ" + counterOfJmp + ")\n");
        arithmeticCommands.put("gt", generalStartOfArithmeticCommand + "D=M-D\n" +
                "@isGT" + counterOfJmp + "\n" +
                "D;JGT\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@notGT" + counterOfJmp + "\n" +
                "0;JMP\n" +
                "(isGT" + counterOfJmp + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(notGT"+ counterOfJmp + ")\n");
        arithmeticCommands.put("lt", generalStartOfArithmeticCommand + "D=M-D\n" +
                "@isLT" + counterOfJmp + "\n" +
                "D;JLT\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@notLT" + counterOfJmp + "\n" +
                "0;JMP\n" +
                "(isLT" + counterOfJmp + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(notLT"+ counterOfJmp + ")\n");
    }
    
    public void fillAsmFile() throws IOException {
        System.out.println("number of lines in this file = " + codeLine.getCodeLines.size());
        for (int i = 0; i < codeLine.getCodeLines.size(); i++) {
            currentCommand = codeLine.getCodeLines.get(i);
            String arg2 = currentCommand.replaceAll("[^0-9]", "");
            for (int j = 0; j < codeLine.arithmeticOp.size(); j++) {
                if (currentCommand.contains(codeLine.arithmeticOp.get(j))) {
                    String op = codeLine.arithmeticOp.get(j);
                    writeArithmetic(op);
                }
            }
            if (currentCommand.contains("pop")) {
                for (int j = 0; j < codeLine.labelTable.size(); j++) {
                    if (currentCommand.contains(codeLine.labelTable.get(j))) {
                        writer.write(
                                "@" + codeLine.labelTable.get(j) + "\n" +
                                "D=M\n" +
                                "@" + arg2 + "\n" +
                                "D=D+A\n");
                    }
                }
                if (currentCommand.contains(".")) {
                    writer.write(
                            "@" + currentCommand.substring(4) + "\n" +
                            "D=A\n");
                }
                else if (currentCommand.contains("temp")) {
                    String arg2ForTemp = Integer.toString(5 + Integer.parseInt(arg2));
                    writer.write("@R5\n" +
                            "D=M\n" +
                            "@" + arg2ForTemp + "\n" +
                            "D=D+A\n");
                }
                else if (currentCommand.contains("pointer 0")) {
                    writer.write("@THIS\n" +
                            "D=A\n");
                }
                else if (currentCommand.contains("pointer 1")) {
                    writer.write("@THAT\n" +
                            "D=A\n");
                }
                writePushPop("pop");
            }
            else if (currentCommand.contains("push")) {
                for (int j = 0; j < codeLine.labelTable.size(); j++) {
                    if (currentCommand.contains(codeLine.labelTable.get(j))) {
                        writer.write(
                                "@" + codeLine.labelTable.get(j) + "\n" +
                                        "D=M\n" +
                                        "@" + arg2 + "\n" +
                                        "A=D+A\n" +
                                        "D=M\n");
                    }
                }
                if (currentCommand.contains(".")) {
                    writer.write(
                            "@" + currentCommand.substring(4) + "\n" +
                                    "D=M\n");
                }
                else if (currentCommand.contains("temp")) {
                    String arg2ForTemp = Integer.toString(5 + Integer.parseInt(arg2));
                    writer.write("@R5\n" +
                            "D=M\n" +
                            "@" + arg2ForTemp + "\n" +
                            "A=D+A\n" +
                            "D=M\n");
                }
                else if (currentCommand.contains("pointer 0")) {
                    writer.write("@THIS\n" +
                            "D=M\n");
                }
                else if (currentCommand.contains("pointer 1")) {
                    writer.write("@THAT\n" +
                            "D=M\n");
                }
                else if (currentCommand.contains("constant")) {
                    writer.write("@" + arg2 + "\n" +
                            "D=A\n");
                }
                writePushPop("push");
            }
        }
        writer.close();
    }

    private void writeArithmetic(String op) throws IOException {
        writer.write(arithmeticCommands.get(op));
        counterOfJmp++;
        initArithmeticCommands();
    }

    private void writePushPop(String kindOfOp) throws IOException {
        if (kindOfOp.equals("pop")) {
            writer.write(generalEndOfPopCommand);
        }
        else if (kindOfOp.equals("push")) {
            writer.write(generalEndOfPushCommand);
        }
    }
}
