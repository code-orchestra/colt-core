package codeOrchestra.colt.core.update.tasks

/**
 * @author Dima Kruk
 */
public interface IUpdateCallback {
    void onComplete()
    void onCancel()
    void onError(String message)
}