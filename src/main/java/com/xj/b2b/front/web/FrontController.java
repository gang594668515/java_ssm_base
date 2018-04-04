package com.xj.b2b.front.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xj.b2b.back.entity.UserMessage;
import com.xj.b2b.back.entity.User;
import com.xj.b2b.back.service.UserMessageService;
import com.xj.b2b.back.utils.UserUtils;
import com.xj.common.utils.StringUtils;
import com.xj.common.web.BaseController;

/**
 * 前台Controller
 * 
 * @author zhaojj
 * @version 2016-04-01
 */
@Controller
@RequestMapping(value = "${frontPath}")
public class FrontController extends BaseController {
	@Autowired
	private UserMessageService messageService;

	/**
	 * 获取新消息
	 */
	@RequestMapping(value = "newMsg")
	public @ResponseBody ObjectNode newMsg() {
		User currUser = UserUtils.getUser();
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode result = objectMapper.createObjectNode();
		if (StringUtils.isBlank(currUser.getId())) {
			result.put("newMsg", 0);
			return result;
		}

		UserMessage message = new UserMessage();
		message.setReceiveBy(currUser);
		Integer count = messageService.getNewMessageCount(message);
		result.put("newMsg", count);
		return result;
	}
}
