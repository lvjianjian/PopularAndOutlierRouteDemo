/**
 * Created by work1 on 2015/8/27.
 */
var index = function () {

    $(".networkInformation").click(function(){
        $("#beforeUserInfo").load("/network/networkInformation.html",function(){
        });
    });



    $(".inspectionOfUser").click(function(){
        $("#beforeUserInfo").load("/inspection/inspection.html",function(){
        });
    });


    $(".problemInformation").click(function(){
        $("#beforeUserInfo").load("/problem/problemOfUser.html",function(){
        });
    });

    $(".systemInformationOfUser").click(function(){
        $("#beforeUserInfo").load("/device/userSystemInformation.html",function(){
        });
    });


    $(".inspectionMonthReportInformation").click(function(){
        $("#beforeUserInfo").load("/inspection/inspectionMonthReportInformation.html",function(){
        });
    });



    $(".dataDownload").click(function(){
        $("#beforeUserInfo").load("/data/download.html",function(){
        });
    });


    $(".FAQ").click(function(){
        $("#beforeUserInfo").load("/experience/FAQ.html",function(){
        });
    });

    $(".fieldManager").click(function(){
        $("#beforeUserInfo").load("/user/fieldManager.html",function(){
        });
    });

    $(".dailyRecord").click(function(){
        $("#beforeUserInfo").load("/record/dailyRecord.html",function(){
        });
    });

    $(".individualInformation").click(function(){
        $("#beforeUserInfo").load("/user/individualInformation.html",function(){
        });
    });

    $(".roleManage").click(function(){
        $('#beforeUserInfo').load("/role/roleManage.html",function(){
        });
    });



    $("#myCarousel").carousel({
        interval:3000
    });

    var hitchTypeOption = {
        title: {
            x: 'center',
            text: '系统故障状况',
            subtext: ''
        },
        tooltip: {
            trigger: 'item'
        },
        toolbox: {
            show: true,
            feature: {
                restore: {show: true},
                saveAsImage: {show: true}
            }
        },
        calculable: true,
        xAxis: [
            {
                type: 'category',
                show: true,
                data: []
            }
        ],
        yAxis: [
            {
                type: 'value',
                show: true
            }
        ],
        series: [
            {
                name: '系统故障状况',
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: function(params) {
                            // build a color map as your need.
                            var colorList = [
                                '#C1232B','#B5C334','#FCCE10','#E87C25','#27727B',
                                '#FE8463','#9BCA63','#FAD860','#F3A43B','#60C0DD',
                                '#D7504B','#C6E579','#F4E001','#F0805A','#26C0C0'
                            ];
                            var random=Math.floor(100);

                            return colorList[(params.dataIndex*17+random)%colorList.length]
                        },
                        label: {
                            show: true,
                            position: 'top',
                            formatter: '{b}\n{c}'
                        }
                    }
                },
                data: []

            }
        ]
    };

    var hardwareHitchTypeOption = {
        title: {
            x: 'center',
            text: '硬件故障状况',
            subtext: ''
        },
        tooltip: {
            trigger: 'item'
        },
        toolbox: {
            show: true,
            feature: {
                restore: {show: true},
                saveAsImage: {show: true}
            }
        },
        calculable: true,
        xAxis: [
            {
                type: 'category',
                show: true,
                data: []
            }
        ],
        yAxis: [
            {
                type: 'value',
                show: true
            }
        ],
        series: [
            {
                name: '硬件故障状况',
                type: 'bar',
                itemStyle: {
                    normal: {
                        color: function(params) {
                            // build a color map as your need.
                            var colorList = [
                                '#C1232B','#B5C334','#FCCE10','#E87C25','#27727B',
                                '#FE8463','#9BCA63','#FAD860','#F3A43B','#60C0DD',
                                '#D7504B','#C6E579','#F4E001','#F0805A','#26C0C0'
                            ];
                            var random=Math.floor(100);

                            return colorList[(params.dataIndex*17+random)%colorList.length]
                        },
                        label: {
                            show: true,
                            position: 'top',
                            formatter: '{b}\n{c}'
                        }
                    }
                },
                data: []

            }
        ]
    };

    var hitchTypeChart = echarts.init(document.getElementById('hitchTypeChart'));
    var hardwareHitchTypeChart = echarts.init(document.getElementById('hardwareHitchTypeChart'));
    $("a.monthReport").die().live("click",function(e){
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: "/rest/inspection/getMonthReportById.json",
            async: false,
            cache: false,
            data: {
                "reportId":$(this).attr("reportId")
            },
            dataType: "json",
            success: function (data) {
                $("#viewAreaSelect").val(data.system.area.name);
                $("#viewSystemSelect").val(data.system.systemName);
                $("#viewDatetimepicker").val(data.time);
                $("#viewDeviceCount").val(data.deviceCount);
                $("#viewInWarrantyCount").val(data.inWarrantyCount);
                $("#viewOutWarrantyCount").val(data.outWarrantyCount);
                $("#viewRemoteCount").val(data.remoteCount);
                $("#viewRemoteTime").val(data.remoteTime);
                $("#viewLocalCount").val(data.localCount);
                $("#viewLocalTime").val(data.localTime);
                $("#viewConclusion").val(data.conclusion);
                var count=0;
                var html="<div class='row'>";
                hitchTypeOption.xAxis[0].data=[];
                hitchTypeOption.series[0].data=[];
                for(var key in data.hitchTypeReportMap){
                    hitchTypeOption.xAxis[0].data.push(data.hitchTypeReportMap[key].name);
                    hitchTypeOption.series[0].data.push(data.hitchTypeReportMap[key].count);

                    html+='<div class="col-md-6"><div class="form-group"><label class="control-label col-md-3">'+data.hitchTypeReportMap[key].name+':</label>';
                    html+='<div class="col-md-8"><input type="text" class="form-control" value="'+data.hitchTypeReportMap[key].count+'">';
                    html+='<span class="help-block"></span></div><label class="control-label col-md-1">(次)</label></div></div>';
                    count++;
                    if(count%3==2){
                        html+="</div><div class='row'>";
                    }
                }
                $("#viewHitchTypeReportInfo").empty();
                $("#viewHitchTypeReportInfo").html(html);


                count=0;
                html="<div class='row'>";
                hardwareHitchTypeOption.xAxis[0].data=[];
                hardwareHitchTypeOption.series[0].data=[];

                for(var key in data.hardwareHitchTypeReportMap){
                    hardwareHitchTypeOption.xAxis[0].data.push(data.hardwareHitchTypeReportMap[key].name);
                    hardwareHitchTypeOption.series[0].data.push(data.hardwareHitchTypeReportMap[key].count);
                    html+='<div class="col-md-6"><div class="form-group"><label class="control-label col-md-3">'+data.hardwareHitchTypeReportMap[key].name+':</label>';
                    html+='<div class="col-md-8"><input type="text" class="form-control" value="'+data.hardwareHitchTypeReportMap[key].count+'">';
                    html+='<span class="help-block"></span></div><label class="control-label col-md-1">(次)</label></div></div>';
                    count++;
                    if(count%3==2){
                        html+="</div><div class='row'>";
                    }
                }

                $("#viewHardwareHitchTypeReportInfo").empty();
                $("#viewHardwareHitchTypeReportInfo").html(html);
            }});
        hitchTypeChart.setOption(hitchTypeOption);
        hardwareHitchTypeChart.setOption(hardwareHitchTypeOption);
        $("#managePortlet").hide();
        $("#viewPortlet").show();
    });

    $("#viewMonthReportCancelBtn").click(function(e){
        e.preventDefault();
        $("#managePortlet").show();
        $("#viewPortlet").hide();
    });

    $('a.fileOfAreaDownload').die().live('click', function (e) {
        e.preventDefault();
        if (confirm("您确定要下载该文件?") == false) {
            return;
        }
        window.location.href=$(this).attr("urls");
    });

    $("a.noticeList").die().live("click",function(e){
        var title = $(this).attr("title");
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: "/rest/notice/getNoticeDetail.json",
            async: false,
            cache: false,
            data:{
                noticeId:$(this).attr("noticeId")
            },
            dataType: "json",
            success:function(data){
                if(data){
                    $("#noticeContext").html("<h3 style='text-align: center'>"+ data.name +"</h3>"+'<p style="text-align:center;font-size: xx-small">'+data.time+'</p>' +data.brief);
                }else{
                    alert("添加失败,请检查数据是否正确!");
                }
            }
        });
        $("#managePortlet").hide();
        $("#noticePortlet").show();
    });

    $("#noticeCancelBtn").click(function(e){
        e.preventDefault();
        $("#managePortlet").show();
        $("#noticePortlet").hide();
    });
    return {
        //main function
        init: function () {
            $("#viewPortlet").hide();
            $("#noticePortlet").hide();
        }
    };
}();