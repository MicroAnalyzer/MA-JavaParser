package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

/**
 * A visitor for import statements in a class.
 */
public final class ImportVisitor extends VoidVisitorAdapter<List<String>> {
    @Override
    public void visit(ImportDeclaration importDeclaration, List<String> imports) {
        imports.add(importDeclaration.getNameAsString());
    }
}