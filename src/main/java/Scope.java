import org.bytedeco.javacpp.LLVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scope {
    public HashMap<String, LLVM.LLVMTypeRef> table;    //符号表
    public List<Scope> childScopes;      //子作用域

    public Scope parent;     //该作用域的父级作用域

    Scope(Scope parent) {
        table = new HashMap<>();
        this.parent = parent;
        childScopes = new ArrayList<>();
    }

    public LLVM.LLVMTypeRef find(String name) {     //只在同级作用域里找
        if (table.containsKey(name))
            return table.get(name);
        return null;
    }

    public void put(String name, LLVM.LLVMTypeRef typeRef) {
        this.table.put(name, typeRef);
    }

    Scope() {
        table = new HashMap<>();
        childScopes = new ArrayList<>();
    }
}
