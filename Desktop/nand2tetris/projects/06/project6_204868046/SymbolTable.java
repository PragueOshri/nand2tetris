import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SymbolTable {

    Map<String, Integer> labelTable;

    SymbolTable() {
        labelTable = new HashMap<>();

        labelTable.put("SP", 0);
        labelTable.put("LCL", 1);
        labelTable.put("ARG", 2);
        labelTable.put("THIS", 3);
        labelTable.put("THAT", 4);
        labelTable.put("SCREEN", 16384);
        labelTable.put("KBD", 24576);
        for (int i = 0; i < 15; i++) {
            labelTable.put("R" + i, i);
        }
    }


    public void addEntry(String symbol, int address) {
        labelTable.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return labelTable.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return labelTable.get(symbol);
    }

}
