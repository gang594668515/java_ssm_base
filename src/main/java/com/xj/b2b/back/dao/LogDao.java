
package com.xj.b2b.back.dao;

import com.xj.b2b.back.entity.Log;
import com.xj.common.persistence.CrudDao;
import com.xj.common.persistence.annotation.MyBatisDao;

/**
 * 日志DAO接口
 * @version 2014-05-16
 */
@MyBatisDao
public interface LogDao extends CrudDao<Log> {

}
