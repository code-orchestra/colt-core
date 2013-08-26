package codeOrchestra.colt.core.annotation.ast

import codeOrchestra.colt.core.annotation.Service

/**
 * @author Eugene Potapenko
 */
class AnyClass {
    @Service ColtAsController2 hello

    AnyClass() {
        println(hello)
    }
}
