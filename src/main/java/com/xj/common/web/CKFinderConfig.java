
package com.xj.common.web;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import com.ckfinder.connector.configuration.Configuration;
import com.ckfinder.connector.data.AccessControlLevel;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.xj.b2b.back.entity.User;
import com.xj.b2b.back.security.SystemAuthorizingRealm.Principal;
import com.xj.b2b.back.utils.UserUtils;
import com.xj.common.config.Global;
import com.xj.common.utils.FileUtils;

/**
 * CKFinder配置
 * 
 * @version 2014-06-25
 */
public class CKFinderConfig extends Configuration {

	public CKFinderConfig(ServletConfig servletConfig) {
		super(servletConfig);
	}

	@Override
	protected Configuration createConfigurationInstance() {
		Principal principal = (Principal) UserUtils.getPrincipal();
		if (principal == null) {
			return new CKFinderConfig(this.servletConf);
		}
		boolean isView = true;// UserUtils.getSubject().isPermitted("cms:ckfinder:view");
		boolean isUpload = true;// UserUtils.getSubject().isPermitted("cms:ckfinder:upload");
		boolean isEdit = true;// UserUtils.getSubject().isPermitted("cms:ckfinder:edit");
		AccessControlLevel alc = this.getAccessConrolLevels().get(0);
		alc.setFolderView(isView);
		alc.setFolderCreate(isEdit);
		alc.setFolderRename(isEdit);
		alc.setFolderDelete(isEdit);
		alc.setFileView(isView);
		alc.setFileUpload(isUpload);
		alc.setFileRename(isEdit);
		alc.setFileDelete(isEdit);
		// for (AccessControlLevel a : this.getAccessConrolLevels()){
		// System.out.println(a.getRole()+", "+a.getResourceType()+",
		// "+a.getFolder()
		// +", "+a.isFolderView()+", "+a.isFolderCreate()+",
		// "+a.isFolderRename()+", "+a.isFolderDelete()
		// +", "+a.isFileView()+", "+a.isFileUpload()+", "+a.isFileRename()+",
		// "+a.isFileDelete());
		// }
		AccessControlUtil.getInstance(this).loadACLConfig();
		try {
			// Principal principal =
			// (Principal)SecurityUtils.getSubject().getPrincipal();
			// this.baseURL =
			// ServletContextFactory.getServletContext().getContextPath()+"/userfiles/"+principal+"/";
			User currUser = UserUtils.getUser();
			if (currUser.isAdmin()) {
				this.baseURL = FileUtils.path(Servlets.getRequest().getContextPath() + Global.USERFILES_BASE_URL);
				this.baseDir = FileUtils.path(Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL);
			}
			else {
				this.baseURL = FileUtils.path(Servlets.getRequest().getContextPath() + Global.USERFILES_BASE_URL
						+ Global.getUserfilesUrl(principal.getId()));
				this.baseDir = FileUtils.path(Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL
						+ Global.getUserfilesUrl(principal.getId()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new CKFinderConfig(this.servletConf);
	}

	@Override
	public boolean checkAuthentication(final HttpServletRequest request) {
		return UserUtils.getPrincipal() != null;
	}

}
