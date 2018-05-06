package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos.DeclarationType;
import joelbits.model.ast.protobuf.ASTProtos.Variable;
import joelbits.model.ast.protobuf.ASTProtos.Modifier;
import joelbits.model.ast.protobuf.ASTProtos.Declaration;
import joelbits.model.ast.protobuf.ASTProtos.Method;
import joelbits.modules.preprocessing.plugins.utils.TypeConverter;
import joelbits.modules.preprocessing.utils.ASTNodeCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * A visitor for top-level classes and interfaces.
 */
public final class ClassOrInterfaceVisitor extends VoidVisitorAdapter<List<Declaration>> {
    private List<Declaration> namespaceDeclarations;
    private final ASTNodeCreator astNodeCreator = new ASTNodeCreator();
    private final TypeConverter typeConverter = new TypeConverter();

    public ClassOrInterfaceVisitor() {}

    public ClassOrInterfaceVisitor(List<Declaration> namespaceDeclarations) {
        this.namespaceDeclarations = namespaceDeclarations;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, List<Declaration> nestedDeclarations) {
        List<Variable> allFields = new ArrayList<>();
        List<Method> allMethods = new ArrayList<>();

        for (BodyDeclaration member : declaration.getMembers()) {
            if (member.isMethodDeclaration()) {
                member.accept(new MethodVisitor(), allMethods);
            }
            if (member.isFieldDeclaration()) {
                member.accept(new FieldVisitor(), allFields);
            }
            if (member.isClassOrInterfaceDeclaration()) {
                member.accept(new ClassOrInterfaceVisitor(), nestedDeclarations);
            }
        }

        List<Modifier> modifiers = new ArrayList<>();
        declaration.accept(new DeclarationModifierVisitor(), modifiers);

        DeclarationType type = typeConverter.getDeclarationType(declaration);
        if (declaration.isTopLevelType()) {
            namespaceDeclarations.add(astNodeCreator.createNamespaceDeclaration(declaration.getNameAsString(), type, allFields, allMethods, modifiers, nestedDeclarations));
            nestedDeclarations.clear();
        } else {
            nestedDeclarations.add(astNodeCreator.createNestedDeclaration(declaration.getNameAsString(), type, allFields, allMethods, modifiers));
        }
    }
}