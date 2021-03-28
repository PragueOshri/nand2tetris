import java.io.*;


public class VMTranslator {

    public static void main(String args[]) throws IOException {

        if (args.length == 1) {
            String path = args[0];
            Parser file = new Parser(args[0]);
            File f = new File(args[0]);
            String fileParent = f.getParent();
            String fileName = f.getName();
            int lastDot = fileName.lastIndexOf('.');
            String base = (lastDot == -1) ? fileName : fileName.substring(0, lastDot);
            String asmFile = fileParent + '/' + base + ".asm";
            CodeWriter createAsmFile = new CodeWriter(asmFile, path);
            createAsmFile.fillAsmFile();
        }
/*
        if (args.length == 1) {
            String fileName = args[0];
            Parser file = new Parser(fileName);
            System.out.println("create a Parse object");
            String fileP = FilenameUtils.getFullPath(fileName);
            String fileN = FilenameUtils.getName(fileName);
            String fileB = FilenameUtils.getBaseName(fileN);
            String asmFile = fileP + fileB + ".asm";
            CodeWriter createAsmFile = new CodeWriter(asmFile, fileName);
            System.out.println("create a asm file");
            createAsmFile.fillAsmFile();
        }
*/
    }
}
