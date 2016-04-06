package org.bahmni.module.admin.csv.persister;

import org.bahmni.csv.Messages;
import org.bahmni.module.admin.csv.models.RelationshipRow;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class RelationshipPersisterTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldPassValidationIfAllRequiredFieldsAreProvided() throws Exception {
        Messages messages = getRelationshipPersister().validate(new RelationshipRow("GAN200012", "GAN200015", "Parent", "Child", "2014-02-01", "2015-01-01"));
        assertEquals(0, messages.size());
    }

    @Test
    public void shouldThrowExceptionIfPersonAIsNotSpecified() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Patient unique identifier not specified.");
        getRelationshipPersister().validateRow(new RelationshipRow(null, "GAN200015", "", "Child", "2014-02-01", "2015-01-01"));
    }

    @Test
    public void shouldThrowExceptionIfProviderNameAndRelationalIdentifierIsNotSpecified() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Both Provider Name and Relation Identifier cannot be null.");
        getRelationshipPersister().validateRow(new RelationshipRow("GAN200015", null, "", "Child", "2014-02-01", "2015-01-01"));
    }

    @Test
    public void shouldThrowExceptionIfRelationshipBIsToANotSpecified() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Relationship type is not specified.");
        getRelationshipPersister().validateRow(new RelationshipRow("GAN200015", "GAN200016", "ProviderName", null, "2014-02-01", "2015-01-01"));
    }


    @Test
    public void shouldThrowExceptionIfEndDateIsBeforeStartDate() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Start date should be before end date.");
        getRelationshipPersister().validateRow(new RelationshipRow("GAN200012", "GAN200015", "ProviderName", "Child", "2015-02-01", "2014-01-01"));
    }

    @Test
    public void shouldThrowExceptionIfTheStartDateFormatIsWrong() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Could not parse provided dates. Please provide date in format yyyy-mm-dd");
        getRelationshipPersister().validateRow(new RelationshipRow("GAN200012", "GAN200015", "ProviderName", "Child", "02-01-2015", "2014-01-01"));
    }

    @Test
    public void shouldThrowExceptionIfTheEndDateFormatIsWrong() throws Exception {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Could not parse provided dates. Please provide date in format yyyy-mm-dd");
        getRelationshipPersister().validateRow(new RelationshipRow("GAN200012", "GAN200015", "ProviderName", "Child", "2015-02-01", "01-01-2014"));
    }

    private RelationshipPersister getRelationshipPersister() {
        return new RelationshipPersister();
    }


}