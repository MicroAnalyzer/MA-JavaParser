package joelbits.modules.preprocessing.parsers.visitors;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.inject.Guice;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.modules.preprocessing.parsers.InjectionParserModule;
import joelbits.modules.preprocessing.parsers.utils.ASTNodeCreator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * A visitor parsing data about each method invocation performed inside a specific method.
 */
public final class MethodCallVisitor extends VoidVisitorAdapter<List<ASTProtos.Expression>> {
    @Inject
    private ASTNodeCreator astNodeCreator;

    public MethodCallVisitor() {
        Guice.createInjector(new InjectionParserModule()).injectMembers(this);
    }

    @Override
    public void visit(MethodCallExpr methodCall, List<ASTProtos.Expression> methodInvocations) {
        super.visit(methodCall, methodInvocations);

        List<ASTProtos.Expression> methodArguments = new ArrayList<>();
        for (Expression parameter : methodCall.getArguments()) {
            methodArguments.add(astNodeCreator.createMethodCallArgumentExpression(parameter.toString()));
        }

        methodInvocations.add(astNodeCreator.createMethodCallExpression(methodCall, methodArguments));
    }
}