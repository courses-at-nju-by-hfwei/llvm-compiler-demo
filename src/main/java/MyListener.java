import org.antlr.v4.runtime.ParserRuleContext;

public class MyListener extends  SysYParserBaseListener{
    @Override
    public void enterEveryRule(ParserRuleContext ctx) {

        for(int i=1;i<ctx.depth();++i){
            System.out.print("  ");
        }
        String name = SysYParser.ruleNames[ctx.getRuleIndex()];
        name = name.replace(name.charAt(0), (char)(name.charAt(0)+'A'-'a'));
        System.out.println(name);
        super.enterEveryRule(ctx);
    }

}
