package com.xj.b2b.front.utils;

import java.util.HashMap;

import com.xj.b2b.front.dao.SubtotalDao;
import com.xj.common.utils.SpringContextHolder;

/**
 * 分类统计工具类
 * 
 * @author yangb
 * @version 2016-05-10
 */
public class SubtotalUtils {

	private static SubtotalDao subtotalDao = SpringContextHolder.getBean(SubtotalDao.class);

	/**
	 * 后台统计信息 <br>
	 * Map map = new HashMap<String,String>();<br>
	 * map.put("type0","");<br>
	 * SubtotalUtils.backStatistics(map);<br>
	 * map.get("val1");
	 */
	public static void backStatistics(HashMap<String, String> map) {
		subtotalDao.backStatistics(map);
	}

}
