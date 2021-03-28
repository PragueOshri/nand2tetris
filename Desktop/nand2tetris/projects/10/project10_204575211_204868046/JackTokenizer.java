import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JackTokenizer {
    BufferedReader sourceCodeReader;
    Map<String, eKeyword> keywordDictionary;
    HashSet<String> symbolSet;
    String currentTokenLine;
    String nextTokenLine;
    String currentToken;
    int nextTokenStartIndex;

    public JackTokenizer(BufferedReader i_SourceCodeReader) throws IOException{
        sourceCodeReader = i_SourceCodeReader;
        initializeKeywordDictionary();
        initializeSymbolSet();
        currentTokenLine = null;
        nextTokenLine = null;
        currentToken = null;
        nextTokenStartIndex = 0;
        advanceLine();
    }

    private void initializeKeywordDictionary(){
        keywordDictionary = new HashMap<>();
        keywordDictionary.put("class", eKeyword.CLASS);
        keywordDictionary.put("constructor", eKeyword.CONSTRUCTOR);
        keywordDictionary.put("function", eKeyword.FUNCTION);
        keywordDictionary.put("method", eKeyword.METHOD);
        keywordDictionary.put("field", eKeyword.FIELD);
        keywordDictionary.put("static", eKeyword.STATIC);
        keywordDictionary.put("var", eKeyword.VAR);
        keywordDictionary.put("int", eKeyword.INT);
        keywordDictionary.put("char", eKeyword.CHAR);
        keywordDictionary.put("boolean", eKeyword.BOOLEAN);
        keywordDictionary.put("void", eKeyword.VOID);
        keywordDictionary.put("true", eKeyword.TRUE);
        keywordDictionary.put("false", eKeyword.FALSE);
        keywordDictionary.put("null", eKeyword.NULL);
        keywordDictionary.put("this", eKeyword.THIS);
        keywordDictionary.put("let", eKeyword.LET);
        keywordDictionary.put("do", eKeyword.DO);
        keywordDictionary.put("if", eKeyword.IF);
        keywordDictionary.put("else", eKeyword.ELSE);
        keywordDictionary.put("while", eKeyword.WHILE);
        keywordDictionary.put("return", eKeyword.RETURN);
    }

    private void initializeSymbolSet(){
        symbolSet = new HashSet<>();
        symbolSet.add("{");
        symbolSet.add("}");
        symbolSet.add("(");
        symbolSet.add(")");
        symbolSet.add("[");
        symbolSet.add("]");
        symbolSet.add(".");
        symbolSet.add(",");
        symbolSet.add(";");
        symbolSet.add("+");
        symbolSet.add("-");
        symbolSet.add("*");
        symbolSet.add("/");
        symbolSet.add("&");
        symbolSet.add("|");
        symbolSet.add("<");
        symbolSet.add(">");
        symbolSet.add("=");
        symbolSet.add("~");
    }

    public boolean hasMoreTokens(){
        if (hasMoreTokenLines()){
            return true;
        }
        else{
            return nextTokenStartIndex < currentTokenLine.length();
        }
    }

    public void advance() throws IOException{
        if (currentTokenLine == null || nextTokenStartIndex >= currentTokenLine.length()){
            advanceLine();
            nextTokenStartIndex = 0;
        }
        advanceToNextTokenInCurLine();
    }

    public eTokenType tokenType(){
        if (keywordDictionary.containsKey(currentToken)){
            return eTokenType.T_KEYWORD;
        }
        else if (symbolSet.contains(currentToken)){
            return eTokenType.T_SYMBOL;
        }
        else{
            try{
                Integer.parseInt(currentToken);
                return eTokenType.T_INTEGERCONSTANT;
            }
            catch (NumberFormatException e){

            }
        }
        if (currentToken.startsWith("\"") && currentToken.endsWith("\"")){
            return eTokenType.T_STRINGCONSTANT;
        }
        return eTokenType.T_IDENTIFIER;
    }

    public char symbol(){
        return currentToken.charAt(0);
    }

    public boolean op(){
        boolean isOp = false;
        if (symbolSet.contains(currentToken)){
            isOp |= currentToken.charAt(0) == '+';
            isOp |= currentToken.charAt(0) == '-';
            isOp |= currentToken.charAt(0) == '*';
            isOp |= currentToken.charAt(0) == '/';
            isOp |= currentToken.charAt(0) == '&';
            isOp |= currentToken.charAt(0) == '|';
            isOp |= currentToken.charAt(0) == '<';
            isOp |= currentToken.charAt(0) == '>';
            isOp |= currentToken.charAt(0) == '=';
        }

        return isOp;
    }

    public boolean unaryOp(){
        boolean isUnaryOp = false;
        if(symbolSet.contains(currentToken)){
            isUnaryOp |= currentToken.charAt(0) == '-';
            isUnaryOp |= currentToken.charAt(0) == '~';
        }
        return isUnaryOp;
    }

    public String identifier(){
        return currentToken;
    }

    public String stringVal(){
        return currentToken.substring(1, currentToken.length() - 1);
    }

    public eKeyword keyWord(){
        return keywordDictionary.get(currentToken);
    }

    private void advanceLine() throws IOException{
        this.currentTokenLine = nextTokenLine;
        this.nextTokenLine = retrieveNextTokenLine();
    }

    private boolean hasMoreTokenLines(){
        return this.nextTokenLine != null;
    }

    private void advanceToNextTokenInCurLine(){
        int nextTokenEndIndex = nextTokenStartIndex + 1;
        if(currentTokenLine.charAt(nextTokenStartIndex) == '\"')
        {
            while(currentTokenLine.charAt(nextTokenEndIndex) != '\"'){
                nextTokenEndIndex++;
            }
            nextTokenEndIndex++;
        }
        else if(!symbolSet.contains(String.valueOf(currentTokenLine.charAt(nextTokenStartIndex)))){
            while(nextTokenEndIndex < currentTokenLine.length() && !Character.isWhitespace(currentTokenLine.charAt(nextTokenEndIndex)) && !symbolSet.contains(String.valueOf(currentTokenLine.charAt(nextTokenEndIndex)))){
                nextTokenEndIndex++;
            }
        }

        currentToken = currentTokenLine.substring(nextTokenStartIndex, nextTokenEndIndex);
        nextTokenStartIndex = nextTokenEndIndex;
        while(nextTokenStartIndex < currentTokenLine.length() && Character.isWhitespace(currentTokenLine.charAt(nextTokenStartIndex))){
            nextTokenStartIndex++;
        }
    }

    /**
     * This method is used to remove comments from a line of source code. The method simply
     * trims off any text that appears after '//'.
     * @param i_InputLine A line read from the source code.
     * @return The line with comments removed.
     */
    private String removeCommentsFromLine(String i_InputLine){
        String result = i_InputLine;
        int indexWhereCommentStarts = i_InputLine.indexOf("//");
        int indexWhereSpecialCommentStarts = i_InputLine.indexOf("/*");
        if (indexWhereCommentStarts != -1 ){
            result = result.substring(0, indexWhereCommentStarts);
        }
        else if (indexWhereSpecialCommentStarts != -1){
            result = result.substring(0, indexWhereSpecialCommentStarts);
        }

        return result;
    }

    /**
     * This method is used to retrieve the next line of instructions from the code.
     * @return The next instruction from the source code.
     * @exception IOException
     */
    private String retrieveNextTokenLine() throws IOException{
        String nextLine = null;
        boolean nextInstructionReached = false;
        while(!nextInstructionReached){
            nextLine = this.sourceCodeReader.readLine();
            nextInstructionReached = nextLine == null;
            if (!nextInstructionReached && nextLine.contains("/*")){
                while(!nextInstructionReached && !nextLine.contains("*/")){
                    nextLine = this.sourceCodeReader.readLine();
                    nextInstructionReached = nextLine == null;
                }
                if (!nextInstructionReached){
                    nextLine = this.sourceCodeReader.readLine();
                }
            }
            if (!nextInstructionReached){
                nextLine = removeCommentsFromLine(nextLine.trim());
                nextInstructionReached = !nextLine.isEmpty();
            }
        }
        return nextLine;
    }
}