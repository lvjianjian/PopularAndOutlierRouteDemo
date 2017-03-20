/**
 * Created by JQ-Cao on 2015/11/25.
 */
var vehicle = function(){

    var map = new AMap.Map('container', {
        resizeEnable: true,
        center: [118.78463,32.041372],
        zoom: 15
    });
    map.clearMap();  // 清除地图覆盖物
    var center = map.getCenter();
    // 添加事件监听, 使地图自适应显示到合适的范围
    AMap.event.addDomListener(document.getElementById('setFitView'), 'click', function() {
        var newCenter = map.setFitView();
        document.getElementById('centerCoord').innerHTML = '当前中心点坐标：' + newCenter.getCenter();
        document.getElementById('tips').innerHTML = '通过setFitView，地图自适应显示到合适的范围内,点标记已全部显示在视野中！';
    });

    $('#addUtb_Point').die().live('click',function(e){
        $.ajax({
            type: "POST",
            url: "/rest/vehicle/getAllInformation.json",
            async: false,
            cache: false,
            dataType: "json",
            success: function (data) {

                data.forEach(function(data) {
                    new AMap.Marker({
                        map: map,
                        icon: "http://webapi.amap.com/theme/v1.3/markers/n/mark_b.png",
                        position: [data.point_LONGITUDE, data.point_LATITUDE],
                        offset: new AMap.Pixel(-12, -36)
                    });
                });
            }
        });
    });

    return {
        init:function(){

        }
    }
}();