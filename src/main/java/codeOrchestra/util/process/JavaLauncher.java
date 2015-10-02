package codeOrchestra.util.process;

import codeOrchestra.colt.core.execution.ExecutionException;
import codeOrchestra.util.StringUtils;
import codeOrchestra.util.SystemInfo;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Alexander Eliseyev
 */
public class JavaLauncher {

    private File myWorkingDirectory = new File(System.getProperty("user.home"));

    private List<String> myClassPath;

    private String myVirtualMachineParameter;
    private String myProgramParameter;

    private String myJrePath = JavaLauncher.getJdkHome();

    public JavaLauncher(List<String> classPath) {
        myClassPath = classPath;
    }

    public void setWorkingDirectory(File workingDirectory) {
        myWorkingDirectory = workingDirectory;
    }

    public void setVirtualMachineParameter(String virtualMachineParameter) {
        myVirtualMachineParameter = virtualMachineParameter;
    }

    public void setProgramParameter(String programParameter) {
        myProgramParameter = programParameter;
    }

    public ProcessBuilder createProcessBuilder() {
        List<String> commandLine = getCommand();

        ProcessBuilder builder = new ProcessBuilder(commandLine);
        builder.directory(myWorkingDirectory);

        return builder;
    }

    private List<String> getCommand() {
        String java;
        try {
            java = JavaLauncher.getJavaCommand(myJrePath);
        } catch (ExecutionException e) {
            throw new RuntimeException("Can't locate a Java executable", e);
        }

        try {
            new File(java).setExecutable(true, true);
        } catch (Throwable ignored) {
        }

        String classPathString = protect(StringUtils.join(myClassPath, File.pathSeparator));

        ProcessHandlerBuilder processHandlerBuilder = new ProcessHandlerBuilder().append(java).append(myVirtualMachineParameter).appendKey("classpath", classPathString).append(myProgramParameter);
        return processHandlerBuilder.getCommandLine();
    }

    public static List<String> getJavaHomes() {
        String systemJavaHome = System.getProperty("java.home");
        List<String> homes = new LinkedList<>();
        String systemJdkHome = systemJavaHome.substring(0, systemJavaHome.length() - "/jre".length());
        if (systemJavaHome.endsWith("jre") && new File(systemJdkHome + File.separator + "bin").exists()) {
            homes.add(systemJdkHome);
        }
        if (StringUtils.isNotEmpty(System.getenv("JAVA_HOME"))) {
            homes.add(System.getenv("JAVA_HOME"));
        }
        homes.add(systemJavaHome);
        return homes;
    }

    public static String getJavaCommand(String javaHome) throws ExecutionException {
        if (StringUtils.isEmpty(javaHome) || !(new File(javaHome).exists())) {
            javaHome = JavaLauncher.getJdkHome();
        }
        if (StringUtils.isEmpty(javaHome)) {
            throw new ExecutionException("Could not find valid java home.");
        }
        return JavaLauncher.protect(JavaLauncher.getJavaCommandUnprotected(javaHome));
    }

    public static String getJdkHome() {
        List<String> homes = JavaLauncher.getJavaHomes();
        for (String javaHome : homes) {
            if (new File(JavaLauncher.getJavaCommandUnprotected(javaHome)).exists()) {
                return javaHome;
            }
        }
        return null;
    }

    public static String getJavaCommandUnprotected(String javaHome) {
        String result = javaHome + File.separator + "bin" + File.separator;
        String java = "java";
        if (SystemInfo.isMac) {
            result += java;
        } else if (SystemInfo.isWindows) {
            result += java + ".exe";
        } else {
            result += java;
        }
        return result;
    }

    public static String protect(String result) {
        if (result.contains(" ")) {
            return "\"" + result + "\"";
        }
        return result;
    }

}
