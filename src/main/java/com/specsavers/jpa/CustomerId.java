package com.specsavers.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer_id_windows")
/**
 * POJO for saving in table customer_id_windows.
 */
public class CustomerId {

	private long firstFree;
	private long lastFree;

	@Id
	@Column(name = "first_free")
	public long getFirstFree() {
		return firstFree;
	}

	public void setFirstFree(final long firstFree) {
		this.firstFree = firstFree;
	}

	@Column(name = "last_free")
	public long getLastFree() {
		return lastFree;
	}

	public void setLastFree(final long lastFree) {
		this.lastFree = lastFree;
	}

	@Override
	public String toString() {
		return "[" + this.getClass().getName() + ": " + getFirstFree() + "/"
				+ getLastFree() + "]";
	}

}
