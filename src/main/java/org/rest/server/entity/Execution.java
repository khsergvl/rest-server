package org.rest.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Execution {

    @Id
    @Column
    private Long id;

    @Column
    private String execution;

    @Column
    private long ts;
}
