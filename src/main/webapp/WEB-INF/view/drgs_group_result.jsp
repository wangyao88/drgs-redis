<%@ page language="java" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
    <title>分组中间结果</title>
    <meta name="description" content="">
    <meta name="keywords" content="">
    <link rel="icon" href="data:image/ico;base64,aWNv">
    <link rel="stylesheet" href="<%=basePath%>assets/css/bootstrap.css">
 	<link rel="stylesheet" href="<%=basePath%>assets/css/bootstrap-theme.css">
 	<link rel="stylesheet" href="<%=basePath%>assets/css/bootstrap-table.css">
 	<script type="text/javascript" src="<%=basePath%>assets/js/jquery/jquery-1.10.2.js"></script>
	<script type="text/javascript" src="<%=basePath%>assets/js/bootstrap/bootstrap.js"></script>
	<script type="text/javascript" src="<%=basePath%>assets/js/bootstrap/bootstrap-table.js"></script>
	<script type="text/javascript" src="<%=basePath%>assets/js/jquery/jquery-form.js"></script>
	<script type="text/javascript" src="<%=basePath%>assets/js/drgs/drgs.js"></script>
<body>
   <form id="form1" role="form" name="form1"> 
        <div class="form-group"> 
            <label for="IDCard">请输入hisId</label> 
            <div class="input-group"> 
                <input type="text" class="form-control" id="hisId" name="hisId" placeholder="hisId" > 
                <span class="input-group-btn"> 
                    <button class="btn btn-default" type="button" onClick="drgsGroup()" >分组</button> 
                </span> 
            </div> 
        </div> 
    </form>  
	
	<table id="intermediateResultTable"></table>
	<table id="drgsGroupTable"></table>
 
 <script type="text/javascript">
   

</script>
</body>
</html>
