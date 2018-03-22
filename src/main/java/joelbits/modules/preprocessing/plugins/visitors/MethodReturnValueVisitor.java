package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos.Expression;
import joelbits.model.ast.protobuf.ASTProtos.Expression.ExpressionType;
import joelbits.modules.preprocessing.plugins.utils.ASTNodeCreator;

import java.util.List;

/**
 * A visitor for the return value of a specific method.
 */
public class MethodReturnValueVisitor extends VoidVisitorAdapter<List<Expression>> {
    private final ASTNodeCreator astNodeCreator = new ASTNodeCreator();

    @Override
    public void visit(ReturnStmt returnStmt, List<Expression> methodBodyContent) {
        super.visit(returnStmt, methodBodyContent);

        for (Node child : returnStmt.getChildNodes()) {
            Expression returnValue = astNodeCreator
                    .createMethodBodyExpression(ExpressionType.RETURN_VALUE, child.toString(), child.toString());
            methodBodyContent.add(returnValue);
        }
    }
}
