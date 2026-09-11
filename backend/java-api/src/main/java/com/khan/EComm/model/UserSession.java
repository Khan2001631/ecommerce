package com.khan.EComm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "g_user_session")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_session_id")
    private Long userSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_rid", nullable = false)
    private User user;

    @Column(
            name = "refresh_token_hash",
            length = 255,
            nullable = false,
            unique = true
    )
    private String refreshTokenHash;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "refresh_token_expiry_time", nullable = false)
    private LocalDateTime refreshTokenExpiryTime;

    @Column(name = "last_activity_timestamp")
    private LocalDateTime lastActivityTimestamp;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    private Long updatedBy;

    public Long getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(Long userSessionId) {
        this.userSessionId = userSessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public void setRefreshTokenHash(String refreshTokenHash) {
        this.refreshTokenHash = refreshTokenHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRefreshTokenExpiryTime() {
        return refreshTokenExpiryTime;
    }

    public void setRefreshTokenExpiryTime(LocalDateTime refreshTokenExpiryTime) {
        this.refreshTokenExpiryTime = refreshTokenExpiryTime;
    }

    public LocalDateTime getLastActivityTimestamp() {
        return lastActivityTimestamp;
    }

    public void setLastActivityTimestamp(LocalDateTime lastActivityTimestamp) {
        this.lastActivityTimestamp = lastActivityTimestamp;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}