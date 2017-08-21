package org.bibalex.eol.dwca.validation.rules;

import org.apache.logging.log4j.Logger;
import org.bibalex.eol.handlers.LogHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ahmad.Eldefrawy
 */

public class ValidationRulesLoaderTest {
    private static Logger logger;
    private static String propertiesFile = "configs.properties";
    private static List<String> rowTypeList = new ArrayList<String>();
    private static List<FieldValidationRule> fieldValidationRuleList = new
            ArrayList<FieldValidationRule>();
    private static List<RowValidationRule> rowValidationRuleList = new
            ArrayList<RowValidationRule>();
    private static List<MetaFileValidationRule> metaFileValidationRuleList = new
            ArrayList<MetaFileValidationRule>();
    private static ValidationRulesLoader rulesLoader;

    @BeforeClass
    public static void ValidationRulesLoadTest() {
        LogHandler.initializeHandler(propertiesFile);
        logger = LogHandler.getLogger(ValidationRulesLoader.class.getName());
        rulesLoader = new ValidationRulesLoader(propertiesFile);
        rulesLoader.loadValidationRules();
    }

    @Test
    public void loadValidationRules_Test() {
        try {
            Assert.assertTrue(rulesLoader.loadValidationRules());
        } catch (Exception e) {
            Assert.fail("Failed to loadValidationRules_Test: " + e);
        }
    }

    @Test
    public void getValidationRulesOfFieldType_Test() {
        String rowType = "http://eol.org/schema/media/Document";
        try {
            List<FieldValidationRule> rules = rulesLoader.getValidationRulesOfFieldType(rowType);
            Assert.assertEquals(rules.size(), 15);
        } catch (Exception e) {
            Assert.fail("Failed to loadValidationRules_Test: " + e);
        }
    }

    @Test
    public void getValidationRulesOfRowType_Test() {
        String rowType = "http://eol.org/schema/media/Document";
        try {
            List<RowValidationRule> rules = rulesLoader.getValidationRulesOfRowType(rowType);
            Assert.assertEquals(rules.size(), 6);
        } catch (Exception e) {
            Assert.fail("Failed to loadValidationRules_Test: " + e);
        }
    }
}
