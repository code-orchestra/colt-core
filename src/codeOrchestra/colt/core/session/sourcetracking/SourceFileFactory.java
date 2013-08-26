package codeOrchestra.colt.core.session.sourcetracking;

import codeOrchestra.colt.core.ColtService;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public interface SourceFileFactory<S extends SourceFile> extends ColtService {

    S createSourceFile(File file, File baseDir);

}
