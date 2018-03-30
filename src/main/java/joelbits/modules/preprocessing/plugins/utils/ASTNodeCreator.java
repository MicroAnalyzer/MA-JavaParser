package joelbits.modules.preprocessing.plugins.utils;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import static joelbits.model.ast.protobuf.ASTProtos.Modifier.VisibilityType;
import static joelbits.model.ast.protobuf.ASTProtos.Method;
import static joelbits.model.ast.protobuf.ASTProtos.Variable;
import static joelbits.model.ast.protobuf.ASTProtos.Declaration;
import static joelbits.model.ast.protobuf.ASTProtos.DeclarationType;
import static joelbits.model.ast.protobuf.ASTProtos.Statement;
import static joelbits.model.ast.protobuf.ASTProtos.Statement.StatementType;
import static joelbits.model.ast.protobuf.ASTProtos.Modifier;
import static joelbits.model.ast.protobuf.ASTProtos.Modifier.ModifierType;
import static joelbits.model.ast.protobuf.ASTProtos.Expression;
import static joelbits.model.ast.protobuf.ASTProtos.Expression.ExpressionType;
import static joelbits.model.ast.protobuf.ASTProtos.Type;
import static joelbits.model.ast.protobuf.ASTProtos.ASTRoot;
import static joelbits.model.ast.protobuf.ASTProtos.Namespace;

import java.util.Arrays;
import java.util.List;

/**
 * Creates the Protocol Buffer representation of the AST nodes used within MicroAnalyzer. The created PB
 * objects contains the parsed data and are later converted to their binary form for persistence.
 */
public final class ASTNodeCreator {
    public Variable createVariable(String name, String type, List<Modifier> argumentModifiers) {
        return Variable.newBuilder()
                .setType(createType(type))
                .setName(name)
                .addAllModifiers(argumentModifiers)
                .build();
    }

    public Type createType(String name) {
        return Type.newBuilder()
                .setName(name)
                .setType(DeclarationType.OTHER)
                .build();
    }

    public Modifier createAnnotationModifier(String annotation, List<String> membersAndValues) {
        return Modifier.newBuilder()
                .setType(ModifierType.ANNOTATION)
                .setName(annotation)
                .addAllMembersAndValues(membersAndValues)
                .build();
    }

    public Method createMethod(List<Modifier> modifiers, String name, String type, List<Variable> arguments, List<Expression> methodBody, List<Statement> bodyStatements) {
        return Method.newBuilder()
                .setName(name)
                .setReturnType(createType(type))
                .addAllModifiers(modifiers)
                .addAllArguments(arguments)
                .addAllBodyContent(methodBody)
                .addAllStatements(bodyStatements)
                .build();
    }

    public Modifier createModifier(String modifierName) {
        ModifierType type = getModifierType(modifierName);
        Modifier.Builder builder = Modifier.newBuilder();

        if (type.equals(ModifierType.VISIBILITY)) {
            return builder.setVisibility(VisibilityType.valueOf(modifierName)).setType(type).build();
        }
        if (type.equals(ModifierType.OTHER)) {
            return builder.setOther(modifierName).setType(type).build();
        }

        return builder.setName(modifierName).setType(type).build();
    }

    public Expression createMethodCallExpression(String methodCall, List<Expression> methodArguments) {
        return Expression.newBuilder()
                .setType(ExpressionType.METHODCALL)
                .setMethod(methodCall.substring(0, methodCall.indexOf("(")))
                .addAllMethodArguments(methodArguments)
                .build();
    }

    public Expression createVarDeclarationExpression(String literal, String variable, List<Variable> variables, List<Expression> arguments, List<Expression> expressions) {
        return Expression.newBuilder()
                .setType(ExpressionType.VARIABLE_DECLARATION)
                .setLiteral(literal)
                .setVariable(variable)
                .addAllMethodArguments(arguments)
                .addAllExpressions(expressions)
                .build();
    }

    public Expression createArgumentExpression(String argument) {
        return Expression.newBuilder()
                .setType(ExpressionType.OTHER)
                .setVariable(argument)
                .build();
    }

    public Expression createAssignmentExpression(ExpressionType type, String literal, String variable, List<Expression> expressions) {
        return Expression.newBuilder()
                .setType(type)
                .setLiteral(literal)
                .setVariable(variable)
                .addAllExpressions(expressions)
                .build();
    }

    public Expression createCreationExpression(String literal, String variable, List<Expression> arguments) {
        return Expression.newBuilder()
                .setType(ExpressionType.NEW)
                .setLiteral(literal)
                .setVariable(variable)
                .addAllMethodArguments(arguments)
                .build();
    }

