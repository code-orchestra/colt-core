package codeOrchestra.colt.core.model

/**
 * @author Dima Kruk
 */
public interface IModelElement {
    Closure buildXml()
    void buildModel(Object node)
}