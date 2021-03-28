import java.io.*;

public class JackAnalyzer {
    CompilationEngine engine;
    PrintWriter writer;

    public static void main(String[] args) throws Exception {
        JackAnalyzer analyzer = new JackAnalyzer();
        analyzer.Analyze(args[0]);
    }


    public void Analyze(String i_InputPath) throws Exception{
        File f = new File(i_InputPath);
        if (f.isDirectory()){
            AnalyzeDirectory(f);
        }
        else{
            AnalyzeFile(f);
        }
    }

    public void AnalyzeFile(File i_InputFile) throws IOException{
        initializeWriter(i_InputFile);
        engine = new CompilationEngine(writer, new BufferedReader(new FileReader(i_InputFile)));
        engine.compileClass();
        writer.close();
    }

    private void initializeWriter(File i_InputFile) throws IOException{
        int trimPoint = i_InputFile.getAbsolutePath().lastIndexOf(".");
        String writePath = i_InputFile.getAbsolutePath().substring(0, trimPoint) + ".xml";
        writer = new PrintWriter(new FileWriter(writePath));
    }

    private void AnalyzeDirectory(File i_InputDirectory) throws IOException{
        for (File f : i_InputDirectory.listFiles()){
            if (f.getName().endsWith(".jack")){
                AnalyzeFile(f);
            }
        }
    }

}
