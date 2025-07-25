package de.friendlyhedgehog.lox;

public class AstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitLambdaExpr(Expr.Lambda expr) {
        return parenthesize("lambda");
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return "(" + expr.name.lexeme + " = " + print(expr.value) + ")";
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.type == TokenType.OR ? "OR" : "AND", expr.left, expr.right);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitSuperExpr(Expr.Super expr) {
        return "";
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        return parenthesize(parenthesize("fun", expr.callee), (Expr[]) expr.arguments.toArray());
    }

    @Override
    public String visitSetExpr(Expr.Set expr) {
        return "(set [" + print(expr.object) + "]." + expr.name + "=" + print(expr.value);
    }

    @Override
    public String visitThisExpr(Expr.This expr) {
        return "this";
    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        return "(get [" + print(expr.object) + "]." + expr.name + ")";
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(1234)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)
                )
        );
        System.out.println(new AstPrinter().print(expression));
    }
}
