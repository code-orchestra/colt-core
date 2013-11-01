package codeOrchestra.colt.core.gradle;

import codeOrchestra.colt.core.model.Project;

import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public interface GradleTask<P extends Project> {

    List<String> getRepositories();

    List<String> getClasspathDependencies();

    List<String> getImports();

    List<String> getPlugins();

    String getType();

    boolean isApplicable(P project);

    List<String> getTaskNames();

    void append(StringBuilder script, GradleTask<P> previousTask, String target);

    List<String> getOutputFiles();

}
