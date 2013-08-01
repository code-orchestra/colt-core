package codeOrchestra.colt.core;

import codeOrchestra.colt.core.loading.LiveCodingHandlerLoadingException;
import codeOrchestra.colt.core.loading.LiveCodingHandlerManager;

/**
 * @author Alexander Eliseyev
 */
public final class HeadlessStarter {

    public static void main(String[] args) {
        LiveCodingLanguageHandler liveCodingHandler = null;
        try {
            liveCodingHandler = LiveCodingHandlerManager.getInstance().load("AS");
        } catch (LiveCodingHandlerLoadingException e) {
            e.printStackTrace();
            System.exit(0);
        }

        System.out.println("Name of handler loaded: " + liveCodingHandler.getName());
    }

}
