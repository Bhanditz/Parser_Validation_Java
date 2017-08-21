package org.bibalex.eol.handlers;

import org.apache.commons.io.FilenameUtils;
import org.bibalex.eol.MyArchiveFile;
import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.dwca.validation.TermURIs;
import org.bibalex.eol.dwca.validation.functions.MediaValidationFunctions;
import org.bibalex.eol.dwca.validation.functions.ReferenceValidationFunctions;
import org.bibalex.eol.dwca.validation.functions.TaxonValidationFunctions;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwca.io.Archive;
import org.gbif.dwca.io.ArchiveFactory;
import org.gbif.dwca.io.ArchiveField;
import org.gbif.dwca.io.ArchiveFile;
import org.gbif.dwc.terms.Term;

import org.gbif.dwca.record.RecordImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Mina.Edward on 9/1/2016.
 * Testing the DwcaHandler
 */
public class DwcaHandlerTest {


    @BeforeClass
    public static void prepareLogger() {
        String propertiesFile = "configs.properties";
        LogHandler.initializeHandler(propertiesFile);
    }

    @Test
    public void getArchiveFileTest_CoreFile() {
        String rowType = TaxonValidationFunctions.ROW_TYPE;

        Archive dwcArchive = new Archive();
        ArchiveFile archiveFile = new ArchiveFile();
        Term term = TermFactory.instance().findTerm(rowType);
        archiveFile.setRowType(term);
        dwcArchive.setCore(archiveFile);
        try {
            ArchiveFile returnedArchiveFile = DwcaHandler.getArchiveFile(dwcArchive, rowType);
            Assert.assertTrue("Failed to get the archive file", returnedArchiveFile.getRowType().qualifiedName().equalsIgnoreCase(archiveFile.getRowType().qualifiedName()));
        } catch (Exception e) {
            Assert.fail("Failed to get the archive file");
        }
    }

    @Test
    public void getArchiveFileTest_ExtensionFiles() {
        String[] rowTypes = {TaxonValidationFunctions.ROW_TYPE, MediaValidationFunctions.ROW_TYPE, ReferenceValidationFunctions.ROW_TYPE};
        Archive dwcArchive = new Archive();
        for (String type : rowTypes) {
            Term term = TermFactory.instance().findTerm(type);
            ArchiveFile archiveFile = new ArchiveFile();
            archiveFile.setRowType(term);
            dwcArchive.addExtension(archiveFile);
        }
        for (String type : rowTypes) {
            try {
                ArchiveFile returnedArchiveFile = DwcaHandler.getArchiveFile(dwcArchive, type);
                Assert.assertTrue("Failed to get the archive file", type.equalsIgnoreCase(returnedArchiveFile.getRowType().qualifiedName()));
            } catch (Exception e) {
                Assert.fail("Failed to get the archive file");
            }
        }
    }

    @Test(expected = Exception.class)
    public void getArchiveFileTest_NotExistingArchiveFile() throws Exception {
        Archive dwcArchive = new Archive();
        ArchiveFile archiveFile = new ArchiveFile();
        Term term = TermFactory.instance().findTerm(TaxonValidationFunctions.ROW_TYPE);
        archiveFile.setRowType(term);
        dwcArchive.setCore(archiveFile);
        DwcaHandler.getArchiveFile(dwcArchive, MediaValidationFunctions.ROW_TYPE);
    }

    @Test
    public void getTermFromArchiveFileTest_Existing() {
        ArchiveFile archiveFile = new ArchiveFile();
        Term term = TermFactory.instance().findTerm(TaxonValidationFunctions.ROW_TYPE);
        archiveFile.setRowType(term);
        ArchiveField field = new ArchiveField();
        field.setTerm(TermFactory.instance().findTerm(TermURIs.scientificNameURI));
        archiveFile.addField(field);
        try {
            Term resultTerm = DwcaHandler.getTermFromArchiveFile(archiveFile, TermURIs.scientificNameURI);
            Assert.assertTrue(resultTerm.qualifiedName().equalsIgnoreCase(TermURIs.scientificNameURI));
        } catch (Exception e) {
            Assert.fail("Failed to get the Term from archive file");
        }
    }

