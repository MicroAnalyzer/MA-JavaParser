package joelbits.modules.preprocessing.plugins.utils;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static joelbits.model.ast.protobuf.ASTProtos.Modifier.VisibilityType;
import static joelbits.model.ast.protobuf.ASTProtos.Method;
import static joelbits.model.ast.protobuf.ASTProtos.Variable;
import static joelbits.model.ast.protobuf.ASTProtos.Declaration;
import static joelbits.model.ast.protobuf.ASTProtos.DeclarationType;
import static joelbits.model.ast.protobuf.ASTProtos.Modifier;
import static joelbits.model.ast.protobuf.ASTProtos.Modifier.ModifierType;
import static joelbits.model.ast.protobuf.ASTProtos.Expression;
import static joelbits.model.ast.protobuf.ASTProtos.Expression.ExpressionType;
import static joelbits.model.ast.protobuf.ASTProtos.Type;
import static joelbits.model.ast.protobuf.ASTProtos.ASTRoot;
import static joelbits.model.ast.protobuf.ASTProtos.Namespace;

import java.util.Arrays;
import java.util.List;

public class ASTNodeCreator {
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

    public Modifier createAnnotationModifier(AnnotationExpr annotationExpr, List<String> membersAndValues) {
        return Modifier.newBuilder()
                .setType(ModifierType.ANNOTATION)
                .setName(annotationExpr.getNameAsString())
                .addAllMembersAndValues(membersAndValues)
                .build();
    }

    public Method createMethod(List<Modifier> methodModifiers, MethodDeclaration method, List<Variable> arguments, List<Expression> methodBody) {
        return Method.newBuilder()
                .setName(method.getNameAsString())
                .setReturnType(createType(method.getType().asString()))
                .addAllModifiers(methodModifiers)
                .addAllArguments(arguments)
                .addAllBodyContent(methodBody)
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

    public Expression createMethodCallExpression(MethodCallExpr methodCall, List<Expression> methodArguments) {
        return Expression.newBuilder()
                .setType(ExpressionType.METHODCALL)
                .setMethod(methodCall.getNameAsString())
                .addAllMethodArguments(methodArguments)
                .build();
    }

    public Expression createMethodCallArgumentExpression(String argument) {
        return Expression.newBuilder()
                .setType(ExpressionType.OTHER)
                .setVariable(argument)
                .build();
    }

    public Expression createExpression(ExpressionType type, String literal, String variable, List<Variable> declarations, List<Expression> arguments) {
        return Expression.newBuilder()
                .setType(type)
                .setLiteral(literal)
                .setVariable(variable)
                .addAllVariableDeclarations(declarations)
                .addAllMethodArguments(arguments)
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
