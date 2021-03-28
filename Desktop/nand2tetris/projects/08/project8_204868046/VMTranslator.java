
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//import org.apache.commons.io.FilenameUtils;


public class VMTranslator {

    public static void main(String args[]) throws IOException {
        if (args.length == 1) {
            Parser parser = new Parser();
            String fileName = args[0];
            File f = new File(fileName);
            if (f.isFile()) {
                parser.processFile(fileName);
                String fileParent = f.getParent();
                String fileN = f.getName();
                int lastDot = fileN.lastIndexOf('.');
                String base = (lastDot == -1) ? fileN : fileN.substring(0, lastDot);
                String asmFile = fileParent + '/' + base + ".asm";
                //String fileP = FilenameUtils.getFullPath(fileName);
                //String fileN = FilenameUtils.getName(fileName);
                //String fileB = FilenameUtils.getBaseName(fileN);
                //String asmFile = fileP + fileB + ".asm";
                CodeWriter createAsmFile = new CodeWriter(parser, asmFile);
                createAsmFile.fillAsmFile();
            }
            else if (f.isDirectory()) {
                parser.isDir = true;
                try (Stream<Path> walk = Files.walk(Paths.get(fileName), 2)) {
                    List<String> result = walk.map(x -> x.toString()).filter(y -> y.endsWith(".vm")).collect(Collectors.toList());
                    for (String vmFileInDir : result) {
                        parser.processFile(vmFileInDir);
                    }
                    // parser.printLines();
                    //String fileN = FilenameUtils.getName(fileName);
                    String fileN = f.getName();
                    String asmFile = fileName + "/" + fileN + ".asm";
                    CodeWriter createAsmFile = new CodeWriter(parser, asmFile);
                    createAsmFile.fillAsmFile();
                }

            }
        }
    }
}
