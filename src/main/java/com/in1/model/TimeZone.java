package com.in1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;


@Entity
@Table(name = "timezones")
@Configurable
public class TimeZone {
    
    @PersistenceContext
    protected transient EntityManager entityManager;

    public static final EntityManager entityManager() {
        EntityManager em = new TimeZone().entityManager;
        if (em == null)
            throw new IllegalStateException(
                    "Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }    


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Integer id;
    
    @Column(name="timezonecode")
    private String code;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    

    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
        	TimeZone attached = findTimeZone(this.getId());
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

    @Transactional
    public TimeZone merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TimeZone merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }    
    
    @Transactional(readOnly=true)
    public static TimeZone findTimeZone(Integer id) {
        if (id == null) return null;
        return entityManager().find(TimeZone.class, id);
    }    
    
    @Transactional(readOnly=true)
    public static TimeZone findTimeZoneById(Integer id) {
    	if (id == null) return null;
        return entityManager().createQuery("SELECT tz FROM TimeZone tz WHERE tz.id = :id", TimeZone.class)
        		.setParameter("id", id).getSingleResult();
    } 
}
