import ir.ArrayType;
import ir.IntType;
import ir.Type;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class CheckHelper {
    public static boolean isEqual(Type first, Type second){
        if (first instanceof IntType && second instanceof  IntType){
            return true;
        }
        if (!(first instanceof ArrayType && second instanceof ArrayType)){
            return false;
        }
        return isEqual(((ArrayType) first).getEleType(), ((ArrayType) second).getEleType());
    }

    public static boolean isListTypeEqual(List<Type> first, ArrayList<Type> second) {
        if (first.size() != second.size()){
            return false;
        }
        for (int i = 0; i < first.size(); i++){
            if (!isEqual(first.get(i), second.get(i))){
                return false;
            }
        }
        return true;
    }
}
