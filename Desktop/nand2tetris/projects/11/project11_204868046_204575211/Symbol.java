public class Symbol {

    public static enum KIND {
        STATIC,
        FIELD,
        ARG,
        VAR,
        NONE
    }
    private String type;
    private KIND kind;
    private int index;

    public Symbol(String i_type, KIND i_kind, int i_index){
        this.type = i_type;
        this.kind = i_kind;
        this.index = i_index;
    }
    public String getType(){
        return type;
    }
    public KIND getKind(){
        return kind;
    }
    public int getIndex(){
        return index;
    }
}
