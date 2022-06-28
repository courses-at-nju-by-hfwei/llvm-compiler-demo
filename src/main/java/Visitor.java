import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;

public class Visitor extends SysYParserBaseVisitor<Void> {
    public static Scope scope = new Scope();    // 全局作用域
    public static Scope curScope = scope;     //当前所处的作用域


    private Pointer tmpTy;
    private PointerPointer<Pointer> tmpTyArr;
    int count;
    private int tmpInt;

    @Override
    public Void visitDecl(SysYParser.DeclContext ctx) {
        return super.visitDecl(ctx);
    }

    @Override
    public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
        return super.visitConstDecl(ctx);
    }

    @Override
    public Void visitBType(SysYParser.BTypeContext ctx) {
        return super.visitBType(ctx);
    }

    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        return super.visitConstDef(ctx);
    }

    @Override
    public Void visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        return super.visitConstInitVal(ctx);
    }

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
        return super.visitVarDecl(ctx);
    }

    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        return super.visitVarDef(ctx);
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
        LLVM.LLVMTypeRef retType = LLVM.LLVMVoidType();
        String typeStr = ctx.getChild(0).getText();
        if (typeStr.equals("int"))
            retType = LLVM.LLVMInt32Type();     // 返回值类型为int32
        if (ctx.funcFParams() != null) {
            visit(ctx.funcFParams());
        }
        LLVM.LLVMTypeRef functionType = LLVM.LLVMFunctionType(retType, tmpTyArr, count, 0);

        curScope.put(funcName, functionType);   // 顶层作用域压入此函数
        Scope child = new Scope(curScope);      // 新建子作用域
        curScope = child;                       // 切换作用域为函数的作用域

        curScope = curScope.parent;             // 函数的作用域构建完毕，切换回上层作用域
        return null;
    }

    // 访问两次，一次用来生成函数的信息压入顶级作用域，一次生成参数的信息，压入函数作用域
    @Override
    public Void visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        PointerPointer<Pointer> pointerPointer = new PointerPointer<>();
        count = 0;
        ctx.funcFParam().forEach(param -> {
            visit(param);
            pointerPointer.put(count++, tmpTy);
        });
        tmpTyArr = pointerPointer;
        return null;
    }

    @Override
    public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        //数组参数
        if (!ctx.L_BRACKT().isEmpty()) {
            LLVM.LLVMTypeRef type = LLVM.LLVMInt32Type();
            for (int i = 0; i < ctx.exp().size(); i++) {
                visit(ctx.exp(ctx.exp().size() - (i + 1))); // 从后往前访问
                type = LLVM.LLVMArrayType(type, tmpInt);       // 构建嵌套的数组类型
            }
            // todo
            tmpTy = type;
        } else {
            tmpTy = LLVM.LLVMInt32Type();
        }
        return null;
    }

    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        return super.visitBlock(ctx);
    }

    @Override
    public Void visitBlockItem(SysYParser.BlockItemContext ctx) {
        return super.visitBlockItem(ctx);
    }

    @Override
    public Void visitStmt(SysYParser.StmtContext ctx) {
        return super.visitStmt(ctx);
    }

    @Override
    public Void visitExp(SysYParser.ExpContext ctx) {
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
    public Void visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        return super.visitFuncRParams(ctx);
    }

    @Override
    public Void visitParam(SysYParser.ParamContext ctx) {
        return super.visitParam(ctx);
    }

    @Override
    public Void visitConstExp(SysYParser.ConstExpContext ctx) {
        return super.visitConstExp(ctx);
    }
}
