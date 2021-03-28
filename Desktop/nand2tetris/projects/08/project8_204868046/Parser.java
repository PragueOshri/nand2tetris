
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
    boolean writeOnce;
    boolean isDir;


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

    Parser() {
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
        writeOnce = false;
        isDir = false;
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
        currentCommand = currentCommand.replaceAll("\\s+", " ");
        if (currentCommand.length() != 0) {
            int indexOf = currentCommand.indexOf("//");
            if (indexOf != -1) {
                currentCommand = currentCommand.substring(0, indexOf);
            }
            boolean isSpaceLast = currentCommand.endsWith(" ");
            if (isSpaceLast && currentCommand.length() > 1) {
                currentCommand = currentCommand.substring(0,currentCommand.length()-1);
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
            }
            else if (currentCommand.contains("push")) {
                return CommandType.C_PUSH;
            }
            else if (currentCommand.contains("call")) {
                return CommandType.C_CALL;
            }
            else if (currentCommand.contains("label")) {
                return CommandType.C_LABEL;
            }
            else if (currentCommand.contains("if")) {
                return CommandType.C_IF;
            }
            else if (currentCommand.contains("goto")) {
                return CommandType.C_GOTO;
            }
            else if (currentCommand.contains("function")) {
                return CommandType.C_FUNCTION;
            }
            else if (currentCommand.contains("return")) {
                return CommandType.C_RETURN;
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
        String argument2 = "";
        StringTokenizer str = new StringTokenizer(currentCommand," ");
        if (str.countTokens() >= 2) {
            String rightStr = str.nextToken();
            String functionName = str.nextToken();
            argument2 = str.nextToken();
        }
        argument2val = Integer.parseInt(argument2);
        return argument2val;
    }

    private String getFunctionName() {
        String functionName = "not initialize ";
        StringTokenizer str = new StringTokenizer(currentCommand," ");
        if (str.countTokens() >= 2) {
            String rightStr = str.nextToken();
            functionName = str.nextToken();
        }
        return functionName;
    }


    // creating a list of just the command lines of the code
    //in order to convert them to assembly in easier way
    private void fillCodeLines() throws IOException {
        if (isDir && !writeOnce) {
            // doing the bootstrap code
            getCodeLines.add("push retAddress");
            getCodeLines.add("call_push_segments");
            getCodeLines.add("call_command_repositions Sys.init 0");
            getCodeLines.add("goto Sys.init");
            getCodeLines.add("label retAddress");
            writeOnce = true;
        }

        while (hasMoreLines(br)) {
            advance();
            if (isArithmetic()) {
                getCodeLines.add(arg1());
            }
            else if (commandType() == CommandType.C_POP) {
                if (isInLabelTable()) {
                    for (int i = 0; i < labelTable.size(); i++) {
                        if (currentCommand.contains(labelTable.get(i))) {
                            getCodeLines.add("pop " + labelTable.get(i) + " " + arg2());
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
                    getCodeLines.add("push constant " + arg2());
                }
                else {
                    getCodeLines.add("push " + fileBase + "." + arg2());
                }
            }
            else if (commandType() == CommandType.C_LABEL) {
                getCodeLines.add(currentCommand);
            }
            else if (commandType() == CommandType.C_GOTO) {
                getCodeLines.add(currentCommand);
            }
            else if (commandType() == CommandType.C_IF) {
                getCodeLines.add(currentCommand);
            }
            else if (commandType() == CommandType.C_CALL) {

                getCodeLines.add("push retAddress");
                getCodeLines.add("call_push_segments");
                getCodeLines.add("call_command_repositions " + getFunctionName() + " " + arg2());
                getCodeLines.add("goto " + getFunctionName());
                getCodeLines.add("label retAddress");
            }
            else if (commandType() == CommandType.C_FUNCTION) {
                getCodeLines.add("label " + getFunctionName());
                int nVars = arg2();
                for (int i = 0; i < nVars; i++) {
                    getCodeLines.add("push constant 0");
                }
            }
            else if (commandType() == CommandType.C_RETURN) {
                getCodeLines.add("return");
            }
        }
    }

    public void printLines() {
        for (int i = 0; i < getCodeLines.size(); i++) {
            System.out.println(getCodeLines.get(i));
        }
    }

    public void processFile(String path) throws IOException {
        System.out.println("Parser now processing file " + path);
        file = new File(path);
        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf('.');
        fileBase = (lastDot == -1) ? fileName : fileName.substring(0, lastDot);

        //String fileName = FilenameUtils.getName(path);
        //fileBase = FilenameUtils.getBaseName(fileName);
        reader = new FileReader(file);
        br = new BufferedReader(reader);
        fillCodeLines();
    }

}
