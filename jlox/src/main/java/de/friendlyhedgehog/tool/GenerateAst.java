package de.friendlyhedgehog.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", List.of(
                "Assign     : Token name, Expr value",
                "Binary     : Expr left, Token operator, Expr right",
                "Call       : Expr callee, Token paren, List<Expr> arguments",
                "Get        : Expr object, Token name",
                "Grouping   : Expr expression",
                "Literal    : Object value",
                "Logical    : Expr left, Token operator, Expr right",
                "Set        : Expr object, Token name, Expr value",
                "This       : Token keyword",
                "Super      : Token keyword, Token method",
                "Variable   : Token name",
                "Unary      : Token operator, Expr right",
                "Lambda     : List<Token> params, List<Stmt> body"
        ));
        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block      : List<Stmt> statements",
                "Class      : Token name, Expr.Variable superclass, List<Stmt.Function> methods",
                "Expression : Expr expression",
                "Function   : Token name, List<Token> params, List<Stmt> body",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Print      : Expr expression ",
                "Return     : Token keyword, Expr value",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body"
        ));
    }

    private static void defineAst(
            String outputDir,
            String baseName,
            List<String> types
    ) throws IOException {
        String path = outputDir + "\\" + baseName + ".java";
        try (PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package de.friendlyhedgehog.lox;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println();
            writer.println("abstract class " + baseName + " {");
            defineVisitor(writer, baseName, types);
            for (String type : types) {
                String[] split = type.split(":");
                String className = split[0].trim();
                String fields = split[1].trim();
                defineType(writer, baseName, className, fields);
            }
            writer.println();
            writer.println("    abstract <R> R accept(Visitor<R> visitor); ");
            writer.println("}");
        }
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> { ");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }");
    }

    private static void defineType(
            PrintWriter writer,
            String baseName,
            String className,
            String fieldList
    ) throws IOException {
        writer.println("    static class " + className + " extends " + baseName + " {");
        writer.println("        " + className + "(" + fieldList + "){");
        String[] fields = fieldList.split(",");
        for (String field : fields) {
            String[] split = field.trim().split(" ");
            String name = split[1];
            writer.println("            this." + name + " = " + name + ";");
        }
        writer.println("        }");

        // Visitor Pattern
        writer.println();
        writer.println("        @Override ");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");

        for (String field : fields) {
            writer.println("        final " + field + ";");
        }
        writer.println("    }");
    }
}
