package codeOrchestra.colt.core.gradle;

import codeOrchestra.colt.core.model.Project;

import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public interface GradleTask<P extends Project> {

    boolean isApplicable(P project);

    List<String> getTaskNames();

    void append(StringBuilder script, GradleTask<P> previousTask);

    List<String> getOutputFiles();

}
