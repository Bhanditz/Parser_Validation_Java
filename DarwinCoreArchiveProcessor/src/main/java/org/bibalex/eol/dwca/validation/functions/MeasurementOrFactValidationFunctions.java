package org.bibalex.eol.dwca.validation.functions;

import org.apache.logging.log4j.Logger;
import org.bibalex.eol.handlers.LogHandler;

/**
 * @author Ahmad.Eldefrawy
 * Functions populated in ValidationRules.xml using ValidationFunctions.java
 * 1. Check whether measurementTypes exists or not
 * 2. Check whether identifier measurementValues exists or not
 */
public class MeasurementOrFactValidationFunctions {
    public static final String ROW_TYPE = "http://eol.org/schema/Association";
    private static Logger logger = LogHandler.getLogger(AssociationValidationFunctions.class.getName());

}
