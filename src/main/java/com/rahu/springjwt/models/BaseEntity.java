package com.rahu.springjwt.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicReference;

@MappedSuperclass
@Setter
@Getter
public abstract class BaseEntity implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 4276129915194210633L;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Timestamp updatedAt;

  @Column(name = "deleted_at")
  private Timestamp deletedAt;
}
