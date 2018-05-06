package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.model.ast.protobuf.ASTProtos.Variable;
import joelbits.model.ast.protobuf.ASTProtos.Expression.ExpressionType;
import joelbits.modules.preprocessing.utils.ASTNodeCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A visitor for expressions inside a method body.
 */
public final class MethodBodyExpressionVisitor extends VoidVisitorAdapter<List<ASTProtos.Expression>> {
    private final ASTNodeCreator astNodeCreator = new ASTNodeCreator();

    @Override
    public void visit(FieldAccessExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        String object = expression.getScope().toString();
        String field = expression.getNameAsString();
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.FIELD_ACCESS, object, field));
    }

    @Override
    public void visit(VariableDeclarationExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        List<ASTProtos.Modifier> variableModifiers = new ArrayList<>();
        for (Modifier modifier : expression.getModifiers()) {
            variableModifiers.add(astNodeCreator.createModifier(modifier.asString()));
        }
        for (VariableDeclarator declaration : expression.getVariables()) {
            String name = declaration.getName().asString();
            String type = declaration.getType().asString();
            String assignedValue = "";

            Optional<Expression> initializer = declaration.getInitializer();
            if (initializer.isPresent()) {
                assignedValue = initializer.get().toString();
            }

            Variable variable = astNodeCreator.createVariable(name, type, variableModifiers);
            methodBodyContent.add(astNodeCreator
                    .createVarDeclarationExpression(assignedValue, name, Collections.singletonList(variable), Collections.emptyList(), Collections.emptyList()));
        }
    }

    @Override
    public void visit(ArrayAccessExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        String arrayName = expression.getName().toString();
        String index = expression.getIndex().toString();
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.OTHER, arrayName, index));
    }

    @Override
    public void visit(NullLiteralExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.LITERAL, "", expression.toString()));
    }

    @Override
    public void visit(NameExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.OTHER, expression.toString(), ""));
    }

    @Override
    public void visit(EnclosedExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        expression.getInner().accept(new MethodBodyExpressionVisitor(), methodBodyContent);
    }

    @Override
    public void visit(BinaryExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        expression.getLeft().accept(new MethodBodyExpressionVisitor(), methodBodyContent);
        expression.getRight().accept(new MethodBodyExpressionVisitor(), methodBodyContent);
    }

    @Override
    public void visit(StringLiteralExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.LITERAL, "", expression.toString()));
    }

    @Override
    public void visit(IntegerLiteralExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.LITERAL, "", expression.toString()));
    }

    @Override
    public void visit(BooleanLiteralExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.LITERAL, "", expression.toString()));
    }

    @Override
    public void visit(CharLiteralExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.LITERAL, "", expression.toString()));
    }

    @Override
    public void visit(DoubleLiteralExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.LITERAL, "", expression.toString()));
    }

    @Override
    public void visit(LongLiteralExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        methodBodyContent.add(astNodeCreator
                .createMethodBodyExpression(ExpressionType.LITERAL, "", expression.toString()));
    }

    @Override
    public void visit(ObjectCreationExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        List<ASTProtos.Expression> arguments = new ArrayList<>();
        for (Expression argument : expression.getArguments()) {
            arguments.add(astNodeCreator.createArgumentExpression(argument.toString()));
        }

        if (expression.getType().isBoxedType()) {
            methodBodyContent.add(astNodeCreator
                    .createCreationExpression( "", expression.getType().asString(), arguments));
        } else {
            methodBodyContent.add(astNodeCreator
                    .createCreationExpression(expression.getType().asString(), "", arguments));
        }
    }

    @Override
    public void visit(AssignExpr expression, List<ASTProtos.Expression> methodBodyContent) {
        List<ASTProtos.Expression> values = new ArrayList<>();
        expression.getValue().accept(new MethodBodyExpressionVisitor(), values);
        List<ASTProtos.Expression> rebuiltValues = setAsPostfix(values);
        expression.getTarget().accept(new MethodBodyExpressionVisitor(), rebuiltValues);

        methodBodyContent.add(astNodeCreator
                .createAssignmentExpression(ExpressionType.ASSIGN, "", "", rebuiltValues));
    }

    private List<ASTProtos.Expression> setAsPostfix(List<ASTProtos.Expression> values) {
        List<ASTProtos.Expression> rebuiltValues = new ArrayList<>();
        for (ASTProtos.Expression value : values) {
            rebuiltValues.add(value.toBuilder().setIsPostfix(true).build());
        }
        return rebuiltValues;
    }

    @Override
    public void visit(MethodCallExpr methodCall, List<ASTProtos.Expression> methodBodyContent) {
        List<ASTProtos.Expression> methodArguments = new ArrayList<>();
        for (Expression argument : methodCall.getArguments()) {
            argument.accept(new MethodBodyExpressionVisitor(), methodArguments);
        }

        methodBodyContent.add(astNodeCreator.createMethodCallExpression(methodCall.toString(), methodArguments));
    }
}