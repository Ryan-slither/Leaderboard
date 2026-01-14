package com.osc.leaderboard.pullrequest.model;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pullrequests")
public class PullRequest {

    @Id
    private String Id;

    private Instant mergedAt;

    private ObjectId developerId;

    private ObjectId repoId;

    public PullRequest() {
    }

    public PullRequest(String id, Instant mergedAt, ObjectId developerId, ObjectId repoId) {
        Id = id;
        this.mergedAt = mergedAt;
        this.developerId = developerId;
        this.repoId = repoId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Instant getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(Instant mergedAt) {
        this.mergedAt = mergedAt;
    }

    public ObjectId getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(ObjectId developerId) {
        this.developerId = developerId;
    }

    public ObjectId getRepoId() {
        return repoId;
    }

    public void setRepoId(ObjectId repoId) {
        this.repoId = repoId;
    }

    @Override
    public String toString() {
        return "PullRequest [Id=" + Id + ", mergedAt=" + mergedAt + ", developerId=" + developerId + ", repoId="
                + repoId + "]";
    }

}