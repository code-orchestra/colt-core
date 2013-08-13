package codeOrchestra.colt.core.rpc.command;

import codeOrchestra.colt.core.controller.COLTControllerCallbackEx;

/**
 * @author Alexander Eliseyev
 */
public interface RemoteAsyncCommand<T> {
    String getName();

    void execute(COLTControllerCallbackEx<T> callback);
}

