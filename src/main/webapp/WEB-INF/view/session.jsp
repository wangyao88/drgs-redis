<%@ page language="java" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
    <title>Title</title>
</head>
<body>
<form action="<%=basePath%>setsession">
    <input type="text" name="name"><input type="submit" value="提交">
</form>
<form action="<%=basePath%>getsession">
    <input type="submit" value="获取">
</form>
</body>
</html>