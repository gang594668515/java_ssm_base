
package com.xj.b2b.back.dao;

import java.util.List;

import com.xj.b2b.back.entity.Dict;
import com.xj.common.persistence.CrudDao;
import com.xj.common.persistence.annotation.MyBatisDao;

/**
 * 字典DAO接口
 * 
 * @author yangb
 * @version 2016年4月11日
 */
@MyBatisDao
public interface DictDao extends CrudDao<Dict> {
	/**
	 * 获取字典类型列表
	 */
	public List<String> findTypeList(Dict dict);

}
