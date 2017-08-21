package org.bibalex.eol.dwca.validation.functions;

import org.apache.logging.log4j.Logger;
import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.dwca.validation.TermURIs;
import org.bibalex.eol.handlers.DwcaHandler;
import org.bibalex.eol.handlers.LogHandler;
import org.gbif.dwc.terms.Term;
import org.gbif.dwca.io.ArchiveFile;
import org.gbif.dwca.record.Record;

import java.util.Arrays;

/**
 * @author Ahmad.Eldefrawy
 * @author Mina.Edward
 *
 * Functions populated in ValidationRules.xml using ValidationFunctions.java
 * 1.Check whether identifier exists or not
 */
public class AgentValidationFunctions {
    public static final String ROW_TYPE = "http://eol.org/schema/agent/Agent";
    private static Logger logger = LogHandler.getLogger(AgentValidationFunctions.class.getName());

    /**
     * Checking the existence of URL and that it's Syntax is valid as an http or ftp link "/^(https?|ftp):\/\/.*\./i"
     *
     * @param archiveFile
     * @param fieldURI
     * @return ArchiveFileState
     */
    public static ArchiveFileState checkAgentHasValidURL_FieldValidator(ArchiveFile archiveFile, String fieldURI) throws Exception {
        Term urlTerm = null;
        try {
            urlTerm = DwcaHandler.getTermFromArchiveFile(archiveFile, fieldURI);
        } catch (Exception e) {
            return new ArchiveFileState(true);
        }
        int failures = 0;
        int totalLines = 0;
        for (Record record : archiveFile) {
            if (record.value(urlTerm) == null || record.value(urlTerm).length() <= 0 ||
                    record.value(urlTerm).matches("/^(https?|ftp)://.*\\./i")) {
                //TODO DEFRAWY regex format is weak according to sources online
                logger.debug(
                        "line : " + record.toString() + " is violating a rule \"" +
                                "Does not have a valid field : " + fieldURI + " = " + record.value(urlTerm) + " \"");
                failures++;
            }
            totalLines++;
        }
        return new ArchiveFileState(totalLines, failures);
    }

    /**
     * Making sure the role is one of the following roles (in lower case): "animator", "author", "compiler", "composer",
     * "creator", "director", "editor", "illustrator", "photographer", "project", "provider"  "publisher", "recorder",
     * "source"
     *
     * @param archiveFile
     * @param fieldURI
     * @return ArchiveFileState
     */
    public static ArchiveFileState checkAgentHasValidRole_FieldValidator(ArchiveFile archiveFile, String fieldURI) throws Exception {
        String[] roles = {"animator", "author", "compiler", "composer", "creator", "director", "editor", "illustrator",
                "photographer", "project", "provider", "publisher", "recorder", "source"};
        Term roleTerm = null;
        try {
            roleTerm = DwcaHandler.getTermFromArchiveFile(archiveFile, fieldURI);
        } catch (Exception e) {
            return new ArchiveFileState(true);
        }

        int failures = 0;
        int totalLines = 0;
        for (Record record : archiveFile) {
            if (record.value(roleTerm) == null || record.value(roleTerm).length() <= 0 ||
                    !Arrays.asList(roles).contains(record.value(roleTerm))) {
                logger.debug(
                        "line : " + record.toString() + " is violating a rule \"" +
                                "Does not have a valid field : " + fieldURI + " = " + record.value(roleTerm) + " \"");
                failures++;
            }
            totalLines++;
        }
        return new ArchiveFileState(totalLines, failures);
    }


    /**
     * Checking if any of the following fields exist:
     * http://xmlns.com/foaf/spec/#term_name
     * http://xmlns.com/foaf/spec/#term_firstName
     * http://xmlns.com/foaf/spec/#term_familyName
     *
     * @param archiveFile
     * @return
     */
    public static ArchiveFileState checkAgentsHaveNames_RowValidator(ArchiveFile archiveFile) {
        String[] termsString = {TermURIs.termNameURI, TermURIs.termFirstNameURI, TermURIs.termFamilyNameURI};
        return DwcaHandler.checkRecordsHaveAtLeastOneOfTermsList(archiveFile, termsString);
    }
}
