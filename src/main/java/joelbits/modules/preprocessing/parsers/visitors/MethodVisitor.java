package joelbits.modules.preprocessing.parsers.visitors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.inject.Guice;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.model.ast.protobuf.ASTProtos.Method;
import joelbits.model.ast.protobuf.ASTProtos.Variable;
import joelbits.model.ast.protobuf.ASTProtos.Expression;
import joelbits.modules.preprocessing.parsers.InjectionParserModule;
import joelbits.modules.preprocessing.parsers.utils.ASTNodeCreator;
import joelbits.modules.preprocessing.parsers.utils.TypeConverter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * A visitor parsing data from the loaded class on a method level.
 */
public final class MethodVisitor extends VoidVisitorAdapter<List<Method>> {
    @Inject
    private ASTNodeCreator astNodeCreator;
    @Inject
    private TypeConverter typeConverter;

    public MethodVisitor() {
        Guice.createInjector(new InjectionParserModule()).injectMembers(this);
    }

    @Override
    public void visit(MethodDeclaration method, List<Method> methods) {
        List<ASTProtos.Modifier> methodModifiers = new ArrayList<>();

        for (Modifier modifier : method.getModifiers()) {
            ASTProtos.Modifier visibility = astNodeCreator.createModifier(modifier.name());
            methodModifiers.add(visibility);
        }

        for (AnnotationExpr annotationExpr : method.getAnnotations()) {
            List<String> annotationMembers = typeConverter.convertAnnotationMembers(annotationExpr);
            methodModifiers.add(astNodeCreator.createAnnotationModifier(annotationExpr, annotationMembers));
        }

        List<Variable> arguments = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            List<ASTProtos.Modifier> argumentModifiers = typeConverter.convertModifiers(parameter.getModifiers());
            arguments.add(astNodeCreator.createVariable(parameter.getNameAsString(), parameter.getType().asString(), argumentModifiers));
        }

        List<Expression> bodyContent = new ArrayList<>();
        method.accept(new MethodBodyAssignmentVisitor(), bodyContent);
        method.accept(new MethodBodyVariableDeclarationVisitor(), bodyContent);
        method.accept(new MethodCallVisitor(), bodyContent);

        methods.add(astNodeCreator.createMethod(methodModifiers, method, arguments, bodyContent));
    }
}