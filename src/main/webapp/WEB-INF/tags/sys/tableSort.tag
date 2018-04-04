<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ attribute name="id" type="java.lang.String" required="true"%>
<%@ attribute name="name" type="java.lang.String" required="true"%>
<%@ attribute name="value" type="java.lang.String" required="true"%>
<%@ attribute name="callback" type="java.lang.String" required="true"%>
<%@ attribute name="version" type="java.lang.String" required="false" description="值为4时，表示使用新版fontawesome，其他或不填表示使用旧版fontawesome。解决fontawesome版本不同导致CSS样式不一致问题"%>
<input id="${id}" name="${name}" type="hidden" value="${value}"/>
<%-- 使用方法： 1.将本tag写在查询的from里；2.在需要排序th列class上添加：sort-column + 排序字段名；3.后台sql添加排序引用page.orderBy；实例文件：userList.jsp、UserDao.xml --%>
<script type="text/javascript">
	$(document).ready(function() {
		var iconArrow = "${version=='4'?'fa fa-long-arrow-':'icon icon-arrow-'}";
		var orderBy = $("#${id}").val().split(" ");
		$(".sort-column").each(function(){
			if ($(this).hasClass(orderBy[0])){
				orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
				$(this).html($(this).html()+" <i class=\""+iconArrow+orderBy[1]+"\"></i>");
			}
		});
		$(".sort-column").click(function(){
			var order = $(this).attr("class").split(" ");
			var sort = $("#${id}").val().split(" ");
			for(var i=0; i<order.length; i++){
				if (order[i] == "sort-column"){order = order[i+1]; break;}
			}
			if (order == sort[0]){
				sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
				$("#${id}").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
			}else{
				$("#${id}").val(order+" ASC");
			}
			${callback}
		});
	});
</script>