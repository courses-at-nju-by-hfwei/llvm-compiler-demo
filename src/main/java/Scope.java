import main.ir.Type;
import org.bytedeco.javacpp.LLVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scope {
    public HashMap<String, Type> table;    //符号表
    public List<Scope> childScopes;      //子作用域

    public Scope parent;     //该作用域的父级作用域

    Scope(Scope parent) {
        table = new HashMap<>();
        this.parent = parent;
        childScopes = new ArrayList<>();
    }

    public Type find(String name) {     // 只在当前作用域里找
        if (table.containsKey(name))
            return table.get(name);
        return null;
    }

    public Type findWholeScope(String name){
        if (table.containsKey(name)){
            return table.get(name);
        }else if (this.parent != null){
            return parent.findWholeScope(name);
        }
        return null;
    }

    public void put(String name, Type type) {
        this.table.put(name, type);
    }

    Scope() {
        table = new HashMap<>();
        childScopes = new ArrayList<>();
    }
}
