package org.w2fc.geoportal.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Yevhen
 */
@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table(name = "OPERATION_STATUS")
public class OperationStatus extends AbstractDomain<OperationStatus> implements Serializable{

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @Column (name = "guid", nullable = false)
    private Long guid;

    @Column (name = "iKey", nullable = true)
    private  String iKey;

    @Column (name = "message", nullable = true)
    private String message;

    @Column (name = "action", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Action action;

    @Column (name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public OperationStatus() {
    }

    public OperationStatus(Long guid, Action action, Status status) {
        this.guid = guid;
        this.action = action;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuid() {
        return guid;
    }

    public void setGuid(Long guid) {
        this.guid = guid;
    }

    public String getiKey() {
        return iKey;
    }

    public void setiKey(String iKey) {
        this.iKey = iKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static enum Action {
        CREATE, UPDATE, DELETE
    }

    public static enum Status {
        SUCCESS, FAILURE
    }
}
