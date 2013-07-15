package org.bahmni.csv;

import junit.framework.Assert;
import org.bahmni.csv.exception.MigrationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MigratorTest {
    private CSVFile mockInputFile;

    @Before
    public void setup() throws IOException {
        mockInputFile = mock(CSVFile.class);
        doNothing().when(mockInputFile).open();
        doNothing().when(mockInputFile).close();
    }

    @Test
    public void migrate_returns_success_on_completion() throws IOException, IllegalAccessException, InstantiationException {
        when(mockInputFile.getHeaderRow()).thenReturn(new String[]{"id", "name"});
        when(mockInputFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<DummyCSVEntity>(mockInputFile, null, null, new AllPassEnitityPersister(), 1, 1);

        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();

        Assert.assertTrue("should return true as migration was successful", migrateStatus.isValidationSuccessful());
        Assert.assertTrue("should return true as migration was successful", migrateStatus.isMigrationSuccessful());
        Assert.assertEquals(0, migrateStatus.numberOfFailedMigrationRecords());

        verify(mockInputFile, times(2)).open(); // for migration and validation
        verify(mockInputFile, times(2)).close(); // for migration and validation
    }

    @Test
    public void migrate_fails_validation_on_validation_errors() throws IllegalAccessException, IOException, InstantiationException {
        when(mockInputFile.getHeaderRow()).thenReturn(new String[]{"id", "name"});
        when(mockInputFile.getFileName()).thenReturn("/tmp/somedirectory/fileToImport.csv");
        when(mockInputFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        CSVFile mockValidationErrorFile = mock(CSVFile.class);
        List<String[]> errorRecords = new ArrayList<>();
        errorRecords.add(new String[]{"1", "dummyEntity1"});
        errorRecords.add(new String[]{"2", "dummyEntity2"});
        doNothing().when(mockValidationErrorFile).writeRecords(new String[]{"id", "name"}, errorRecords);

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<DummyCSVEntity>(mockInputFile, mockValidationErrorFile, null, new ValidationFailedEnitityPersister(), 1, 1);

        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();
        Assert.assertFalse("should return false as validation failed", migrateStatus.isValidationSuccessful());
        Assert.assertFalse("migration was unsuccessful as validation failed", migrateStatus.isMigrationSuccessful());
        Assert.assertEquals(2, migrateStatus.numberOfFailedValidationRecords());
        Assert.assertEquals(0, migrateStatus.numberOfFailedMigrationRecords());

        verify(mockInputFile, times(1)).open(); // for validation
        verify(mockInputFile, times(1)).close(); // for validation
    }

    @Test
    public void migrate_fails_on_validation_pass_but_migration_errors() throws IllegalAccessException, IOException, InstantiationException {
        when(mockInputFile.getHeaderRow()).thenReturn(new String[]{"id", "name"});
        when(mockInputFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null)
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        CSVFile mockMigrationErrorFile = mock(CSVFile.class);
        List<String[]> errorRecords = new ArrayList<>();
        errorRecords.add(new String[] {"1", "dummyEntity1"});
        errorRecords.add(new String[] {"2", "dummyEntity2"});
        doNothing().when(mockMigrationErrorFile).writeRecords(new String[]{"id", "name"}, errorRecords);

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<DummyCSVEntity>(mockInputFile, null, mockMigrationErrorFile, new MigrationFailedEnitityPersister(), 1, 1);

        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();
        Assert.assertTrue("should return true as validation passed", migrateStatus.isValidationSuccessful());
        Assert.assertFalse("should return false as migration failed", migrateStatus.isMigrationSuccessful());

        verify(mockInputFile, times(2)).open(); // for migration and validation
        verify(mockInputFile, times(2)).close(); // for migration and validation
    }

    @Test(expected = MigrationException.class)
    public void any_exception_during_migration_throws_MigrationException() throws IOException {
        doThrow(new IOException("any exception")).when(mockInputFile).open();

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<DummyCSVEntity>(mockInputFile, null, null, new AllPassEnitityPersister(), 1, 1);
        dummyCSVEntityMigrator.migrate();

        verify(mockInputFile, times(1)).open(); // for validation
        verify(mockInputFile, times(1)).close(); // for validation
    }

}
