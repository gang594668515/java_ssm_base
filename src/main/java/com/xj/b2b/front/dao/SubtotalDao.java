package com.xj.b2b.front.dao;

import java.util.HashMap;

import com.xj.b2b.front.entity.Subtotal;
import com.xj.common.persistence.CrudDao;
import com.xj.common.persistence.annotation.MyBatisDao;

/**
 * 分类汇总统计Dao
 * 
 * @author yangb
 * @version 2016-05-10
 */
@MyBatisDao
public interface SubtotalDao extends CrudDao<Subtotal> {
	
	/**
	 * 后台统计信息
	 */
	public void backStatistics(HashMap<String, String> map);

}
