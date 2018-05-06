package joelbits.modules.preprocessing.plugins.utils;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import joelbits.model.ast.protobuf.ASTProtos;
import joelbits.model.ast.protobuf.ASTProtos.Modifier.ModifierType;
import joelbits.model.ast.protobuf.ASTProtos.Modifier.VisibilityType;

import java.util.*;

public final class TypeConverter {

    public List<ASTProtos.Modifier> convertModifiers(EnumSet<Modifier> modifiers) {
        List<ASTProtos.Modifier> argumentModifiers = new ArrayList<>();

        for (Modifier modifier : modifiers) {
            ModifierType type = getModifierType(modifier.name());
            ASTProtos.Modifier.Builder builder = ASTProtos.Modifier.newBuilder();

            if (type.equals(ModifierType.VISIBILITY)) {
                argumentModifiers.add(builder
                        .setVisibility(VisibilityType.valueOf(modifier.name()))
                        .setType(type).build());
            } else if (type.equals(ModifierType.OTHER)) {
                argumentModifiers.add(builder
                        .setOther(modifier.name())
                        .setType(type).build());
            } else {
                argumentModifiers.add(builder
                        .setName(modifier.name())
                        .setType(type).build());
            }
        }

        return argumentModifiers;
    }

    private ModifierType getModifierType(String modifier) {
        if (ModifierType.STATIC.name().equals(modifier.toUpperCase())) {
            return ModifierType.STATIC;
        } else if (ModifierType.FINAL.name().equals(modifier.toUpperCase())) {
            return ModifierType.FINAL;
        } else if (ModifierType.ABSTRACT.name().equals(modifier.toUpperCase())) {
            return ModifierType.ABSTRACT;
        } else if (ModifierType.SYNCHRONIZED.name().equals(modifier.toUpperCase())) {
            return ModifierType.SYNCHRONIZED;
        }

        String[] visibilityModifiers = Arrays.asList(VisibilityType.values()).stream().map(Enum::name).toArray(String[]::new);
        if (Arrays.asList(visibilityModifiers).contains(modifier.toUpperCase())) {
            return ModifierType.VISIBILITY;
        }

        return ModifierType.OTHER;
    }

    public List<String> convertAnnotationMembers(AnnotationExpr annotationExpr) {
        List<String> membersAndValues = new ArrayList<>();

        if (annotationExpr.isNormalAnnotationExpr()) {
            NodeList<MemberValuePair> pairs = annotationExpr.asNormalAnnotationExpr().getPairs();
            for (MemberValuePair pair : pairs) {
                String memberName = handleAbsentMembers(annotationExpr, pair.getNameAsString());
                membersAndValues.add(memberName + " " + pair.getValue().toString());
            }
        } else if (annotationExpr.isSingleMemberAnnotationExpr()) {
            SingleMemberAnnotationExpr singleMember = annotationExpr.asSingleMemberAnnotationExpr();
            String memberName = handleAbsentMembers(annotationExpr, singleMember.getNameAsString());
            membersAndValues.add(memberName + " " + singleMember.getMemberValue().toString());
        }

        return membersAndValues;
    }

    /**
     * If an annotation only contains a single value (e.g., an Enum) and no member name the member name attribute
     * should be blank since no member exist.
     *
     * @param annotationExpr        the object corresponding to the annotation
     * @param memberName            the extracted member name
     * @return                      member name if a member exist in annotation, otherwise empty string
     */
    private String handleAbsentMembers(AnnotationExpr annotationExpr, String memberName) {
        if (memberName.equals(annotationExpr.getNameAsString())) {
            memberName = "";
        }
        return memberName;
    }

    public ASTProtos.DeclarationType getDeclarationType(ClassOrInterfaceDeclaration declaration) {
        if (declaration.isInterface()) {
            return ASTProtos.DeclarationType.INTERFACE;
        } else if (declaration.isAnnotationDeclaration()) {
            return ASTProtos.DeclarationType.ANNOTATION;
        } else if (declaration.isEnumDeclaration()) {
            return ASTProtos.DeclarationType.ENUM;
        } else if (declaration.isGeneric()) {
            return ASTProtos.DeclarationType.GENERIC;
        } else if (declaration.isInnerClass() || declaration.isLocalClassDeclaration() || declaration.isClassOrInterfaceDeclaration()) {
            return ASTProtos.DeclarationType.CLASS;
        }

        return ASTProtos.DeclarationType.OTHER;
    }
}
