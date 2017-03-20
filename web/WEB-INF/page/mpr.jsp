<%--
  Created by IntelliJ IDEA.
  User: JQ-Cao
  Date: 2016/3/3
  Time: 12:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no, width=device-width">
    <title>频繁路径查询</title>
    <link rel="stylesheet" href="http://cache.amap.com/lbs/static/main1119.css"/>
    <style>
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
        }

        .info-tip {
            position: absolute;
            top: 10px;
            right: 10px;
            font-size: 12px;
            background-color: #fff;
            height: 35px;
            text-align: left;
        }

        .region {
            position: fixed;
            right: 10px;
            bottom: 10px;
        }

        /*#container{*/
        /*/!*height:330px;*!/*/
        /*height:100%;*/
        /*width:50%;*/
        /*}*/
    </style>
    <script src="http://cache.amap.com/lbs/static/es5.min.js"></script>
    <script src="http://webapi.amap.com/maps?v=1.3&key=0d0be35a518ab659f366a5acac815573&plugin=AMap.Driving"></script>
    <script type="text/javascript" src="http://cache.amap.com/lbs/static/addToolbar.js"></script>
</head>
<body>
<%@ include file="./shared/importJs.jsp" %>
<%@ include file="./shared/importCss.jsp" %>
<div class="row">
    <div id="container"></div>
    <div id="panel" ></div>
    <div class="col-md-5 pull-right">
        <%--<div class="btn-group col-md-12">--%>
        <%--<div style="margin-left: auto;margin-right:auto ">--%>
        <%--<input id="sampleQuery1" class="btn btn-success col-md-4" style="border: 0px" value="sample_1"/>--%>
        <%--<input id="sampleQuery2" class="btn btn-success col-md-4" style="border: 0px" value="sample_2"/>--%>
        <%--<input id="sampleQuery3" class="btn btn-success col-md-4" style="border: 0px" value="sample_3"/>--%>
        <%--</div>--%>
        <%--</div>--%>
        <div class="row col-md-12">
            <div class="col-md-6">
                <input id="startId" class="form-control" type="text" placeholder="输入起点编号"/>
            </div>
            <div class="col-md-6">
                <input id="endId" class="form-control" type="text" placeholder="输入终点编号"/>
            </div>
            <div class="col-sm-3 col-lg-3">
                <div class="dash-unit">
                    <div class="switch" id="topK">
                        <input type="radio" class="switch" name="topKSwitch" value="Off" id="off" checked="">
                        <label for="on" class="switch-label switch-label-off">Off</label>
                        <input type="radio" class="switch-input" name="topKSwitch" value="On" id="On">
                        <label for="off" class="switch-label switch-label-on">On</label>
                        <span class="switch-selection"></span>
                    </div>
                    <div class="switch" id="compare">
                        <input type="radio" class="switch" name="compareOff" value="Off" id="compareOff" checked="">
                        <label for="on" class="switch-label switch-label-off">Off</label>
                        <input type="radio" class="switch-input" name="compareOff" value="On" id="compareOn">
                        <label for="off" class="switch-label switch-label-on">On</label>
                        <span class="switch-selection"></span>
                    </div>
                </div>
            </div>
        </div>
        <div class="row col-md-12">
            <div class="col-md-4">

            </div>
            <input id="findMPP" class="btn btn-success col-md-4" style="margin-left: auto;margin-right:auto "
                   type="button" value="检测"/>

            <div class="col-md-4">
                <input id="save" class="btn btn-info col-md-6" style="margin-left: auto;margin-right:auto "
                       type="button" value="save"/>
            </div>
        </div>

        <%--<div>--%>
        <div id="result-group">
            <table class="table table-striped table-hover table-bordered NoticeTable col-md-6"
                   style="font-size: smaller" id="pushTable">
                <thead>
                <tr>
                    <th width="50px">起点</th>
                    <th width="50px">终点</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <%--</div>--%>
        <%--<input id="setFitView" class="button" type="button" value="自适应"/>--%>
        <div class="vertex" >
            <div class="row">
                <div class="col-md-5">
                    <input id="vertex_id" class="form-control" type="text"  placeholder="vertex id" />
                </div>
                <div class="col-md-2">
                    <input id="locationVertex" class="btn btn-success col-md-12"
                                    style="margin-left: auto;margin-right:auto;text-align: center"
                                    type="button" value="Vertex"/>
                </div>
            </div>
        </div>
            <div class="edge" >
                <div class="row">
                    <div class="col-md-5">
                        <input id="edge_id" class="form-control" type="text"  placeholder="edge id" />
                    </div>
                    <div class="col-md-2">
                        <input id="locationEdge" class="btn btn-success col-md-12"
                               style="margin-left: auto;margin-right:auto;text-align: center"
                               type="button" value="Edge"/>
                    </div>
                </div>
            </div>
        <div class="region">
            <div class="row">
                <div class="col-md-5">
                    <input id="start_region" class="form-control" type="text" readonly="true"/>
                </div>
                <div class="col-md-5">
                    <input id="end_region" class="form-control" type="text" readonly="true"/>
                </div>
                <div class="col-md-2">
                    <input id="getRegionInf" class="btn btn-success col-md-12"
                           style="margin-left: auto;margin-right:auto;text-align: center"
                           type="button" value="click"/>
                </div>
            </div>
        </div>
    </div>
