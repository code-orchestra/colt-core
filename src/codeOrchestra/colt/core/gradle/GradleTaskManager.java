package codeOrchestra.colt.core.gradle;

import codeOrchestra.colt.core.ColtService;
import codeOrchestra.colt.core.model.Project;

import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public interface GradleTaskManager<P extends Project> extends ColtService<P> {

    List<GradleTask<P>> getTasks(String type);

    /**
     * @return list of names of tasks applied
     */
    List<String> appendTasks(StringBuilder script, String type);

}
