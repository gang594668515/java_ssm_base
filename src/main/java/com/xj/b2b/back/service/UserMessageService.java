package com.xj.b2b.back.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xj.b2b.back.dao.UserMessageDao;
import com.xj.b2b.back.entity.UserMessage;
import com.xj.b2b.back.utils.UserUtils;
import com.xj.common.persistence.Page;
import com.xj.common.service.CrudService;
import com.xj.common.utils.CacheUtils;

/**
 * 用户消息 Service
 */
@Service
public class UserMessageService extends CrudService<UserMessageDao, UserMessage> {

	@Transactional(readOnly = true)
	public UserMessage get(String id) {
		return dao.get(id);
	}

	/**
	 * 分页查询消息
	 */
	public Page<UserMessage> findPage(Page<UserMessage> page, UserMessage userMessage) {
		// 设置分页参数
		userMessage.setPage(page);
		// 执行分页查询
		page.setList(dao.findList(userMessage));
		return page;
	}

	@Transactional(readOnly = false)
	public void updateReadFlag(UserMessage userMessage) {
		userMessage.preUpdate();
		dao.updateReadFlag(userMessage);
		CacheUtils.remove(UserUtils.CACHE_USER_MESSAGE, UserUtils.CACHE_USER_ID_ + userMessage.getReceiveBy().getId());
	}

	@Transactional(readOnly = false)
	public void delete(UserMessage userMessage) {
		dao.delete(userMessage);
		CacheUtils.remove(UserUtils.CACHE_USER_MESSAGE, UserUtils.CACHE_USER_ID_ + userMessage.getReceiveBy().getId());
	}

	public Integer getNewMessageCount(UserMessage userMessage) {
		return UserUtils.getNewUserMessageCount(userMessage);
	}

}
