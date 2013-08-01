package codeOrchestra.colt.core.model

import groovy.xml.MarkupBuilder

/**
 * @author Dima Kruk
 */
abstract class COLTProject implements IModelElement {
    String name
    String handlerId = "as"

    String path
    boolean disposed

    // Default aspects
    abstract COLTProjectPaths getProjectPaths()
    abstract COLTProjectLiveSettings getProjectLiveSettings()
    abstract COLTProjectBuildSettings getProjectBuildSettings()

    @Override
    void buildModel(Object node) {
        name = node.@projectName
        handlerId = node.@type
    }

    String toXmlString() {
        StringWriter writer = new StringWriter()
        new MarkupBuilder(writer).xml(projectName:name, projectType:handlerId, buildXml())
        writer.toString()
    }

    void fromXmlString(String source) {
        buildModel(new XmlSlurper().parseText(source))
    }
}
