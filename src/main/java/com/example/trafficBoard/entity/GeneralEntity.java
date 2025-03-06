package com.example.trafficBoard.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
public class GeneralEntity {

    /** 등록일시 */
    @Column(name = "regDt",columnDefinition = "DATETIME")
    @CreatedDate
    private LocalDateTime registrationDate;

    /** 수정일시 */
    @Column(name = "modDt",columnDefinition = "DATETIME")
    @LastModifiedDate
    private LocalDateTime lastUpdatedDate;
}
