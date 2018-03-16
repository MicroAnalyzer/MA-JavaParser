package joelbits.modules.preprocessing.parsers.visitors;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public final class ImportVisitor extends VoidVisitorAdapter<List<String>> {
    @Override
    public void visit(ImportDeclaration importDeclaration, List<String> imports) {
        imports.add(importDeclaration.getNameAsString());
    }
}