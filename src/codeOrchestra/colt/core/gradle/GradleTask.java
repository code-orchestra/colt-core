package codeOrchestra.colt.core.gradle;

import codeOrchestra.colt.core.model.Project;

/**
 * @author Alexander Eliseyev
 */
public interface GradleTask<P extends Project> {

    boolean isApplicable(P project);

    String getName();

    void append(StringBuilder script);

}
