package org.bibalex.eol.dwca.validation;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.bibalex.eol.dwca.validation.rules.FieldValidationRule;
import org.bibalex.eol.dwca.validation.rules.MetaFileValidationRule;
import org.bibalex.eol.dwca.validation.rules.RowValidationRule;
import org.bibalex.eol.dwca.validation.rules.ValidationRulesLoader;
import org.bibalex.eol.handlers.LogHandler;
import org.gbif.dwca.io.Archive;
import org.gbif.dwca.io.ArchiveFactory;
import org.gbif.dwca.io.ArchiveFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DwcaValidator {

    private Logger logger;
    private ValidationRulesLoader rulesLoader;

    /**
     * Construct new DwcaValidator, and load validation rules
     *
     * @throws Exception in case of failing in loading rules
     */
    public DwcaValidator(String propertiesFile) throws Exception {
        LogHandler.initializeHandler(propertiesFile);
        logger = LogHandler.getLogger(DwcaValidator.class.getName());
        rulesLoader = new ValidationRulesLoader(propertiesFile);
        if (!rulesLoader.loadValidationRules()) {
            throw new Exception("Failed to load the validation rules while creating new dwca " +
                    "validator");
        }
    }

    public static void main(String[] args) {
        String dwcArchivePath = args[0];
        String outputReportPath = args[1];
        String propertiesFile = args[2];

//        String propertiesFile = "configs.properties";
        DwcaValidator validator = null;
        try {
            validator = new DwcaValidator(propertiesFile);
        } catch (Exception e) {
            System.err.println("Failed to create dwca validator : " + e.getMessage());
            System.exit(1);
        }


        ValidationResult result = null;
        try {
            result = validator.validateArchive(dwcArchivePath);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputReportPath));
            writer.write(result.toString());
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println(e);
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Validate darwin core archive located at local file sytem
     *
     * @param path path of the darwin core archive on the local file system
     * @return ValidationResult the result of the applying the rules on the archive
     * @throws Exception in casae of problem happening while applying the rules
     */
    public ValidationResult validateArchive(String path) throws Exception {
        ValidationResult validationResult = new ValidationResult(path);
        Archive dwcArchive;
        try {
            File myArchiveFile = new File(path);
            File extractToFolder = new File(FilenameUtils.removeExtension(path) + ".out");
            dwcArchive = ArchiveFactory.openArchive(myArchiveFile, extractToFolder);
        } catch (Exception e) {
            String errorMessage = "Failed to parse the Darwin core archive " + e.getMessage();
            validationResult.addStructuralError(errorMessage);
            logger.error(errorMessage);
            return validationResult;
        }
        if (!validateArchive(dwcArchive, validationResult)) {
            throw new Exception("Problem happened while trying to apply the validation rules on " +
                    "the archive : " + path);
        }
        return validationResult;
    }

    /**
     * Validate darwin core archive and put the results in the ValidationResult object
     *
     * @param dwca input darwin core archive
     * @return false in case of failure in applying any of the validation rules
     */
    public boolean validateArchive(Archive dwca, ValidationResult validationResult) {
        logger.info("Start applying the structuralValidationRules on archive " + dwca.getLocation
                () + " ...");
        if (!applyStructuralValidationRules(dwca, validationResult)) {
            logger.error("Failed in applying some Structural Validation rules");
            return false;
        }
        logger.info("Start applying the RowValidationRules on archive " + dwca.getLocation
                () + " ...");
        if (validationResult.getStructuralErrors().size() > 0) {
            logger.error("Dwca " + dwca.getLocation() + " does not pass some structural " +
                    "validation rules");
            return true;
        }
        logger.info("Start applying the FieldValidationRules on archive " + dwca.getLocation
                () + " ...");
        boolean validationRunWithoutProblems = true;
        if (!applyRowValidationRules(dwca, validationResult)) {
            logger.error("Failed in applying some Row Validation rules");
            validationRunWithoutProblems = false;
        }

        if (!applyFieldValidationRules(dwca, validationResult)) {
            logger.error("Failed in applying some Field Validation rules");
            validationRunWithoutProblems = false;
        }
        return validationRunWithoutProblems;
    }

    /**
     * Apply the Structural Validation Rules on the darwin core archive and put the results in the
     * ValidationResult object
     *
     * @param dwca the input Darwin Core Archive
     * @return false in case of failure in applying any of the validation rules
     */
    private boolean applyStructuralValidationRules(Archive dwca, ValidationResult
            validationResult) {
        List<MetaFileValidationRule> rules = rulesLoader.getMetaFileValidationRules();
        if (rules.isEmpty()) {
            logger.info("No MetaFileValidationRules to apply");
            return true;
        }

        int success = 0;
        int failures = 0;
        for (MetaFileValidationRule rule : rules) {
            if (rule.validate(dwca, validationResult))
                success++;
            else
                failures++;
        }
        logger.info("Out of  " + rules.size() + " MetaFileValidationRules");
        logger.info("Successfully applied " + success + " MetaFileValidationRules");
        logger.info("Failed in applying " + failures + " MetaFileValidationRules");

        return failures == 0;
    }

    private List<String> filterNotExistingRowTypes(Archive archive, List<String> rowTypeList) {
        logger.info("Prepare HashSet for the rowtypes of the archive");
        HashSet<String> archiveList = new HashSet<String>();
        archiveList.add(archive.getCore().getRowType().qualifiedName().toLowerCase());
        for (ArchiveFile archiveFile : archive.getExtensions()) {
            archiveList.add(archiveFile.getRowType().qualifiedName().toLowerCase());
        }
        logger.info("Using the HashSet in filtering the rowTypes");
        List<String> filteredList = new ArrayList<String>();
        for (String rowType : rowTypeList) {
            if (archiveList.contains(rowType.toLowerCase()))
                filteredList.add(rowType);
            else
                logger.error("RowType : " + rowType + "  is not found at the DwcArchive");
        }
        logger.info("Returning " + filteredList.size() + " rowType out of " + rowTypeList.size() + " rowtype");
        return filteredList;
    }

    /**
     * Apply the Row Validation Rules on the darwin core archive and put the results in the
     * ValidationResult object
     *
     * @param dwcArchive the input Darwin Core Archive
     * @return false in case of failure in applying any of the validation rules
     */
    private boolean applyRowValidationRules(Archive dwcArchive, ValidationResult validationResult) {
        List<String> rowTypeList = rulesLoader.getRowTypeList();
        if (rowTypeList.isEmpty()) {
            logger.warn("Empty rowType list. No rowTypes have validation rules");
            return true;
        }
        int success = 0;
        int failures = 0;
        rowTypeList = filterNotExistingRowTypes(dwcArchive, rowTypeList);
        for (String rowType : rowTypeList) {
            List<RowValidationRule> rules = rulesLoader.getValidationRulesOfRowType(rowType);
            if (rules.isEmpty()) {
                logger.info("Row type " + rowType + " has no row validation rules");
                continue;
            }
            logger.info("start applying "+rules.size()+" row Validations on archive file " + rowType );
            int localSuccess = 0;
            int localFailures = 0;
            for (RowValidationRule rule : rules) {
                if (!rule.validate(dwcArchive, validationResult)) {
                    localFailures++;
                    logger.error("RowType : " + rowType + " , Failed in applying the following " +
                            "rule : " + rule.toString());
                } else
                    localSuccess++;
            }
            if (localFailures == 0)
                logger.info("Row validation rules on the rowType " + rowType + " all run " +
                        "successfully");
            else
                logger.info("Row validation rules on the rowType " + rowType + " had problems. " +
                        "Failed in applying " + localFailures + " out of " + (localFailures +
                        localSuccess));
            success += localSuccess;
            failures += localFailures;
        }
        if (failures > 0) {
            logger.info("Row validation had  " + failures + " failed to be applied rules out of "
                    + (failures + success));
            return false;
        } else
            return true;
    }

    /**
     * Apply the Field Validation Rules on the darwin core archive and put the results in the
     * ValidationResult object
     *
     * @param dwcArchive the input Darwin Core Archive
     * @return false in case of failure in applying any of the validation rules
     */
    private boolean applyFieldValidationRules(Archive dwcArchive, ValidationResult
            validationResult) {
        List<String> rowTypeList = rulesLoader.getRowTypeList();
        if (rowTypeList.isEmpty()) {
            logger.warn("Empty rowType list. No rowTypes have validation rules");
            return true;
        }
        int success = 0;
        int failures = 0;
        rowTypeList = filterNotExistingRowTypes(dwcArchive, rowTypeList);
        for (String rowType : rowTypeList) {
            List<FieldValidationRule> rules = rulesLoader.getValidationRulesOfFieldType(rowType);
            if (rules.isEmpty()) {
                logger.info("Row type " + rowType + " has no field field validation rules");
                continue;
            }
            logger.info("start applying "+rules.size()+" field Validations on archive file " + rowType );
            int localSuccess = 0;
            int localFailures = 0;
            for (FieldValidationRule rule : rules) {
                if (!rule.validate(dwcArchive, validationResult)) {
                    localFailures++;
                    logger.error("RowType : " + rowType + " , Failed in applying the following " +
                            "rule : " + rule.toString());
                } else
                    localSuccess++;
            }
            if (localFailures == 0)
                logger.info("Field validation rules on the rowType " + rowType + " all run " +
                        "successfully");
            else
                logger.info("Field validation rules on the rowType " + rowType + " had problems. " +
                        "Failed in applying " + localFailures + " out of " + (localFailures +
                        localSuccess));
            success += localSuccess;
            failures += localFailures;
        }
        if (failures > 0) {
            logger.info("Field validation had  " + failures + " failed to be applied rules out of" +
                    " " + (failures + success));
            return false;
        } else
            return true;
    }

}