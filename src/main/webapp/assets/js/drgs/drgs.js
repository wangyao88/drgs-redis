$(document).ready(function(){
   var intermediateResultColumns=[
    	 {
              field: "drgCode",
              title: "DRG编码"
          },{
              field: "icd10",
              title: "主诊断"
          },{
              field: "originalIcd10",
              title: "原主诊断"
          },{
              field: "primaryDiagnosisCode",
              title: "附属诊断"
          },{
              field: "icd9",
              title: "主手术"
          },{
              field: "unMainOprStr",
              title: "非主手术"
          },{
              field: "mainOprCosts",
              title: "手术费用"
          },{
              field: "mainOprDate",
              title: "手术日期"
          },{
              field: "diseaseMdc",
              title: "主诊断对应MDC"
          },{
              field: "mainOprAdrgs",
              title: "mainOprAdrgs"
          },{
              field: "diseaseAdrgs",
              title: "diseaseAdrgs"
          },{
              field: "defaultAdrgs",
              title: "defaultAdrgs"
          }, {
              field: "hisId",
              title: "hisId"
          },{
              field: "groupNo",
              title: "groupNo"
          }
      ];
       
       $("#intermediateResultTable").bootstrapTable({
           dataType: "json",
           pagination: true,
           sidePagination: 'server',
           pageSize: 20,//每页数据  
           pageList: [20, 50, 100, 500],//可选的每页数据  
           columns: intermediateResultColumns,
       }
    );
                             
	var drgsGroupColumns = [
	  {
          field: "drgCnCode",
          title: "DRG编码"
      }, {
          field: "matchCount",
          title: "匹配数量"
      }, {
          field: "drgType",
          title: "DRG类别"
      }, {
          field: "oprgroupCode",
          title: "结果ADRG"
      }, {
          field: "otherFactorId1",
          title: "其他因素1"
      }, {
          field: "otherFactorId2",
          title: "其他因素2"
      }, {
          field: "otherFactorId3",
          title: "其他因素3"
      }, {
          field: "ccFlag",
          title: "合并症"
      }, {
          field: "drgsSex",
          title: "性别"
      }, {
          field: "drgsAge",
          title: "年龄"
      }, {
          field: "drgsWeight",
          title: "体重"
      }, {
          field: "dfxzycsFlag",
          title: "多发性创伤"
      }, {
          field: "drgCnName",
          title: "DRG名称"
      }, {
          field: "defaultAdrg",
          title: "defaultAdrg"
      }, {
          field: "drgsOutcome",
          title: "出院方式"
      }, {
          field: "drgsTrtime",
          title: "治疗时长"
      }
  ];

   $("#drgsGroupTable").bootstrapTable({
          dataType: "json",
          pagination: true,
          sidePagination: 'server',
          pageSize: 20,//每页数据  
          pageList: [20, 50, 100, 500],//可选的每页数据  
          columns: drgsGroupColumns,
          onLoadSuccess : function(){
        	  $("#drgsGroupTable").find("tr:eq(1)").css("color","red");
        	  var intermediateResultUrl = "drgsGroup/drgsGroupByHisIdForIntermediateResult/"+hisId;
        	  $("#intermediateResultTable").bootstrapTable('refresh',{'url':intermediateResultUrl}); 
          }
      }
  );
  
});

function drgsGroup(){
	  var hisId = $('#hisId').val();
	  var drgsGroupUrl = "drgsGroup/drgsGroupByHisIdForReslutList/"+hisId;
	  $("#drgsGroupTable").bootstrapTable('refresh',{'url':drgsGroupUrl}); 
	  $('#form1').resetForm(); 
}
