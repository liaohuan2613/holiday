package com.lhk.cms.bean;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 9079240629963834620L;
	
	private Long id;
	private String fullname;
	private String username;
	private String pwd;
	private String sex;
	private String phone;

	public User() {
		super();
	}

	public User(String fullname, String username, String pwd, String sex, String phone) {
		super();
		this.fullname = fullname;
		this.username = username;
		this.pwd = pwd;
		this.sex = sex;
		this.phone = phone;
	}
	
	public User(Long id, String fullname, String username, String pwd, String sex, String phone) {
		super();
		this.id = id;
		this.fullname = fullname;
		this.username = username;
		this.pwd = pwd;
		this.sex = sex;
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", fullname=" + fullname + ", username=" + username + ", pwd=" + pwd
				+ ", sex=" + sex + ", phone=" + phone + "]";
	}

}
