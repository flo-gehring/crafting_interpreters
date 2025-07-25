package de.friendlyhedgehog.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    final Environment enclosing;

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name, Object value) {
        values.put(name, value);
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        if (enclosing != null) {
            return enclosing.get(name);
        }
        throw new Interpreter.RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'."
        );
    }

    public void assign(Token name, Object value) {
        String lexeme = name.lexeme;
        if (values.containsKey(lexeme)) {
            values.put(lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new Interpreter.RuntimeError(name, "Undefined variable '" + lexeme + "'.");

    }
}
