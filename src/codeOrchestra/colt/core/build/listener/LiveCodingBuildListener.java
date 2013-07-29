package codeOrchestra.colt.core.build.listener;

import codeOrchestra.colt.core.session.LiveCodingSession;

/**
 * @author Alexander Eliseyev
 */
public interface LiveCodingBuildListener {

    void onSuccessfulBuild(BuildArtifact buildArtifact);

    void onFailedBaseBuild(LiveCodingSession session);

    void onFailedIncrementalBuild();

}
