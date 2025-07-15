package de.friendlyhedgehog.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final String name;
    private final Expr.Lambda declaration;
    private final Environment closure;

    LoxFunction(Stmt.Function declaration, Environment closure) {
        this.name = declaration.name.lexeme;
        this.declaration = new Expr.Lambda(declaration.params, declaration.body);
        this.closure = closure;
    }

    LoxFunction(Expr.Lambda lambda, Environment closure) {
        this.name = "lambda";
        this.declaration = lambda;
        this.closure = closure;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); ++i) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + this.name + ">";
    }
}
