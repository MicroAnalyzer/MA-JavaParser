package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos.Declaration;
import joelbits.model.ast.protobuf.ASTProtos.Namespace;

import java.util.List;

public final class NamespaceVisitor extends VoidVisitorAdapter<List<Declaration>> {
    private final List<Namespace> namespaces;

    public NamespaceVisitor(List<Namespace> namespaces) {
        this.namespaces = namespaces;
    }

    @Override
    public void visit(PackageDeclaration namespace, List<Declaration> declarations) {
        Namespace packageDeclaration = Namespace.newBuilder()
                .setName(namespace.getNameAsString())
                .addAllDeclarations(declarations)
                .build();

        namespaces.add(packageDeclaration);
    }
}