import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);
        ParseTree tree = sysYParser.program();
        Visitor visitor = new Visitor();
        visitor.visit(tree);
        Integer row = Integer.parseInt(args[1]);
        Integer column = -1;
        String newName = "";
        if (row != -1) {
            column = Integer.parseInt(args[2]);
            newName = args[3];
            FindScopeVisitor findScope = new FindScopeVisitor(Visitor.scope, row, column);
            findScope.visit(tree);
            Scope targetScope = findScope.targetScope;
            String varName = findScope.varName;
            RenameVisitor renameVisitor=new RenameVisitor(Visitor.scope,targetScope,varName,newName);
            renameVisitor.visit(tree);
        }
//        SysYParserVisitor<Void> parserVisitor = new MyParserVisitor();
//        parserVisitor.visit(tree);
    }

}
