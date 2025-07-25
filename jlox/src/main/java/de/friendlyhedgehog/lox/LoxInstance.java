package de.friendlyhedgehog.lox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoxInstance {
    private LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    public LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }

    public Object get(Token name) {
        String lexeme = name.lexeme;
        if (fields.containsKey(lexeme)) {
            return fields.get(lexeme);
        }
        LoxFunction method = klass.findMethod(name.lexeme);
        if(method != null) return method.bind(this);
        throw new Interpreter.RuntimeError(name, "Undefined property '" + lexeme + "'.");
    }

    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }
}
