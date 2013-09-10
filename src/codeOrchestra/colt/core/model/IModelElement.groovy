package codeOrchestra.colt.core.model

/**
 * @author Dima Kruk
 */
public interface IModelElement {
    Closure buildXml(Project project)
    void buildModel(Object node)
}