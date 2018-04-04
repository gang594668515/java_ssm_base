
package com.xj.b2b.back.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xj.b2b.front.utils.SubtotalUtils;
import com.xj.common.web.BaseController;

/**
 * 后台Controller
 * 
 * @author yangb
 * @version 2016年6月3日
 */
@Controller
@RequestMapping(value = "${adminPath}")
public class BackController extends BaseController {

	@RequiresPermissions("sys:back:login")
	@RequestMapping(value = "sys/statistics/info")
	public String statisticsInfo(HttpServletRequest request, HttpServletResponse response, Model model) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type0", "all");
		SubtotalUtils.backStatistics(map);
		model.addAttribute("val1", map.get("val1"));
		model.addAttribute("val2", map.get("val2"));
		model.addAttribute("val3", map.get("val3"));
		model.addAttribute("val4", map.get("val4"));
		model.addAttribute("val5", map.get("val5"));
		model.addAttribute("val6", map.get("val6"));
		model.addAttribute("val7", map.get("val7"));
		model.addAttribute("val8", map.get("val8"));
		model.addAttribute("val9", map.get("val9"));

		return "b2b/back/statistics";
	}

}
