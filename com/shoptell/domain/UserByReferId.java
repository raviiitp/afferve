package com.shoptell.domain;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "afferve", name = "user_by_referId")
public class UserByReferId {
	@PartitionKey
    private String referId;
	
	@ClusteringColumn
    private String id; //afferve user id

	public UserByReferId() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getReferId() {
		return referId;
	}

	public void setReferId(String referId) {
		this.referId = referId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((referId == null) ? 0 : referId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserByReferId other = (UserByReferId) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (referId == null) {
			if (other.referId != null)
				return false;
		} else if (!referId.equals(other.referId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserByReferId [referId=" + referId + ", id=" + id + "]";
	}
}
