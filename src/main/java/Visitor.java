import ir.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class Visitor extends SysYParserBaseVisitor<Void> {
    public static Scope scope = new Scope();    // 全局作用域
    public static Scope curScope = scope;     //当前所处的作用域


    private Type tmpTy;
    private ArrayList<Type> tmpTyArr =  new ArrayList<>();
    private ArrayList<String> tmpNameArr = new ArrayList<>();
    private int tmpInt;
    private boolean needInt = false;
    private Type hopeRetType;


    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        String name = ctx.IDENT().getText();
        if (curScope.find(name) != null) {
            OutputHelper
                    .printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(),
                            ctx.IDENT().getText());
            return null;
        }
        if (ctx.constExp().isEmpty()) {     // 非数组
            visit(ctx.constInitVal());
            if (!CheckHelper.isEqual(IntType.getI32(), tmpTy)) {
                OutputHelper.printSemanticError(ErrorType.MISMATCH_ASSIGN,
                        ctx.IDENT().getSymbol().getLine());
                return null;
            }
            scope.put(name, IntType.getI32());
        } else {
            Type arrType = IntType.getI32();
            for (int i = 0; i < ctx.constExp().size(); i++) {
                visit(ctx.constExp(ctx.constExp().size() - (i + 1)));
                arrType = new ArrayType(arrType, tmpInt);
            }
//            visit(ctx.constInitVal());
//            if (!CheckHelper.isEqual(arrType, tmpTy)) {
//                OutputHelper.printSemanticError(ErrorType.MISMATCH_ASSIGN,
//                        ctx.IDENT().getSymbol().getLine());
//                return null;
//            }
            curScope.put(name, arrType);
        }
        return null;
    }

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
        return super.visitVarDecl(ctx);
    }

    @Override
    public Void visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        if (ctx.constExp() != null) {
            if (ctx.constExp().exp() instanceof SysYParser.CallFuncExpContext) {  //函数调用
                SysYParser.CallFuncExpContext callFuncExpContext = (SysYParser.CallFuncExpContext) ctx.constExp().exp();
                String funcName = callFuncExpContext.IDENT().getText();
                if (scope.find(funcName) != null) {
                    Type type = scope.find(funcName);
                    if (((FunctionType) type).getRetTType().isIntType()) {
                        tmpTy = IntType.getI32();
                    } else {
                        tmpTy = Type.VoidType.getVoidType();
                    }
                }
            } else {
                tmpTy = IntType.getI32();
            }
            return null;
        } //else {
