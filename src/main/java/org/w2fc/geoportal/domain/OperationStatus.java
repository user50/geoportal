package org.w2fc.geoportal.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

    @Column (name = "user_id", nullable = false)
    private Long userId;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "date", nullable = true)
    private Date date;

    @Column (name = "layer_id", nullable = false)
    private Long layerId;

    public OperationStatus() {
    }

    public OperationStatus(Long guid, Action action, Status status) {
        this.guid = guid;
        this.action = action;
        this.status = status;
        this.date = new Date();
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getLayerId() {
        return layerId;
    }

    public void setLayerId(Long layerId) {
        this.layerId = layerId;
    }

    public static enum Action {
        CREATE, UPDATE, DELETE
    }

    public static enum Status {
        SUCCESS, FAILURE
    }
}
