parser grammar SysYParser;

options {
    tokenVocab = SysYLexer;
}

program
   : compUnit
   ;

compUnit
   : (funcDef | decl)+ EOF
   ;

decl
   : constDecl
   | varDecl
   ;

constDecl
   : CONST bType constDef (COMMA constDef)* SEMICOLON
   ;

bType
   : INT
   ;

constDef
   : IDENT (L_BRACKT constExp R_BRACKT)* ASSIGN constInitVal
   ;

constInitVal
   : constExp
   | L_BRACE (constInitVal (COMMA constInitVal)*)? R_BRACE
   ;

varDecl
   : bType varDef (COMMA varDef)* SEMICOLON
   ;

varDef
   : IDENT (L_BRACKT constExp R_BRACKT)* (ASSIGN initVal)?
   ;

initVal
   : exp
   | L_BRACE (initVal (COMMA initVal)*)? R_BRACE
   ;

funcDef
   : funcType IDENT L_PAREN funcFParams? R_PAREN block
   ;

funcType
   : VOID
   | INT
   ;

funcFParams
   : funcFParam (COMMA funcFParam)*
   ;

funcFParam
   : bType IDENT (L_BRACKT R_BRACKT (L_BRACKT exp R_BRACKT)*)?
   ;

block
   : L_BRACE blockItem* R_BRACE
   ;

blockItem
   : decl
   | stmt
   ;

stmt
   : lVal ASSIGN exp SEMICOLON
   | exp? SEMICOLON
   | block
   | IF L_PAREN cond R_PAREN stmt (ELSE stmt)?
   | WHILE L_PAREN cond R_PAREN stmt
   | BREAK SEMICOLON
   | CONTINUE SEMICOLON
   | RETURN (exp)? SEMICOLON
   ;

exp
   : addExp
   ;

cond
   : lOrExp
   ;

lVal
   : IDENT (L_BRACKT exp R_BRACKT)*
   ;

primaryExp
   : L_PAREN exp R_PAREN
   | lVal
   | number
   ;

number
   : INTEGR_CONST
   ;

unaryExp
   : primaryExp
   | IDENT L_PAREN funcRParams? R_PAREN
   | unaryOp unaryExp
   ;

unaryOp
   : PLUS
   | MINUS
   | NOT
   ;

funcRParams
   : param (COMMA param)*
   ;

param
   : exp
   | STRING
   ;

mulExp
   : unaryExp ((MUL | DIV | MOD) unaryExp)*
   ;

addExp
   : mulExp ((PLUS | MINUS) mulExp)*
   ;

relExp
   : addExp ((LT | GT | LE | GE) addExp)*
   ;

eqExp
   : relExp ((EQ | NEQ) relExp)*
   ;

lAndExp
   : eqExp (AND eqExp)*
   ;

lOrExp
   : lAndExp (OR lAndExp)*
   ;

constExp
   : addExp
   ;