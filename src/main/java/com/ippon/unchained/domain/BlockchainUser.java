package com.ippon.unchained.domain;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A BlockchainUser.
 */
@Entity
@Table(name = "blockchain_user")
public class BlockchainUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "active_polls")
    private String activePolls;

    @Column(name = "inactive_polls")
    private String inactivePolls;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public BlockchainUser name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActivePolls() {
        return activePolls;
    }

    public List<ActivePoll> getActivePollsAsList() {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        try {
            return mapper.readValue(this.getActivePolls(), typeFactory.constructCollectionType(List.class, ActivePoll.class));
        } catch (Exception e) {

        }
        return null;
    }

    public BlockchainUser activePolls(String activePolls) {
        this.activePolls = activePolls;
        return this;
    }

    public BlockchainUser activePolls(List<ActivePoll> activePolls) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.activePolls = mapper.writeValueAsString(activePolls);
        } catch (Exception e) {

        }
        return this;
    }

    public void setActivePolls(List<ActivePoll> activePolls) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.activePolls = mapper.writeValueAsString(activePolls);
        } catch (Exception e) {

        }
    }

    public void setActivePolls(String activePolls) {
        this.activePolls = activePolls;
    }

    public String getInactivePolls() {
        return inactivePolls;
    }

    public List<String> getInactivePollsAsList() {
        ObjectMapper mapper = new ObjectMapper();
        TypeFactory typeFactory = mapper.getTypeFactory();
        try {
            return mapper.readValue(this.getInactivePolls(), typeFactory.constructCollectionType(List.class, String.class));
        } catch (Exception e) {

        }
        return null;
    }

    public BlockchainUser inactivePolls(String inactivePolls) {
        this.inactivePolls = inactivePolls;
        return this;
    }

    public BlockchainUser inactivePolls(List<String> inactivePolls) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.inactivePolls = mapper.writeValueAsString(inactivePolls);
        } catch (Exception e) {

        }
        return this;
    }

    public void setInactivePolls(List<String> inactivePolls) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.inactivePolls = mapper.writeValueAsString(inactivePolls);
        } catch (Exception e) {

        }
    }

    public void setInactivePolls(String inactivePolls) {
        this.inactivePolls = inactivePolls;
    }

    // These eight methods are redundant, HOWEVER,
    // they are necessary for objectMapper to map properties correctly.
    // Making the blockchain properties more verbose to match the above methods instead
    // may be something to look into later.

    public void setActive(String active) {
        this.setActivePolls(active);
    }

    public void setActive(List<ActivePoll> active) {
        this.setActivePolls(active);
    }

    public String getActive() {
        return  this.getActivePolls();
    }

    public BlockchainUser active(String active) {
        return this.activePolls(active);
    }

    public void setInactive(String active) {
        this.setInactivePolls(active);
    }

    public void setInactive(List<String> active) {
        this.setInactivePolls(active);
    }

    public String getInactive() {
        return  this.getInactivePolls();
    }

    public BlockchainUser inactive(String inactive) {
        return this.activePolls(inactive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockchainUser blockchainUser = (BlockchainUser) o;
        if (blockchainUser.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), blockchainUser.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BlockchainUser{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", activePolls='" + getActivePolls() + "'" +
            ", inactivePolls='" + getInactivePolls() + "'" +
            "}";
    }

    public String toJSONString() {
        return "{" +
            "\"active\":" + getActivePolls() +
            ", \"inactive\":" + getInactivePolls() +
            "}";
    }
}
