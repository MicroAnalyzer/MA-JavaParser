package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos.Modifier;
import joelbits.modules.preprocessing.plugins.utils.ASTNodeCreator;
import joelbits.modules.preprocessing.plugins.utils.TypeConverter;

import java.util.List;

/**
 * A visitor for top-level declarations in a class.
 */
public final class DeclarationModifierVisitor extends VoidVisitorAdapter<List<Modifier>> {
    private final ASTNodeCreator astNodeCreator = new ASTNodeCreator();
    private final TypeConverter typeConverter = new TypeConverter();

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, List<Modifier> modifiers) {
        for (AnnotationExpr topLevelAnnotation : declaration.getAnnotations()) {
            List<String> membersAndValues = typeConverter.convertAnnotationMembers(topLevelAnnotation);
            modifiers.add(astNodeCreator.createAnnotationModifier(topLevelAnnotation.getNameAsString(), membersAndValues));
        }

        for (com.github.javaparser.ast.Modifier topLevelModifier : declaration.getModifiers()) {
            modifiers.add(astNodeCreator.createModifier(topLevelModifier.name()));
        }
    }
}
