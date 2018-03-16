package joelbits.modules.preprocessing.parsers.visitors;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.inject.Guice;
import joelbits.model.ast.protobuf.ASTProtos.Expression;
import joelbits.model.ast.protobuf.ASTProtos.Expression.ExpressionType;
import joelbits.modules.preprocessing.parsers.InjectionParserModule;
import joelbits.modules.preprocessing.parsers.utils.ASTNodeCreator;

import javax.inject.Inject;
import java.util.List;

/**
 * A visitor for the assignments performed inside a specific method.
 */
public class MethodBodyAssignmentVisitor extends VoidVisitorAdapter<List<Expression>> {
    @Inject
    private ASTNodeCreator astNodeCreator;

    public MethodBodyAssignmentVisitor() {
        Guice.createInjector(new InjectionParserModule()).injectMembers(this);
    }

    @Override
    public void visit(AssignExpr assignmentExpression, List<Expression> methodBodyAssignments) {
        super.visit(assignmentExpression, methodBodyAssignments);

        String target = assignmentExpression.getTarget().toString();
        String value = assignmentExpression.getValue().toString();
        Expression assignment = astNodeCreator.
                createMethodBodyAssignmentExpression(ExpressionType.ASSIGN, target, value);

        methodBodyAssignments.add(assignment);
    }
}
