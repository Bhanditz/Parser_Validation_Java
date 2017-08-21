package org.bibalex.eol.dwca.validation.functions;

import org.apache.logging.log4j.Logger;
import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.handlers.DwcaHandler;
import org.bibalex.eol.handlers.LogHandler;
import org.gbif.dwc.terms.Term;
import org.gbif.dwca.io.ArchiveFile;
import org.gbif.dwca.record.Record;

import java.io.UnsupportedEncodingException;

/**
 * @author Ahmad.Eldefrawy
 * @author Mina.Edward
 *         Dynamically loaded validation functions that are common between multiple archiveFile types
 */
public class ValidationFunctions {
    private static Logger logger = LogHandler.getLogger(ValidationFunctions.class.getName());

    /**
     * Check whether ArchiveFile have field or not
     *
     * @param archiveFile the input archive file
     * @return ArchiveFileState  number of violating lines and total number of lines
     */
    public static ArchiveFileState checkArchiveFileHasField_FieldValidator(ArchiveFile archiveFile, String fieldURI) throws Exception {

        Term fieldTerm = null;
        try {
            fieldTerm = DwcaHandler.getTermFromArchiveFile(archiveFile, fieldURI);
        } catch (Exception e) {
            ArchiveFileState archiveFileState = new ArchiveFileState();
            archiveFileState.setAllLinesViolating(true);
            return archiveFileState;
        }
        int failures = 0;
        int totalLines = 0;
        for (Record record : archiveFile) {
            if (record.value(fieldTerm) == null || record.value(fieldTerm).length() <= 0) {

                logger.debug("line violating a rule \"Does not have the field : " + fieldURI + " \"");


                failures++;
            }
            totalLines++;
        }
        return new ArchiveFileState(totalLines, failures);
    }

    /**
     * Check if the languages syntax using standardized ISO 639 language codes
     *
     * @param archiveFile the input archive file
     * @return ArchiveFileState  number of violating lines and total number of lines
     */
    public static ArchiveFileState checkLanguageIsValid_FieldValidator(ArchiveFile archiveFile, String fieldURI) throws Exception {
        Term languageTerm = DwcaHandler.getTermFromArchiveFile(archiveFile, fieldURI);
        int failures = 0;

        int totalLines = 0;
        for (Record record : archiveFile) {

            if (record.value(languageTerm) == null || record.value(languageTerm).length() <= 0 || !record.value(languageTerm).matches("^[a-z]{2,3}(-[a-z]{2,5})?$")) {
                logger.debug("line violating a rule \"Does not have the field : " + fieldURI + " \"");


                failures++;
            }
            totalLines++;
        }
        return new ArchiveFileState(totalLines, failures);
    }

    /**
     * Checks if the term name is following the "UTF-8" encoding
     *
     * @param archiveFile
     * @param fieldURI
     * @return
     * @throws Exception
     */
    public static ArchiveFileState checkTermOfFieldURIisUTF8_FieldValidator(ArchiveFile archiveFile, String
            fieldURI)
            throws Exception {
        Term term = null;
        try {
            term = DwcaHandler.getTermFromArchiveFile(archiveFile, fieldURI);
        } catch (Exception e) {
            return new ArchiveFileState(true);
        }
        int failures = 0;
        int totalLines = 0;
        for (Record record : archiveFile) {
            if (record.value(term) == null || record.value(term).length() <= 0 ||
                    !isUTF8(record.value(term))) {
                logger.debug(
                        "line : " + record.toString() + " is violating a rule \"" +
                                "Does not have a valid field : " + fieldURI + " = " + record.value(term) + " \"");
                failures++;
            }
            totalLines++;
        }
        return new ArchiveFileState(totalLines, failures);
    }

    /**
     * Check if a string is valid UTF-8 or not
     *
     * @param string
     * @return boolean
     */
    public static boolean isUTF8(String string) {
        try {
            byte[] bytes = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
