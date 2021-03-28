import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.commons.io.FilenameUtils;


public class Parser {

    File file;
    //FileReader readFile;
    //BufferedReader br;
    String currentInstruction;
    FileWriter hackFile;
    BufferedWriter writer;
    SymbolTable labelTable;
    Code compTable;
    Code destTable;
    Code jumpTable;
    List<String> codeLines;

    public enum InstructionsTypes {
        A_INSTRUCTION,
        C_INSTRUCTION,
        L_INSTRUCTION
    }

    Parser(String path) throws IOException {
        //File f = new File(path);
        file = new File(path);
        String fileParent = file.getParent();
        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf('.');
        String fileBase = (lastDot == -1) ? fileName : fileName.substring(0, lastDot);
        String fileExt = (lastDot == -1) ? "" : fileName.substring(lastDot+1);
        //String filePath = FilenameUtils.getFullPath(path);
        //String fileName = FilenameUtils.getName(path);
        //String fileBase = FilenameUtils.getBaseName(fileName);
        //String fileExt = FilenameUtils.getExtension(fileName);
        //file = new File(path);
        //readFile = new FileReader(file);
        //br = new BufferedReader(readFile);
        labelTable = new SymbolTable();
        compTable = new Code();
        destTable = new Code();
        jumpTable = new Code();
        codeLines = new ArrayList<>();
    }

    private boolean hasMoreLines(BufferedReader br) throws IOException{
        String line = br.readLine();
        if (line != null) {
            currentInstruction = line;
            return true;
        }
        return false;
    }

    private void advance() {
        if (currentInstruction == "\n") {
            currentInstruction = currentInstruction.replace('\n',' ');
        }
        currentInstruction = currentInstruction.replaceAll("\\s","");
        if (currentInstruction.length() != 0) {
            int indexOf = currentInstruction.indexOf("//");
            if (indexOf != -1) {
                currentInstruction = currentInstruction.substring(0, indexOf);
            }
        }
    }

    private InstructionsTypes instructionType() throws IOException{
        if (currentInstruction.charAt(0) == '@') {
            return InstructionsTypes.A_INSTRUCTION;
        } else if (currentInstruction.charAt(0) == '(') {
            return InstructionsTypes.L_INSTRUCTION;
        }
        return InstructionsTypes.C_INSTRUCTION;
    }

    private String symbol() throws IOException{
        if (instructionType() == InstructionsTypes.L_INSTRUCTION) {
            Matcher m = Pattern.compile("\\((.*?)\\)").matcher(currentInstruction);
            m.find();
            return m.group(1);
        }
        if (instructionType() == InstructionsTypes.A_INSTRUCTION) {
            return currentInstruction.substring(1);
        }
        return "not L_instruction OR A_instruction";
    }

    private String dest() throws IOException{
        String dest = "null";
        if (instructionType() == InstructionsTypes.C_INSTRUCTION) {
            for (int i = 1; i < currentInstruction.length(); i++) {
                if (currentInstruction.charAt(i) == '=') {
                    dest = currentInstruction.substring(0,i);
                }
            }
            return dest;
        }
        return "Not a C_instruction";
    }

    private String comp() throws IOException {
        String comp = "";
        if (instructionType() == InstructionsTypes.C_INSTRUCTION) {
            StringTokenizer st1 = new StringTokenizer(currentInstruction, "=");
            String rightEq;
            if (st1.countTokens() == 2) {
                // read the left side of the "="
                st1.nextToken();
                // read the right side of the "="
                rightEq = st1.nextToken();
            }
            else {
                rightEq = currentInstruction;
            }
            StringTokenizer st2 = new StringTokenizer(rightEq, ";");
            if (st2.hasMoreTokens()) {
                comp = st2.nextToken();
            }
            else {
                comp = rightEq;
            }
        }
        return comp;
    }
    private String jump() throws IOException{
        String jump = "null";
        if (instructionType() == InstructionsTypes.C_INSTRUCTION) {
            for (int i = 0; i < currentInstruction.length(); i++) {
                if (currentInstruction.charAt(i) == ';') {
                    jump = currentInstruction.substring(i+1);
                }
            }
            return jump;
        }
        return "Not a C_instruction";
    }

    public void firstPass() throws IOException {
        String newSymbol;
        String var;
        int address = 16;
        int countLine = -1;
        FileReader readFile1 = new FileReader(file);
        BufferedReader br1 = new BufferedReader(readFile1);
        while (hasMoreLines(br1)) {
            advance();
            if (currentInstruction != null && currentInstruction.length() != 0) {
                if (instructionType() == InstructionsTypes.L_INSTRUCTION) {
                    newSymbol = symbol();
                    labelTable.addEntry(newSymbol, countLine + 1);
                } else {
                    countLine++;
                }
            }
        }
        FileReader readFile2 = new FileReader(file);
        BufferedReader br2 = new BufferedReader(readFile2);
        while (hasMoreLines(br2)) {
            advance();
            if (currentInstruction != null && currentInstruction.length() != 0) {
                if (instructionType() == InstructionsTypes.A_INSTRUCTION) {
                    var = symbol();
                    if ((var.charAt(0) >= 'a' && var.charAt(0) <= 'z') || var.charAt(0) >= 'A' && var.charAt(0) <= 'Z') {
                        if (!labelTable.contains(var)) {
                            labelTable.addEntry(var, address);
                            address++;
                        }
                        codeLines.add("@" + labelTable.getAddress(var));
                    } else {
                        codeLines.add(currentInstruction);
                    }
                } else if (instructionType() != InstructionsTypes.L_INSTRUCTION) {
                    codeLines.add(currentInstruction);
                }
            }
        }
    }

    private void writeAInstruction() throws IOException {
        String var = currentInstruction.substring(1);
        int address = Integer.parseInt(var);
        String addressToBinary = String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0');
        writer.write(addressToBinary);
        writer.newLine();
    }

    private void writeCInstruction() throws IOException {
        writer.write("111");
        String comp = comp();
        if (comp != null && comp.length() != 0) {
            writer.write(compTable.comp(comp));
        }
        String dest = dest();
        writer.write(destTable.dest(dest));

        String jump = jump();
        writer.write(jumpTable.jump(jump));

        writer.newLine();

    }

    public void secondPass() throws IOException{
        for (String line : codeLines) {
            currentInstruction = line;
            if (instructionType() == InstructionsTypes.A_INSTRUCTION) {
                writeAInstruction();
            }
            else if (instructionType() == InstructionsTypes.C_INSTRUCTION) {
                writeCInstruction();
            }
        }
        writer.close();
    }

    public void createHackFile(String filename) throws IOException {
        hackFile = new FileWriter(filename);
        writer = new BufferedWriter(hackFile);
    }

    public void printLines() {
        for (String line : codeLines) {
            System.out.println(line);
        }
    }
}