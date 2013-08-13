package codeOrchestra.colt.core.rpc;

import codeOrchestra.colt.core.model.COLTProject;
import codeOrchestra.colt.core.rpc.security.COLTRemoteSecurityManager;
import codeOrchestra.colt.core.rpc.security.InvalidAuthTokenException;
import codeOrchestra.colt.core.rpc.security.InvalidShortCodeException;
import codeOrchestra.colt.core.rpc.security.TooManyFailedCodeTypeAttemptsException;

/**
 * @author Alexander Eliseyev
 */
public abstract class AbstractCOLTRemoteService<P extends COLTProject> implements COLTRemoteService<P> {

    @Override
    public String obtainAuthToken(String shortCode) throws TooManyFailedCodeTypeAttemptsException, InvalidShortCodeException {
        return COLTRemoteSecurityManager.getInstance().obtainAuthToken(shortCode);
    }

    @Override
    public void requestShortCode(final String requestor) throws COLTRemoteTransferableException {
        COLTRemoteSecurityManager.getInstance().createNewTokenAndGetShortCode(requestor);
    }

    @Override
    public void checkAuth(String securityToken) throws InvalidAuthTokenException {
        if (!COLTRemoteSecurityManager.getInstance().isValidToken(securityToken)) {
            throw new InvalidAuthTokenException();
        }
    }

    @Override
    public int ping() {
        return 0;
    }

}
