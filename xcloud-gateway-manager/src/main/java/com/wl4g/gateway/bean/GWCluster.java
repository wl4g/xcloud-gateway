package com.wl4g.gateway.bean;

import com.wl4g.components.core.bean.BaseBean;

public class GWCluster extends BaseBean {
	private static final long serialVersionUID = -3298424126317938674L;

	private String name;

	private String namespace;

	private String address;

	private String addressType;

	private Integer idcId;

	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace == null ? null : namespace.trim();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType == null ? null : addressType.trim();
	}

	public Integer getIdcId() {
		return idcId;
	}

	public void setIdcId(Integer idcId) {
		this.idcId = idcId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}