package org.bibalex.eol.dwca.validation.functions;

import org.bibalex.eol.handlers.LogHandler;
import org.junit.Assert;
import org.bibalex.eol.MyArchiveFile;
import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.dwca.validation.TermURIs;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwca.io.ArchiveField;
import org.gbif.dwca.record.RecordImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;


/**
 * Created by Mina.Edward on 9/7/2016.
 */
public class MediaValidationFunctionsTest extends MediaValidationFunctions {

    static MyArchiveFile archiveFile1, archiveFile2, withoutTypeMediaArchiveFile;
    static Term accessTerm, scientificNameTerm, termTypeTerm, languageTerm, subjectTerm, licenseTerm;
    static RecordImpl record1, record2, record3;


    public static void createArchiveFile1() {

        // create archive fields
        ArchiveField idField = new ArchiveField();
        ArchiveField field1 = new ArchiveField();
        ArchiveField field2 = new ArchiveField();
        ArchiveField field3 = new ArchiveField();
        ArchiveField field4 = new ArchiveField();
        ArchiveField field5 = new ArchiveField();

        // set field types
        idField.setTerm(accessTerm);
        field1.setTerm(scientificNameTerm);
        field2.setTerm(termTypeTerm);
        field3.setTerm(languageTerm);
        field4.setTerm(subjectTerm);
        field5.setTerm(licenseTerm);

        // set indexes
        field1.setIndex(0);
        field2.setIndex(1);
        field3.setIndex(2);
        field4.setIndex(3);
        field5.setIndex(4);

        // add them to list
        ArrayList<ArchiveField> fieldList = new ArrayList<ArchiveField>();
        fieldList.add(field1);
        fieldList.add(field2);
        fieldList.add(field3);
        fieldList.add(field4);
        fieldList.add(field5);

        // create record
        record1 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record2 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record3 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);


        // add values
        String[] recordValues1 = {"panthera tigurus", "http://purl.org/dc/dcmitype/movingimage", "eng", "http://rs.tdwg.org/ontology/voc/SPMInfoItems#associations", "http://creativecommons.org/publicdomain/zero/1.0/"};
        String[] recordValues2 = {"panthera tigurus", "", "no valid language code", "http://rs.tdwg.org/pccore/", "not public domain"};
        String[] recordValues3 = {"panthera tigurus", "http://purl.org/dc/dcmitype/text", "eng-eng", "", ""};
        record1.setRow(recordValues1);
        record2.setRow(recordValues2);
        record3.setRow(recordValues3);

