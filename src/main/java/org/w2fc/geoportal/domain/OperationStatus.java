package org.w2fc.geoportal.domain;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Yevhen
 */
@Entity
@org.hibernate.annotations.Entity(
        dynamicUpdate = true,     /* Optimize update sql */
        dynamicInsert = true        /* Optimize insert sql */
)

@Table(name = "OPERATION_STATUS")
public class OperationStatus implements Serializable{

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private Long id;

    @Column (name = "guid", nullable = true)
    private String guid;

    @Column (name = "pid", nullable = false)
    private String pid;

    @Column (name = "user_id", nullable = false)
    private Long userId;

    @Column (name = "message", nullable = true)
    private String message;

    @Column (name = "action", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Action action;

    @Column (name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column (name = "date", nullable = false)
    private Date date;

    @Column (name = "layer_ids", nullable = true)
    private String layerIds;

    public OperationStatus() {
    }

    public OperationStatus(String guid, String pid, Long userId, Action action, Status status, Date date, String layerIds, String message) {
        this.guid = guid;
        this.pid = pid;
        this.userId = userId;
        this.action = action;
        this.status = status;
        this.date = date;
        this.layerIds = layerIds;
        this.message = message;
    }

    public OperationStatus(String guid, String pid, Long userId, Action action, Status status, Date date, String layerIds) {
        this.guid = guid;
        this.pid = pid;
        this.userId = userId;
        this.action = action;
        this.status = status;
        this.date = date;
        this.layerIds = layerIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getLayerIds() {
        return layerIds;
    }

    public void setLayerIds(String layerIds) {
        this.layerIds = layerIds;
    }

    public static enum Action {
        CREATE, UPDATE, DELETE
    }

    public static enum Status {
        SUCCESS, FAILURE
    }
}
