package codeOrchestra.colt.core.session.sourcetracking;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public interface SourceFileFactory<S extends SourceFile> {

    S createSourceFile(File file, File baseDir);

}
