package codeOrchestra.colt.core.annotation.ast

import codeOrchestra.groovyfx.FXBindable
import groovy.transform.TypeChecked
import javafx.beans.property.*
import javafx.collections.FXCollections
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.runtime.MetaClassHelper
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.objectweb.asm.Opcodes

import static codeOrchestra.groovyfx.ast.PropertyClassUtil.*
import static org.codehaus.groovy.ast.ClassHelper.getWrapper
import static org.codehaus.groovy.ast.ClassHelper.make

/**
 * @author jimclarke (inspired by Danno Ferrin (shemnon) and Chris Reeved)
 * @author Dean Iverson
 * @author Eugene Potapenko
 */

@SuppressWarnings("GroovyUnusedDeclaration")
@TypeChecked
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class ServiceASTTransformation implements ASTTransformation, Opcodes {

    private static final ClassNode BOUND_CLASS_NODE = make(FXBindable);

    private static final ClassNode OBSERVABLE_LIST = make(javafx.collections.ObservableList, true);
    private static final ClassNode OBSERVABLE_MAP = make(javafx.collections.ObservableMap, true);
    private static final ClassNode OBSERVABLE_SET = make(javafx.collections.ObservableSet, true);
    private static final ClassNode FX_COLLECTIONS_TYPE = make(FXCollections, true);
    private static final ClassNode LIST_TYPE = make(List, true);
    private static final ClassNode MAP_TYPE = make(Map, true);
    private static final ClassNode SET_TYPE = make(Set, true);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: wrong types: ${nodes[0].class} / ${nodes[1].class}");
        }

        AnnotationNode node = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];
        ClassNode declaringClass = parent.declaringClass;

        if (parent instanceof FieldNode) {
            FieldNode fieldNode = parent as FieldNode


        }
    }
}