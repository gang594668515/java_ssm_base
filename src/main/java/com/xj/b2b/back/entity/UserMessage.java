package com.xj.b2b.back.entity;

import java.util.Date;

import com.xj.common.persistence.DataEntity;

/**
 * 用户消息Entity
 */
public class UserMessage extends DataEntity<UserMessage> {

	private static final long serialVersionUID = 1L;

	public static final String READ_FLAG_YES = "1";
	public static final String READ_FLAG_NO = "0";

	private String title;
	private String content;
	private String href;
	private String type;
	private User sendBy;
	private Date sendDate;
	private User receiveBy;
	private Date receiveDate;
	private String readFlag;

	public UserMessage() {
		super();
	}

	public UserMessage(String id) {
		super(id);
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

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User getSendBy() {
		return sendBy;
	}

	public void setSendBy(User sendBy) {
		this.sendBy = sendBy;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public User getReceiveBy() {
		return receiveBy;
	}

	public void setReceiveBy(User receiveBy) {
		this.receiveBy = receiveBy;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}

}
