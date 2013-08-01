package codeOrchestra.colt.core.model

import groovy.util.slurpersupport.GPathResult

/**
 * @author Dima Kruk
 * @author Alexander Eliseyev
 */
class COLTProjectHandlerIdParser {

    String source
    GPathResult node;

    COLTProjectHandlerIdParser(String source) {
        this.source = source
        node = new XmlSlurper().parseText(source)
    }

    String getHandlerId() {
        return node.@projectType
    }

}
