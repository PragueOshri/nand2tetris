import java.io.*;
import java.util.*;

public class Parser {

    File file;
    FileReader reader;
    BufferedReader br;
    String fileBase;
    String currentCommand;
    Map<Integer, String> labelTable;

    List<String> arithmeticOp;

    List<String> getCodeLines;


    public enum CommandType {
        C_ARITHMETIC,
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL
    }

    Parser(String path) throws IOException {
        file = new File(path);
        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf('.');
        String fileBase = (lastDot == -1) ? fileName : fileName.substring(0, lastDot);
        //fileBase = FilenameUtils.getBaseName(fileName);
        reader = new FileReader(file);
        br = new BufferedReader(reader);
        getCodeLines = new ArrayList<>();

        labelTable = new HashMap<>();
        labelTable.put(0, "SP");
        labelTable.put(1, "LCL");
        labelTable.put(2, "ARG");
        labelTable.put(3, "THIS");
        labelTable.put(4, "THAT");

        arithmeticOp = new ArrayList<>();
        arithmeticOp.add("add");
        arithmeticOp.add("sub");
        arithmeticOp.add("and");
        arithmeticOp.add("or");
        arithmeticOp.add("neg");
        arithmeticOp.add("not");
        arithmeticOp.add("eq");
        arithmeticOp.add("lt");
        arithmeticOp.add("gt");


    }

    private boolean hasMoreLines(BufferedReader br) throws IOException{
        String line = br.readLine();
        if (line != null) {
            currentCommand = line;
            return true;
        }
        return false;
    }

    private void advance() {
        if (currentCommand.equals("\n")) {
            currentCommand = currentCommand.replace("\n"," ");
        }
        currentCommand = currentCommand.replaceAll("\\s", " ");
        if (currentCommand.length() != 0) {
            int indexOf = currentCommand.indexOf("//");
            if (indexOf != -1) {
                currentCommand = currentCommand.substring(0, indexOf);
            }
        }
    }

    public boolean isArithmetic() {
        for (int i = 0; i < arithmeticOp.size(); i++) {
            if (currentCommand.contains(arithmeticOp.get(i))) {
                return true;
            }
        }
        return false;
    }

    public CommandType commandType() {
        if (currentCommand.length() != 0) {
            if (currentCommand.contains("pop")) {
                return CommandType.C_POP;
            } else if (currentCommand.contains("push")) {
                return CommandType.C_PUSH;
            }
        }
        return CommandType.C_ARITHMETIC;
    }

    public boolean isConstant() {
        if (currentCommand.contains("constant")) {
            return true;
        }
        return false;
    }

    public boolean isStatic() {
        if (currentCommand.contains("static")) {
            return true;
        }
        return false;
    }

    public boolean isTemp() {
        if (currentCommand.contains("temp")) {
            return true;
        }
        return false;
    }

    public boolean isPointer() {
        if (currentCommand.contains("pointer")) {
            return true;
        }
        return false;
    }

    public boolean isInLabelTable() {
        if (currentCommand.contains("local")) {
            currentCommand = currentCommand.replace("local", "LCL");
            return true;
        }
        else if (currentCommand.contains("argument")) {
            currentCommand = currentCommand.replace("argument", "ARG");
            return true;
        }
        else if (currentCommand.contains("this")) {
            currentCommand = currentCommand.replace("this", "THIS");
            return true;
        }
        else if (currentCommand.contains("that")) {
            currentCommand = currentCommand.replace("that", "THAT");
            return true;
        }
        return false;
    }

    public String arg1() {
        String argument1 = "argument1 is not initialize";
        if (commandType() == CommandType.C_ARITHMETIC) {
            argument1 = currentCommand;
        }
        return argument1;
    }

    public int arg2() {
        int argument2val = -1;
        String argument2;

        argument2 = currentCommand.replaceAll("[^0-9]", "");
        argument2val = Integer.parseInt(argument2);
        return argument2val;
    }

    /* creating a list of just the command lines of the code
    * in order to convert them to assembly in easier way */
    public void fillCodeLines() throws IOException {
        while (hasMoreLines(br)) {
            advance();
            if (isArithmetic()) {
                getCodeLines.add(arg1());
            }
            else if (commandType() == CommandType.C_POP) {
                if (isInLabelTable()) {
                    System.out.println("in isInLabelTable");
                    System.out.println("current command = " + currentCommand + "\n");
                    for (int i = 0; i < labelTable.size(); i++) {
                        if (currentCommand.contains(labelTable.get(i))) {
                            getCodeLines.add("pop " + labelTable.get(i) + " " + arg2());
                            System.out.println("pop " + labelTable.get(i) + " " + arg2());
                        }
                    }
                }
                else if (isTemp()) {
                    getCodeLines.add("pop temp " + arg2());
                }
                else if (isPointer()) {
                    if (currentCommand.contains("0")) {
                        getCodeLines.add("pop pointer 0");
                    }
                    else {
                        getCodeLines.add("pop pointer 1");
                    }
                }
                else {
                    getCodeLines.add("pop " + fileBase + "." + arg2());
                }
            }
            else if (commandType() == CommandType.C_PUSH) {
                if (isInLabelTable()) {
                    for (int i = 0; i < labelTable.size(); i++) {
                        if (currentCommand.contains(labelTable.get(i))) {
                            getCodeLines.add("push " + labelTable.get(i) + " " + arg2());
                        }
                    }
                }
                else if (isTemp()) {
                    getCodeLines.add("push temp " + arg2());
                }
                else if (isPointer()) {
                    if (currentCommand.contains("0")) {
                        getCodeLines.add("push pointer 0");
                    }
                    else {
                        getCodeLines.add("push pointer 1");
                    }
                }
                else if (isConstant()) {
                    getCodeLines.add("push constant" + arg2());
                }
                else {
                    getCodeLines.add("push " + fileBase + "." + arg2());
                }
            }
        }
    }
}
