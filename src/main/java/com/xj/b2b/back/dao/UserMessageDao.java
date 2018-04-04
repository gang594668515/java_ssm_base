package com.xj.b2b.back.dao;

import com.xj.b2b.back.entity.UserMessage;
import com.xj.common.persistence.CrudDao;
import com.xj.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface UserMessageDao extends CrudDao<UserMessage> {

	/**
	 * 更新已读标识
	 */
	public int updateReadFlag(UserMessage userMessage);

	/**
	 * 获取用户未读消息数量
	 */
	public Integer getNewMessageCount(UserMessage userMessage);

}
