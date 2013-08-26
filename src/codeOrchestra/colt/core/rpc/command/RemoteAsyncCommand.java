package codeOrchestra.colt.core.rpc.command;

import codeOrchestra.colt.core.controller.ColtControllerCallbackEx;

/**
 * @author Alexander Eliseyev
 */
public interface RemoteAsyncCommand<T> {
    String getName();

    void execute(ColtControllerCallbackEx<T> callback);
}

