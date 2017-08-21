package org.bibalex.eol.dwca.validation.functions;

import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.dwca.validation.TermURIs;
import org.bibalex.eol.handlers.DwcaHandler;
import org.gbif.dwca.io.ArchiveFile;

/**
 * @author Mina.Edward
 * @author Ahmad.Eldefrawy
 * Functions populated in ValidationRules.xml using ValidationFunctions.java
 * 1. Check whether identifier exists or not
 */

public class ReferenceValidationFunctions {
    public static final String ROW_TYPE = "http://eol.org/schema/reference/Reference";

    /**
     * Checking if any of the following fields exist:
     * http://eol.org/schema/reference/
     * http://eol.org/schema/reference/primaryTitle
     * http://purl.org/dc/terms/title'
     *
     * @param archiveFile
     * @return
     */
    public static ArchiveFileState checkReferencesHasTitles_RowValidator(ArchiveFile archiveFile) {
        String[] termsString = {TermURIs.referenceURI, TermURIs.primaryTitleURI, TermURIs.titleURI};
        return DwcaHandler.checkRecordsHaveAtLeastOneOfTermsList(archiveFile, termsString);
    }

}
