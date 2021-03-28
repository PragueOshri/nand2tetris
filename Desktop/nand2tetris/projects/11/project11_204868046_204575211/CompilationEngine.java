import java.io.*;

public class CompilationEngine {
    VMWriter writer;
    JackTokenizer tokenizer;
    SymbolTable symbolTable;
    String curClassName;
    String curSubroutineName;
    int loopLabel;
    int ifLabel;
    int numExp;

    public CompilationEngine(PrintWriter i_writer, BufferedReader i_SourceCodeReader) throws IOException {
        tokenizer = new JackTokenizer(i_SourceCodeReader);
        tokenizer.advance();
        writer = new VMWriter(i_writer);
        symbolTable = new SymbolTable();
        curClassName = "";
        curSubroutineName = "";
        loopLabel = 0;
        ifLabel = 0;
        numExp = 0;
    }

    private void validateTokenType(eTokenType type){
        eTokenType currentType = tokenizer.tokenType();
        if (currentType != type){
            System.out.println(tokenizer.currentTokenLine + " ---> " + tokenizer.currentToken);
            System.err.println("Compilation error!");
            System.exit(-1);
        }
    }

    public void compileClass() throws IOException{
        process(eTokenType.T_KEYWORD); // class
        curClassName = tokenizer.identifier(); // save the class name
        process(eTokenType.T_IDENTIFIER);
        process(eTokenType.T_SYMBOL); // supposed to be '{'
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD){
            while(tokenizer.keyWord() == eKeyword.STATIC || tokenizer.keyWord() == eKeyword.FIELD){
                compileClassVarDec();
            }
        }
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD){
            while(tokenizer.keyWord() == eKeyword.CONSTRUCTOR || tokenizer.keyWord() == eKeyword.FUNCTION || tokenizer.keyWord() == eKeyword.METHOD){
                compileSubroutine();
            }
        }
        process(eTokenType.T_SYMBOL); // supposed to be '}'
    }

    private void compileClassVarDec() throws IOException{
        Symbol.KIND kind;
        String type = "";
        String varName;
        if(tokenizer.keyWord() == eKeyword.STATIC){
            kind = Symbol.KIND.STATIC;
        }
        else {
            kind = Symbol.KIND.FIELD;
        }
        process(eTokenType.T_KEYWORD);
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD &&
                (tokenizer.keyWord() == eKeyword.BOOLEAN || tokenizer.keyWord() == eKeyword.INT || tokenizer.keyWord() == eKeyword.CHAR)){
            type = tokenizer.keyWord().toString().toLowerCase();
            process(eTokenType.T_KEYWORD);
        }
        else if(tokenizer.tokenType() == eTokenType.T_IDENTIFIER){
            type = tokenizer.identifier();
            process(eTokenType.T_IDENTIFIER);
        }
        varName = tokenizer.identifier();
        process(eTokenType.T_IDENTIFIER);
        symbolTable.define(varName, type, kind);
        while(tokenizer.symbol() == ','){
            process(eTokenType.T_SYMBOL);
            varName = tokenizer.identifier();
            symbolTable.define(varName, type, kind);
            process(eTokenType.T_IDENTIFIER);
        }
        process(eTokenType.T_SYMBOL); // supposed to be ';'
    }

    private void compileSubroutine() throws IOException{
        symbolTable.startSubroutine();
        boolean isMethod = tokenizer.keyWord() == eKeyword.METHOD;
        boolean isConstructor = tokenizer.keyWord() == eKeyword.CONSTRUCTOR;
        process(eTokenType.T_KEYWORD);
        if(isMethod){
            symbolTable.define("this", curClassName, Symbol.KIND.ARG);
        }
        if(tokenizer.tokenType() == eTokenType.T_IDENTIFIER){ // if return an object
            process(eTokenType.T_IDENTIFIER);
        }
        else if(tokenizer.tokenType() == eTokenType.T_KEYWORD && (tokenizer.keyWord() == eKeyword.BOOLEAN
        || tokenizer.keyWord() == eKeyword.CHAR || tokenizer.keyWord() == eKeyword.INT || tokenizer.keyWord() == eKeyword.VOID)) {
            process(eTokenType.T_KEYWORD);
        }
        curSubroutineName = tokenizer.identifier(); // save the subroutine name
        process(eTokenType.T_IDENTIFIER);
        process(eTokenType.T_SYMBOL); // supposed to be '('
        compileParameterList();
        process(eTokenType.T_SYMBOL); // supposed to be ')'
        process(eTokenType.T_SYMBOL); // supposed to be '{'
        while(tokenizer.tokenType() == eTokenType.T_KEYWORD && tokenizer.keyWord() == eKeyword.VAR){
            compileVarDec();
        }
        writer.writeFunction(curClassName + "." + curSubroutineName, symbolTable.varCount(Symbol.KIND.VAR));
        curSubroutineName = "";
        if(isConstructor){
            int numFields = symbolTable.varCount(Symbol.KIND.FIELD);
            writer.writePush(VMWriter.SEGMENT.CONSTANT, numFields);
            writer.writeCall("Memory.alloc", 1);
            writer.writePop(VMWriter.SEGMENT.POINTER, 0);
        }
        else if(isMethod){
            writer.writePush(VMWriter.SEGMENT.ARGUMENT, 0);
            writer.writePop(VMWriter.SEGMENT.POINTER, 0);
        }
        compileStatements();
        process(eTokenType.T_SYMBOL); // supposed to be '}'
        loopLabel = 0;
        ifLabel = 0;
    }

    private void compileParameterList() throws IOException{
        String type = "";
        String name;
        while(tokenizer.tokenType() == eTokenType.T_IDENTIFIER || (tokenizer.tokenType() == eTokenType.T_KEYWORD
                && (tokenizer.keyWord() == eKeyword.CHAR || tokenizer.keyWord() == eKeyword.INT || tokenizer.keyWord() == eKeyword.BOOLEAN))){
            if(tokenizer.tokenType() == eTokenType.T_IDENTIFIER){
                type = tokenizer.identifier();
                process(eTokenType.T_IDENTIFIER);
            }
            else if(tokenizer.tokenType() == eTokenType.T_KEYWORD){
                type = tokenizer.keyWord().toString().toLowerCase();
                process(eTokenType.T_KEYWORD);
            }
            name = tokenizer.identifier();
            process(eTokenType.T_IDENTIFIER);
            symbolTable.define(name, type, Symbol.KIND.ARG);
            if(tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() == ','){
                process(eTokenType.T_SYMBOL);
            }
            else{
                break;
            }
        }
    }

    private void compileVarDec() throws IOException{
        String type = "";
        String varName;
        process(eTokenType.T_KEYWORD); // var
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD){
            type = tokenizer.keyWord().toString().toLowerCase();
            process(eTokenType.T_KEYWORD);
        }
        else{
            type = tokenizer.identifier();
            process(eTokenType.T_IDENTIFIER);
        }
        varName = tokenizer.identifier();
        process(eTokenType.T_IDENTIFIER);
        symbolTable.define(varName, type, Symbol.KIND.VAR);
        while(tokenizer.symbol() == ','){
            process(eTokenType.T_SYMBOL);
            varName = tokenizer.identifier();
            process(eTokenType.T_IDENTIFIER);
            symbolTable.define(varName, type, Symbol.KIND.VAR);
        }
        process(eTokenType.T_SYMBOL); // supposed to be ';'
    }

    private void compileStatements() throws IOException{
        while (tokenizer.tokenType() == eTokenType.T_KEYWORD){
            switch(tokenizer.keyWord()){
                case LET:
                    compileLet();
                    break;
                case IF:
                    compileIf();
                    break;
                case WHILE:
                    compileWhile();
                    break;
                case DO:
                    compileDo();
                    break;
                case RETURN:
                    compileReturn();
                    break;
            }
        }
    }

    private VMWriter.SEGMENT getSegment(Symbol.KIND kind){
        VMWriter.SEGMENT segment = null;
        switch(kind){
            case ARG:
                segment = VMWriter.SEGMENT.ARGUMENT;
                break;
            case STATIC:
                segment = VMWriter.SEGMENT.STATIC;
                break;
            case FIELD:
                segment = VMWriter.SEGMENT.THIS;
                break;
            case VAR:
                segment = VMWriter.SEGMENT.LOCAL;
                break;
        }
        return segment;
    }

    private void compileLet() throws IOException{
        boolean isArray = false;
        process(eTokenType.T_KEYWORD); // let
        String varName = tokenizer.identifier();
        process(eTokenType.T_IDENTIFIER);
        if(tokenizer.symbol() == '['){
            isArray = true;
            process(eTokenType.T_SYMBOL);
            compileExpression();
            Symbol.KIND kind = symbolTable.kindOf(varName);
            VMWriter.SEGMENT segment = getSegment(kind);
            writer.writePush(segment, symbolTable.indexOf(varName));
            writer.writeArithmetic(VMWriter.COMMAND.ADD);
            process(eTokenType.T_SYMBOL); // supposed to be ']'
        }
        process(eTokenType.T_SYMBOL); // supposed to be '='
        compileExpression();
        if(isArray){
            writer.writePop(VMWriter.SEGMENT.TEMP, 0);
            writer.writePop(VMWriter.SEGMENT.POINTER, 1);
            writer.writePush(VMWriter.SEGMENT.TEMP, 0);
            writer.writePop(VMWriter.SEGMENT.THAT, 0);
        }
        else{
            Symbol.KIND kind = symbolTable.kindOf(varName);
            writer.writePop(getSegment(kind), symbolTable.indexOf(varName));
        }
        process(eTokenType.T_SYMBOL); // supposed to be ';'
    }

    private void compileIf() throws IOException{
        int numLabel = ifLabel++;
        process(eTokenType.T_KEYWORD);
        process(eTokenType.T_SYMBOL);
        compileExpression();
        writer.writeIf("IfT" + numLabel);
        writer.writeGoto("IfF" + numLabel);
        process(eTokenType.T_SYMBOL);
        process(eTokenType.T_SYMBOL);
        writer.writeLabel("IfT" + numLabel);
        compileStatements();
        process(eTokenType.T_SYMBOL);
        boolean haveElse = false;
        if(tokenizer.tokenType() == eTokenType.T_KEYWORD && tokenizer.keyWord() == eKeyword.ELSE){
            haveElse = true;
            writer.writeGoto("IfEnd" + numLabel);
        }
        writer.writeLabel("IfF" + numLabel);
        if(haveElse){
            process(eTokenType.T_KEYWORD); // 'else'
            process(eTokenType.T_SYMBOL);
            compileStatements();
            process(eTokenType.T_SYMBOL);
        }
        writer.writeLabel("IfEnd" + numLabel);
    }

    private void compileWhile() throws IOException{
        int numLabel = loopLabel++;
        writer.writeLabel("WhileLabel" + numLabel);
        process(eTokenType.T_KEYWORD);
        process(eTokenType.T_SYMBOL);
        compileExpression();
        writer.writeArithmetic(VMWriter.COMMAND.NOT);
        writer.writeIf("WhileEnd" + numLabel);
        process(eTokenType.T_SYMBOL);
        process(eTokenType.T_SYMBOL); // supposed to be '{'
        compileStatements();
        process(eTokenType.T_SYMBOL); // supposed to be '}'
        writer.writeGoto("WhileLabel" + numLabel);
        writer.writeLabel("WhileEnd" + numLabel);
    }

    private void compileDo() throws IOException{
        process(eTokenType.T_KEYWORD);
        compileSubRoutineCall();
        process(eTokenType.T_SYMBOL);
        writer.writePop(VMWriter.SEGMENT.TEMP, 0);
    }

    private void compileReturn() throws IOException{
        boolean isVoid = true;
        process(eTokenType.T_KEYWORD);
        while(tokenizer.tokenType() != eTokenType.T_SYMBOL || (tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() != ';')){
            isVoid = false;
            compileExpression();
        }
        process(eTokenType.T_SYMBOL);
        if(isVoid){
            writer.writePush(VMWriter.SEGMENT.CONSTANT, 0);
            writer.writeReturn();
        } else {
            writer.writeReturn();
        }
    }

    private void compileExpression() throws IOException{

        compileTerm();
        while(tokenizer.op()){
            char op = tokenizer.symbol();
            process(eTokenType.T_SYMBOL);
            compileTerm();
            switch (op) {
                case '+':
                    writer.writeArithmetic(VMWriter.COMMAND.ADD);
                    break;
                case '-':
                    writer.writeArithmetic(VMWriter.COMMAND.SUB);
                    break;
                case '&':
                    writer.writeArithmetic(VMWriter.COMMAND.AND);
                    break;
                case '|':
                    writer.writeArithmetic(VMWriter.COMMAND.OR);
                    break;
                case '<':
                    writer.writeArithmetic(VMWriter.COMMAND.LT);
                    break;
                case '>':
                    writer.writeArithmetic(VMWriter.COMMAND.GT);
                    break;
                case '=':
                    writer.writeArithmetic(VMWriter.COMMAND.EQ);
                    break;
                case '*':
                    writer.writeCall("Math.multiply", 2);
                    break;
                case '/':
                    writer.writeCall("Math.divide", 2);
                    break;
            }
        }
    }

    private void compileTerm() throws IOException {
        if(tokenizer.tokenType() == eTokenType.T_INTEGERCONSTANT){
            writer.writePush(VMWriter.SEGMENT.CONSTANT, Integer.parseInt(tokenizer.currentToken));
            process(eTokenType.T_INTEGERCONSTANT);
        }
        else if(tokenizer.tokenType() == eTokenType.T_STRINGCONSTANT){
            String strCont = tokenizer.stringVal();
            int strContLength = strCont.length();
            writer.writePush(VMWriter.SEGMENT.CONSTANT, strContLength);
            writer.writeCall("String.new", 1);
            for (int i = 0; i < strContLength; i++) {
                writer.writePush(VMWriter.SEGMENT.CONSTANT, (int)strCont.charAt(i));
                writer.writeCall("String.appendChar", 2);
            }
            process(eTokenType.T_STRINGCONSTANT);
        }
        else if(tokenizer.tokenType() == eTokenType.T_KEYWORD && (tokenizer.keyWord() == eKeyword.THIS
                || tokenizer.keyWord() == eKeyword.TRUE || tokenizer.keyWord() == eKeyword.FALSE
                || tokenizer.keyWord() == eKeyword.NULL)){
            if(tokenizer.keyWord() == eKeyword.THIS){
                writer.writePush(VMWriter.SEGMENT.POINTER, 0);
            }
            else if(tokenizer.keyWord() == eKeyword.TRUE){
                writer.writePush(VMWriter.SEGMENT.CONSTANT, 0);
                writer.writeArithmetic(VMWriter.COMMAND.NOT);
            }
            else{
                writer.writePush(VMWriter.SEGMENT.CONSTANT, 0);
            }
            process(eTokenType.T_KEYWORD);
        }
        else if(tokenizer.unaryOp()){
            char op = tokenizer.symbol();
            process(eTokenType.T_SYMBOL);
            compileTerm();
            switch (op) {
                case '-':
                    writer.writeArithmetic(VMWriter.COMMAND.NEG);
                    break;
                case '~':
                    writer.writeArithmetic(VMWriter.COMMAND.NOT);
                    break;
            }
        }
        else if(tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() == '('){
            process(eTokenType.T_SYMBOL);
            compileExpression();
            process(eTokenType.T_SYMBOL);
        }
        else if(tokenizer.tokenType() == eTokenType.T_IDENTIFIER){
            process(eTokenType.T_IDENTIFIER);
            char symbol = tokenizer.symbol();
            tokenizer.goBack();
            String name = tokenizer.identifier();
            if(symbol == '['){
                String varName = tokenizer.identifier();
                process(eTokenType.T_IDENTIFIER);
                process(eTokenType.T_SYMBOL); // supposed to be '['
                compileExpression();
                Symbol.KIND kind = symbolTable.kindOf(varName);
                writer.writePush(getSegment(kind), symbolTable.indexOf(varName));
                writer.writeArithmetic(VMWriter.COMMAND.ADD);
                process(eTokenType.T_SYMBOL); // supposed to be ']'
                writer.writePop(VMWriter.SEGMENT.POINTER, 1);
                writer.writePush(VMWriter.SEGMENT.THAT, 0);
            }
            else if(symbol == '.' || symbol == '('){
                //process(eTokenType.T_SYMBOL);
                compileSubRoutineCall();
            }
            else{
                Symbol.KIND kind = symbolTable.kindOf(tokenizer.identifier());
                writer.writePush(getSegment(kind), symbolTable.indexOf(tokenizer.identifier()));
                process(eTokenType.T_IDENTIFIER);
            }
        }
    }

    private void compileSubRoutineCall() throws IOException{
        boolean isSubroutine = false;
        String name = tokenizer.identifier();
        process(eTokenType.T_IDENTIFIER);
        if (tokenizer.symbol() == '('){
            isSubroutine = true;
        }
        else if(tokenizer.symbol() == '.') {
            isSubroutine = false;
        }
        process(eTokenType.T_SYMBOL);
        if(isSubroutine){
            writer.writePush(VMWriter.SEGMENT.POINTER, 0);
            compileExpressionList();
            writer.writeCall(curClassName + "." + name, numExp+1);
            numExp = 0;
        }else{
            boolean isMethodCall = false;
            if(Character.isUpperCase(name.charAt(0))){
                name += "." + tokenizer.identifier();
            }
            else{
                isMethodCall = true;
                Symbol.KIND kind = symbolTable.kindOf(name);
                writer.writePush(getSegment(kind), symbolTable.indexOf(name));
                name = symbolTable.typeOf(name) + "." + tokenizer.identifier();
            }
            process(eTokenType.T_IDENTIFIER);
            process(eTokenType.T_SYMBOL); // supposed to be '('
            compileExpressionList();
            if(isMethodCall){
                numExp += 1;
            }
            writer.writeCall(name, numExp);
            numExp = 0;
        }
        process(eTokenType.T_SYMBOL); // supposed to be ')'
    }

    private void compileExpressionList() throws IOException{
        while(tokenizer.tokenType() == eTokenType.T_IDENTIFIER || tokenizer.tokenType() == eTokenType.T_INTEGERCONSTANT
                || tokenizer.tokenType() == eTokenType.T_STRINGCONSTANT || (tokenizer.tokenType() == eTokenType.T_KEYWORD &&
                (tokenizer.keyWord() == eKeyword.TRUE || tokenizer.keyWord() == eKeyword.FALSE
                        || tokenizer.keyWord() == eKeyword.NULL || tokenizer.keyWord() == eKeyword.THIS))
                || (tokenizer.tokenType() == eTokenType.T_SYMBOL &&
                (tokenizer.symbol() == '(' || tokenizer.symbol() == '-' || tokenizer.symbol() == '~'))){
            numExp++;
            compileExpression();
            if(tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() == ','){
                process(eTokenType.T_SYMBOL);
            }
            else{
                break;
            }
        }
    }

    private void process(eTokenType tokenType) throws IOException{
        validateTokenType(tokenType);
        if (tokenizer.hasMoreTokens()){
            tokenizer.advance();
        }
    }
}
