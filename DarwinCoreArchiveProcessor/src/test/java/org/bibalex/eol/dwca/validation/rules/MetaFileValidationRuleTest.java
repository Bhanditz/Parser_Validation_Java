package org.bibalex.eol.dwca.validation.rules;

import org.bibalex.eol.MyArchiveFile;
import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.dwca.validation.TermURIs;
import org.bibalex.eol.dwca.validation.ValidationResult;
import org.bibalex.eol.dwca.validation.functions.MediaValidationFunctions;
import org.bibalex.eol.handlers.DwcaHandler;
import org.bibalex.eol.handlers.LogHandler;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwca.io.Archive;
import org.gbif.dwca.io.ArchiveField;
import org.gbif.dwca.io.ArchiveFile;
import org.gbif.dwca.record.RecordImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Mina.Edward on 9/20/2016.
 */
public class MetaFileValidationRuleTest extends MetaFileValidationRule {

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
            Class<?> myClass = Class.forName(MetaFileValidationRuleTest.class.getName());
            Method method = myClass.getMethod("dummyMetaValidationFunction", Archive.class, MetaFileValidationRule.class);
            Archive dwcArchive = new Archive();
            dwcArchive.setCore(myArchivefile);
            ValidationResult validationResult = new ValidationResult("Testing archive");
            this.rowTypeURI = MediaValidationFunctions.ROW_TYPE;
            this.fieldURI = TermURIs.languageURI;
            Assert.assertTrue("callValidationFunction return false, whcich means a problem happened in calling validation function",
                    callValidationFunction(method, dwcArchive, validationResult));
            Assert.assertTrue("Structural errors number not equal one",
                    validationResult.getStructuralErrors().size() == 1);
        } catch (Exception e) {
            Assert.fail("Failed to instantiate a method " + e);
        }
    }

    public static boolean dummyMetaValidationFunction(Archive dwcArchive, MetaFileValidationRule rule) throws Exception {
        ArchiveFile archiveFile = DwcaHandler.getArchiveFile(dwcArchive, MediaValidationFunctions.ROW_TYPE);
        if (!archiveFile.getRowType().qualifiedName().equalsIgnoreCase(MediaValidationFunctions.ROW_TYPE))
            throw new Exception("archiveFile row type did not arrive to the dummy validation function correctly");
        if (!rule.fieldURI.equalsIgnoreCase(TermURIs.languageURI))
            throw new Exception("rule fieldURI  did not arrive to the dummy validation function correctly");
        return false;
    }

    @Test
    public void dynamicallyLoadMethodTest() {
        this.validationFunction = MetaFileValidationRuleTest.class.getCanonicalName() +".dummyMetaValidationFunction";
        try {
            Method method=this.dynamicallyLoadMethod();
            if(method==null || method.getName().equals(this.validationFunction))
                throw new Exception("");
        } catch (Exception e) {
            Assert.fail("Failed to dynamicallyLoadMethodTest " + e);
        }
    }
}
