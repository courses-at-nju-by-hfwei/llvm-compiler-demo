import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.Stack;

import static utils.Constants.terminalNodeColors;
import static utils.TreePrinter.printFormatTree;

public class RenameVisitor extends SysYParserBaseVisitor<Void> {
    private Scope scope;
    private Scope targetScope;
    private String varName;
    private String newName;
    private Scope curScope;
    private Stack<Integer> scopeIndex;

    public RenameVisitor(Scope scope, Scope targetScope, String varName, String newName) {
        this.scope = scope;
        this.targetScope = targetScope;
        this.varName = varName;
        this.newName = newName;
        this.curScope = scope;
        this.scopeIndex = new Stack<Integer>();
    }

    @Override
    public Void visitChildren(RuleNode node) {
        printFormatTree(node.getRuleContext().depth());
        String name = SysYParser.ruleNames[node.getRuleContext().getRuleIndex()];
        name = name.replaceFirst(String.valueOf(name.charAt(0)), String.valueOf((char) (name.charAt(0) + 'A' - 'a')));
        System.out.println(name);
        return super.visitChildren(node);
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
        boolean flag = false;
        if (node.getSymbol().getType() == SysYLexer.IDENT) {
            Token t = node.getSymbol();
            if (t.getText().equals(varName)) {
                if (((RuleContext) node.getParent()).getRuleIndex() != SysYParser.RULE_funcFParam) {
                    Scope temp = curScope;
                    while (temp.parent != null && temp.find(t.getText()) == null) {
                        temp = temp.parent;
                    }
                    if (targetScope.equals(temp)) {
                        flag = true;
                    }
                } else {
                    int index = 0;
                    if (!scopeIndex.isEmpty()) {
                        index = scopeIndex.peek();
                    }
                    if (curScope.childScopes.get(index).equals(targetScope)) {
                        flag = true;
                    }
                }
            }
        }
        //print
        RuleNode n = (RuleNode) node.getParent();
        if (node.getSymbol().getType() > 1 && node.getSymbol().getType() <= terminalNodeColors.length && !terminalNodeColors[node.getSymbol().getType() - 1].contains("white")) {
            printFormatTree(n.getRuleContext().depth() + 1);
            if (!flag) {
                System.out.println(node.getSymbol().getText() + " " + terminalNodeColors[node.getSymbol().getType() - 1]);
            } else {
                System.out.println(newName + " " + terminalNodeColors[node.getSymbol().getType() - 1]);
            }
        }
        return super.visitTerminal(node);
    }
}
