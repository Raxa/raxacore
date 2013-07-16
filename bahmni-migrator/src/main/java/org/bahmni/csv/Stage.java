package org.bahmni.csv;

import org.apache.log4j.Logger;
import org.bahmni.csv.exception.MigrationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Stage<T extends CSVEntity> {
    static final Stage VALIDATION = new Stage("validation");
    static final Stage MIGRATION = new Stage("migration");

    private String stageName;
    private int numberOfThreads;
    private CSVFile errorFile;
    private CSVFile inputCSVFile;

    private static Logger logger = Logger.getLogger(Stage.class);

    private Stage(String stageName) {
        this.stageName = stageName;
    }

    public Callable<RowResult> getCallable(EntityPersister entityPersister, CSVEntity csvEntity) {
        // TODO : Mujir - can we do this more elegantly? If not for csvEntity we could inject Callable by constructor
        if (this == Stage.VALIDATION)
            return new ValidationCallable(entityPersister, csvEntity);

        return new MigrationCallable(entityPersister, csvEntity);
    }

    public MigrateResult<T> run(EntityPersister entityPersister) throws IOException, IllegalAccessException, InstantiationException {
        logger.info("Starting " + stageName + " Stage with file - " + inputCSVFile.getAbsoluteFileName());
        MigrateResult<T> stageResult = new MigrateResult<>(stageName);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        try {
            inputCSVFile.openForRead();

            CSVEntity csvEntity;
            List<Future<RowResult>> results = new ArrayList<>();
            while ((csvEntity = inputCSVFile.readEntity()) != null) {
                Future<RowResult> rowResult = executorService.submit(getCallable(entityPersister, csvEntity));
                results.add(rowResult);
            }

            for (Future<RowResult> result : results) {
                RowResult<T> rowResult = result.get();
                stageResult.addResult(rowResult);
                if (!rowResult.isSuccessful()) {
                    logger.error("Failed for record - " + rowResult.getRowWithErrorColumnAsString());
                    errorFile.writeARecord(rowResult, inputCSVFile.getHeaderRow());
                }
            }

        } catch (InterruptedException e) {
            logger.error("Thread interrupted exception. " + getStackTrace(e));
            throw new MigrationException("Could not execute threads", e);
        } catch (ExecutionException e) {
            logger.error("Could not execute threads. " + getStackTrace(e));
            throw new MigrationException("Could not execute threads", e);
        } finally {
            logger.warn("Stage : " + stageName + ". Successful records count : " + stageResult.numberOfSuccessfulRecords() + ". Failed records count : " + stageResult.numberOfFailedRecords());

            executorService.shutdown();
            inputCSVFile.close();
            errorFile.close();
        }
        return stageResult;
    }

    void setErrorFile(CSVFile<T> errorFile) {
        this.errorFile = errorFile;
    }

    void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    void setInputCSVFile(CSVFile inputCSVFile) {
        this.inputCSVFile = inputCSVFile;
    }

    private static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    @Override
    public String toString() {
        return stageName;
    }
}


class StageBuilder<T extends CSVEntity> {
    private Stage stage;
    private CSVFile<T> errorFile;
    private int numberOfThreads;
    private CSVFile inputCSVFileLocation;

    public StageBuilder<T> validation() {
        stage = Stage.VALIDATION;
        return this;
    }

    public StageBuilder<T> migration() {
        stage = Stage.MIGRATION;
        return this;
    }

    public StageBuilder<T> withErrorFile(CSVFile<T> validationErrorFile) {
        this.errorFile = validationErrorFile;
        return this;
    }

    public Stage build() {
        stage.setErrorFile(errorFile);
        stage.setNumberOfThreads(numberOfThreads);
        stage.setInputCSVFile(inputCSVFileLocation);
        return stage;
    }

    public StageBuilder<T> withNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        return this;
    }

    public StageBuilder withInputFile(CSVFile inputCSVFileLocation) {
        this.inputCSVFileLocation = inputCSVFileLocation;
        return this;
    }
}