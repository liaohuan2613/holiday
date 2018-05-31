package com.lhk.cms.bean;

import java.io.Serializable;

public class Comment implements Serializable {

	private static final long serialVersionUID = 5348181856754598753L;

	private Long id;
	private Long user_id;
	private String fullname;
	private Long micro_blog_id;
	private String title;
	private String content;
	private String publish_date;

	public Comment() {
		super();
	}

	public Comment(Long user_id, String fullname, Long micro_blog_id, String title, String content,
			String publish_date) {
		super();
		this.user_id = user_id;
		this.fullname = fullname;
		this.micro_blog_id = micro_blog_id;
		this.title = title;
		this.content = content;
		this.publish_date = publish_date;
	}

	public Comment(Long id, Long user_id, String fullname, Long micro_blog_id, String title, String content,
			String publish_date) {
		super();
		this.id = id;
		this.user_id = user_id;
		this.fullname = fullname;
		this.micro_blog_id = micro_blog_id;
		this.title = title;
		this.content = content;
		this.publish_date = publish_date;
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

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Long getMicro_blog_id() {
		return micro_blog_id;
	}

	public void setMicro_blog_id(Long micro_blog_id) {
		this.micro_blog_id = micro_blog_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	@Override
	public String toString() {
		return "Comment [id=" + id + ", user_id=" + user_id + ", fullname=" + fullname + ", micro_blog_id="
				+ micro_blog_id + ", title=" + title + ", content=" + content + ", publish_date=" + publish_date + "]";
	}
	
}
