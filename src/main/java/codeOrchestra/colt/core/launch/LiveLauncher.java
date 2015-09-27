package codeOrchestra.colt.core.launch;

import codeOrchestra.colt.core.ColtService;
import codeOrchestra.colt.core.execution.ExecutionException;
import codeOrchestra.colt.core.execution.ProcessHandlerWrapper;
import codeOrchestra.colt.core.model.Project;

/**
 * @author Alexander Eliseyev
 */
public interface LiveLauncher<P extends Project> extends ColtService {

    ProcessHandlerWrapper launch(P project, boolean multiple, boolean production) throws ExecutionException;

}
