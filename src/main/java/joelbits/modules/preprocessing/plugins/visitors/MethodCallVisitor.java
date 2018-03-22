package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.modules.preprocessing.plugins.utils.ASTNodeCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * A visitor parsing data about each method invocation performed inside a specific method.
 */
public final class MethodCallVisitor extends VoidVisitorAdapter<List<ASTProtos.Expression>> {
    private final ASTNodeCreator astNodeCreator = new ASTNodeCreator();

    @Override
    public void visit(MethodCallExpr methodCall, List<ASTProtos.Expression> methodBodyContent) {
        super.visit(methodCall, methodBodyContent);

        List<ASTProtos.Expression> methodArguments = new ArrayList<>();
        for (Expression parameter : methodCall.getArguments()) {
            methodArguments.add(astNodeCreator.createMethodCallArgumentExpression(parameter.toString()));
        }

        methodBodyContent.add(astNodeCreator.createMethodCallExpression(methodCall, methodArguments));
    }
}