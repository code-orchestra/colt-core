package codeOrchestra.colt.core.ui.testmode

import codeOrchestra.colt.core.execution.OSProcessHandler
import codeOrchestra.colt.core.execution.ProcessHandler
import codeOrchestra.colt.core.execution.ProcessHandlerWrapper
import codeOrchestra.util.process.ProcessHandlerBuilder

/**
 * @author Dima Kruk
 */
class GitHelper {
    File baseDir

    protected int commitCount = 1

    GitHelper(File baseDir) {
        this.baseDir = baseDir
    }

    public void init() {
        executeProcess(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "init")
                        .build(baseDir),
                true))
    }

    public ArrayList<String> getCommints() {
        ArrayList<String> result = new ArrayList<>()
        Process process = new ProcessBuilder().command("git", "log", '--pretty=format:"%h:%s"').directory(baseDir).start()

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.inputStream))
        String line
        while ((line = reader.readLine()) != null) {
            result.add(line)
        }
        reader.close()

        return result.reverse()
    }

    public void addDirectories(List<String> paths) {
        ProcessHandlerBuilder builder = new ProcessHandlerBuilder()
        builder.append("git", "add")
        paths.each {
            builder = builder.append(it + "/*")
        }
        executeProcess(new ProcessHandlerWrapper(
                builder.build(baseDir),
                true))
    }

    public void createBranch(String name, boolean checkout = false) {
        if (checkout) {
            executeProcess(new ProcessHandlerWrapper(
                    new ProcessHandlerBuilder()
                            .append("git", "checkout", "-b", name)
                            .build(baseDir),
                    true))
        } else {
            executeProcess(new ProcessHandlerWrapper(
                    new ProcessHandlerBuilder()
                            .append("git", "branch", name)
                            .build(baseDir),
                    true))
        }
    }

    public void createBranch(String name, String fromBranch) {
        executeProcess(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "checkout", "-b", name, fromBranch)
                        .build(baseDir),
                true))
    }

    public void deleteBranch(String name) {
        executeProcess(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "branch", "-d", name)
                        .build(baseDir),
                true))
    }

    public void makeCommit(String name) {
        executeProcess(new ProcessHandlerWrapper(
                new OSProcessHandler(baseDir, "git", "commit", "-m", name),
                true))
    }

    public void makeCommit() {
        executeProcess(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "add", "-u")
                        .build(baseDir),
                true))
        executeProcess(new ProcessHandlerWrapper(
                new OSProcessHandler(baseDir, "git", "commit", "-m", "commit " + commitCount),
                true))

        commitCount++
    }

    public void checkoutCommit(String commit) {
        executeProcess(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "checkout", commit)
                        .build(baseDir),
                true))
    }

    public void checkoutBranch(String branch) {
        executeProcess(new ProcessHandlerWrapper(
                new ProcessHandlerBuilder()
                        .append("git", "checkout", branch)
                        .build(baseDir),
                true))
    }

    static protected executeProcess(ProcessHandlerWrapper wrapper) {
        ProcessHandler processHandler = wrapper.getProcessHandler()
        processHandler.addProcessListener(new GitProcessListener())
        processHandler.startNotify()
        if (wrapper.mustWaitForExecutionEnd()) {
            processHandler.waitFor();
        }
    }
}
