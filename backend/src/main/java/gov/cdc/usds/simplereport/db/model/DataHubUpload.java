package gov.cdc.usds.simplereport.db.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import gov.cdc.usds.simplereport.db.model.auxiliary.DataHubUploadStatus;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@DynamicUpdate
public class DataHubUpload {

    // We have to pass config into the constructor because Configuration doesn't work in constructors
    public DataHubUpload() {
        jobStatus = DataHubUploadStatus.IN_PROGRESS;
        recordsProcessed = 0;
        responseData = "{}";
        errorMessage = "";
    }

    @Column(updatable = false, nullable = false)
    @Id
    @GeneratedValue(generator = "UUID4")
    private UUID internalId;

    // set to "SUCCESS" when done.
    @Column(nullable = false)
    @Type(type = "pg_enum")
    @Enumerated(EnumType.STRING)
    private DataHubUploadStatus jobStatus;

    @Column(updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column
    @UpdateTimestamp
    private Date updatedAt;

    @Column
    private int recordsProcessed;

    @Column
    private String errorMessage;

    @Column
    private Date earliestRecordedTimestamp;

    @Column
    private Date latestRecordedTimestamp;

    @Column
    @Type(type = "jsonb")
    private String responseData;

    public UUID getInternalId() {
        return internalId;
    }

    public DataHubUploadStatus getJobStatus() {
        return jobStatus;
    }

    public DataHubUpload setJobStatus(DataHubUploadStatus jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public int getRecordsProcessed() {
        return recordsProcessed;
    }

    public DataHubUpload setRecordsProcessed(int recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
        return this;
    }

    public Date getEarliestRecordedTimestamp() {
        return earliestRecordedTimestamp;
    }

    public DataHubUpload setEarliestRecordedTimestamp(Date earliestRecordedTimestamp) {
        this.earliestRecordedTimestamp = earliestRecordedTimestamp;
        return this;
    }

    public Date getLatestRecordedTimestamp() {
        return latestRecordedTimestamp;
    }

    public DataHubUpload setLatestRecordedTimestamp(Date latestRecordedTimestamp) {
        this.latestRecordedTimestamp = latestRecordedTimestamp;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public DataHubUpload setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getResponseData() {
        return responseData;
    }

    public DataHubUpload setResponseData(String responseData) {
        this.responseData = responseData;
        return this;
    }
}