
package com.xj.b2b.back.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xj.b2b.back.dao.DictDao;
import com.xj.b2b.back.entity.Dict;
import com.xj.common.mapper.JsonMapper;
import com.xj.common.utils.CacheUtils;
import com.xj.common.utils.SpringContextHolder;

/**
 * 字典工具类
 * 
 * @version 2013-5-29
 */
public class DictUtils {

	private static DictDao dictDao = SpringContextHolder.getBean(DictDao.class);

	public static final String CACHE_DICT_MAP = "dictMap";

	/**
	 * 获取某类型的数据字典列表（已使用缓存，所有其他工具类方法的基础方法）
	 * 
	 * @param type
	 *            类型
	 * @return
	 */
	public static List<Dict> getDictList(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>) CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			for (Dict dict : dictDao.findAllList(new Dict())) {
				List<Dict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		List<Dict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	/**
	 * 获取数据字典中某类型中指定标签的值
	 * 
	 * @param label
	 *            标签名
	 * @param type
	 *            类型
	 * @param defaultLabel
	 *            默认值，未获取到则返回默认值
	 */
	public static String getDictValue(String label, String type, String defaultLabel) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && label.equals(dict.getLabel())) {
					return dict.getValue();
				}
			}
		}
		return defaultLabel;
	}

	/**
	 * 获取数据字典中某类型中指定值对应的标签(如有多个，返回第一个)
	 * 
	 * @param value
	 *            值
	 * @param type
	 *            类型
	 * @param defaultValue
	 *            默认值，未获取到则返回默认值
	 * @return
	 */
	public static String getDictLabel(String value, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
			for (Dict dict : getDictList(type)) {
				if (type.equals(dict.getType()) && value.equals(dict.getValue())) {
					return dict.getLabel();
				}
			}
		}
		return defaultValue;
	}

	/**
	 * 获取数据字典中某类型中一系列值对应的英文逗号分隔的标签字符串
	 * 
	 * @param values
	 *            英文逗号分隔的值的字符串
	 * @param type
	 *            类型
	 * @param defaultValue
	 *            默认值，未获取到则返回默认值
	 * @return
	 */
	public static String getDictLabels(String values, String type, String defaultValue) {
		if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)) {
			List<String> valueList = Lists.newArrayList();
			for (String value : StringUtils.split(values, ",")) {
				valueList.add(getDictLabel(value, type, defaultValue));
			}
			return StringUtils.join(valueList, ",");
		}
		return defaultValue;
	}

	/**
	 * 获取某类型的数据字典列表（JSON）
	 * 
	 * @param type
	 *            类型
	 */
	public static String getDictListJson(String type) {
		return JsonMapper.toJsonString(getDictList(type));
	}

}
