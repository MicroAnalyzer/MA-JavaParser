package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos.Expression;
import joelbits.model.ast.protobuf.ASTProtos.Expression.ExpressionType;
import joelbits.modules.preprocessing.plugins.utils.ASTNodeCreator;

import java.util.List;

/**
 * A visitor for the assignments performed inside a specific method.
 */
public class MethodBodyAssignmentVisitor extends VoidVisitorAdapter<List<Expression>> {
    private final ASTNodeCreator astNodeCreator = new ASTNodeCreator();

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