import main.ir.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;

public class Visitor extends SysYParserBaseVisitor<Void> {
    public static Scope scope = new Scope();    // 全局作用域
    public static Scope curScope = scope;     //当前所处的作用域


    private Type tmpTy;
    private ArrayList<Type> tmpTyArr;
    private ArrayList<String> tmpNameArr;
    private int tmpInt;
    private boolean needInt = false;


    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        String name = ctx.IDENT().getText();
        if (curScope.find(name) != null) {
            // todo 变量命名冲突
        }
        if (ctx.constExp().isEmpty()) {
            scope.put(name, IntType.getI32());
        } else {
            Type arrType = IntType.getI32();
            for (int i = 0; i < ctx.constExp().size(); i++){
                visit(ctx.constExp(ctx.constExp().size() - (i + 1)));
                arrType = new ArrayType(arrType, tmpInt);
            }
            curScope.put(name, arrType);
        }
        return null;
    }

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
        return super.visitVarDecl(ctx);
    }

    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        String varName = ctx.IDENT().getText();
        if (curScope.find(varName) != null){
            // todo 变量命名重复
        }
        if (ctx.constExp().isEmpty()){
            scope.put(varName, IntType.getI32());
        }else {
            Type arrType = IntType.getI32();
            for (int i = 0; i < ctx.constExp().size(); i++){
                visit(ctx.constExp(ctx.constExp().size() - (i + 1)));
                arrType = new ArrayType(arrType, tmpInt);
            }
            curScope.put(varName, arrType);
        }
        return null;
    }

    @Override
    public Void visitInitVal(SysYParser.InitValContext ctx) {
        return super.visitInitVal(ctx);
    }

    /**
     * funcDef : funcType IDENT L_PAREN funcFParams? R_PAREN block
     */
    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
        String funcName = ctx.IDENT().getText();
        if (curScope.find(funcName) != null) {
            // todo 函数命名冲突
        }
        Type retType = Type.VoidType.getVoidType();
        String typeStr = ctx.getChild(0).getText();
        if (typeStr.equals("int"))
            retType = IntType.getI32();     // 返回值类型为int32
        ArrayList<Type> paramsTyList = new ArrayList<>();
        if (ctx.funcFParams() != null) {
            visit(ctx.funcFParams());
            paramsTyList.addAll(tmpTyArr);
        }
        FunctionType functionType = new FunctionType(retType, paramsTyList);
        //顶层作用域中压入此函数
        curScope.put(funcName, functionType);
        //切换作用域为函数的作用域
        curScope = new Scope(curScope);
        //把函数参数全部压入当前作用域
        for (int i = 0; i < tmpNameArr.size(); i++) {
            curScope.put(tmpNameArr.get(i), tmpTyArr.get(i));
        }
        visit(ctx.block());
        //函数的作用域构建完毕，切换回上层作用域
        curScope = curScope.parent;
        return null;
    }

    @Override
    public Void visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        ArrayList<Type> params = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ctx.funcFParam().forEach(param -> {
            visit(param);
            params.add(tmpTy);
            names.add(param.getText());
        });
        tmpTyArr = params;
        tmpNameArr = names;
        return null;
    }

    @Override
    public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        //数组参数
        if (!ctx.L_BRACKT().isEmpty()) {
            Type type = IntType.getI32();
            for (int i = 0; i < ctx.exp().size(); i++) {
                visit(ctx.exp(ctx.exp().size() - (i + 1))); // 从后往前访问
                type = new ArrayType(type, tmpInt);       // 构建嵌套的数组类型
            }
            tmpTy = new PointerType(type);   //如果只是一维数组的话，那type只会是IntType
        } else {
            tmpTy = IntType.getI32();
        }
        return null;
    }

    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        //新一层作用域
        curScope = new Scope(curScope);
        ctx.blockItem().forEach(this::visit);
        //切换回父级作用域
        curScope = curScope.parent;
        return null;
    }

    @Override
    public Void visitStmt(SysYParser.StmtContext ctx) {
        return super.visitStmt(ctx);
    }

    @Override
    public Void visitExp(SysYParser.ExpContext ctx) {
        if (ctx.IDENT() != null){ // IDENT L_PAREN funcRParams? R_PAREN
            if (curScope.findWholeScope(ctx.IDENT().getText()) == null){
                OutputHelper.printSemanticError(ErrorType.UNDEF_FUNC, ctx.IDENT().getSymbol().getLine(),
                        ctx.IDENT().getText());
            }
        }
        return super.visitExp(ctx);
    }

    @Override
    public Void visitCond(SysYParser.CondContext ctx) {
        return super.visitCond(ctx);
    }

    @Override
    public Void visitLVal(SysYParser.LValContext ctx) {
        return super.visitLVal(ctx);
    }

    @Override
    public Void visitPrimaryExp(SysYParser.PrimaryExpContext ctx) {
        return super.visitPrimaryExp(ctx);
    }

    @Override
    public Void visitNumber(SysYParser.NumberContext ctx) {
        TerminalNode node = ctx.INTEGR_CONST();
        if (node.getText().startsWith("0x") || node.getText().startsWith("0X")) {    //十六进制
            tmpInt = Integer.parseInt(node.getText().substring(2), 16);
        } else if (node.getText().length() > 1 && node.getText().startsWith("0")) {   // 八进制
            tmpInt = Integer.parseInt(node.getText(), 8);
        } else {    //十进制
            tmpInt = Integer.parseInt(node.getText(), 10);
        }
        return null;
    }

    @Override
    public Void visitUnaryOp(SysYParser.UnaryOpContext ctx) {
        return super.visitUnaryOp(ctx);
    }


    @Override
    public Void visitConstExp(SysYParser.ConstExpContext ctx) {
        needInt = true;
        visit(ctx.exp());
        needInt = false;
        return null;
    }
}
