package com.ippon.unchained.domain;


import javax.persistence.*;
import java.io.Serializable;
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

    public BlockchainUser activePolls(String activePolls) {
        this.activePolls = activePolls;
        return this;
    }

    public void setActivePolls(String activePolls) {
        this.activePolls = activePolls;
    }

    public String getInactivePolls() {
        return inactivePolls;
    }

    public BlockchainUser inactivePolls(String inactivePolls) {
        this.inactivePolls = inactivePolls;
        return this;
    }

    public void setInactivePolls(String inactivePolls) {
        this.inactivePolls = inactivePolls;
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
}
