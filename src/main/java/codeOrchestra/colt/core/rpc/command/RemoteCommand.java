package codeOrchestra.colt.core.rpc.command;

import codeOrchestra.colt.core.rpc.ColtRemoteException;

/**
 * @author Alexander Eliseyev
 */
public interface RemoteCommand<T> {
    String getName();
    T execute() throws ColtRemoteException;
}