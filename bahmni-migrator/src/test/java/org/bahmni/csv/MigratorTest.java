package org.bahmni.csv;

import junit.framework.Assert;
import org.bahmni.csv.exception.MigrationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class MigratorTest {
    private CSVFile mockFile;

    @Before
    public void setup() throws IOException {
        mockFile = mock(CSVFile.class);
        doNothing().when(mockFile).open();
        doNothing().when(mockFile).close();
    }

    @Test
    public void migrate_returns_success_on_completion() throws IOException, IllegalAccessException, InstantiationException {
        when(mockFile.getHeaderRow()).thenReturn(new String[] {"id", "name"});
        when(mockFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<DummyCSVEntity>(mockFile, new AllPassEnitityPersister());

        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();

        Assert.assertTrue("should return true as migration was successful", migrateStatus.isValidationSuccessful());
        Assert.assertTrue("should return true as migration was successful", migrateStatus.isMigrationSuccessful());
        Assert.assertEquals(0, migrateStatus.numberOfFailedRecords());

        verify(mockFile, times(2)).open(); // for migration and validation
        verify(mockFile, times(2)).close(); // for migration and validation
    }

    @Test
    public void migrate_fails_validation_on_validation_errors() throws IllegalAccessException, IOException, InstantiationException {
        when(mockFile.getHeaderRow()).thenReturn(new String[] {"id", "name"});
        when(mockFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<DummyCSVEntity>(mockFile, new ValidationFailedEnitityPersister());

        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();
        Assert.assertFalse("should return false as validation failed", migrateStatus.isValidationSuccessful());
        Assert.assertFalse("migration was unsuccessful as validation failed", migrateStatus.isMigrationSuccessful());
        Assert.assertEquals(2, migrateStatus.numberOfValidatedRecords());
        Assert.assertEquals(2, migrateStatus.numberOfFailedRecords());

        verify(mockFile, times(1)).open(); // for validation
        verify(mockFile, times(1)).close(); // for validation
    }

    @Test
    public void migrate_fails_on_validation_pass_but_migration_errors() throws IllegalAccessException, IOException, InstantiationException {
        when(mockFile.getHeaderRow()).thenReturn(new String[] {"id", "name"});
        when(mockFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null)
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<DummyCSVEntity>(mockFile, new MigrationFailedEnitityPersister());

        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();
        Assert.assertTrue("should return true as validation passed", migrateStatus.isValidationSuccessful());
        Assert.assertFalse("should return false as migration failed", migrateStatus.isMigrationSuccessful());

        verify(mockFile, times(2)).open(); // for migration and validation
        verify(mockFile, times(2)).close(); // for migration and validation
    }

    @Test(expected = MigrationException.class)
    public void any_exception_during_migration_throws_MigrationException() throws IOException {
        doThrow(new IOException("any exception")).when(mockFile).open();

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<DummyCSVEntity>(mockFile, new AllPassEnitityPersister());
        dummyCSVEntityMigrator.migrate();

        verify(mockFile, times(1)).open(); // for validation
        verify(mockFile, times(1)).close(); // for validation
    }

}
