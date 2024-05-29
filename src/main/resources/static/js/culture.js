var map;
function initializeMap() {
    var mapContainer = document.getElementById('map'); // 지도를 표시할 div
    var mapOption = {
        center: new kakao.maps.LatLng(37.54982267371823, 126.84233877714716), // 지도의 중심좌표
        level: 8 // 지도의 확대 레벨
    };
    map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
}

$(document).ready(function() {
    initializeMap();

    let lat1 = 37; // 초기 위도 설정
    let lon1 = 126; // 초기 경도 설정

    // HTML5의 geolocation으로 사용할 수 있는지 확인합니다
    if (navigator.geolocation) {
        // GeoLocation을 이용해서 접속 위치를 얻어옵니다
        navigator.geolocation.getCurrentPosition(function(position) {
            lat1 = position.coords.latitude; // 현재 위치의 위도
            lon1 = position.coords.longitude; // 현재 위치의 경도

            console.log(lat1, lon1);
        });
    } else {
        // HTML5의 GeoLocation을 사용할 수 없을때 기본 마커 위치와 메시지를 설정합니다
        var locPosition = new kakao.maps.LatLng(33.450701, 126.570667),
            message = 'geolocation을 사용할수 없어요..';

        displayMarker(locPosition, message);
    }

    var currentPage1 = 1;
    const itemsPerPage1 = 20;
    const lcLa = 126.84233877714716; // 기준 위치 경도
    const lclo = 37.54982267371823; // 기준 위치 위도
    var markers = []; // 마커를 저장할 배열

    $("#btnCulture").on("click", function () {
        currentPage1 = 1; // 페이지를 다시 1로 설정
        $("#parkData").empty(); // park 리스트 지우기
        $("#hospitalData").empty(); // hospital 리스트 지우기
        $("#sidebarContent").removeClass("hidden").addClass("expanded"); // 사이드바 확장
        $("#map").addClass("expanded"); // 지도를 좁힘
        $(".sidebar-btn").removeClass("active"); // 모든 버튼의 active 클래스 제거
        $(this).addClass("active"); // 클릭된 버튼에 active 클래스 추가
        $(".divTableBody").hide(); // 다른 리스트 숨기기
        $("#cultureData").show().addClass("active"); // culture 리스트 보여주기
        doCulture(currentPage1, itemsPerPage1);
    });

    function doCulture(page, itemsPerPage1) {
        let f = document.getElementById("f"); // form 태그

        // Ajax 호출해서 문화 시설 정보 가져오기
        $.ajax({
            url: "/map/v1/getCultureInfo",
            type: "post", // 전송방식은 Post
            data: {
                page: page,
                itemsPerPage: itemsPerPage1,
                lcLa: lon1,
                lcLo: lat1
            },
            dataType: "JSON", // 전송 결과는 JSON으로 받기
        }).then(
            function (json) {
                const dataList = json.data;
                $("#cultureData").empty();
                markers.forEach(marker => marker.setMap(null)); // 이전 페이지의 마커 삭제
                markers = []; // 배열 비우기
                var positions = [];
                var bounds = new kakao.maps.LatLngBounds();

                // 중복 제거를 위한 Set 객체
                const seen = new Set();
                const uniqueDataList = dataList.filter(data => {
                    const key = data.fcltyNm + data.rdnmadrNm;
                    return seen.has(key) ? false : seen.add(key);
                });

                const startIndex = (page - 1) * itemsPerPage1;
                const endIndex = startIndex + itemsPerPage1;
                for (let i = startIndex; i < endIndex && i < uniqueDataList.length; i++) {
                    const data = uniqueDataList[i];
                    const $row = $("<div class=\"divTableRow\">" +
                        "<div class=\"divTableCell\">" + data.fcltyNm + "</div>" +
                        "<div class=\"divTableCell\">" + data.rdnmadrNm + "</div>" +
                        "</div>");

                    // 클릭 이벤트 핸들러 추가
                    $row.on("click", (function (index) {
                        return function () {
                            const clickedMarkerPosition = positions[index].latlng; // 클릭된 리스트의 마커 위치 가져오기
                            map.panTo(clickedMarkerPosition); // 해당 마커 위치로 지도 이동
                            map.setLevel(1, { anchor: clickedMarkerPosition }); // 해당 마커가 중앙에 오도록 지도 확대
                            map.setCenter(clickedMarkerPosition); // 해당 마커가 지도의 중앙에 오도록 설정
                        };
                    })(i)); // 클로저 문제 해결

                    $("#cultureData").append($row);

                    const position = new kakao.maps.LatLng(data.lcLa, data.lcLo);
                    positions.push({
                        content: '<div>' + data.fcltyNm + '</div>',
                        latlng: position
                    });

                    bounds.extend(position); // bounds 객체에 위치 추가
                }

                // 지도에 마커 표시
                for (var i = 0; i < positions.length; i++) {
                    var marker = new kakao.maps.Marker({
                        map: map,
                        position: positions[i].latlng
                    });
                    markers.push(marker); // 배열에 마커 추가

                    var infowindow = new kakao.maps.InfoWindow({
                        content: positions[i].content
                    });

                    // 마커 클릭 시 해당 위치로 지도 이동
                    kakao.maps.event.addListener(marker, 'click', (function (marker, i) {
                        return function () {
                            map.panTo(marker.getPosition());
                        }
                    })(marker, i));

                    kakao.maps.event.addListener(marker, 'mouseover', makeOverListener(map, marker, infowindow));
                    kakao.maps.event.addListener(marker, 'mouseout', makeOutListener(infowindow));
                }

                // 지도 영역을 bounds에 맞게 조정
                map.setBounds(bounds);

                // 페이지네이션 업데이트
                let totalPage = Math.ceil(dataList.length / itemsPerPage1); // 전체 페이지 수 계산
                updatePagination(totalPage);
            }
        )
    }

    // 인포윈도우를 표시하는 클로저를 만드는 함수입니다
    function makeOverListener(map, marker, infowindow) {
        return function () {
            infowindow.open(map, marker);
        };
    }

    // 인포윈도우를 닫는 클로저를 만드는 함수입니다
    function makeOutListener(infowindow) {
        return function () {
            infowindow.close();
        };
    }
});
