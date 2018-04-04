package com.xj.b2b.front.entity;

import com.xj.common.persistence.DataEntity;

/**
 * 分类汇总统计Entity
 * 
 * @author yangb
 * @version 2016-05-10
 */
public class Subtotal extends DataEntity<Subtotal> {
	private static final long serialVersionUID = 1L;

	private String key;
	private long value;

	public Subtotal() {
		super();
	}

	public Subtotal(String id) {
		super(id);
	}

	@Override
	public String toString() {
		return key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

}
