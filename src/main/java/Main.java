import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {

  public static void main(String[] args){
    CharStream input = CharStreams.fromString("int a=aaa()+2*3;\nint aaa(){if(a()==a+3)a=a+3;}\n");
    SysYLexer lexer = new SysYLexer(input);
    SysYParser parser = new SysYParser(new CommonTokenStream(lexer));
    ParseTree tree = parser.program();
    SysYParserVisitor visitor = new SysYParserBaseVisitor();
    SysYParserListener listener = new MyListener();
    visitor.visit(tree);
    ParseTreeWalker walker=new ParseTreeWalker();
    walker.walk(listener, tree);
    // System.out.println("======================");
    // visitor.verify();
    // //System.out.println("======================");
    // //visitor.optimize();
    // System.out.println("======================");
    // visitor.exec();
  }

}
