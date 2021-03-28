import java.io.File;
import java.io.IOException;

public class HackAssembler {
    public static void main(String[] args) throws IOException {

        if (args.length == 1) {
            Parser file = new Parser(args[0]);
            file.firstPass();
            File f = new File(args[0]);
            String fileParent = f.getParent();
            String fileName = f.getName();
            int lastDot = fileName.lastIndexOf('.');
            String base = (lastDot == -1) ? fileName : fileName.substring(0, lastDot);
            String hackFile = fileParent + '/' + base + ".hack";
            file.createHackFile(hackFile);
            file.secondPass();
            file.printLines();
        }
        else {
            System.out.println("no valid input");
        }
/*
        if (args.length == 1) {
            String fileName = args[0];
            Parser file = new Parser(args[0]);
            file.firstPass();
            //file.printLines();
            String fileP = FilenameUtils.getFullPath(fileName);
            String fileN = FilenameUtils.getName(fileName);
            String fileB = FilenameUtils.getBaseName(fileN);
            String hackFile = fileP + fileB + ".hack";
            file.createHackFile(hackFile);
            file.secondPass();
        }
        else {
            System.out.println("no valid input");
        }
*/
    }
}
