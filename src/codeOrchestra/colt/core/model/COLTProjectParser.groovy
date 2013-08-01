package codeOrchestra.colt.core.model

//import codeOrchestra.colt.as.model.COLTAsProject

/**
 * @author Dima Kruk
 */
class COLTProjectParser {

    COLTProject modelFormXmlString(String source) {
        Object node =  new XmlSlurper().parseText(source)
        COLTProject result

        // TODO: move to handler

        /*
        if(node.@type == "as") {
            result = new COLTAsProject()
            result.buildModel(node)
            return result
        }
       */
    }

}