</div>


<div class="info-tip">
    <div id="centerCoord"></div>
    <div id="tips"></div>
</div>
<script type="text/javascript">


    jQuery(document).ready(function () {
        initNoPushTable();
    });
    var initNoPushTable = function () {
        $('#pushTable').dataTable({
            "oLanguage": {
                "sLengthMenu": "显示_MENU_条",
                "sZeroRecords": "没有您要搜索的内容",
                "sInfo": "本页显示_START_到_END_条  共_TOTAL_条",
                "sInfoEmpty": "记录数为0",
                "sInfoPostFix": "",
                "sSearch": "搜索",
                "oPaginate": {
                    "sFirst": "第一页",
                    "sPrevious": "上一页",
                    "sNext": "下一页",
                    "sLast": "最后一页"
                }
            },
            "searching": false,
            "bPaginate": true,
            "info": false,
            "paging": true,
            "lengthChange": false,
            "processing": true,
            //设置显示列数
            "aLengthMenu": [
                [5, 10, 15, 20, -1],
                [5, 10, 15, 20, "All"] // change per page values here
            ],
            //默认显示5列
            "iDisplayLength": 10,
            "sPaginationType": "bootstrap"
        });

        jQuery('#pushTable_wrapper .dataTables_filter input').addClass("form-control input-medium"); // modify table search input
        jQuery('#pushTable_wrapper .dataTables_length select').addClass("form-control input-small"); // modify table per page dropdown
        jQuery('#pushTable_wrapper .dataTables_length select').select2({
            showSearchInput: false //hide search box with special css class
        }); // initialize select2 dropdown
    };
    var map = new AMap.Map('container', {
        center: [116.397428, 39.90923],
        zoom: 15
    });
    var driving = new AMap.Driving({
        map: map
    });
    map.clearMap();  // 清除地图覆盖物
    var center = map.getCenter();
    // 添加事件监听, 使地图自适应显示到合适的范围
    //    AMap.event.addDomListener(document.getElementById('setFitView'), 'click', function () {
    //        var newCenter = map.setFitView();
    //        document.getElementById('centerCoord').innerHTML = '当前中心点坐标：' + newCenter.getCenter();
    //        document.getElementById('tips').innerHTML = '通过setFitView，地图自适应显示到合适的范围内,点标记已全部显示在视野中！';
    //    });

    var infoWindow = new AMap.InfoWindow({offset: new AMap.Pixel(0, -30)});
    function markerClick(e) {
        infoWindow.setContent(e.target.content);
        infoWindow.open(map, e.target.getPosition());
    }

    var select = false;
    var clickEventListener = map.on('click', function (e) {
        select = !select;
        if (select) {
            $('#start_region').val(e.lnglat.getLng() + ',' + e.lnglat.getLat());
        } else {
            $('#end_region').val(e.lnglat.getLng() + ',' + e.lnglat.getLat());
        }

    });

    var polyline1 = new AMap.Polyline({
        //设置线覆盖物路径
        strokeColor: "#3366FF", //线颜色
        strokeOpacity: 1,       //线透明度
        strokeWeight: 5,        //线宽
        strokeStyle: "solid",   //线样式
        strokeDasharray: [10, 5] //补充线样式
    });

    var polyline2 = new AMap.Polyline({
        //设置线覆盖物路径
        strokeColor: "#F33", //线颜色
        strokeOpacity: 1,       //线透明度
        strokeWeight: 5,        //线宽
        strokeStyle: "solid",   //线样式
        strokeDasharray: [10, 5] //补充线样式
    });

    var polyline3 = new AMap.Polyline({
        //设置线覆盖物路径
        strokeColor: "#FF33FF", //线颜色
        strokeOpacity: 1,       //线透明度
        strokeWeight: 5,        //线宽
        strokeStyle: "solid",   //线样式
        strokeDasharray: [10, 5] //补充线样式
    });

    $('#addUtb_Point').die().live('click', function (e) {
        $.ajax({
            type: "POST",
            url: "/rest/lbs/getAllStartPoint.json",
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                alert(data.length)
                data.forEach(function (data) {
                    var icon = "";
                    icon = "http://localhost:8080/images/icon/Blue.png"
                    var marker = new AMap.Marker({
                        map: map,
                        icon: icon,
                        position: [data.point.lng, data.point.lat],
                        offset: new AMap.Pixel(-12, -36)
                    });
                    marker.content = "" + data.id;
                    marker.on('click', markerClick);
                    marker.emit('click', {target: marker});
                });
            }
        });
    });


    var saveCheck = false;
    $('#save').die().live('click', function (e) {
        if (saveCheck) {
            saveCheck = false;
            var startId = $('#startId').val();
            var endId = $('#endId').val();
            if ("" == startId || "" == endId) {
                alert("数据缺失！");
            } else {
                $.ajax({
                    type: "POST",
                    url: "/rest/lbs/cacheAsSample.json",
                    async: false,
                    cache: false,
                    data: {
                        startNo: startId,
                        disNo: endId
                    },
                    dataType: "json",
                    success: function (data) {
                        if (!data) {
                            alert("保存失败！")
                        }
                    }
                })
            }
        }
    });

    $('#getRegionInf').die().live('click', function (e) {
        map.clearMap();
        var regionStart = $('#start_region').val();
        var regionEnd = $('#end_region').val();
        if ("" == regionStart || "" == regionEnd) {
            alert("数据缺失！");
        } else {
            $.ajax({
                type: "POST",
                url: "/rest/lbs/getRegionInf.json",
                async: false,
                cache: false,
                data: {
                    regionStart: regionStart,
                    regionEnd: regionEnd
                },
                dataType: "json",
                success: function (data) {
                    if (data.length > 0) {
                        for (var j = 0; j < data.length; ++j) {
                            var temp = data[j];
                            var startNode = temp[0];
                            var marker = new AMap.Marker({
                                icon: 'http://webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
                                position: [startNode.longitude, startNode.latitude],
                                map: map,
                                zIndex: 80
                            });
                            if (startNode.status) {
                                marker.setIcon('http://localhost:8080/images/icon/Yellow.png');
                            }
                            var content = "id=" + startNode.id + "&nbsp&nbsp" + "nextId:&nbsp&nbsp";
                            marker.on('click', markerClick);
                            marker.emit('click', {target: marker});
                            for (var i = 1; i < temp.length; ++i) {
                                content = content + temp[i].id + "&nbsp&nbsp&nbsp";
                                var line = new AMap.Polyline({
                                    strokeOpacity: 1,       //线透明度
                                    strokeWeight: 2,        //线宽
                                    strokeStyle: "solid",   //线样式
                                    map: map
                                });
                                var lineArr = [];
                                lineArr.push([startNode.longitude, startNode.latitude]);
                                lineArr.push([temp[i].longitude, temp[i].latitude])
                                line.setPath(lineArr)
                                var tempMark = new AMap.Marker({
                                    position: [temp[i].longitude, temp[i].latitude],
                                    map: map,
                                    zIndex: 50
                                });
                                if (temp[i].status) {
                                    tempMark.setIcon('http://localhost:8080/images/icon/Yellow.png');
                                    tempMark.setzIndex(100);
                                }
                                tempMark.content = "id=" + temp[i].id + "&nbsp&nbsp";
                                tempMark.on('click', markerClick);
                                tempMark.emit('click', {target: tempMark});
                            }
                            marker.content = content;
                        }
                    }
                }
            })
        }
    });


    $('#findMPP').die().live('click', function (e) {
        map.clearMap();
        if ("" == $('#startId').val() || "" == $('#endId').val()) {
            alert("数据缺失！");
        } else {
            $.ajax({
                type: "POST",
                url: "/rest/lbs/findPath.json",
                async: false,
                cache: false,
                data: {
                    startNo: $('#startId').val(),
                    disNo: $('#endId').val(),
                    topKSwitch: $('#topK input[name="topKSwitch"]:checked').val()
                },
                dataType: "json",
                success: function (data) {
                    // 根据起终点经纬度规划驾车导航路线
                    if("On"==$('#compare input[name="compareOff"]:checked').val()){
                        driving.search(new AMap.LngLat(data[0][0].longitude, data[0][0].latitude), new AMap.LngLat(data[0][data[0].length - 1].longitude, data[0][data[0].length - 1].latitude));
                    }
                    saveCheck = true;
                    if (data.length > 0) {
                        polyline1.setMap(null);
                        polyline2.setMap(null);
                        polyline3.setMap(null);

                        for (var i = 0; i < data.length && i < 3; ++i) {
                            var lineArr = [];
                            data[i].forEach(function (temp) {
                                lineArr.push([temp.longitude, temp.latitude])
                            });
                            if (i == 0) {
                                polyline1.setPath(lineArr);
                                polyline1.setMap(map);
                            } else if (i == 1) {
                                polyline2.setPath(lineArr);
                                polyline2.setMap(map);
                            } else {
                                polyline3.setPath(lineArr);
                                polyline3.setMap(map);
                            }

                        }

                    } else {
                        alert("未能查询到频繁路径！");
                    }


                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    alert(XMLHttpRequest.responseText);
                }
            });
        }
    });

    $(document).ready(function() {
        map.clearMap();
        $.ajax({
            type:"POST",
            url:"/rest/lbs/getPairForTable.json",
            async:false,
            cache:false,
            dataType:"json",
            success:function(data){
                var dataSource = [];
                for(var i=0;i<data.length;i=i+2){
                    var temp = [];
                    temp.push(data[i],data[i+1]);
                    dataSource.push(temp);
                }
                var Table = $('#pushTable').dataTable();
                Table.fnClearTable();
                Table.fnAddData(dataSource);
            }
        });
        var table = $('#pushTable').DataTable();
        $('#pushTable tbody').on('click', 'tr', function () {
            var data = table.row(this).data();
            if ("" == data[0] || "" == data[1]) {
                alert("数据缺失！");
            } else {
                $.ajax({
                    type: "POST",
                    url: "/rest/lbs/findPath.json",
                    async: false,
                    cache: false,
                    data: {
                        startNo: data[0],
                        disNo: data[1],
                        topKSwitch: $('#topK input[name="topKSwitch"]:checked').val()
                    },
                    dataType: "json",
                    success: function (data) {
                        // 根据起终点经纬度规划驾车导航路线
                        if("On"==$('#compare input[name="compareOff"]:checked').val()){
                            driving.search(new AMap.LngLat(data[0][0].longitude, data[0][0].latitude), new AMap.LngLat(data[0][data[0].length - 1].longitude, data[0][data[0].length - 1].latitude));
                        }
                        saveCheck = true;
                        if (data.length > 0) {
                            polyline1.setMap(null);
                            polyline2.setMap(null);
                            polyline3.setMap(null);

                            for (var i = 0; i < data.length && i < 3; ++i) {
                                var lineArr = [];
                                data[i].forEach(function (temp) {
                                    lineArr.push([temp.longitude, temp.latitude])
                                });
                                if (i == 0) {
                                    polyline1.setPath(lineArr);
                                    polyline1.setMap(map);
                                } else if (i == 1) {
                                    polyline2.setPath(lineArr);
                                    polyline2.setMap(map);
                                } else {
                                    polyline3.setPath(lineArr);
                                    polyline3.setMap(map);
                                }

                            }

                        } else {
                            alert("未能查询到频繁路径！");
                        }


                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        alert(XMLHttpRequest.responseText);
                    }
                });
            }
        } );
    } );

    //location vertex
    $('#locationVertex').die().live('click', function (){
        var v_id = $('#vertex_id').val()
        alert(v_id)
    })

    //location edge
    $('#locationEdge').die().live('click',function(){
        var edge_id = $('#edge_id').val()
        alert(edge_id)
    })
</script>
</body>
</html>
