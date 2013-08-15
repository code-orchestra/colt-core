package codeOrchestra.colt.core.ui.components;

/**
 * @author Alexander Eliseyev
 */
public interface COLTProgressIndicator {

    void start();

    void stop();

    void setProgress(int percents);

    void setText(String text);

}
