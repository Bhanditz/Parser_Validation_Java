package org.bibalex.eol.dwca.validation.rules;


import org.bibalex.eol.MyArchiveFile;
import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.dwca.validation.TermURIs;
import org.bibalex.eol.dwca.validation.ValidationResult;
import org.bibalex.eol.dwca.validation.functions.MediaValidationFunctions;
import org.bibalex.eol.handlers.LogHandler;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwca.io.Archive;
import org.gbif.dwca.io.ArchiveField;
import org.gbif.dwca.io.ArchiveFile;
import org.gbif.dwca.record.RecordImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Mina.Edward on 9/19/2016.
 */
public class FieldValidationRuleTest extends FieldValidationRule {
    static MyArchiveFile myArchivefile;


    public static void createArchiveFile() {
        Term scientificNameTerm = TermFactory.instance().findTerm(TermURIs.scientificNameURI);
        Term languageTerm = TermFactory.instance().findTerm(TermURIs.languageURI);
        RecordImpl record1, record2, record3;
        // create archive fields
        ArchiveField idField = new ArchiveField();
        ArchiveField field1 = new ArchiveField();

        // set field types
        idField.setTerm(scientificNameTerm);
        field1.setTerm(languageTerm);

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
        String[] recordValues1 = {"panthera tigurus", "http://purl.org/dc/dcmitype/movingimage", "eng", "http://rs.tdwg.org/ontology/voc/SPMInfoItems#associations", "http://creativecommons.org/publicdomain/zero/1.0/"};
        String[] recordValues2 = {"panthera tigurus", "", "no valid language code", "http://rs.tdwg.org/pccore/", "not public domain"};
        String[] recordValues3 = {"panthera tigurus", "http://purl.org/dc/dcmitype/text", "eng-eng", "", ""};
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
    }

    @BeforeClass
    public static void prepareArchiveFiles() {
        String propertiesFile = "configs.properties";
        LogHandler.initializeHandler(propertiesFile);
        createArchiveFile();
    }

    @Test
    public void callValidationFunctionTest() {
        try {
            Class<?> myClass = Class.forName(FieldValidationRuleTest.class.getName());
            Method method = myClass.getMethod("dummyFieldValidationFunction", ArchiveFile.class, String.class);
            Archive dwcArchive = new Archive();
            dwcArchive.setCore(myArchivefile);
            ValidationResult validationResult = new ValidationResult("Testing archive");
            this.rowTypeURI = MediaValidationFunctions.ROW_TYPE;
            this.fieldURI = TermURIs.scientificNameURI;
            Assert.assertTrue(callValidationFunction(method, dwcArchive, validationResult));
        } catch (Exception e) {
            Assert.fail("Failed to instantiate a method " + e);
        }
    }

    public static ArchiveFileState dummyFieldValidationFunction(ArchiveFile archiveFile, String fieldURI) throws Exception {
        if (!fieldURI.equalsIgnoreCase(TermURIs.scientificNameURI))
            throw new Exception("fieldURI did not arrive to the dummy validation function");
        if (!archiveFile.getRowType().qualifiedName().equalsIgnoreCase(MediaValidationFunctions.ROW_TYPE))
            throw new Exception("archiveFile row type did not arrive to the dummy validation function");
        ArchiveFileState result = new ArchiveFileState();
        result.setAllLinesComplying(true);
        return result;
    }

    @Test
    public void dynamicallyLoadMethodTest() {
        this.validationFunction = FieldValidationRuleTest.class.getCanonicalName()
                + ".dummyFieldValidationFunction";
        try {
            Method method = this.dynamicallyLoadMethod();
            if (method == null || method.getName().equals(this.validationFunction))
                throw new Exception("");
        } catch (Exception e) {
            Assert.fail("Failed to dynamicallyLoadMethodTest " + e);
        }
    }

}
