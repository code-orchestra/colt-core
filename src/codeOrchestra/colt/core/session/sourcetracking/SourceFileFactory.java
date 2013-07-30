package codeOrchestra.colt.core.session.sourcetracking;

import codeOrchestra.colt.core.COLTService;

import java.io.File;

/**
 * @author Alexander Eliseyev
 */
public interface SourceFileFactory<S extends SourceFile> extends COLTService {

    S createSourceFile(File file, File baseDir);

}
