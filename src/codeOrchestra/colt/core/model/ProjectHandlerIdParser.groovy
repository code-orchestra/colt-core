package codeOrchestra.colt.core.model

import groovy.util.slurpersupport.GPathResult

/**
 * @author Dima Kruk
 * @author Alexander Eliseyev
 */
class ProjectHandlerIdParser {

    String source
    GPathResult node;

    ProjectHandlerIdParser(String source) {
        this.source = source
        node = new XmlSlurper().parseText(source)
    }

    String getHandlerId() {
        return node.@projectType
    }

    Boolean getIsPlugin() {
        return node.@isPlugin == "true"
    }

}