//            // 数组类型
//            Queue<SysYParser.ConstInitValContext> constInitValContextQueue = new LinkedList<>();
//            constInitValContextQueue.add(ctx);
//            SysYParser.ConstInitValContext context;
//            ArrayList<Integer> list = new ArrayList<>();
//            while (!constInitValContextQueue.isEmpty()){
//                int maxWidth = 0;
//                Queue<SysYParser.ConstInitValContext> nextLayer = new LinkedList<>();
//                while (!constInitValContextQueue.isEmpty()){
//                    context = constInitValContextQueue.poll();
//                    maxWidth =Math.max(maxWidth, context.constInitVal().size());
//                    if (context.constExp() == null) {
//                        nextLayer.addAll(context.constInitVal());
//                    }
//                }
//                list.add(maxWidth);
//                constInitValContextQueue = nextLayer;
//            }
//            Type arrType = IntType.getI32();
//        }
        return null;
    }

    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        String varName = ctx.IDENT().getText();
        if (curScope.find(varName) != null) {
            OutputHelper
                    .printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(),
                            ctx.IDENT().getText());
            return null;
        }
        if (ctx.constExp().isEmpty()) {     //非数组
            if (ctx.ASSIGN() != null) {
                visitInitVal(ctx.initVal());
                if (!CheckHelper.isEqual(IntType.getI32(), tmpTy)) {
                    OutputHelper.printSemanticError(ErrorType.MISMATCH_ASSIGN,
                            ctx.IDENT().getSymbol().getLine());
                    return null;
                }
            }
            scope.put(varName, IntType.getI32());
        } else {
            Type arrType = IntType.getI32();
            for (int i = 0; i < ctx.constExp().size(); i++) {
                visit(ctx.constExp(ctx.constExp().size() - (i + 1)));
                arrType = new ArrayType(arrType, tmpInt);
            }
            curScope.put(varName, arrType);
        }
        return null;
    }

    @Override
    public Void visitInitVal(SysYParser.InitValContext ctx) {
        if (ctx.exp() != null) {
            if (ctx.exp() instanceof SysYParser.CallFuncExpContext) {  //函数调用
                SysYParser.CallFuncExpContext callFuncExpContext = (SysYParser.CallFuncExpContext) ctx.exp();
                String funcName = callFuncExpContext.IDENT().getText();
                if (scope.find(funcName) != null) {
                    Type type = scope.find(funcName);
                    if (((FunctionType) type).getRetTType().isIntType()) {
                        tmpTy = IntType.getI32();
                    } else {
                        tmpTy = Type.VoidType.getVoidType();
                    }
                }
            } else {
                tmpTy = IntType.getI32();
            }
            return null;
        }
        return null;
    }

    /**
     * funcDef : funcType IDENT L_PAREN funcFParams? R_PAREN block
     */
    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
        String funcName = ctx.IDENT().getText();
        if (curScope.find(funcName) != null) {
            OutputHelper.printSemanticError(ErrorType.REDEF_FUNC, ctx.IDENT().getSymbol().getLine(),
                    ctx.IDENT().getText());
            return null;
        }
        Type retType = Type.VoidType.getVoidType();
        String typeStr = ctx.getChild(0).getText();
        if (typeStr.equals("int"))
            retType = IntType.getI32();     // 返回值类型为int32
        hopeRetType = retType;
        ArrayList<Type> paramsTyList = new ArrayList<>();
        if (ctx.funcFParams() != null) {
            visit(ctx.funcFParams());
            paramsTyList.addAll(tmpTyArr);
        }
        FunctionType functionType = new FunctionType(retType, paramsTyList);
        //顶层作用域中压入此函数
        curScope.put(funcName, functionType);
        visit(ctx.block());
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
        Scope newScope = new Scope(curScope);
        curScope.childScopes.add(newScope);
        curScope = newScope;

        if (ctx.parent instanceof SysYParser.FuncDefContext) {
            // 将函数的参数压入当前作用域
            for (int i = 0; i < tmpNameArr.size(); i++) {
                curScope.put(tmpNameArr.get(i), tmpTyArr.get(i));
            }
        }
        ctx.blockItem().forEach(this::visit);
        //切换回父级作用域
        curScope = curScope.parent;
        return null;
    }

    @Override
    public Void visitExpCond(SysYParser.ExpCondContext ctx) {
        visit(ctx.exp());
        if (tmpTy != null && tmpTy != IntType.getI32()) {
            OutputHelper
                    .printSemanticError(ErrorType.MISMATCH_OPRAND, ctx.exp().getStart().getLine());
            return null;
        }
        return null;
    }

    @Override
    public Void visitPlusExp(SysYParser.PlusExpContext ctx) {
        checkArithmeticOperation(ctx.exp(0), ctx.exp(1), (TerminalNode) ctx.getChild(1));
        return null;
    }

    @Override
    public Void visitMulExp(SysYParser.MulExpContext ctx) {
        return super.visitMulExp(ctx);
    }

    @Override
    public Void visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        ArrayList<Type> params = null;
        Type currentType;
        for (int i = 0; i < ctx.param().size(); i++) {
            visit(ctx.param(i));
            if (tmpTy == null) {
                continue;
            }
            currentType = tmpTy;
            if (params == null) {
                params = new ArrayList<>();
            }
            params.add(currentType);
        }
        tmpTyArr = params;
        return null;
    }

    @Override
    public Void visitCallFuncExp(SysYParser.CallFuncExpContext ctx) {
        if (scope.find(ctx.IDENT().getText()) == null) {
            OutputHelper.printSemanticError(ErrorType.UNDEF_FUNC, ctx.IDENT().getSymbol().getLine(),
                    ctx.IDENT().getText());
            tmpTy = null;
            return null;
        }
        if (!(scope.find(ctx.IDENT().getText()) instanceof FunctionType)) {
            OutputHelper.printSemanticError(ErrorType.NON_FUNC, ctx.IDENT().getSymbol().getLine(),
                    ctx.IDENT().getText());
            tmpTy = null;
            return defaultResult();
        }
        List<Type> params = ((FunctionType) scope.find(ctx.IDENT().getText())).getParamsTypes();
        if (ctx.funcRParams() == null) { //形参为null
            if (params != null || params.size() != 0) {
                OutputHelper.printSemanticError(ErrorType.MISMATCH_PARAM,
                        ctx.L_PAREN().getSymbol().getLine());
            }
        } else {
            visit(ctx.funcRParams());
            if (tmpTyArr == null || tmpTyArr.size() == 0) {
                return null;
            }
            if (!CheckHelper.isListTypeEqual(params, tmpTyArr)) {
                OutputHelper.printSemanticError(ErrorType.MISMATCH_PARAM, ctx.L_PAREN().getSymbol().getLine());
            }
        }
        tmpTy = ((FunctionType) scope.find(ctx.IDENT().getText())).getRetTType();
        return null;
    }

    @Override
    public Void visitNumberExp(SysYParser.NumberExpContext ctx) {
        tmpTy = IntType.getI32();
        return null;
    }

    @Override
    public Void visitLvalExp(SysYParser.LvalExpContext ctx) {
        super.visitLvalExp(ctx);
        tmpTy = IntType.getI32();
        return null;
    }

    @Override
    public Void visitLVal(SysYParser.LValContext ctx) {
        if (curScope.findWholeScope(ctx.IDENT().getText()) == null) {
            OutputHelper.printSemanticError(ErrorType.UNDEF_VAR, ctx.IDENT().getSymbol().getLine(),
                    ctx.IDENT().getText());
            return null;
        } else if (curScope.findWholeScope(ctx.IDENT().getText()) instanceof IntType && !ctx.L_BRACKT().isEmpty()) {
            OutputHelper.printSemanticError(ErrorType.NON_ARRAY, ctx.exp(0).getStart().getLine(),
                    ctx.exp(0).getText());
            return null;
        }

        return null;
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
    public Void visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
        if (ctx.exp() == null) {     //void
            if (!(hopeRetType instanceof Type.VoidType)) {
                OutputHelper
                        .printSemanticError(ErrorType.MISMATCH_RETURN, ctx.exp().getStart().getLine());
            }
        } else {
            visit(ctx.exp());
            if (!CheckHelper.isEqual(tmpTy, hopeRetType)) {
                OutputHelper
                        .printSemanticError(ErrorType.MISMATCH_RETURN, ctx.exp().getStart().getLine());
            }
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

    private Void checkArithmeticOperation(SysYParser.ExpContext exp1, SysYParser.ExpContext exp2,
                                          TerminalNode operand) {
        visit(exp1);
        Type type1 = tmpTy;
        visit(exp2);
        Type type2 = tmpTy;
        if (type1 == null && type2 == null) {
            tmpTy = null;

        } else if (type1 == type2) {    //两个int32类型
            tmpTy = type1;
        } else if (!(type1 instanceof IntType)) {
            OutputHelper.printSemanticError(ErrorType.MISMATCH_OPRAND, exp1.getStart().getLine());
            tmpTy = null;
        } else if (!(type2 instanceof IntType)) {
            OutputHelper.printSemanticError(ErrorType.MISMATCH_OPRAND, exp1.getStart().getLine());
            tmpTy = null;
        }
        return null;
    }
}
