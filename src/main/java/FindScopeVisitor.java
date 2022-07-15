import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Queue;
import java.util.Stack;

public class FindScopeVisitor extends SysYParserBaseVisitor<Void> {
    public Scope scope;
    public Scope targetScope;
    public String varName;
    private int row;
    private int column;
    private Scope curScope;
    private Stack<Integer> scopeIndex;

    public FindScopeVisitor(Scope scope, int row, int column) {
        this.scope = scope;
        this.row = row;
        this.column = column;
        this.curScope = scope;
        this.scopeIndex = new Stack<Integer>();
    }

    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        int index = 0;
        if (!scopeIndex.empty()) {
            index = scopeIndex.pop();
        }
        curScope = curScope.childScopes.get(index);
        //处理子节点
        ctx.blockItem().forEach(this::visit);
        index = index + 1;
        curScope = curScope.parent;
        if (index < curScope.childScopes.size()) {
            scopeIndex.push(index);
        }
        return null;
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        if (node.getSymbol().getType() == SysYLexer.IDENT) {
            Token t = node.getSymbol();
            if (t.getLine() == row && t.getCharPositionInLine() == column) {
                if (((RuleContext) node.getParent()).getRuleIndex() != SysYParser.RULE_funcFParam) {
                    Scope temp = curScope;
                    while (temp.parent != null && temp.find(t.getText()) == null) {
                        temp = temp.parent;
                    }
                    targetScope = temp;
                } else {
                    int index = 0;
                    if (!scopeIndex.isEmpty()) {
                        index = scopeIndex.peek();
                    }
                    targetScope = curScope.childScopes.get(index);
                }
                varName = t.getText();
            }
        }
        return super.visitTerminal(node);
    }

    @Override
    public Void visit(ParseTree tree) {
        if (this.targetScope == null) {
            return super.visit(tree);
        } else {
            return null;
        }
    }
}
