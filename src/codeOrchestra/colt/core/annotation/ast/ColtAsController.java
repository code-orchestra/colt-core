package codeOrchestra.colt.core.annotation.ast;

import codeOrchestra.colt.core.controller.ColtController;

import java.io.IOException;

/**
 * @author Eugene Potapenko
 */
public class ColtAsController implements ColtController, Appendable {
    @Override
    public void dispose() {
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        return null;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        return null;
    }

    @Override
    public Appendable append(char c) throws IOException {
        return null;
    }
}
