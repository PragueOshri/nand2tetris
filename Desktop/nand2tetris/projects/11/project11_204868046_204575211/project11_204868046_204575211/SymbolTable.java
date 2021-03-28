import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, Symbol> classSymbolTable;
    private HashMap<String, Symbol> subroutineSymbolTable;
    int countStatic;
    int countFieled;
    int countArg;
    int countVar;

    public SymbolTable(){
        classSymbolTable = new HashMap<String, Symbol>();
        subroutineSymbolTable = new HashMap<String, Symbol>();
        countStatic = 0;
        countFieled = 0;
        countArg = 0;
        countVar = 0;
    }

    public void startSubroutine(){
        subroutineSymbolTable.clear();
        countArg = 0;
        countVar = 0;
    }

    public void define(String name, String type, Symbol.KIND kind){
        if(kind == Symbol.KIND.STATIC){
            Symbol symbol = new Symbol(type, kind, countStatic);
            countStatic++;
            classSymbolTable.put(name, symbol);
        }
        else if(kind == Symbol.KIND.FIELD){
            Symbol symbol = new Symbol(type, kind, countFieled);
            countFieled++;
            classSymbolTable.put(name, symbol);
        }
        else if(kind == Symbol.KIND.ARG){
            Symbol symbol = new Symbol(type, kind, countArg);
            countArg++;
            subroutineSymbolTable.put(name, symbol);
        }
        else if(kind == Symbol.KIND.VAR){
            Symbol symbol = new Symbol(type, kind, countVar);
            countVar++;
            subroutineSymbolTable.put(name, symbol);
        }
    }

    public int varCount(Symbol.KIND kind){
        if(kind == Symbol.KIND.STATIC){
            return countStatic;
        }
        else if(kind == Symbol.KIND.FIELD){
            return countFieled;
        }
        else if(kind == Symbol.KIND.ARG){
            return countArg;
        }
        else if(kind == Symbol.KIND.VAR){
            return countVar;
        }
        return -1;
    }

    public Symbol.KIND kindOf(String name){
        Symbol.KIND kindOf = Symbol.KIND.NONE;
        if(subroutineSymbolTable.containsKey(name)){
            kindOf = subroutineSymbolTable.get(name).getKind();
        }
        else if(classSymbolTable.containsKey(name)){
           kindOf = classSymbolTable.get(name).getKind();
        }
        return kindOf;
    }

    public String typeOf(String name){
        String typeOf = "";
        if(subroutineSymbolTable.containsKey(name)){
            typeOf = subroutineSymbolTable.get(name).getType();
        }
        else if(classSymbolTable.containsKey(name)){
            typeOf = classSymbolTable.get(name).getType();
        }
        return typeOf;
    }

    public int indexOf(String name){
        int indexOf = -1;
        if(subroutineSymbolTable.containsKey(name)){
            indexOf = subroutineSymbolTable.get(name).getIndex();
        }
        else if(classSymbolTable.containsKey(name)){
            indexOf = classSymbolTable.get(name).getIndex();
        }
        return indexOf;
    }
}
