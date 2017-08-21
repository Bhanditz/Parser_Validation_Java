package org.bibalex.eol.dwca.validation.functions;

import org.bibalex.eol.MyArchiveFile;
import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.dwca.validation.TermURIs;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwca.io.ArchiveField;
import org.gbif.dwca.record.RecordImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Mina.Edward on 9/7/2016.
 * Testing the ValidationFunctions
 */
public class ValidationFunctionsTest {

    static MyArchiveFile myArchivefile;
    static Term accessTerm, scientificNameTerm, termTypeTerm, languageTerm;
    static RecordImpl record1, record2, record3;

    @BeforeClass
    public static void prepareArchiveFile() {
        // prepare terms
        accessTerm = TermFactory.instance().findTerm(TermURIs.accessURI);
        scientificNameTerm = TermFactory.instance().findTerm(TermURIs.scientificNameURI);
        termTypeTerm = TermFactory.instance().findTerm(TermURIs.termTypeURI);
        languageTerm = TermFactory.instance().findTerm(TermURIs.languageURI);

        // create archive fields
        ArchiveField idField = new ArchiveField();
        ArchiveField field1 = new ArchiveField();
        ArchiveField field2 = new ArchiveField();
        ArchiveField field3 = new ArchiveField();

        // set field types
        idField.setTerm(accessTerm);
        field1.setTerm(scientificNameTerm);
        field2.setTerm(termTypeTerm);
        field3.setTerm(languageTerm);

        // set indexes
        field1.setIndex(0);
        field2.setIndex(1);
        field3.setIndex(2);

        // add them to list
        ArrayList<ArchiveField> fieldList = new ArrayList<ArchiveField>();
        fieldList.add(field1);
        fieldList.add(field2);
        fieldList.add(field3);

        // create record
        record1 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(TaxonValidationFunctions.ROW_TYPE), false, false);
        record2 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(TaxonValidationFunctions.ROW_TYPE), false, false);
        record3 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(TaxonValidationFunctions.ROW_TYPE), false, false);

        // add values
        String[] recordValues1 = {"panthera tigurus", "common name", "eng"};
        String[] recordValues2 = {"panthera tigurus", "", "no valid language code"};
        String[] recordValues3 = {"panthera tigurus", "scientific name", "eng-eng"};
        record1.setRow(recordValues1);
        record2.setRow(recordValues2);
        record3.setRow(recordValues3);

        //Creating Archive File
        myArchivefile = new MyArchiveFile();
        myArchivefile.addField(field1);
        myArchivefile.addField(field2);
        myArchivefile.addField(field3);
        myArchivefile.addRecord(record1);
        myArchivefile.addRecord(record2);
        myArchivefile.addRecord(record3);
    }

    @Test
    public void checkArchiveFileHasField_FieldValidator_Test1() {
        try {
            ArchiveFileState result = ValidationFunctions.checkArchiveFileHasField_FieldValidator(myArchivefile, TermURIs.termTypeURI);
            Assert.assertFalse(result.isAllLinesViolating());
            Assert.assertTrue(result.getLinesViolatingRule() == 1);
        } catch (Exception e) {
            Assert.fail("Failed to apply checkArchiveFileHasField_FieldValidator , Exception message : " + e.getMessage());
        }
    }

    @Test
    public void checkArchiveFileHasField_FieldValidator_Test2() {
        try {
            ArchiveFileState result = ValidationFunctions.checkArchiveFileHasField_FieldValidator(myArchivefile, TermURIs.scientificNameURI);
            Assert.assertFalse(result.isAllLinesViolating());
            Assert.assertTrue(result.getLinesViolatingRule() == 0);
            Assert.assertTrue(result.getTotalNumberOfLines() == 3);
        } catch (Exception e) {
            Assert.fail("Failed to apply checkArchiveFileHasField_FieldValidator , Exception message : " + e.getMessage());
        }
    }

    @Test
    public void checkLanguageIsValid_FieldValidator_Test() {
        try {
            ArchiveFileState result = ValidationFunctions.checkLanguageIsValid_FieldValidator(myArchivefile, TermURIs.languageURI);
            Assert.assertFalse(result.isAllLinesViolating());
            Assert.assertTrue(result.getLinesViolatingRule() == 1);
            Assert.assertTrue(result.getTotalNumberOfLines() == 3);
        } catch (Exception e) {
            Assert.fail("Failed to apply checkLanguageIsValid_FieldValidator , Exception message : " + e);
        }
    }

    @Test
    public void isUTF8_Test() {
        try {
            Assert.assertTrue(ValidationFunctions.isUTF8("WeakTestingSample!"));
        } catch (Exception e) {
            Assert.fail("Failed to apply istUTF8_Test, Exception message: " + e);
        }
    }

}
