/*
获取浏览器版本，支持IE，Edge，Firefox，Chrome，Safari，Opera等浏览器

调用方式：
var browser;
var version;
var rowserVersion = getBrowserVersion();
if (rowserVersion.browser){
	browser = rowserVersion.browser;
	version = rowserVersion.version;
}
 */
function getBrowserVersion() {
	var uaStr = navigator.userAgent.toLowerCase();
	var match = null;

	match = uaStr.match(/(msie\s|trident.*rv:)([\w.]+)/);
	if (match != null) {
		return {
			browser : "ie",
			version : match[2] || "0"
		};
	}
	match = null;
	match = uaStr.match(/(edge)\/([\w.]+)/);
	if (match != null) {
		return {
			browser : match[1] || "",
			version : match[2] || "0"
		};
	}
	match = null;
	match = uaStr.match(/(firefox)\/([\w.]+)/);
	if (match != null) {
		return {
			browser : match[1] || "",
			version : match[2] || "0"
		};
	}
	match = null;
	match = uaStr.match(/(opera).+version\/([\w.]+)/);
	if (match != null) {
		return {
			browser : match[1] || "",
			version : match[2] || "0"
		};
	}
	match = null;
	match = uaStr.match(/(chrome)\/([\w.]+)/);
	if (match != null) {
		return {
			browser : match[1] || "",
			version : match[2] || "0"
		};
	}
	match = null;
	match = uaStr.match(/version\/([\w.]+).*(safari)/);
	if (match != null) {
		return {
			browser : match[2] || "",
			version : match[1] || "0"
		};
	}

	return {
		browser : "undefined",
		version : uaStr
	};
}

// 设置为主页
function setHome(obj, vrl) {
	try {
		obj.style.behavior = 'url(#default#homepage)';
		obj.setHomePage(vrl);
	} catch (e) {
		if (window.netscape) {
			try {
				netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
			} catch (e) {
				alert("此操作被浏览器拒绝！\n请在浏览器地址栏输入“about:config”并回车\n然后将 [signed.applets.codebase_principal_support]的值设置为'true',双击即可。");
			}
			var prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefBranch);
			prefs.setCharPref('browser.startup.homepage', vrl);
		} else {
			alert("您的浏览器不支持自动自动设置首页, 请使用浏览器菜单手动设置!");
		}
	}
}

// 加入收藏 兼容360和IE6
function addToFavorites(sTitle, sURL) {
	try {
		window.external.addFavorite(sURL, sTitle);
	} catch (e) {
		try {
			window.sidebar.addPanel(sTitle, sURL, "");
		} catch (e) {
			alert("加入收藏失败，请使用Ctrl+D进行添加");
		}
	}
}

/*
 * 检测浏览器兼容性
 */
function checkBrowser() {
	var rowserVersion = getBrowserVersion();
	if (rowserVersion.browser == "ie") {
		if (rowserVersion.version != "11.0") {
			if (getBrowserCookie("checkBrowser") == null) {
				alert("您使用的浏览器可能存在兼容性问题，会导致部分功能异常，建议使用  Chrome 或  Firefox 浏览器。");
				setBrowserCookie("checkBrowser", "1", 1000 * 60 * 5);
			}
		}
	}
}

// 写cookies
function setBrowserCookie(name, value, expireSeconds) {
	var str = name + "=" + escape(value);
	// 为0时不设定过期时间，浏览器关闭时cookie自动消失
	if (expireSeconds > 0) {
		var exp = new Date();
		exp.setTime(exp.getTime() + expireSeconds);
		str += ";expires=" + exp.toGMTString();
	}
	document.cookie = str;
}

// 读取cookies
function getBrowserCookie(name) {
	var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
	if (arr = document.cookie.match(reg)) {
		return unescape(arr[2]);
	} else {
		return null;
	}
}

// 删除cookies
function delBrowserCookie(name) {
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval = getBrowserCookie(name);
	if (cval != null) {
		document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
	}
}