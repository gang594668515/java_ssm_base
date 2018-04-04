
package com.xj.b2b.back.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xj.common.web.BaseController;

/**
 * 标签Controller
 * @author yangb
 * @version 2016年4月3日
 */
@Controller
@RequestMapping(value = "${adminPath}/tag")
public class TagController extends BaseController {
	
	/**
	 * 树结构选择标签（treeselect.tag）
	 */
	@RequiresPermissions("sys:back:login")
	@RequestMapping(value = "treeselect")
	public String treeselect(HttpServletRequest request, Model model) {
		model.addAttribute("url", request.getParameter("url")); 	// 树结构数据URL
		model.addAttribute("extId", request.getParameter("extId")); // 排除的编号ID
		model.addAttribute("checked", request.getParameter("checked")); // 是否可复选
		model.addAttribute("selectIds", request.getParameter("selectIds")); // 指定默认选中的ID
		model.addAttribute("isAll", request.getParameter("isAll")); 	// 是否读取全部数据，不进行权限过滤
		model.addAttribute("module", request.getParameter("module"));	// 过滤栏目模型（仅针对CMS的Category树）
		return "b2b/back/tagTreeselect";
	}
	
	/**
	 * 图标选择标签（iconselect.tag）
	 */
	@RequiresPermissions("sys:back:login")
	@RequestMapping(value = "iconselect")
	public String iconselect(HttpServletRequest request, Model model) {
		model.addAttribute("value", request.getParameter("value"));
		return "b2b/back/tagIconselect";
	}
	
}
