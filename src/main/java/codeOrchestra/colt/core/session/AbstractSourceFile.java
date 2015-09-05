package codeOrchestra.colt.core.session;

import codeOrchestra.colt.core.session.sourcetracking.SourceFile;
import codeOrchestra.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractSourceFile implements SourceFile {

    protected File file;
    private String checkSum;

    protected AbstractSourceFile(File file) {
        this.file = file;
    }

    @Override
    public void updateChecksum() {
        try {
            this.checkSum = FileUtils.getFileDigestMD5(file);
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public String getChecksum() {
        if (checkSum == null) {
            updateChecksum();
        }
        return checkSum;
    }
}
