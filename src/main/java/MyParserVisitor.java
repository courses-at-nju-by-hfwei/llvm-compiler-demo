import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import utils.Constants;

import static utils.Constants.terminalNodeColors;
import static utils.TreePrinter.printFormatTree;

public class MyParserVisitor extends SysYParserBaseVisitor<Void> {

    @Override
    public Void visitChildren(RuleNode node) {
        printFormatTree(node.getRuleContext().depth());
        String name = SysYParser.ruleNames[node.getRuleContext().getRuleIndex()];
        name = name.replaceFirst(String.valueOf(name.charAt(0)), String.valueOf((char) (name.charAt(0) + 'A' - 'a')));
        System.out.println(name);
        return super.visitChildren(node);
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        RuleNode n = (RuleNode) node.getParent();
        if (node.getSymbol().getType() > 1 && node.getSymbol().getType() <= terminalNodeColors.length && !terminalNodeColors[node.getSymbol().getType() - 1].contains("white")) {
            printFormatTree(n.getRuleContext().depth() + 1);
            System.out.println(node.getSymbol().getText() + " " + terminalNodeColors[node.getSymbol().getType() - 1]);
        }
        return super.visitTerminal(node);
    }
}
