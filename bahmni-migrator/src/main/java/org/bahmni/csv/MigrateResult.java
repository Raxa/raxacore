package org.bahmni.csv;

public class MigrateResult<T extends CSVEntity> {
    private int successCount;
    private int failCount;
    private String stageName;

    public MigrateResult(String stageName) {
        this.stageName = stageName;
    }

    public boolean hasFailed() {
        return failCount > 0;
    }

    public void addResult(RowResult<T> rowResult) {
        if (rowResult.isSuccessful()) {
            successCount++;
        } else {
            failCount++;
        }
    }

    public int numberOfFailedRecords() {
        return failCount;
    }

    public int numberOfSuccessfulRecords() {
        return successCount;
    }

    public String getStageName() {
        return stageName;
    }
}
