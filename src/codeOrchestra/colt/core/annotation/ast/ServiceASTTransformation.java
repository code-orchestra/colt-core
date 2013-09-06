package codeOrchestra.colt.core.annotation.ast;

import codeOrchestra.colt.core.ServiceProvider;
import groovy.transform.TypeChecked;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

/**
 * @author Eugene Potapenko
 */

@SuppressWarnings("GroovyUnusedDeclaration")
@TypeChecked
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class ServiceASTTransformation implements ASTTransformation, Opcodes {

    @Override
    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new GroovyBugError("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes));
        }

        if (nodes[1] instanceof FieldNode) {
            FieldNode fieldNode = (FieldNode) nodes[1];
            Class type;

            try {
                type = Class.forName(fieldNode.getType().getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            Class service = findService(type);
            if(service == null)service = type;

            fieldNode.setInitialValueExpression(new MethodCallExpression(
                    new ClassExpression(
                            ClassHelper.make(ServiceProvider.class)
                    ),
                    new ConstantExpression("get"),
                    new ArgumentListExpression(
                            new ClassExpression(ClassHelper.make(service))
                    )
            ));

        }
    }

    private Class findService(Class type) {
        while (type.getSuperclass() != null) {
            for (Class cl : type.getInterfaces()) {
                for (Class s : ServiceProvider.KNOWN_SERVICES) {
                    if (s == cl) {
                        return  s;
                    }
                }
            }

            type = type.getSuperclass();
        }
        return  null;
    }
}