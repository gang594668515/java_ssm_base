
package com.xj.b2b.back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xj.b2b.back.dao.DictDao;
import com.xj.b2b.back.entity.Dict;
import com.xj.b2b.back.utils.DictUtils;
import com.xj.common.service.CrudService;
import com.xj.common.utils.CacheUtils;
import com.xj.common.utils.StringUtils;

/**
 * 字典Service
 * 
 * @author yangb
 * @version 2016年4月2日
 */
@Service
@Transactional(readOnly = true)
public class DictService extends CrudService<DictDao, Dict> {

	@Autowired
	private DictDao dictDao;

	/**
	 * 查询字典类型列表
	 * 
	 * @return
	 */
	public List<String> findTypeList() {
		return dao.findTypeList(new Dict());
	}

	@Transactional(readOnly = false)
	public void save(Dict dict) {
		// 保存或更新实体
		if (StringUtils.isBlank(dict.getId())) {
			dict.setIsNewRecord(true); // 使用自定义ID
			dict.preInsert();
			dictDao.insert(dict);
		} else {
			dict.preUpdate();
			dictDao.update(dict);
		}
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

	@Transactional(readOnly = false)
	public void delete(Dict dict) {
		super.delete(dict);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

}
