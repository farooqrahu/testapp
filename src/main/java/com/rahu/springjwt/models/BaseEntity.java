package com.rahu.springjwt.models;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 4276129915194210633L;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Timestamp createdAt;

  @LastModifiedDate
  @UpdateTimestamp
  @Column(name = "updated_at")
  private Timestamp updatedAt;

  @Column(name = "deleted_at")
  private Timestamp deletedAt;


  public Timestamp getCreatedAt() {
    return createdAt;
  }
  @PrePersist
  public void setCreatedAt() {
    createdAt = new Timestamp(new Date().getTime());
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }
  @PreUpdate
  public void setUpdatedAt() {
    this.updatedAt = new Timestamp(new Date().getTime());;
  }

  public Timestamp getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(Timestamp deletedAt) {
    this.deletedAt = deletedAt;
  }
}
