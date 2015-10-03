package codeOrchestra.colt.core.rpc.model;

import codeOrchestra.colt.core.model.Project;

/**
 * @author Alexander Eliseyev
 */
public interface ColtRemoteProject<P extends Project> {
    String getType();
    String getName();
    String getPath();
    void copyToProject(P project);
}