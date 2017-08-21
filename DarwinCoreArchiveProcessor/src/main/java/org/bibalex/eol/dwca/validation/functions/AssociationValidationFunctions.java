package org.bibalex.eol.dwca.validation.functions;

import org.apache.logging.log4j.Logger;
import org.bibalex.eol.handlers.LogHandler;


/**
 * @author Ahmad.Eldefrawy
 *
 * Functions populated in ValidationRules.xml using ValidationFunctions.java
 * 1.Check whether occurrenceIDs exists or not
 * 2.Check whether identifier associationTypes exists or not
 * 3.Check whether identifier targetOccurrenceIDs exists or not
*/
public class AssociationValidationFunctions {
    public static final String ROW_TYPE = "http://eol.org/schema/Association";
    private static Logger logger = LogHandler.getLogger(AssociationValidationFunctions.class.getName());


}



