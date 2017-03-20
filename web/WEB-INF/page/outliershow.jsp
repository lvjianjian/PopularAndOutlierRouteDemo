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
    <title>异常路径检测</title>
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
        .w{ width:100%; clear:both; overflow:hidden;}
        .c{ width:22%; float:left;border:1px solid green; height:100%}
        .d{ width:78%; margin-left:22%;border:1px solid orange;}
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
<body class="w">
<%@ include file="./shared/importJs.jsp" %>
<%@ include file="./shared/importCss.jsp" %>

<div class="c" >
    <div>
        <div id="result-group">
            <table class="table table-striped table-hover table-bordered NoticeTable col-md-6"
                   style="font-size: smaller" id="pushTable">
                <thead>
                <tr>
                    <th width="20%">编号</th>
                    <th width="40%">起点</th>
                    <th width="40%">终点</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-3">
                    <input id="numberid" class="form-control" type="text" placeholder="编号" width="20px"
                           readonly="true"/>
            </div>
            <div class="col-md-3">
                <input id="checkOutlier" class="btn btn-success"
                       type="button" value="检测" style="width:100%"/>
            </div>
            <div class="col-md-5">
                <input id="outliershow" class="form-control" type="text" placeholder="" width="20px"
                       readonly="true"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-8">
                <label style="font-size:16px"> <input id="representative" type="checkbox" style="width:20px;height:20px;"> show representative</label>
            </div>

        </div>
        <div class="row">
            <div class="col-md-8">
                <label style="font-size:16px"><input id="recommend" type="checkbox" style="width:20px;height:20px;" > show recommend</label>
            </div>
        </div>

    </div>
</div>


<div class="row">
    <div id="container" class="d"></div>
    <div id="panel" ></div>
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
            "iDisplayLength": 20,
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




    var blueMarker = new AMap.Marker({
        map: map,
        icon: "http://localhost:8080/images/icon/Blue.png",
    });

    var redMarker = new AMap.Marker({
        map: map,
        icon: "http://localhost:8080/images/icon/Red.png",
    });

    function getPolyline(color,width) {
        return polyline1 = new AMap.Polyline({
            //设置线覆盖物路径
            strokeColor: color, //线颜色
            strokeOpacity: 1,       //线透明度
            strokeWeight: width,        //线宽
            strokeStyle: "solid",   //线样式
            strokeDasharray: [10, 5] //补充线样式
        });
    }

    var polyline1 = getPolyline("#3366FF",10)

    var polyline2 = getPolyline("#6495ED",6)

    var polyline3 = getPolyline("#FF33FF",3)

    function randomPolyline() {
        return getPolyline('#' + ('00000' + (Math.random() * 0x1000000 << 0).toString(16)).slice(-6))
    }

    function drawRouteByNodes(nodes,polyline) {
        var start_lng = nodes[0].longitude
        var start_lat = nodes[0].latitude
        var end_lng = nodes[nodes.length - 1].longitude
        var end_lat = nodes[nodes.length - 1].latitude
        blueMarker.setPosition(new AMap.LngLat(start_lng, start_lat))
        blueMarker.setMap(map)
        redMarker.setPosition(new AMap.LngLat(end_lng, end_lat))
        redMarker.setMap(map)
//        var i = routes_index
//        var freq = routes[i].frequency
//        $('#freq').val(freq)
//        $('#route_id').val(routes[i].id)
//        alert(routes[i].isOutlier)
//        if(routes[i].isOutlier)
//            $('#setOutlier').val("outlier")
//        else
//            $('#setOutlier').val("normal")
//        $('#ibat_score').val(routes[i].ibat_score)
//        $('#search_score').val(routes[i].search_score)
        var lineArr = [];
        nodes.forEach(function (temp) {
            lineArr.push([temp.longitude, temp.latitude])
        });
        polyline.setPath(lineArr);
        polyline.setMap(map);
    }


    representative_choose = false
    recomment_choose = false


    $("#representative").die().live('click',function () {
        if($("#representative").attr("checked")){
            representative_choose = true
        }else {
            representative_choose = false
        }
        requestRoute(numberid,representative_choose,recomment_choose)
    })

    $("#recommend").die().live('click',function () {
        if($("#recommend").attr("checked")){
            recomment_choose = true
        }else {
            recomment_choose = false
        }
        requestRoute(numberid,representative_choose,recomment_choose)
    })

    function isOutlier(numberid,sid,eid,representative_choose,recomment_choose) {
        $.ajax({
            type: "POST",
            url: "/rest/outliershow/checkOutlier.json",
            async: true,
            cache: false,
            data: {
                numberID: numberid,
                sid: sid,
                eid: eid
            },
            dataType: "json",
            success: function (data) {
                if (data == true)
                    $("#outliershow").val("outlier")
                else
                    $("#outliershow").val("normal")
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(XMLHttpRequest.responseText);
            }
        });
    }

    $('#checkOutlier').die().live('click', function (e) {
        if (numberid == -1) {
            $("#outliershow").val("请选择一条轨迹!");
        } else {
            isOutlier(numberid,sid,eid,representative_choose,recomment_choose);
        }
    });

    numberid = -1
    sid = -1
    eid = -1
    function requestRoute(numberid,representative_choose,recomment_choose) {
        $.ajax({
            type: "POST",
            url: "/rest/outliershow/getRouteBynumberID.json",
            async: true,
            cache: false,
            data: {
                numberID: numberid,
            },
            dataType: "json",
            success: function (data) {
                map.clearMap();
                drawRouteByNodes(data.nodes,polyline1)
                var nodes = data.nodes
                var start_lng = nodes[0].longitude
                var start_lat = nodes[0].latitude
                map.setCenter(new AMap.LngLat(start_lng, start_lat))


                if(representative_choose){
                    $.ajax({
                        type: "POST",
                        url: "/rest/outliershow/getRepresentativeRoute.json",
                        async: true,
                        cache: false,
                        data: {
                            numberID: numberid,
                            sid:sid,
                            eid:eid
                        },
                        dataType: "json",
                        success: function (data) {
                            drawRouteByNodes(data.nodes,polyline2)
                        }
                    })
                }

                if(recomment_choose){
                    driving.search(new AMap.LngLat(nodes[0].longitude, nodes[0].latitude), new AMap.LngLat(nodes[nodes.length - 1].longitude, nodes[nodes.length - 1].latitude));
                }


            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(XMLHttpRequest.responseText);
            }
        });
    }
    $(document).ready(function() {
        map.clearMap();
//        $.ajax({
//            type:"GET",
//            url:"/rest/outliershow/test.json",
//            async:false,
//            cache:false,
//            dataType:"json",
//            success:function(data){
//                alert(data)
//            }
//        })

        $.ajax({
            type:"POST",
            url:"/rest/outliershow/getTrajForTable.json",
            async:true,
            cache:false,
            dataType:"json",
            success:function(data){
                var dataSource = [];
                for(var i=0;i<data.length;i=i+1){
                    var temp = [];
                    temp.push(data[i][0],data[i][1],data[i][2]);
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
            if ("" == data[0] || "" == data[1] || "" == data[2]) {
                alert("数据缺失！");
            } else {
                numberid = data[0]
                sid = data[1]
                eid = data[2]
                $("#numberid").val(numberid)
                $("#outliershow").val("")
                requestRoute(numberid,representative_choose,recomment_choose);
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
