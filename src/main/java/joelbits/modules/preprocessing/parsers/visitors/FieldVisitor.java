package joelbits.modules.preprocessing.parsers.visitors;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.inject.Guice;
import joelbits.model.ast.protobuf.ASTProtos.Modifier;
import joelbits.model.ast.protobuf.ASTProtos.Variable;
import joelbits.modules.preprocessing.parsers.InjectionParserModule;
import joelbits.modules.preprocessing.parsers.utils.ASTNodeCreator;
import joelbits.modules.preprocessing.parsers.utils.TypeConverter;

import javax.inject.Inject;
import java.util.List;

public class FieldVisitor extends VoidVisitorAdapter<List<Variable>> {
    @Inject
    private ASTNodeCreator astNodeCreator;
    @Inject
    private TypeConverter typeConverter;

    public FieldVisitor() {
        Guice.createInjector(new InjectionParserModule()).injectMembers(this);
    }

    @Override
    public void visit(FieldDeclaration field, List<Variable> fieldsInDeclaration) {
        List<Modifier> modifiers = typeConverter.convertModifiers(field.getModifiers());

        for (AnnotationExpr annotationExpr : field.getAnnotations()) {
            List<String> annotationMembers = typeConverter.convertAnnotationMembers(annotationExpr);
            modifiers.add(astNodeCreator.createAnnotationModifier(annotationExpr, annotationMembers));
        }

        for (VariableDeclarator test : field.getVariables()) {
            fieldsInDeclaration.add(astNodeCreator.createVariable(test.getName().asString(), field.getElementType().asString(), modifiers));
        }
    }
}