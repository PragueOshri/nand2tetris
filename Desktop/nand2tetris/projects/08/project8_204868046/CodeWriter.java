
import java.util.*;
import java.io.*;

public class CodeWriter {

    FileWriter asmFile;
    BufferedWriter writer;
    Parser codeLine;
    List<String> segments;
    Map<String, String> arithmeticCommands;
    String generalStartOfArithmeticCommand;
    String generalEndOfPushCommand;
    String generalEndOfPopCommand;
    int counterOfJmp;
    int countReturnAddress;

    CodeWriter(Parser parser, String asmFileName) throws IOException {
        asmFile = new FileWriter(asmFileName);
        writer = new BufferedWriter(asmFile);
        codeLine = parser;
        counterOfJmp = 0;
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


    // update the arithmetic commands map because if there is
    // more then one operation like eq, gt, or lt consecutively
    // we need to count them according their order with counterOfJump
    // so every jump get to the correct place
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

     // Implement the convention of VM
    private void booting() throws IOException {
        writer.write("// Bootstrap code\n" +
                        "@256\n" +
                        "D=A\n" +
                        "@SP\n" +
                        "M=D\n");
    }

    private void writeStartOfPopCommand(String currentCommand) throws IOException {
        String arg2 = currentCommand.replaceAll("[^0-9]", "");
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
    }

    private void writeStartOfPushCommand(String currentCommand) throws IOException {
        String arg2 = currentCommand.replaceAll("[^0-9]", "");
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
    }

    private String getLabel(String currentCommand) {
        return currentCommand.substring(6);
    }

    private void writeLabel(String label) throws IOException {
        if (label != null && label.length() != 0) {
            writer.write("(" + label + ")\n");
        }
        else {
            System.out.println("label does not exist");
        }
    }

    private void writeGoto(String label) throws IOException {
        writer.write("@" + label + "\n" +
                         "0;JMP\n");
    }

     // Jump to label if the stack's top element no zero
    private void writeIf(String label) throws IOException {
        writer.write(generalStartOfArithmeticCommand +
                        "@" + label + "\n" +
                        "D;JNE\n");
    }

    private void writeCall(String functionName, int nArgs) throws IOException {
        writer.write("// ARG = SP-5-nArgs\n" +
                        "@SP\n" +
                        "D=M\n" +
                        "@5\n" +
                        "D=D-A\n" +
                        "@" + nArgs + "\n" +
                        "D=D-A\n" +
                        "@ARG\n" +
                        "M=D\n" +
                        "// LCL = SP\n" +
                        "@SP\n" +
                        "D=M\n" +
                        "@LCL\n" +
                        "M=D\n");
    }

    // private void writeFunction(String functionName, int nArgs) { }
    private String getEndFrameReposition(String labelTabelArg) {
        return "@R11\n" +
                "D=M-1\n" +
                "AM=D\n" +
                "D=M\n" +
                "@" + labelTabelArg + "\n" +
                "M=D\n";
    }

    private void writeReturn() throws IOException {
        writer.write("// endFrame = LCL\n" +
                "@LCL\n" +
                "D=M\n" +
                "@R11\n" +
                "M=D\n" +
                "@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@R12\n" +
                "M=D\n" +
                "@ARG\n" +
                "D=M\n" +
                generalEndOfPopCommand +
                "@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "M=D+1\n" +
                getEndFrameReposition("THAT")+
                getEndFrameReposition("THIS")+
                getEndFrameReposition("ARG")+
                getEndFrameReposition("LCL") +
                "@R12\n" +
                "A=M\n" +
                "0;JMP\n");
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

    private void close() throws IOException {
        writer.close();
    }

    public void fillAsmFile() throws IOException {
        booting();
        for (int i = 0; i < codeLine.getCodeLines.size(); i++) {
            String currentCommand = codeLine.getCodeLines.get(i);
            writer.write("// " + currentCommand + "\n");
            for (int j = 0; j < codeLine.arithmeticOp.size(); j++) {
                if (currentCommand.equals(codeLine.arithmeticOp.get(j))) {
                    String op = codeLine.arithmeticOp.get(j);
                    writeArithmetic(op);
                }
            }
            if (currentCommand.contains("pop")) {
                writeStartOfPopCommand(currentCommand);
                writePushPop("pop");
            }
            else if (currentCommand.contains("push") && !currentCommand.contains("call_push_segments")) {
                // checking for the C_CALL command
                if (currentCommand.contains("retAddress")) {
                    writer.write("@return address " + countReturnAddress + "\n" +
                                      "D=A\n" + generalEndOfPushCommand);
                }
                else {
                    writeStartOfPushCommand(currentCommand);
                    writePushPop("push");
                }
            }
            else if (currentCommand.contains("label")) {
                if (currentCommand.contains("retAddress")) {
                    writeLabel("return address " + countReturnAddress);
                    countReturnAddress++;
                }
                else {
                    String label = getLabel(currentCommand);
                    writeLabel(label);
                }
            }

            // Want to call to writeGoto with label or functionName,
            // whatever is after goto in this command
            else if (currentCommand.contains("goto") && !currentCommand.contains("if-goto")) {
                writeGoto(currentCommand.substring(5));
            }

            // doing the C_CALL command
            // repositioning ARG and LCL
            // in the writeCall method
            else if (currentCommand.contains("call_push_segments")) {
                writer.write("@LCL\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n" +
                        "@ARG\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n" +
                        "@THIS\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n" +
                        "@THAT\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n");
            }
            else if (currentCommand.contains("call_command_repositions")) {
                StringTokenizer str = new StringTokenizer(currentCommand, " ");
                String startCommand = str.nextToken(); // just for advance to the second token
                String functionName = str.nextToken();
                String argument2 = str.nextToken();
                int arg2 = Integer.parseInt(argument2);
                writeCall(functionName, arg2);
            }
            else if (currentCommand.contains("if")) {
                writeIf(currentCommand.substring(8));
            }
            else if (currentCommand.equals("return")) {
                writeReturn();
            }
        }
        close();
    }
}
