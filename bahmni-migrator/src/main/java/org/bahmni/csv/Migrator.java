package org.bahmni.csv;

import org.apache.log4j.Logger;
import org.bahmni.csv.exception.MigrationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

// Assumption - if you use multithreading, there should be no dependency between the records in the file.
public class Migrator<T extends CSVEntity> {
    private Stages<T> allStages;
    private final EntityPersister entityPersister;

    private static Logger logger = Logger.getLogger(Migrator.class);

    public Migrator(EntityPersister entityPersister, Stages allStages) {
        this.entityPersister = entityPersister;
        this.allStages = allStages;
    }

    public MigrateResult<T> migrate() {
        MigrateResult<T> stageResult = null;
        try {
            while (allStages.hasMoreStages()) {
                stageResult = allStages.nextStage().run(entityPersister);
                if (stageResult.hasFailed()) {
                    return stageResult;
                }
            }
        } catch(MigrationException e) {
            logger.error(getStackTrace(e));
            throw e;
        } catch (Exception e) {
            logger.error(getStackTrace(e));
            throw new MigrationException(getStackTrace(e), e);
        }
        return stageResult;
    }


    private static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}
