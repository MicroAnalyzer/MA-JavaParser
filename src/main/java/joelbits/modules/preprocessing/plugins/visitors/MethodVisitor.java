package joelbits.modules.preprocessing.plugins.visitors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.model.ast.protobuf.ASTProtos.Method;
import joelbits.model.ast.protobuf.ASTProtos.Variable;
import joelbits.modules.preprocessing.plugins.utils.ASTNodeCreator;
import joelbits.modules.preprocessing.plugins.utils.TypeConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A visitor parsing data from the loaded class on a method level.
 */
public final class MethodVisitor extends VoidVisitorAdapter<List<Method>> {
    private final ASTNodeCreator astNodeCreator = new ASTNodeCreator();
    private final TypeConverter typeConverter = new TypeConverter();

    @Override
    public void visit(MethodDeclaration method, List<Method> methods) {
        List<ASTProtos.Modifier> methodModifiers = new ArrayList<>();
        createModifiers(method, methodModifiers);
        createAnnotations(method, methodModifiers);

        List<Variable> arguments = createArguments(method);
        List<ASTProtos.Statement> bodyContent = createBody(method);

        methods.add(astNodeCreator.createMethod(methodModifiers, method.getNameAsString(), method.getType().asString(), arguments, Collections.emptyList(), bodyContent));
    }

    private void createModifiers(MethodDeclaration method, List<ASTProtos.Modifier> methodModifiers) {
        for (Modifier modifier : method.getModifiers()) {
            ASTProtos.Modifier visibility = astNodeCreator.createModifier(modifier.name());
            methodModifiers.add(visibility);
        }
    }

    private void createAnnotations(MethodDeclaration method, List<ASTProtos.Modifier> methodModifiers) {
        for (AnnotationExpr annotationExpr : method.getAnnotations()) {
            List<String> annotationMembers = typeConverter.convertAnnotationMembers(annotationExpr);
            methodModifiers.add(astNodeCreator.createAnnotationModifier(annotationExpr.getNameAsString(), annotationMembers));
        }
    }

    private List<Variable> createArguments(MethodDeclaration method) {
        List<Variable> arguments = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            List<ASTProtos.Modifier> argumentModifiers = typeConverter.convertModifiers(parameter.getModifiers());
            arguments.add(astNodeCreator
                    .createVariable(parameter.getNameAsString(), parameter.getType().asString(), argumentModifiers));
        }
        return arguments;
    }

    private List<ASTProtos.Statement> createBody(MethodDeclaration method) {
        List<ASTProtos.Statement> bodyContent = new ArrayList<>();
        if (method.getBody().isPresent()) {
            method.getBody().get().accept(new MethodBodyStatementVisitor(), bodyContent);
        }
        return bodyContent;
    }
}