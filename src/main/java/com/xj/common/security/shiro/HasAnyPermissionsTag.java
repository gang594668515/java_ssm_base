
package com.xj.common.security.shiro;

import org.apache.shiro.subject.Subject;

/**
 * Shiro HasAnyPermissions Tag.
 * 
 * @author calvin
 */
public class HasAnyPermissionsTag extends org.apache.shiro.web.tags.PermissionTag {

	private static final long serialVersionUID = 1L;
	private static final String PERMISSION_NAMES_DELIMETER = ",";

	@Override
	protected boolean showTagBody(String paramString) {
		boolean hasAnyPermission = false;
		Subject subject = getSubject();
		if (subject != null) {
			// Iterate through permissions and check to see if the user has one
			// of the permissions
			for (String permission : paramString.split(PERMISSION_NAMES_DELIMETER)) {

				if (subject.isPermitted(permission.trim())) {
					hasAnyPermission = true;
					break;
				}

			}
		}
		return hasAnyPermission;
	}

}
