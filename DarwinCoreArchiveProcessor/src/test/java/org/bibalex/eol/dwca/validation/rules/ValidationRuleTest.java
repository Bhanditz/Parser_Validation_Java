package org.bibalex.eol.dwca.validation.rules;

import org.bibalex.eol.dwca.validation.ArchiveFileState;
import org.bibalex.eol.dwca.validation.ValidationResult;
import org.bibalex.eol.dwca.validation.functions.TaxonValidationFunctions;
import org.junit.Test;
import org.junit.Assert;


/**
 * Created by Mina.Edward on 9/19/2016.
 */
public class ValidationRuleTest {

    @Test
    public void reportResultTest_allComplying() {
        ArchiveFileState result1 = new ArchiveFileState();
        result1.setAllLinesComplying(true);
        ValidationResult validationResult = new ValidationResult("Testing resource");
        ValidationRule rule = new FieldValidationRule();
        rule.reportResult(TaxonValidationFunctions.ROW_TYPE, result1, validationResult);

        Assert.assertTrue(validationResult.getStructuralErrors().isEmpty());
        Assert.assertTrue(validationResult.getRowByRowErrors().isEmpty());
        Assert.assertTrue(validationResult.getRowByRowWarnings().isEmpty());
    }

    @Test
    public void reportResultTest_allViolating() {
        ArchiveFileState archiveFileState = new ArchiveFileState(true);
        ValidationResult validationResult = new ValidationResult("Testing resource");
        ValidationRule rule = new FieldValidationRule();
        rule.setFailureType(ValidationRule.FailureTypes.ERROR);
        rule.setFailureMessage("Row do not have a scientific name");
        rule.reportResult(TaxonValidationFunctions.ROW_TYPE, archiveFileState, validationResult);
        Assert.assertTrue(validationResult.getStructuralErrors().isEmpty());
        Assert.assertTrue(validationResult.getRowByRowErrors().size() == 1);
        Assert.assertTrue(validationResult.getRowByRowWarnings().isEmpty());
    }

    @Test
    public void reportResultTest_SomeLinesViolating() {
        ValidationResult validationResult = new ValidationResult("Testing resource");
        ValidationRule rule = new FieldValidationRule();
        rule.setFailureType(ValidationRule.FailureTypes.WARNING);
        rule.setFailureMessage("Row do not have a scientific name");

        ArchiveFileState result = new ArchiveFileState(40, 20);
        rule.reportResult(TaxonValidationFunctions.ROW_TYPE, result, validationResult);

        Assert.assertTrue(validationResult.getStructuralErrors().isEmpty());
        Assert.assertTrue(validationResult.getRowByRowErrors().isEmpty());
        Assert.assertTrue(validationResult.getRowByRowWarnings().size() == 1);
    }
}
