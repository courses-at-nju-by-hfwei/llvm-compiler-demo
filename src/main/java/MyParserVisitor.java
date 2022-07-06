import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class MyParserVisitor extends SysYParserBaseVisitor<Void>{

    private String[] terminalNodeColors=new String[] {
            "CONST[orange]", "INT[orange]", "VOID[orange]", "IF[orange]", "ELSE[orange]", "WHILE[orange]", "BREAK[orange]", "CONTINUE[orange]", "RETURN[orange]",
            "PLUS[blue]", "MINUS[blue]", "MUL[blue]", "DIV[blue]", "MOD[blue]", "ASSIGN[blue]", "EQ[blue]", "NEQ[blue]", "LT[blue]", "GT[blue]",
            "LE[blue]", "GE[blue]", "NOT[blue]", "AND[blue]", "OR[blue]", "L_PAREN[white]", "R_PAREN[white]", "L_BRACE[white]", "R_BRACE[white]",
            "L_BRACKT[white]", "R_BRACKT[white]", "COMMA[white]", "SEMICOLON[white]", "IDENT[red]", "INTEGR_CONST[green]",
            "STRING[green]", "DECIMAL_CONST[green]", "OCTAL_CONST[green]", "HEXADECIMAL_CONST[green]", "ESC[green]",
            "WS[white]", "LINE_COMMENT[gray]", "MULTILINE_COMMENT[gray]"
    };

    private void printFormatTree(int depth){
        for(int i=1;i<depth;++i){
            System.out.print("  ");
        }
    }

    @Override
    public Void visitChildren(RuleNode node) {
        printFormatTree(node.getRuleContext().depth());
        String name = SysYParser.ruleNames[node.getRuleContext().getRuleIndex()];
        name = name.replace(name.charAt(0), (char)(name.charAt(0)+'A'-'a'));
        System.out.println(name);
        return super.visitChildren(node);
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        RuleNode n=(RuleNode) node.getParent();
        if(node.getSymbol().getType()>1 && node.getSymbol().getType()<=terminalNodeColors.length && !terminalNodeColors[node.getSymbol().getType() - 1].contains("white")) {
            printFormatTree(n.getRuleContext().depth() + 1);
            System.out.println(node.getSymbol().getText() + " " + terminalNodeColors[node.getSymbol().getType() - 1]);
        }
        return super.visitTerminal(node);
    }
}
