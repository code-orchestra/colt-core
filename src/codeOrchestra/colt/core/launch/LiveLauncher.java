package codeOrchestra.colt.core.launch;

import codeOrchestra.colt.core.COLTService;
import codeOrchestra.colt.core.execution.ExecutionException;
import codeOrchestra.colt.core.execution.ProcessHandlerWrapper;
import codeOrchestra.colt.core.model.COLTProject;

/**
 * @author Alexander Eliseyev
 */
public interface LiveLauncher<P extends COLTProject> extends COLTService<P> {

    ProcessHandlerWrapper launch(P project) throws ExecutionException;

}
