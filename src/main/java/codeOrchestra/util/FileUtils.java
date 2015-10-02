package codeOrchestra.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Alexander Eliseyev
 */
public class FileUtils {

    private static final String[] IGNORED_DIRS = new String[]{".svn", ".git", "_svn"};

    public static final FileFilter FILES_ONLY_FILTER = File::isFile;

    public static final FileFilter DIRECTORY_FILTER = File::isDirectory;

    public static String getFileDigestMD5(File file) throws IOException {
        FileChannel fileChannel = new FileInputStream(file).getChannel();
        int size = (int) fileChannel.size();
        byte[] bytes = new byte[size];

        ByteBuffer mappedFile = ByteBuffer.allocate(size);
        fileChannel.read(mappedFile);
        fileChannel.close();

        mappedFile.position(0);
        mappedFile.get(bytes);

        return DigestUtils.md5Hex(bytes);
    }

    public static String normalize(String path) {
        if (SystemInfo.isWindows) {
            return path.replace("/", "\\");
        }
        return path.replace("\\", "/");
    }

    public static void makeExecutable(String path) {
        if (!SystemInfo.isWindows) {
            try {
                new File(path).setExecutable(true, true);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public static String getFileExtension(File file) {
        if (file.isDirectory()) {
            return null;
        }

        String fileName = file.getName();
        if (fileName.lastIndexOf('.') > 0) {
            return fileName.substring(fileName.lastIndexOf('.') +1);
        }
        return null;
    }

    public static boolean clear(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return true;

        boolean result = true;

        for (File f : files) {
            boolean r = delete(f);
            result = result && r;
        }

        return result;
    }

    public static boolean delete(File root) {
        boolean result = true;

        if (root.isDirectory()) {
            for (File child : root.listFiles()) {
                result = delete(child) && result;
            }
        }
        // !result means one of children was not deleted, hence you may not delete this directory
        return result && root.delete();
    }

    public static File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static ArrayList<String> copyDir(File what, File to, boolean checkEquals) {
        return copyDir(what, to, checkEquals, null);
    }

    /*
     * Returns list of file paths skipped due to CJ-925.
     */
    public static ArrayList<String> copyDir(File what, File to, boolean checkEquals, List<String> excludes) {
        ArrayList<String> skippedFiles = new ArrayList<>();

        assert what.isDirectory();
        if (!to.exists()) {
            to.mkdir();
        }

        for (File f : what.listFiles()) {
            if (f.isDirectory()) {

                if (isIgnoredDir(f.getName())) continue;

                File fCopy = new File(to, f.getName());
                if (!fCopy.exists()) {
                    fCopy.mkdir();
                }

                skippedFiles.addAll(copyDir(f, fCopy, checkEquals, excludes));
            }

            if (f.isFile()) {
                try {
                    if (excludes != null && excludes.contains(f.getPath())) {
                        continue;
                    }

                    // CJ-925
                    if ((f.length() > 52428800) && !f.getName().toLowerCase().endsWith("js")) {
                        skippedFiles.add(f.getAbsolutePath());
                        continue;
                    }

                    copyFileChecked(f, to, checkEquals);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return skippedFiles;
    }

    public static boolean isIgnoredDir(String name) {
        for (String ignoredDir : IGNORED_DIRS) {
            if (ignoredDir.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static void copyFileWithIncrementalReplaces(File f, File to) throws IOException {
        File target;
        if (to.isDirectory()) {
            target = new File(to, f.getName());
        } else {
            target = to;
        }

        if (!to.getParentFile().exists()) {
            to.getParentFile().mkdirs();
        }

        String content = FileUtils.read(f);
        content = content.replaceAll("\\[\\s*Embed.+\\]", "");
        write(target, content);
    }

    public static void copyFileChecked(File f, File to, boolean checkEquals) throws IOException {
        File target;
        if (to.isDirectory()) {
            target = new File(to, f.getName());
        } else {
            target = to;
        }

        if (!to.getParentFile().exists()) {
            to.getParentFile().mkdirs();
        }

        if (checkEquals && target.exists()) {
            String existingContents = FileUtils.read(target);
            if (existingContents.equals(FileUtils.read(f))) {
                return;
            }
        }

        copyFile(f, target);
    }

    /**
     * Faster copying method from http://www.javalobby.org/java/forums/t17036.html
     */
    public static void copyFile (File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String read(File file) {
        try {
            return read(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String read(Reader reader) {
        BufferedReader r = null;
        try {
            r = new BufferedReader(reader);

            StringBuilder result = new StringBuilder();

            String line;
            while ((line = r.readLine()) != null) {
                result.append(line).append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void write(File file, String content) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }

            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            out.write(content);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<File> listFileRecursively(File dir, FileFilter fileFilter) {
        assert dir.isDirectory();
        List<File> files = new ArrayList<>();

        File[] subdirs = dir.listFiles(f -> f.isDirectory() && !f.getName().matches("\\..*"));

        if (subdirs != null) {
            for (File subdir : subdirs) {
                files.addAll(listFileRecursively(subdir, fileFilter));
            }
            addArrayToList(files, dir.listFiles(fileFilter));
        }

        return files;
    }

    public static boolean doesExist(String path) {
        return new File(path).exists();
    }

    public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {
        String[] base = basePath.split(Pattern.quote(pathSeparator));
        String[] target = targetPath.split(Pattern.quote(pathSeparator));

        StringBuilder common = new StringBuilder();

        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length
                && target[commonIndex].equals(base[commonIndex])) {
            common.append(target[commonIndex]).append(pathSeparator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            return null;
        }

        if (target.length == commonIndex && base.length == target.length) {
            return "";
        }

        boolean baseIsFile = true;

        File baseResource = new File(basePath);

        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();

        } else if (basePath.endsWith(pathSeparator)) {
            baseIsFile = false;
        }

        StringBuilder relative = new StringBuilder();

        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                relative.append("..").append(pathSeparator);
            }
        }
        relative.append(targetPath.substring(common.length()));
        return relative.toString();
    }

    private static void addArrayToList(List<File> list, File[] array) {
        Collections.addAll(list, array);
    }

    public static String protect(String path) {
       if (path.contains(" ")) {
         return "\"" + path + "\"";
       }
       return path;
    }

     public static String getResourceContent(URL location) {
        String content = null;
        try {
            InputStream input = location.openStream();
            InputStreamReader is = new InputStreamReader(input);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();
            while (read != null) {
                sb.append(read).append("\n");
                read = br.readLine();
            }
            content = sb.toString();
        } catch (IOException e) {
            System.err.println(e);
        }
        return content;
    }
}