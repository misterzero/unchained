package com.ippon.unchained.domain;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Poll.
 */
@Entity
@Table(name = "poll")
public class Poll implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final AtomicInteger nextId = new AtomicInteger(1);


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "options")
    private String options;

    @Column(name = "expiration")
    private LocalDate expiration;

    private String chainCodeName;
    
    private String owner;

    private int status;

    // This variable is used for storage when creating a poll - see use in PollRepositoryImpl.java's save()
    // format: "1,3,4,..." (CSV of User IDs)
    private String voters;

    public Poll(String name) {
        this.name = name;
    }

    public Poll() {
    }

    public Poll(String name, String options, LocalDate expiration, int status, String voters) {
        this.id = nextId.longValue();
        this.name = name;
        this.options = options;
        this.expiration = expiration;
        this.status = status;
        this.voters = voters;
        nextId.incrementAndGet();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Poll name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }


    public List<Option> getOptionsAsList() {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        try {
            return mapper.readValue(this.getOptions(), typeFactory.constructCollectionType(List.class, Option.class));
        } catch (Exception e) {

        }
        return null;
    }

    public String getOptions() {
        return options;
    }

    public Poll options(String options) {
        this.options = options;
        return this;
    }

    public Poll options(List<Option> options) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.options = mapper.writeValueAsString(options);
        } catch (Exception e) {

        }
        return this;
    }

    public void setOptions(List<Option> options) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.options = mapper.writeValueAsString(options);
        } catch (Exception e) {

        }
    }

    public void setOptions(String options) {
        // NOTE: ONLY WORKS WITH A JSON STRING OF OPTIONS
        // {"options": [{"opt1": 0}, {"opt2": 0}]}
        this.options = options;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public Poll expiration(LocalDate expiration) {
        this.expiration = expiration;
        return this;
    }

    public void setExpiration(LocalDate expiration) {
        this.expiration = expiration;
    }

    public String getChainCodeName() {
        return chainCodeName;
    }

    public void setChainCodeName() {
        this.chainCodeName = getId()+"_"+getName();
    }

    public void setChainCodeName(String chainCodeName) {
        this.chainCodeName = chainCodeName;
        this.setId(Long.parseLong(chainCodeName.split("_",2)[0]));
        this.setName(chainCodeName.split("_",2)[1]);
    }

    public int getStatus() {
        return status;
    }

    public Poll status(int status) {
        this.status = status;
        return this;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setVoters(String voters) {
        this.voters = voters;
    }

    public String getVoters() {
        return this.voters;
    }

    public Poll clone() {
        Poll p = new Poll(name,options,expiration,status,voters);
        p.setChainCodeName();
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Poll poll = (Poll) o;
        if (poll.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), poll.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Poll{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", options='" + getOptions() + "'" +
            ", voters='" + getVoters() + "'" +
            ", expiration='" + getExpiration() + "'" +
            "}";
    }

    public String toJSONString() {
        return "{" +
            "\"options\":" + getOptions() +
            ",\"status\":" + getStatus() +
            "}";
    }
}
