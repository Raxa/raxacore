package org.bahmni.csv;

import junit.framework.Assert;
import org.bahmni.csv.exception.MigrationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class MigratorTest {
    private CSVFile mockInputFile;
    private CSVFile mockValidationErrorFile;
    private CSVFile mockMigrationErrorFile;

    @Before
    public void setup() throws IOException {
        mockInputFile = mock(CSVFile.class);
        mockValidationErrorFile = mock(CSVFile.class);
        mockMigrationErrorFile = mock(CSVFile.class);
        doNothing().when(mockInputFile).openForRead();
        doNothing().when(mockInputFile).close();
    }

    @Test
    public void migrate_returns_success_on_completion() throws IOException, IllegalAccessException, InstantiationException {
        when(mockInputFile.getHeaderRow()).thenReturn(new String[]{"id", "name"});
        when(mockInputFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<>(new AllPassEntityPersister(), getOneStage());
        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();

        Assert.assertTrue("should return true as migration was successful", !migrateStatus.hasFailed());
        Assert.assertEquals(0, migrateStatus.numberOfFailedRecords());

        verify(mockInputFile, times(1)).openForRead();
        verify(mockInputFile, times(1)).close();
    }

    @Test
    public void migrate_fails_validation_on_validation_errors() throws IllegalAccessException, IOException, InstantiationException {
        String[] headerRow = {"id", "name"};
        when(mockInputFile.getHeaderRow()).thenReturn(headerRow);
        when(mockInputFile.getFileName()).thenReturn("/tmp/somedirectory/fileToImport.csv");
        when(mockInputFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<>(new ValidationFailedEntityPersister("validation failed"), getOneStage());
        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();

        Assert.assertTrue("should return false as validation failed", migrateStatus.hasFailed());
        Assert.assertEquals(2, migrateStatus.numberOfFailedRecords());

        verify(mockInputFile, times(1)).openForRead();

        verify(mockValidationErrorFile).writeARecord(new RowResult(new DummyCSVEntity("1", "dummyEntity1"), "validation failed"), headerRow);
        verify(mockValidationErrorFile).writeARecord(new RowResult(new DummyCSVEntity("2", "dummyEntity2"), "validation failed"), headerRow);

        verify(mockInputFile, times(1)).close();
        verify(mockValidationErrorFile, times(1)).close();
    }

    @Test
    public void migrate_fails_on_validation_pass_but_migration_errors() throws IllegalAccessException, IOException, InstantiationException {
        String[] headerRow = {"id", "name"};
        when(mockInputFile.getHeaderRow()).thenReturn(headerRow);
        when(mockInputFile.readEntity())
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null)
                .thenReturn(new DummyCSVEntity("1", "dummyEntity1"))
                .thenReturn(new DummyCSVEntity("2", "dummyEntity2"))
                .thenReturn(null);

        Exception exception = new Exception("migration failed");
        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<>(new MigrationFailedEntityPersister(exception), getTwoStages());
        MigrateResult<DummyCSVEntity> migrateStatus = dummyCSVEntityMigrator.migrate();

        Assert.assertTrue("should return true as migration failed", migrateStatus.hasFailed());

        verify(mockInputFile, times(2)).openForRead();

        verify(mockMigrationErrorFile).writeARecord(new RowResult(new DummyCSVEntity("1", "dummyEntity1"), exception), headerRow);
        verify(mockMigrationErrorFile).writeARecord(new RowResult(new DummyCSVEntity("2", "dummyEntity2"), exception), headerRow);

        verify(mockInputFile, times(2)).close();
        verify(mockValidationErrorFile, times(1)).close();
        verify(mockMigrationErrorFile, times(1)).close();
    }

    @Test(expected = MigrationException.class)
    public void any_exception_during_migration_throws_MigrationException() throws IOException {
        doThrow(new IOException("any exception")).when(mockInputFile).openForRead();

        Migrator<DummyCSVEntity> dummyCSVEntityMigrator = new Migrator<>(new AllPassEntityPersister(), getOneStage());
        dummyCSVEntityMigrator.migrate();

        verify(mockInputFile, times(1)).openForRead();
        verify(mockInputFile, times(1)).close();
    }

    private Stages<DummyCSVEntity> getOneStage() {
        Stage<DummyCSVEntity> validationStage = Stage.VALIDATION;
        validationStage.setInputCSVFile(mockInputFile);
        validationStage.setNumberOfThreads(1);
        validationStage.setErrorFile(this.mockValidationErrorFile);

        Stages<DummyCSVEntity> allStages = new Stages<>();
        allStages.addStage(validationStage);

        return allStages;
    }

    private Stages<DummyCSVEntity> getTwoStages() {
        Stage<DummyCSVEntity> validationStage = Stage.VALIDATION;
        validationStage.setInputCSVFile(mockInputFile);
        validationStage.setNumberOfThreads(1);
        validationStage.setErrorFile(this.mockValidationErrorFile);

        Stage<DummyCSVEntity> migrationStage = Stage.MIGRATION;
        migrationStage.setInputCSVFile(mockInputFile);
        migrationStage.setNumberOfThreads(1);
        migrationStage.setErrorFile(this.mockMigrationErrorFile);

        Stages<DummyCSVEntity> allStages = new Stages<>();
        allStages.addStage(validationStage);
        allStages.addStage(migrationStage);

        return allStages;
    }
}
