package codeOrchestra.colt.core.gradle

import codeOrchestra.colt.core.execution.ExecutionException
import codeOrchestra.colt.core.execution.ProcessHandler
import codeOrchestra.util.PathUtils
import codeOrchestra.util.process.ProcessHandlerBuilder

/**
 * @author Alexander Eliseyev
 */
class GradleLauncher {

    static ProcessHandler launch(String scriptFilename, File baseDir, String task, String... parameters) throws ExecutionException {
        ProcessHandlerBuilder builder = new ProcessHandlerBuilder()
                .append(PathUtils.getGradleExecutable().getPath())
                .append("-b " + scriptFilename)
                .append(parameters)
                .append(task)

        return builder.build(baseDir);
    }

}
