package joelbits.modules.preprocessing.parsers.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.inject.Guice;
import joelbits.model.ast.protobuf.ASTProtos.Modifier;
import joelbits.modules.preprocessing.parsers.InjectionParserModule;
import joelbits.modules.preprocessing.parsers.utils.ASTNodeCreator;
import joelbits.modules.preprocessing.parsers.utils.TypeConverter;

import javax.inject.Inject;
import java.util.List;

public class DeclarationModifierVisitor extends VoidVisitorAdapter<List<Modifier>> {
    @Inject
    private ASTNodeCreator astNodeCreator;
    @Inject
    private TypeConverter typeConverter;

    public DeclarationModifierVisitor() {
        Guice.createInjector(new InjectionParserModule()).injectMembers(this);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, List<Modifier> modifiers) {
        for (AnnotationExpr topLevelAnnotation : declaration.getAnnotations()) {
            List<String> membersAndValues = typeConverter.convertAnnotationMembers(topLevelAnnotation);
            modifiers.add(astNodeCreator.createAnnotationModifier(topLevelAnnotation, membersAndValues));
        }

        for (com.github.javaparser.ast.Modifier topLevelModifier : declaration.getModifiers()) {
            modifiers.add(astNodeCreator.createModifier(topLevelModifier.name()));
        }
    }
}