    public Expression createExpression(ExpressionType type, String literal, String variable, List<Variable> declarations, List<Expression> arguments, Type newType, List<Expression> expressions) {
        return Expression.newBuilder()
                .setType(type)
                .setLiteral(literal)
                .setVariable(variable)
                .addAllVariableDeclarations(declarations)
                .addAllMethodArguments(arguments)
                .setNewType(newType)
                .addAllExpressions(expressions)
                .build();
    }

    public Expression createMethodBodyExpression(ExpressionType type, String variable, String literal) {
        return Expression.newBuilder()
                .setType(type)
                .setVariable(variable)
                .setLiteral(literal)
                .build();
    }

    public Declaration createNamespaceDeclaration(ClassOrInterfaceDeclaration declaration, List<Variable> allFields, List<Method> allMethods, List<Modifier> topModifiers, List<Declaration> nestedDeclarations) {
        return Declaration.newBuilder()
                .setName(declaration.getNameAsString())
                .setType(getDeclarationType(declaration))
                .addAllModifiers(topModifiers)
                .addAllFields(allFields)
                .addAllMethods(allMethods)
                .addAllNestedDeclarations(nestedDeclarations)
                .build();
    }

    public Declaration createNestedDeclaration(ClassOrInterfaceDeclaration declaration, List<Variable> allFields, List<Method> allMethods, List<Modifier> modifiers) {
        return Declaration.newBuilder()
                .setName(declaration.getNameAsString())
                .setType(getDeclarationType(declaration))
                .addAllModifiers(modifiers)
                .addAllFields(allFields)
                .addAllMethods(allMethods)
                .build();
    }

    public Statement createReturnStatement(List<Expression> expressions) {
        return Statement.newBuilder()
                .setType(StatementType.RETURN)
                .addAllExpressions(expressions)
                .build();
    }

    public Statement createStatement(StatementType type, Expression condition, List<Statement> statements) {
        return Statement.newBuilder()
                .setType(type)
                .setCondition(condition)
                .addAllStatements(statements)
                .build();
    }

    public Statement createBlockStatement(List<Expression> expressions, List<Statement> statements) {
        return Statement.newBuilder()
                .setType(StatementType.BLOCK)
                .addAllExpressions(expressions)
                .addAllStatements(statements)
                .build();
    }

    public Statement createTryStatement(List<Statement> statements) {
        return Statement.newBuilder()
                .setType(StatementType.TRY)
                .addAllStatements(statements)
                .build();
    }

    public Statement createStatement(StatementType type, List<Expression> expressions, Expression condition, List<Statement> nestedStatements, List<Expression> initializations, List<Expression> updates) {
        return Statement.newBuilder()
                .setType(type)
                .addAllExpressions(expressions)
                .setCondition(condition)
                .addAllStatements(nestedStatements)
                .addAllInitializations(initializations)
                .addAllUpdates(updates)
                .build();
    }

    public ASTRoot createAstRoot(List<String> imports, List<Namespace> namespaces) {
        return ASTRoot.newBuilder()
                .addAllImports(imports)
                .addAllNamespaces(namespaces)
                .build();
    }

    private DeclarationType getDeclarationType(ClassOrInterfaceDeclaration declaration) {
        if (declaration.isInterface()) {
            return DeclarationType.INTERFACE;
        } else if (declaration.isAnnotationDeclaration()) {
            return DeclarationType.ANNOTATION;
        } else if (declaration.isEnumDeclaration()) {
            return DeclarationType.ENUM;
        } else if (declaration.isGeneric()) {
            return DeclarationType.GENERIC;
        } else if (declaration.isInnerClass() || declaration.isLocalClassDeclaration() || declaration.isClassOrInterfaceDeclaration()) {
            return DeclarationType.CLASS;
        }

        return DeclarationType.OTHER;
    }

    private ModifierType getModifierType(String modifier) {
        if (ModifierType.STATIC.name().equals(modifier.toUpperCase())) {
            return ModifierType.STATIC;
        } else if (ModifierType.FINAL.name().equals(modifier.toUpperCase())) {
            return ModifierType.FINAL;
        } else if (ModifierType.ABSTRACT.name().equals(modifier.toUpperCase())) {
            return ModifierType.ABSTRACT;
        } else if (ModifierType.SYNCHRONIZED.name().equals(modifier.toUpperCase())) {
            return ModifierType.SYNCHRONIZED;
        }

        String[] visibilityModifiers = Arrays.stream(VisibilityType.values()).map(Enum::name).toArray(String[]::new);
        if (Arrays.asList(visibilityModifiers).contains(modifier.toUpperCase())) {
            return ModifierType.VISIBILITY;
        }

        return ModifierType.OTHER;
    }
}