        //Creating Archive File
        archiveFile1 = new MyArchiveFile();
        archiveFile1.setRowType(TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE));
        archiveFile1.addField(field1);
        archiveFile1.addField(field2);
        archiveFile1.addField(field3);
        archiveFile1.addField(field4);
        archiveFile1.addField(field5);
        archiveFile1.addRecord(record1);
        archiveFile1.addRecord(record2);
        archiveFile1.addRecord(record3);
    }

    private static void createArchiveFile2() {
        // create archive fields
        ArchiveField idField = new ArchiveField();
        ArchiveField field1 = new ArchiveField();
        ArchiveField field3 = new ArchiveField();

        // set field types
        idField.setTerm(accessTerm);
        field1.setTerm(scientificNameTerm);
        field3.setTerm(languageTerm);

        // set indexes
        field1.setIndex(0);
        field3.setIndex(2);

        // add them to list
        ArrayList<ArchiveField> fieldList = new ArrayList<ArchiveField>();
        fieldList.add(field1);
        fieldList.add(field3);

        // create record
        record1 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record2 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record3 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);

        // add values
        String[] recordValues1 = {"panthera tigurus", "eng"};
        String[] recordValues2 = {"panthera tigurus", "no valid language code"};
        String[] recordValues3 = {"panthera tigurus", "eng-eng"};
        record1.setRow(recordValues1);
        record2.setRow(recordValues2);
        record3.setRow(recordValues3);

        //Creating Archive File
        withoutTypeMediaArchiveFile = new MyArchiveFile();
        withoutTypeMediaArchiveFile.addField(field1);
        withoutTypeMediaArchiveFile.addField(field3);
        withoutTypeMediaArchiveFile.addRecord(record1);
        withoutTypeMediaArchiveFile.addRecord(record2);
        withoutTypeMediaArchiveFile.addRecord(record3);

    }

    public static void createArchiveFile3() {

        // create archive fields
        ArchiveField idField = new ArchiveField();
        ArchiveField field1 = new ArchiveField();
        ArchiveField field2 = new ArchiveField();

        // set field types
        idField.setTerm(scientificNameTerm);
        field1.setTerm(accessTerm);
        field2.setTerm(termTypeTerm);

        // set indexes
        field1.setIndex(0);
        field2.setIndex(1);

        // add them to list
        ArrayList<ArchiveField> fieldList = new ArrayList<ArchiveField>();
        fieldList.add(field1);
        fieldList.add(field2);

        // create record
        record1 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record2 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record3 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);


        // add values
        String[] recordValues1 = {"http://media.eol.org/content/2014/06/01/16/20283_orig.jpg", "http://purl.org/dc/dcmitype/stillimage"};
        String[] recordValues2 = {"", "http://purl.org/dc/dcmitype/movingimage"};
        String[] recordValues3 = {"http://media.eol.org/content/2014/06/01/16/20283_orig.jpg", "not a valid type"};
        record1.setRow(recordValues1);
        record2.setRow(recordValues2);
        record3.setRow(recordValues3);

        //Creating Archive File
        archiveFile2 = new MyArchiveFile();
        archiveFile2.setRowType(TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE));
        archiveFile2.addField(field1);
        archiveFile2.addField(field2);
        archiveFile2.addRecord(record1);
        archiveFile2.addRecord(record2);
        archiveFile2.addRecord(record3);
    }

    @BeforeClass
    public static void prepareArchiveFiles() {
        String propertiesFile = "configs.properties";
        LogHandler.initializeHandler(propertiesFile);
        // prepare terms
        accessTerm = TermFactory.instance().findTerm(TermURIs.accessURI);
        scientificNameTerm = TermFactory.instance().findTerm(TermURIs.scientificNameURI);
        termTypeTerm = TermFactory.instance().findTerm(TermURIs.termTypeURI);
        languageTerm = TermFactory.instance().findTerm(TermURIs.languageURI);
        subjectTerm = TermFactory.instance().findTerm(TermURIs.cvTermURI);
        licenseTerm = TermFactory.instance().findTerm(TermURIs.licenseURI);
        createArchiveFile1();
        createArchiveFile2();
        createArchiveFile3();
    }

    @Test
    public void checkMediaHasURL_RowValidator_Test1() {
        try {
            ArchiveFileState result = MediaValidationFunctions.checkMediaHasURL_RowValidator(withoutTypeMediaArchiveFile);
            Assert.assertTrue("checkMediaHasURL_RowValidator_Test all lines should be complying", result.isAllLinesComplying());
            Assert.assertFalse("checkMediaHasURL_RowValidator_Test all lines should not be violating", result.isAllLinesViolating());
        } catch (Exception e) {
            Assert.fail("Failed to checkMediaHasURL_RowValidator_Test " + e + " ," + e.getMessage());
        }
    }

    @Test
    public void checkMediaHasURL_RowValidator_Test2() {
        try {
            ArchiveFileState result = MediaValidationFunctions.checkMediaHasURL_RowValidator(archiveFile2);
            Assert.assertFalse(result.isAllLinesViolating());
            Assert.assertTrue(result.getLinesViolatingRule() == 1);
            Assert.assertTrue(result.getTotalNumberOfLines() == 3);
        } catch (Exception e) {
            Assert.fail("Failed to checkMediaHasURL_RowValidator_Test " + e + " ," + e.getMessage());
        }
    }

    @Test
    public void checkTextHasDescription_RowValidator_Test1() {
        try {
            ArchiveFileState result = MediaValidationFunctions.checkTextHasDescription_RowValidator(withoutTypeMediaArchiveFile);
            Assert.assertTrue("checkTextHasDescription_RowValidator_Test1 all lines should be complying", result.isAllLinesComplying());
            Assert.assertFalse("checkTextHasDescription_RowValidator_Test1 all lines should not be violating", result.isAllLinesViolating());
        } catch (Exception e) {
            Assert.fail("Failed to checkMediaHasURL_RowValidator_Test " + e + " ," + e.getMessage());
        }
    }

    @Test
    public void checkTextHasDescription_RowValidator_Test2() {
        try {
            ArchiveFileState result = MediaValidationFunctions.checkTextHasDescription_RowValidator(withoutTypeMediaArchiveFile);
            Assert.assertTrue("checkTextHasDescription_RowValidator_Test1 all lines should be complying", result.isAllLinesComplying());
            Assert.assertFalse("checkTextHasDescription_RowValidator_Test1 all lines should not be violating", result.isAllLinesViolating());
        } catch (Exception e) {
            Assert.fail("Failed to checkMediaHasURL_RowValidator_Test " + e + " ," + e.getMessage());
        }
    }

    @Test
    public void checkValidMediaType_FieldValidation_TestAllViolating() {
        try {
            ArchiveFileState result = MediaValidationFunctions.checkValidMediaType_FieldValidator(withoutTypeMediaArchiveFile, TermURIs.termTypeURI);
            Assert.assertTrue(result.isAllLinesViolating());
        } catch (Exception e) {
            Assert.fail("Failed to checkValidMediaType_FieldValidator " + e + " ," + e.getMessage());
        }
    }

    @Test
    public void checkValidMediaType_FieldValidation_TestSomeViolating() {
        try {
            ArchiveFileState result = MediaValidationFunctions.checkValidMediaType_FieldValidator(archiveFile1, TermURIs.termTypeURI);
            Assert.assertFalse(result.isAllLinesViolating());
            Assert.assertTrue(result.getLinesViolatingRule() == 1);
            Assert.assertTrue(result.getTotalNumberOfLines() == 3);
        } catch (Exception e) {
            Assert.fail("Failed to checkValidMediaType_FieldValidator " + e + " ," + e.getMessage());
        }
    }

    @Test
    public void isValidSubject_Test() {
        String validSubject = "http://eol.org/schema/eol_info_items.xml#" + "cyclicity";
        Assert.assertTrue(super.isValidSubject(validSubject));

        String[] inValidSubjects = {"http://eol.org/schema/eol_info_items.xml#", "cyclicity", "", "http://eol.org/schema/eol_info_items.xml#city", "http://rs.tdwg.org/ontology/voc/SPMInfoItems#generaldescriptiongeneraldescription"};
        for (String invalidSubj : inValidSubjects) {
            Assert.assertFalse(super.isValidSubject(invalidSubj));
        }
    }

    @Test
    public void checkValidSubject_FieldValidation_Test() {
        try {
            ArchiveFileState result = MediaValidationFunctions.checkValidSubject_FieldValidator(archiveFile1, TermURIs.cvTermURI);
            Assert.assertFalse("allLinesViolating should return false", result.isAllLinesViolating());
            Assert.assertTrue("number of violating lines is not correct", result.getLinesViolatingRule() == 2);
            Assert.assertTrue("Total number of lines is not correct", result.getTotalNumberOfLines() == 3);
        } catch (Exception e) {
            Assert.fail("Failed to checkValidSubject_FieldValidator " + e + " ," + e.getMessage());
        }
    }

    @Test
    public void isValidLicense_Test() {
        String[] validLicenses = {"http://creativecommons.org/licences/by/1.0/", "http://creativecommons.org/licences/publicdomain/",
                "http://creativecommons.org/licences/by/3.0/", "http://creativecommons.org/licenses/by-nc-sa/2.0/", "no known copyright restrictions", "public domain", "not applicable"};
        for (String validLicense : validLicenses)
            Assert.assertTrue(super.isValidLicense(validLicense));

        String[] inValidLicenses = {"random String", "http://creativecommons.org/licenses/by-nc-sa/", "http://creativecommons.org/licences/publicdomain/3.0/publicdomain/"};
        for (String inValidLicense : inValidLicenses)
            Assert.assertFalse(super.isValidLicense(inValidLicense));
    }

    @Test
    public void checkValidLicense_FieldValidation_Test() {
        try {
            ArchiveFileState result = MediaValidationFunctions.checkValidLicense_FieldValidator(archiveFile1, TermURIs.licenseURI);
            Assert.assertFalse("allLinesViolating should return false", result.isAllLinesViolating());
            Assert.assertTrue("number of violating lines is not correct", result.getLinesViolatingRule() == 1);
            Assert.assertTrue("Total number of lines is not correct", result.getTotalNumberOfLines() == 3);
        } catch (Exception e) {
            Assert.fail("Failed to checkValidLicense_FieldValidationTest " + e + " ," + e.getMessage());
        }
    }


}
