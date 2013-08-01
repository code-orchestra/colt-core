package codeOrchestra.colt.core.model

import groovy.xml.MarkupBuilder

/**
 * @author Dima Kruk
 */
abstract class COLTProject implements IModelElement {

    String name
    String path

    boolean disposed

    // Default aspects
    abstract COLTProjectPaths getProjectPaths()
    abstract COLTProjectLiveSettings getProjectLiveSettings()
    abstract COLTProjectBuildSettings getProjectBuildSettings()

    abstract String getProjectType();

    @Override
    void buildModel(Object node) {
        name = node.@projectName
    }

    String toXmlString() {
        StringWriter writer = new StringWriter()
        new MarkupBuilder(writer).xml(projectName:name, projectType:getProjectType(), buildXml())
        writer.toString()
    }

    void fromXmlString(String source) {
        buildModel(new XmlSlurper().parseText(source))
    }

    public File getBaseDir() {
        return new File(path).getParentFile();
    }

}
