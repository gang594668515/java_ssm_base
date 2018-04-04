<%@ taglib prefix="shiro" uri="/WEB-INF/tlds/shiros.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%@ taglib prefix="sys" tagdir="/WEB-INF/tags/sys" %>
<c:set var="server" value="${pageContext.request.contextPath}"/>
<c:set var="ctx" value="${pageContext.request.contextPath}${fns:getAdminPath()}"/>
<c:set var="ftx" value="${pageContext.request.contextPath}${fns:getFrontPath()}"/>
<c:set var="mtx" value="${pageContext.request.contextPath}${fns:getMobilePath()}"/>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>