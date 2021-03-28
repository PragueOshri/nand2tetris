import java.io.PrintWriter;
import java.util.HashMap;

public class VMWriter {
    public static enum SEGMENT{
        CONSTANT,
        ARGUMENT,
        LOCAL,
        STATIC,
        THIS,
        THAT,
        POINTER,
        TEMP
    }
    public static enum COMMAND{
        ADD,
        SUB,
        NEG,
        EQ,
        LT,
        GT,
        AND,
        OR,
        NOT
    }
    PrintWriter writer;

    public VMWriter(PrintWriter i_writer){
        this.writer = i_writer;

    }

    public void writePush(SEGMENT segment, int index){
        writer.println("push " + segment.toString().toLowerCase() + " " + index);
    }

    public void writePop(SEGMENT segment, int index) {
        writer.println("pop " + segment.toString().toLowerCase() + " " + index );
    }

    public void writeArithmetic(COMMAND command){
        writer.println(command.toString().toLowerCase());
    }

    public void writeLabel(String label){
        writer.println("label " +label);
    }

    public void writeGoto(String label){
        writer.println("goto " + label);
    }

    public void writeIf(String label){
        writer.println("if-goto " + label);
    }

    public void writeCall(String name, int nArgs){
        writer.println("call " + name + " " + nArgs);
    }

    public void writeFunction(String name, int nLocals){
        writer.println("function " + name + " " + nLocals);
    }

    public void writeReturn(){
        writer.println("return");
    }

    public void close(){
        this.writer.flush();
        this.writer.close();
    }
}
