
package com.xj.common.filter;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

import com.xj.common.utils.SpringContextHolder;

/**
 * 页面高速缓存过滤器
 * @version 2013-8-5
 */
public class PageCachingFilter extends SimplePageCachingFilter {

	private CacheManager cacheManager = SpringContextHolder.getBean(CacheManager.class);
	
	@Override
	protected CacheManager getCacheManager() {
		return cacheManager;
	}
	
}