    @Test(expected = Exception.class)
    public void getTermFromArchiveFileTest_NotExiting() throws Exception {
        ArchiveFile archiveFile = new ArchiveFile();
        Term term = TermFactory.instance().findTerm(TaxonValidationFunctions.ROW_TYPE);
        archiveFile.setRowType(term);
        ArchiveField field = new ArchiveField();
        field.setTerm(TermFactory.instance().findTerm(TermURIs.scientificNameURI));
        archiveFile.addField(field);
        DwcaHandler.getTermFromArchiveFile(archiveFile, TermURIs.identifierURI);
    }

    @Test
    public void recordHasTermTest() {
        // prepare terms
        Term accessTerm = TermFactory.instance().findTerm(TermURIs.accessURI);
        Term scientificNameTerm = TermFactory.instance().findTerm(TermURIs.scientificNameURI);
        Term termTypeTerm = TermFactory.instance().findTerm(TermURIs.termTypeURI);

        // create archive fields
        ArchiveField idField = new ArchiveField();
        ArchiveField field1 = new ArchiveField();
        ArchiveField field2 = new ArchiveField();

        // set field types
        idField.setTerm(accessTerm);
        field1.setTerm(scientificNameTerm);
        field2.setTerm(termTypeTerm);

        // set indexes
        field1.setIndex(0);
        field2.setIndex(1);

        // add them to list
        ArrayList<ArchiveField> fieldList = new ArrayList<ArchiveField>();
        fieldList.add(field1);
        fieldList.add(field2);

        // create record
        RecordImpl record1 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(TaxonValidationFunctions.ROW_TYPE), false, false);
        RecordImpl record2 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(TaxonValidationFunctions.ROW_TYPE), false, false);

        // add values
        String[] recordValues1 = {"panthera tigurus", "common name"};
        String[] recordValues2 = {"panthera tigurus", ""};
        record1.setRow(recordValues1);
        record2.setRow(recordValues2);

        // apply assertions
        Assert.assertTrue(DwcaHandler.recordHasTerm(termTypeTerm, record1));
        Assert.assertFalse(DwcaHandler.recordHasTerm(termTypeTerm, record2));
    }

    @Test
    public void checkRecordsHaveAtLeastOneOfTermsListTest_AllValidOrALLNotValid() {
        URL archivePath = this.getClass().getResource("/3.tar.gz");
        String vernacularNameURI = "http://rs.gbif.org/terms/1.0/VernacularName";
        ArchiveFile archiveFile = null;
        try {
            File myArchiveFile = new File(archivePath.getFile());
            File extractToFolder = new File(FilenameUtils.removeExtension(archivePath.getFile()) + ".out");
            Archive dwcArchive = ArchiveFactory.openArchive(myArchiveFile, extractToFolder);
            archiveFile = dwcArchive.getExtension(TermFactory.instance().findTerm(vernacularNameURI));
        } catch (Exception e) {
            Assert.fail("Failed to parse the Darwin core archive " + e.getMessage());
        }
        String[] existingTermsList = {"http://rs.tdwg.org/dwc/terms/vernacularName", "http://purl.org/dc/terms/language"};
        ArchiveFileState result1 = DwcaHandler.checkRecordsHaveAtLeastOneOfTermsList(archiveFile, existingTermsList);
        Assert.assertFalse(result1.isAllLinesViolating());
        Assert.assertTrue(result1.getLinesViolatingRule() == 0);

        String[] nonExistingTermsList = {"http://purl.org/dc/terms/creator", "http://purl.org/dc/terms/title"};
        ArchiveFileState result2 = DwcaHandler.checkRecordsHaveAtLeastOneOfTermsList(archiveFile, nonExistingTermsList);
        Assert.assertTrue(result2.isAllLinesViolating());
    }

    @Test
    public void checkFieldHasOneOfListOfValuesTest_AllValidOrAllInvalid() {
        MyArchiveFile myArchivefile;
        Term accessTerm = TermFactory.instance().findTerm(TermURIs.accessURI);
        Term scientificNameTerm = TermFactory.instance().findTerm(TermURIs.scientificNameURI);
        RecordImpl record1, record2, record3;

        // create archive fields
        ArchiveField idField = new ArchiveField();
        ArchiveField field1 = new ArchiveField();

        // set field types
        idField.setTerm(accessTerm);
        field1.setTerm(scientificNameTerm);

        // set indexes
        field1.setIndex(0);

        // add them to list
        ArrayList<ArchiveField> fieldList = new ArrayList<ArchiveField>();
        fieldList.add(field1);

        // create record
        record1 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record2 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record3 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);

        // add values
        String[] recordValues1 = {"panthera tigurus"};
        String[] recordValues2 = {"panthera tigurus"};
        String[] recordValues3 = {"panthera tigurus"};
        record1.setRow(recordValues1);
        record2.setRow(recordValues2);
        record3.setRow(recordValues3);

        //Creating Archive File
        myArchivefile = new MyArchiveFile();
        myArchivefile.setRowType(TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE));
        myArchivefile.addField(field1);
        myArchivefile.addRecord(record1);
        myArchivefile.addRecord(record2);
        myArchivefile.addRecord(record3);

        String[] validValues = {"value1"};
        try {
            ArchiveFileState result = DwcaHandler.checkFieldHasOneOfListOfValues(myArchivefile, TermURIs.taxonID_URI, validValues, false);
            Assert.assertTrue(result.isAllLinesViolating());
        } catch (Exception e) {
            Assert.fail("checkFieldHasOneOfListOfValues exception : " + e);
        }
        try {
            ArchiveFileState result = DwcaHandler.checkFieldHasOneOfListOfValues(myArchivefile, TermURIs.taxonID_URI, validValues, true);
            Assert.assertTrue(result.isAllLinesComplying());
            Assert.assertFalse(result.isAllLinesViolating());
        } catch (Exception e) {
            Assert.fail("checkFieldHasOneOfListOfValues exception : " + e);
        }

    }

    @Test
    public void checkFieldHasOneOfListOfValuesTest_testingSomeRecords() {
        MyArchiveFile myArchivefile;
        Term accessTerm = TermFactory.instance().findTerm(TermURIs.accessURI);
        Term scientificNameTerm = TermFactory.instance().findTerm(TermURIs.scientificNameURI);

        RecordImpl record1, record2, record3;

        // create archive fields
        ArchiveField idField = new ArchiveField();
        ArchiveField field1 = new ArchiveField();

        // set field types
        idField.setTerm(accessTerm);
        field1.setTerm(scientificNameTerm);

        // set indexes
        field1.setIndex(0);

        // add them to list
        ArrayList<ArchiveField> fieldList = new ArrayList<ArchiveField>();
        fieldList.add(field1);

        // create record
        record1 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record2 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);
        record3 = new RecordImpl(idField, fieldList, TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE), false, false);

        // add values
        String[] recordValues1 = {"panthera tigurus"};
        String[] recordValues2 = {"jan"};
        String[] recordValues3 = {""};
        record1.setRow(recordValues1);
        record2.setRow(recordValues2);
        record3.setRow(recordValues3);

        //Creating Archive File
        myArchivefile = new MyArchiveFile();
        myArchivefile.setRowType(TermFactory.instance().findTerm(MediaValidationFunctions.ROW_TYPE));
        myArchivefile.addField(field1);
        myArchivefile.addRecord(record1);
        myArchivefile.addRecord(record2);
        myArchivefile.addRecord(record3);

        String[] validValues = {"panthera tigurus", "chicken"};
        try {
            ArchiveFileState result = DwcaHandler.checkFieldHasOneOfListOfValues(myArchivefile, TermURIs.scientificNameURI, validValues, false);
            Assert.assertFalse(result.isAllLinesViolating());
            Assert.assertTrue(result.getTotalNumberOfLines()==3);
            Assert.assertTrue(result.getLinesViolatingRule()==2);
        } catch (Exception e) {
            Assert.fail("checkFieldHasOneOfListOfValues exception : " + e);
        }
        try {
            ArchiveFileState result = DwcaHandler.checkFieldHasOneOfListOfValues(myArchivefile, TermURIs.scientificNameURI, validValues, true);
            Assert.assertFalse(result.isAllLinesViolating());
            Assert.assertTrue(result.getTotalNumberOfLines()==3);
            Assert.assertTrue(result.getLinesViolatingRule()==1);
        } catch (Exception e) {
            Assert.fail("checkFieldHasOneOfListOfValues exception : " + e);
        }

    }


}
