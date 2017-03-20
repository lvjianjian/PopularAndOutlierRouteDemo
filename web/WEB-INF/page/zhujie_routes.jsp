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
    <title>ODPairRoute</title>
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
    <div class="col-md-5 pull-right">

        <div id="result-group">
            <table class="table table-striped table-hover table-bordered NoticeTable col-md-6"
                   style="font-size: smaller" id="pushTable">
                <thead>
                <tr>
                    <th width="20px">序号</th>
                    <th width="150px">起点</th>
                    <th width="150px">终点</th>
                    <th width="20px">路径数量</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-2">
                <input id="freq" class="form-control" type="text" placeholder="frequency" readonly="true"/>
            </div>
            <div class="col-md-3">
                <input id="all_freq" class="form-control" type="text" placeholder="all_frequency" readonly="true"/>
            </div>
            <div class="col-md-2">
                <input id="previous" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="previous"/>
            </div>
            <div class="col-md-2">
                <input id="next" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="next"/>
            </div>
            <div class="col-md-2">
                <input id="all" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="all"/>
            </div>
        </div>
        <div class="cluster">
            <div class="row">
                <div class="col-md-2">
                    <input class="xuhao form-control" type="text" placeholder="序号" readonly="true"/>
                </div>
                <div class="col-md-2">
                    <input id="k" class="form-control" type="text" placeholder="k" width="20px"/>
                </div>
                <div class="col-md-2">
                    <input id="clusterByKMedoids" class="btn btn-success col-md-12"
                           style="margin-left: auto;margin-right:auto;text-align: center"
                           type="button" value="cluster"/>
                </div>

                <div class="col-md-2">
                    <input id="nextcluster" class="btn btn-success col-md-12"
                           style="margin-left: auto;margin-right:auto;text-align: center"
                           type="button" value="next"/>
                </div>
            </div>
        </div>
        <div class="cluster">
            <div class="row">
                <div class="col-md-2">
                    <input class="xuhao form-control" type="text" placeholder="序号" readonly="true"/>
                </div>
                <div class="col-md-2">
                    <input id="all_traj_num" class="form-control" type="text" placeholder="总轨迹数" readonly="true"/>
                </div>
                <div class="col-md-2">
                    <input id="e" class="form-control" type="text" placeholder="e" width="20px"/>
                </div>
                <div class="col-md-2">
                    <input id="minpts" class="form-control" type="text" placeholder="minpts" width="20px"/>
                </div>
                <div class="col-md-2">
                    <input id="overlap_ratio" class="form-control" type="text" placeholder="overlap_ratio"
                           width="20px"/>
                </div>
                <div class="col-md-2">
                    <input id="clusterByDB" class="btn btn-success col-md-12"
                           style="margin-left: auto;margin-right:auto;text-align: center"
                           type="button" value="cluster"/>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <input id="cluster_num" class="form-control" type="text" placeholder="" width="20px" readonly="true"/>
            </div>
            <div class="col-md-2">
                <input id="cluster_freq" class="form-control" type="text" placeholder="" width="20px" readonly="true"/>
            </div>
            <div class="col-md-2">
                <input id="previousclusterByDB" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="previous"/>
            </div>
            <div class="col-md-2">
                <input id="nextclusterByDB" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="next"/>
            </div>
            <div class="col-md-2">
                <input id="individualByDB" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="individual"/>
            </div>
            <div class="col-md-2">
                <input id="allByDB" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="all"/>
            </div>
        </div>
        <div class="row">
            <div class="col-md-2">
                <input id="route_index_in_cluster" class="form-control" type="text" placeholder="" width="20px"
                       readonly="true"/>
            </div>
            <div class="col-md-2">
                <input id="route_freq" class="form-control" type="text" placeholder="" width="20px" readonly="true"/>
            </div>

            <div class="col-md-2">
                <input id="previousInCluster" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="previous"/>
            </div>
            <div class="col-md-2">
                <input id="nextInCluster" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="next"/>
            </div>
            <div class="col-md-3">
                <input id="centerByDB" class="btn btn-success col-md-12"
                       style="margin-left: auto;margin-right:auto;text-align: center"
                       type="button" value="representative"/>
            </div>

        </div>
        <div class="vertex">
            <div class="row">
                <div class="col-md-5">
                    <input id="vertex_id" class="form-control" type="text" placeholder="vertex id"/>
                </div>
                <div class="col-md-2">
                    <input id="locationVertex" class="btn btn-success col-md-12"
                           style="margin-left: auto;margin-right:auto;text-align: center"
                           type="button" value="Vertex"/>
                </div>
            </div>
        </div>
        <div class="edge">
            <div class="row">
                <div class="col-md-5">
                    <input id="edge_id" class="form-control" type="text" placeholder="edge id"/>
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

    function getPolyline(color) {
        return polyline1 = new AMap.Polyline({
            //设置线覆盖物路径
            strokeColor: color, //线颜色
            strokeOpacity: 1,       //线透明度
            strokeWeight: 5,        //线宽
            strokeStyle: "solid",   //线样式
            strokeDasharray: [10, 5] //补充线样式
        });
    }

    var polyline1 = getPolyline("#3366FF")

    var polyline2 = getPolyline("#F33")

    var polyline3 = getPolyline("#FF33FF")


    $('#addUtb_Point').die().live('click', function (e) {
        $.ajax({
            type: "POST",
            url: "/rest/lbs/getAllStartPoint.json",
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
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


    var blueMarker = new AMap.Marker({
        map: map,
        icon: "http://localhost:8080/images/icon/Blue.png",
    });

    var redMarker = new AMap.Marker({
        map: map,
        icon: "http://localhost:8080/images/icon/Red.png",
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
                    if ("On" == $('#compare input[name="compareOff"]:checked').val()) {
                        driving.search(new AMap.LngLat(data[0][0].longitude, data[0][0].latitude), new AMap.LngLat(data[0][data[0].length - 1].longitude, data[0][data[0].length - 1].latitude));
                    }
                    saveCheck = true;
                    if (data.length > 0) {

                        for (var i = 0; i < data.length; ++i) {
                            var lineArr = [];
                            data[i].forEach(function (temp) {
                                lineArr.push([temp.longitude, temp.latitude])
                            });
                            getPolyline("").setPath(lineArr)


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


    var routes = null
    var routes_index = 0

    $(document).ready(function () {
        map.clearMap();
        $.ajax({
            type: "POST",
            url: "/rest/routesOfZJ/getAllTrajs.json",
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {
                var dataSource = [];
                routes = data
//                alert(routes.length)
                for (var i = 0; i < data.length; ++i) {
                    var temp = [];

                    temp.push(i+1);
                    dataSource.push(temp);
                }
                var Table = $('#pushTable').dataTable();
                Table.fnClearTable();
                Table.fnAddData(dataSource);
            },
//            error: function (XMLHttpRequest, textStatus, errorThrown) {
//                alert(XMLHttpRequest.responseText);
//            }
        });
        var table = $('#pushTable').DataTable();
        //click tr and get all routes of the odpair
        $('#pushTable tbody').on('click', 'tr', function () {
            var data = table.row(this).data();
            i = data[0] - 1;
//            alert(i);
            map.clearMap()
            drawRouteByNodes(routes[i].nodes)
        });
    });


    function drawRouteByNodes(nodes) {
        var start_lng = nodes[0].longitude
        var start_lat = nodes[0].latitude
        var end_lng = nodes[nodes.length - 1].longitude
        var end_lat = nodes[nodes.length - 1].latitude
        blueMarker.setPosition(new AMap.LngLat(start_lng, start_lat))
        blueMarker.setMap(map)
        redMarker.setPosition(new AMap.LngLat(end_lng, end_lat))
        redMarker.setMap(map)
        var i = routes_index
        var freq = routes[i][0]
        $('#freq').val(freq)
        var lineArr = [];
        nodes.forEach(function (temp) {
            lineArr.push([temp.longitude, temp.latitude])
        });
        var polyline = getPolyline('#' + ('00000' + (Math.random() * 0x1000000 << 0).toString(16)).slice(-6))
        polyline.setPath(lineArr);
        polyline.setMap(map);

    }

    //get all route
    $('#all').die().live('click', function () {
        if (routes == null) {
            alert("choose od_pair")
            return
        }
        for (i = 0; i < routes.length; ++i) {
            drawRouteByNodes(routes[i][1])
        }
    })

    //get odpair's previous route
    $('#previous').die().live('click', function () {
        if (routes == null) {
            alert("choose od_pair")
            return
        }
        routes_index = (routes_index - 1 + routes.length) % routes.length
        map.clearMap()
        drawRouteByNodes(routes[routes_index][1])
    })
    //get odpair's next route
    $('#next').die().live('click', function () {
        if (routes == null) {
            alert("choose od_pair")
            return
        }
        routes_index = (routes_index + 1 + routes.length) % routes.length
        map.clearMap()
        drawRouteByNodes(routes[routes_index][1])
    })
    //location vertex
    $('#locationVertex').die().live('click', function () {
        var v_id = $('#vertex_id').val()
        $.ajax({
            type: "POST",
            url: "/rest/routes/getVertex.json",
            async: false,
            cache: false,
            dataType: "json",
            data: {
                vertex_id: v_id
            },
            success: function (data) {
                alert(data.id)
            }
        });
    })

    //location edge
    $('#locationEdge').die().live('click', function () {
        var edge_id = $('#edge_id').val()
        alert(edge_id)
    })


    //clusterByKMedoids
    $('#clusterByKMedoids').die().live('click', function () {
        var index = $('.xuhao').val() - 1
        var k = $('#k').val()
        if (k == "" || isNaN(k)) {
            alert("输入聚类个数")
            return
        }
        if (index == -1) {
            alert("请选择一个odpair来聚类")
            return
        }
        $.ajax({
            type: "POST",
            url: "/rest/routes/clusterByKMedoids.json",
            dataType: "json",
            data: {
                index: index,
                k: k
            }
            ,
            success: function (data) {
                var now = 0
                showRoutes(data[0])
                $('#nextcluster').die().live('click', function () {
                    var k = data.length;
                    now = (now + 1) % k
                    var routes = data[now]
                    showRoutes(routes)
                })
            }
        })
    })


    //show routes in cluster
    function showRoutes(routesCluster) {
        map.clearMap()
        var now = 0;
        var routes = routesCluster.routes
        var size = routes.length
        var reference_index = routesCluster.referenceRouteIndex

        function showRouteInCluster(now) {
            map.clearMap()
            drawRouteByNodes(routes[now].nodes)
            $('#route_index_in_cluster').val(now + 1)
            $('#route_freq').val(routes[now].frequency)
        }

        //show each route in the cluster
        $('#individualByDB').die().live('click', function () {
            map.clearMap()
            now = 0
            showRouteInCluster(now)

            $('#previousInCluster').die().live('click', function () {
                now = ((now - 1) + size) % size
                showRouteInCluster(now);
            })

            $('#nextInCluster').die().live('click', function () {
                now = ((now + 1) + size) % size
                showRouteInCluster(now)
            })

            $('#centerByDB').die().live('click', function () {
                showRouteInCluster(reference_index)
            })

        })

        for (i = 0; i < routes.length; i++) {
            drawRouteByNodes(routes[i].nodes)
        }
        $('#cluster_freq').val(routesCluster.all_frequency)


    }

    $('#clusterByDB').die().live('click', function () {
        var index = $('.xuhao').val() - 1
        if (index == -1) {
            alert("请选择一个odpair来聚类")
            return
        }
        var e = $('#e').val()
        var minpts = $('#minpts').val()
        var constraint = $('#overlap_ratio').val()
        if (e == "" || minpts == "" || constraint == "") {
            alert("enter e or minpts or overlap ratio")
            return
        }

        $.ajax({
            type: "POST",
            url: "/rest/routes/clusterByDB2.json",
            dataType: "json",
            data: {
                index: index,
                eRatio: e,
                minptsRatio: minpts,
                overlap_ratio: constraint
            },
            success: function (data) {
                var now = 0
                showRoutes(data[0])
                $('#cluster_num').val(now + 1)
                var all_traj_num = 0
                for (i = 0; i < data.length; ++i) {
                    all_traj_num += data[i].all_frequency
                }
                $('#all_traj_num').val(all_traj_num)
                $('#previousclusterByDB').die().live('click', function () {
                    var k = data.length;
                    now = ((now - 1) + k) % k
                    var routes = data[now]
                    showRoutes(routes)
                    $('#cluster_num').val(now + 1)
                    clearIndividual()
                })
                $('#nextclusterByDB').die().live('click', function () {
                    var k = data.length;
                    now = (now + 1) % k
                    var routes = data[now]
                    showRoutes(routes)
                    $('#cluster_num').val(now + 1)
                    clearIndividual()
                })

                $('#allByDB').die().live('click', function () {
                    showRoutes(data[now])
                })

                function clearIndividual() {
                    $('#route_index_in_cluster').val("")
                    $('#route_freq').val("")
                    $('#previousInCluster').die()
                    $('#nextInCluster').die()
                    $('#centerByDB').die()
                }
            }

        })
    })

</script>
</body>
</html>
