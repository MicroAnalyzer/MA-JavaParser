package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.model.ast.protobuf.ASTProtos.Statement.StatementType;
import joelbits.modules.preprocessing.plugins.utils.ASTNodeCreator;

import java.util.*;

/**
 * A visitor for statements inside a method body.
 */
public final class MethodBodyStatementVisitor extends VoidVisitorAdapter<List<ASTProtos.Statement>> {
    private final ASTNodeCreator astNodeCreator = new ASTNodeCreator();

    @Override
    public void visit(IfStmt statement, List<ASTProtos.Statement> methodBodyContent) {
        List<ASTProtos.Expression> conditions = extractCondition(statement);
        List<ASTProtos.Statement> ifBody = extractIfBody(statement);

        methodBodyContent.add(astNodeCreator
                .createStatement(StatementType.IF, conditions.get(0), ifBody));
    }

    private List<ASTProtos.Expression> extractCondition(IfStmt statement) {
        List<ASTProtos.Expression> conditions = new ArrayList<>();
        statement.getCondition().accept(new MethodBodyExpressionVisitor(), conditions);
        return conditions;
    }

    private List<ASTProtos.Statement> extractIfBody(IfStmt statement) {
        List<ASTProtos.Statement> ifBody = new ArrayList<>();
        if (statement.hasElseBlock() && statement.getElseStmt().isPresent()) {
            statement.getElseStmt().get().accept(new MethodBodyStatementVisitor(), ifBody);
        }
        if (statement.hasThenBlock()) {
            List<ASTProtos.Expression> statementContent = new ArrayList<>();
            List<ASTProtos.Statement> statements = new ArrayList<>();
            for (Statement stmt : statement.getThenStmt().asBlockStmt().getStatements()) {
                if (stmt.isExpressionStmt()) {
                    stmt.asExpressionStmt().accept(new MethodBodyExpressionVisitor(), statementContent);
                } else {
                    stmt.accept(new MethodBodyStatementVisitor(), statements);
                }
            }
            ifBody.add(astNodeCreator.createBlockStatement(statementContent, statements));
        }

        return ifBody;
    }

    @Override
    public void visit(ReturnStmt statement, List<ASTProtos.Statement> methodBodyContent) {
        List<ASTProtos.Expression> returnValues = new ArrayList<>();
        if (statement.getExpression().isPresent()) {
            statement.getExpression().get().accept(new MethodBodyExpressionVisitor(), returnValues);
        }

        methodBodyContent.add(astNodeCreator
                .createReturnStatement(returnValues));
    }

    @Override
    public void visit(DoStmt statement, List<ASTProtos.Statement> methodBodyContent) {
        List<ASTProtos.Statement> doBody = extractDoBody(statement);
        List<ASTProtos.Expression> statementContent = extractCondition(statement);

        methodBodyContent.add(astNodeCreator
                .createStatement(StatementType.DO, statementContent.get(0), doBody));
    }

    private List<ASTProtos.Statement> extractDoBody(DoStmt statement) {
        List<ASTProtos.Statement> doBody = new ArrayList<>();
        Statement body = statement.getBody();
        body.accept(new MethodBodyStatementVisitor(), doBody);
        return doBody;
    }

    private List<ASTProtos.Expression> extractCondition(DoStmt statement) {
        List<ASTProtos.Expression> statementContent = new ArrayList<>();
        Expression condition = statement.getCondition();
        condition.accept(new MethodBodyExpressionVisitor(), statementContent);
        return statementContent;
    }

    @Override
    public void visit(ForStmt statement, List<ASTProtos.Statement> methodBodyContent) {
        List<ASTProtos.Expression> compares = extractCompareExpression(statement);
        List<ASTProtos.Expression> initializations = extractLoopInitializations(statement);
        List<ASTProtos.Expression> updateContent = extractForLoopUpdates(statement);
        List<ASTProtos.Statement> nestedStatements = extractLoopBody(statement);

        methodBodyContent.add(astNodeCreator
                .createStatement(StatementType.FOR, compares, ASTProtos.Expression.getDefaultInstance(), nestedStatements, initializations, updateContent));
    }

    private List<ASTProtos.Expression> extractCompareExpression(ForStmt statement) {
        List<ASTProtos.Expression> compares = new ArrayList<>();
        Optional<Expression> compare = statement.getCompare();
        compare.ifPresent(expression -> expression.accept(new MethodBodyExpressionVisitor(), compares));
        return compares;
    }

    private List<ASTProtos.Expression> extractLoopInitializations(ForStmt statement) {
        List<ASTProtos.Expression> initializations = new ArrayList<>();
        List<Expression> initialization = statement.getInitialization();
        for (Expression init : initialization) {
            init.accept(new MethodBodyExpressionVisitor(), initializations);
        }
        return initializations;
    }

    private List<ASTProtos.Expression> extractForLoopUpdates(ForStmt statement) {
        List<ASTProtos.Expression> updateContent = new ArrayList<>();
        List<Expression> updates = statement.getUpdate();
        for (Expression update : updates) {
            update.accept(new MethodBodyExpressionVisitor(), updateContent);
        }
        return updateContent;
    }

    private List<ASTProtos.Statement> extractLoopBody(ForStmt statement) {
        Statement body = statement.getBody();
        List<ASTProtos.Statement> nestedStatements = new ArrayList<>();
        body.accept(new MethodBodyStatementVisitor(), nestedStatements);
        return nestedStatements;
    }

    @Override
    public void visit(BlockStmt statement, List<ASTProtos.Statement> methodBodyContent) {
        for (Statement stmt : statement.getStatements()) {
            List<ASTProtos.Expression> statementContent = new ArrayList<>();
            List<ASTProtos.Statement> statements = new ArrayList<>();
            if (stmt.isExpressionStmt()) {
                stmt.asExpressionStmt().accept(new MethodBodyExpressionVisitor(), statementContent);
            } else {
                stmt.accept(new MethodBodyStatementVisitor(), statements);
            }
            methodBodyContent.add(astNodeCreator.createBlockStatement(statementContent, statements));
        }
    }

    @Override
    public void visit(TryStmt statement, List<ASTProtos.Statement> methodBodyContent) {
        List<ASTProtos.Statement> tryBody = new ArrayList<>();
        statement.getTryBlock().accept(new MethodBodyStatementVisitor(), tryBody);

        methodBodyContent.add(astNodeCreator
                .createTryStatement(tryBody));
    }

    @Override
    public void visit(WhileStmt statement, List<ASTProtos.Statement> methodBodyContent) {
        List<ASTProtos.Statement> bodyStatements = extractLoopBody(statement);
        List<ASTProtos.Expression> statementContent = extractLoopCondition(statement);

        methodBodyContent.add(astNodeCreator
                .createStatement(StatementType.WHILE, statementContent.get(0), bodyStatements));
    }

    private List<ASTProtos.Expression> extractLoopCondition(WhileStmt statement) {
        Expression condition = statement.getCondition();
        List<ASTProtos.Expression> statementContent = new ArrayList<>();
        condition.accept(new MethodBodyExpressionVisitor(), statementContent);
        return statementContent;
    }

    private List<ASTProtos.Statement> extractLoopBody(WhileStmt statement) {
        List<ASTProtos.Statement> bodyStatements = new ArrayList<>();
        Statement body = statement.getBody();
        body.accept(new MethodBodyStatementVisitor(), bodyStatements);
        return bodyStatements;
    }
}
