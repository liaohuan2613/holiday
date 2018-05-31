package com.lhk.cms.bean;

import java.io.Serializable;

public class MicroBlog implements Serializable {

	private static final long serialVersionUID = 5949168021959707107L;

	private Long id;
	private Long user_id;
	private String username;
	private String fullname;
	private String title;
	private String summary;
	private String content;
	private String publish_date;
	private Long parent_id;
	private Long parent_user_id;
	private String parent_fullname;

	public MicroBlog() {
		super();
	}

	public MicroBlog(Long user_id, String username, String fullname, String title, String summary, String content,
			String publish_date, Long parent_id, Long parent_user_id, String parent_fullname) {
		super();
		this.user_id = user_id;
		this.username = username;
		this.fullname = fullname;
		this.title = title;
		this.summary = summary;
		this.content = content;
		this.publish_date = publish_date;
		this.parent_id = parent_id;
		this.parent_user_id = parent_user_id;
		this.parent_fullname = parent_fullname;
	}

	public MicroBlog(Long id, Long user_id, String username, String fullname, String title, String summary,
			String content, String publish_date, Long parent_id, Long parent_user_id, String parent_fullname) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.username = username;
		this.fullname = fullname;
		this.title = title;
		this.summary = summary;
		this.content = content;
		this.publish_date = publish_date;
		this.parent_id = parent_id;
		this.parent_user_id = parent_user_id;
		this.parent_fullname = parent_fullname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPublish_date() {
		return publish_date;
	}

	public void setPublish_date(String publish_date) {
		this.publish_date = publish_date;
	}

	public Long getParent_id() {
		return parent_id;
	}

	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}

	public Long getParent_user_id() {
		return parent_user_id;
	}

	public void setParent_user_id(Long parent_user_id) {
		this.parent_user_id = parent_user_id;
	}

	public String getParent_fullname() {
		return parent_fullname;
	}

	public void setParent_fullname(String parent_fullname) {
		this.parent_fullname = parent_fullname;
	}

	@Override
	public String toString() {
		return "MicroBlog [id=" + id + ", user_id=" + user_id + ", username=" + username + ", fullname=" + fullname
				+ ", title=" + title + ", summary=" + summary + ", content=" + content + ", publish_date="
				+ publish_date + ", parent_id=" + parent_id + ", parent_user_id=" + parent_user_id
				+ ", parent_fullname=" + parent_fullname + "]";
	}

}
