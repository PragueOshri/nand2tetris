import java.io.*;

public class CompilationEngine {
    PrintWriter writer;
    JackTokenizer tokenizer;
    String spacePrefix;
    

    public CompilationEngine(PrintWriter i_writer, BufferedReader i_SourceCodeReader) throws IOException {
        tokenizer = new JackTokenizer(i_SourceCodeReader);
        tokenizer.advance();
        writer = i_writer;
        spacePrefix = "";
    }

    private void validateTokenType(eTokenType type){
        eTokenType currentType = tokenizer.tokenType();
        if (currentType != type){
            System.err.println("Compilation error!");
            System.exit(-1);
        }
    }

    public void compileClass() throws IOException{
        openComponent("class");
        process(eTokenType.T_KEYWORD);
        process(eTokenType.T_IDENTIFIER);
        process(eTokenType.T_SYMBOL);
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD){
            while(tokenizer.keyWord() == eKeyword.STATIC || tokenizer.keyWord() == eKeyword.FIELD){
                compileClassVarDec();
            }
        }
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD){
            while(tokenizer.keyWord() == eKeyword.CONSTRUCTOR || tokenizer.keyWord() == eKeyword.FUNCTION || tokenizer.keyWord() == eKeyword.METHOD){
                {
                    compileSubroutine();
                }
            }
        }
        process(eTokenType.T_SYMBOL);
        closeComponent("class");
    }

    private void compileClassVarDec() throws IOException{
        openComponent("classVarDec");
        process(eTokenType.T_KEYWORD);
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD){
            process(eTokenType.T_KEYWORD);
        }
        else{
            process(eTokenType.T_IDENTIFIER);
        }
        process(eTokenType.T_IDENTIFIER);
        while(tokenizer.symbol() == ','){
            process(eTokenType.T_SYMBOL);
            process(eTokenType.T_IDENTIFIER);
        }
        process(eTokenType.T_SYMBOL);
        closeComponent("classVarDec");
    }

    private void compileSubroutine() throws IOException{
        openComponent("subroutineDec");
        process(eTokenType.T_KEYWORD);
        if(tokenizer.tokenType() == eTokenType.T_KEYWORD){
            process(eTokenType.T_KEYWORD);
        }
        else{
            process(eTokenType.T_IDENTIFIER);
        }
        process(eTokenType.T_IDENTIFIER);
        process(eTokenType.T_SYMBOL);
        compileParameterList();
        process(eTokenType.T_SYMBOL);
        compileSubroutineBody();
        closeComponent("subroutineDec");
    }

    private void compileParameterList() throws IOException{
        openComponent("parameterList");
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD | tokenizer.tokenType() == eTokenType.T_IDENTIFIER){
            process(tokenizer.tokenType());
            process(eTokenType.T_IDENTIFIER);
            while(tokenizer.symbol() == ','){
                process(eTokenType.T_SYMBOL);
                process(tokenizer.tokenType());
                process(eTokenType.T_IDENTIFIER);
            }
        }
        closeComponent("parameterList");
    }

    private void compileSubroutineBody() throws IOException{
        openComponent("subroutineBody");
        process(eTokenType.T_SYMBOL);
        while (tokenizer.tokenType() == eTokenType.T_KEYWORD && tokenizer.keyWord() == eKeyword.VAR){
            compileVarDec();
        }
        compileStatements();
        process(eTokenType.T_SYMBOL);
        closeComponent("subroutineBody");
    }

    private void compileVarDec() throws IOException{
        openComponent("varDec");
        process(eTokenType.T_KEYWORD);
        if (tokenizer.tokenType() == eTokenType.T_KEYWORD){
            process(eTokenType.T_KEYWORD);
        }
        else{
            process(eTokenType.T_IDENTIFIER);
        }
        process(eTokenType.T_IDENTIFIER);
        while(tokenizer.symbol() == ','){
            process(eTokenType.T_SYMBOL);
            process(eTokenType.T_IDENTIFIER);
        }
        process(eTokenType.T_SYMBOL);
        closeComponent("varDec");
    }

    private void compileStatements() throws IOException{
        openComponent("statements");
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
        closeComponent("statements");
    }

    private void compileLet() throws IOException{
        openComponent("letStatement");
        process(eTokenType.T_KEYWORD);
        process(eTokenType.T_IDENTIFIER);
        if(tokenizer.symbol() == '['){
            process(eTokenType.T_SYMBOL);
            compileExpression();
            process(eTokenType.T_SYMBOL);
        }
        process(eTokenType.T_SYMBOL);
        compileExpression();
        process(eTokenType.T_SYMBOL);
        closeComponent("letStatement");
    }

    private void compileIf() throws IOException{
        openComponent("ifStatement");
        process(eTokenType.T_KEYWORD);
        process(eTokenType.T_SYMBOL);
        compileExpression();
        process(eTokenType.T_SYMBOL);
        process(eTokenType.T_SYMBOL);
        compileStatements();
        process(eTokenType.T_SYMBOL);
        if(tokenizer.tokenType() == eTokenType.T_KEYWORD && tokenizer.keyWord() == eKeyword.ELSE){
            process(eTokenType.T_KEYWORD);
            process(eTokenType.T_SYMBOL);
            compileStatements();
            process(eTokenType.T_SYMBOL);
        }
        closeComponent("ifStatement");
    }

    private void compileWhile() throws IOException{
        openComponent("whileStatement");
        process(eTokenType.T_KEYWORD);
        process(eTokenType.T_SYMBOL);
        compileExpression();
        process(eTokenType.T_SYMBOL);
        if(tokenizer.tokenType() == eTokenType.T_KEYWORD){
            compileStatements();
        }
        else{
            process(eTokenType.T_SYMBOL);
            compileStatements();
            process(eTokenType.T_SYMBOL);
        }
        closeComponent("whileStatement");
    }

    private void compileDo() throws IOException{
        openComponent("doStatement");
        process(eTokenType.T_KEYWORD);
        compileSubRoutineCall();
        process(eTokenType.T_SYMBOL);
        closeComponent("doStatement");
    }

    private void compileReturn() throws IOException{
        openComponent("returnStatement");
        process(eTokenType.T_KEYWORD);
        if(tokenizer.tokenType() != eTokenType.T_SYMBOL){
            compileExpression();
        }
        process(eTokenType.T_SYMBOL);
        closeComponent("returnStatement");
    }

    private void compileExpression() throws IOException{
        openComponent("expression");
        compileTerm();
        while(tokenizer.op()){
            process(eTokenType.T_SYMBOL);
            compileTerm();
        }
        closeComponent("expression");
    }

    private void compileTerm() throws IOException {
        openComponent("term");
        if(tokenizer.tokenType() == eTokenType.T_INTEGERCONSTANT){
            process(eTokenType.T_INTEGERCONSTANT);
        }
        else if(tokenizer.tokenType() == eTokenType.T_STRINGCONSTANT){
            process(eTokenType.T_STRINGCONSTANT);
        }
        else if(tokenizer.tokenType() == eTokenType.T_KEYWORD && (tokenizer.keyWord() == eKeyword.THIS
                || tokenizer.keyWord() == eKeyword.TRUE || tokenizer.keyWord() == eKeyword.FALSE
                || tokenizer.keyWord() == eKeyword.NULL)){
            process(eTokenType.T_KEYWORD);
        }
        else if(tokenizer.tokenType() == eTokenType.T_IDENTIFIER){
            process(eTokenType.T_IDENTIFIER);
            if(tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() == '['){
                process(eTokenType.T_SYMBOL);
                compileExpression();
                process(eTokenType.T_SYMBOL);
            }
            if(tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() == '.'){
                process(eTokenType.T_SYMBOL);
                compileSubRoutineCall();
            }
        }
        else if(tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() == '('){
            process(eTokenType.T_SYMBOL);
            compileExpression();
            process(eTokenType.T_SYMBOL);
        }
        else if(tokenizer.unaryOp()){
            process(eTokenType.T_SYMBOL);
            compileTerm();
        }
        else {
            compileSubRoutineCall();
        }
        closeComponent("term");
    }

    private void compileSubRoutineCall() throws IOException{
        process(eTokenType.T_IDENTIFIER);
        if (tokenizer.symbol() == '('){
            process(eTokenType.T_SYMBOL);
            compileExpressionList();
            process(eTokenType.T_SYMBOL);
        }
        else{
            process(eTokenType.T_SYMBOL);
            process(eTokenType.T_IDENTIFIER);
            process(eTokenType.T_SYMBOL);
            compileExpressionList();
            process(eTokenType.T_SYMBOL);
        }
    }

    private void compileExpressionList() throws IOException{
        openComponent("expressionList");
        if(tokenizer.tokenType() == eTokenType.T_IDENTIFIER || tokenizer.tokenType() == eTokenType.T_INTEGERCONSTANT
                || (tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() == '(')
                || tokenizer.tokenType() == eTokenType.T_KEYWORD || tokenizer.tokenType() == eTokenType.T_STRINGCONSTANT){
            compileExpression();
        }
        while(tokenizer.tokenType() == eTokenType.T_SYMBOL && tokenizer.symbol() == ','){
            process(eTokenType.T_SYMBOL);
            compileExpression();
        }
        closeComponent("expressionList");
    }

    private void process(eTokenType tokenType) throws IOException{
        validateTokenType(tokenType);
        if(tokenType == eTokenType.T_SYMBOL){
            if(tokenizer.symbol() == '&') { tokenizer.currentToken = "&amp;"; }
            if(tokenizer.symbol() == '<') { tokenizer.currentToken = "&lt;"; }
            if(tokenizer.symbol() == '>') { tokenizer.currentToken = "&gt;"; }
        }
        if(tokenType == eTokenType.T_STRINGCONSTANT){
            tokenizer.currentToken = tokenizer.stringVal();
        }

        String typeToPrint;
        if (tokenType == eTokenType.T_STRINGCONSTANT){
            typeToPrint = "stringConstant";
        }
        else if (tokenType == eTokenType.T_INTEGERCONSTANT){
            typeToPrint = "integerConstant";
        }
        else{
            typeToPrint = tokenType.toString().substring(2).toLowerCase();
        }

        writer.println(spacePrefix + "<" + typeToPrint + "> " + tokenizer.currentToken + " </" + typeToPrint + ">");
        if (tokenizer.hasMoreTokens()){
            tokenizer.advance();
        }
    }

    private void openComponent(String i_ToPrint){
        writer.println(spacePrefix + "<" + i_ToPrint + ">");
        spacePrefix += "\t";
    }

    private void closeComponent(String i_ToPrint){
        spacePrefix = spacePrefix.substring(0, spacePrefix.length() - 1);
        writer.println(spacePrefix + "</" + i_ToPrint + ">");
    }
}
