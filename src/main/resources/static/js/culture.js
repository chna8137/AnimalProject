let lat1 = 37;// 위도
let lon1 = 126; // 경도

// HTML5의 geolocation으로 사용할 수 있는지 확인합니다
if (navigator.geolocation) {

    // GeoLocation을 이용해서 접속 위치를 얻어옵니다
    navigator.geolocation.getCurrentPosition(function(position) {

        const lat = position.coords.latitude, // 위도
            lon = position.coords.longitude; // 경도

        lat1 = lat;
        lon1 = lon;

        console.log(lat1, lon1)
    });

} else { // HTML5의 GeoLocation을 사용할 수 없을때 마커 표시 위치와 인포윈도우 내용을 설정합니다

    var locPosition = new kakao.maps.LatLng(33.450701, 126.570667),
        message = 'geolocation을 사용할수 없어요..'

    displayMarker(locPosition, message);
}

var currentPage1 = 1;
const itemsPerPage1 = 20;
const lcLa = 126.84233877714716;
const lclo = 37.54982267371823;
var markers = []; // 마커를 저장할 배열



// HTML로딩이 완료되고, 실행됨
$(document).ready(function () {
    // 버튼 클릭했을때, 발생되는 이벤트 생성함(onclick 이벤트와 동일함)
    $("#btnCulture").on("click", function () {
        currentPage1 = 1; // 페이지를 다시 1로 설정
        doCulture(currentPage1, itemsPerPage1);
    })
})


function doCulture(page, itemsPerPage1) {
    let f = document.getElementById("f"); // form 태그

    // Ajax 호출해서 공원 정보 가져오기
    $.ajax({
        url: "/map/v1/getCultureInfo",
        type: "post", // 전송방식은 Post
        data: {
            page: page,
            itemsPerPage1: itemsPerPage1,
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
            const startIndex = (page - 1) * itemsPerPage;
            const endIndex = startIndex + itemsPerPage;
            for (let i = startIndex; i < endIndex && i < dataList.length; i++) {
                const data = dataList[i];
                const $row = $("<div class=\"divTableRow\">" +
                    "<div class=\"divTableCell\">" + data.fcltyNm + "</div>" +
                    "<div class=\"divTableCell\">" + data.zipNo + "</div>" +
                    "<div class=\"divTableCell\">" + data.rdnmadrNm + "</div>" +
                    "</div>");

                // 클릭 이벤트 핸들러 추가
                $row.on("click", function () {
                    const clickedMarkerIndex = $(this).index(); // 클릭된 리스트의 인덱스 가져오기
                    const clickedMarkerPosition = positions[clickedMarkerIndex].latlng; // 클릭된 리스트의 마커 위치 가져오기
                    map.panTo(clickedMarkerPosition); // 해당 마커 위치로 지도 이동
                    map.setLevel(3, { anchor: clickedMarkerPosition }); // 해당 마커가 중앙에 오도록 지도 확대
                    map.setCenter(clickedMarkerPosition); // 해당 마커가 지도의 중앙에 오도록 설정
                });

                $("#cultureData").append($row);

                positions.push({
                    content: '<div>' + data.fcltyNm + '</div>',
                    latlng: new kakao.maps.LatLng(data.lcLa, data.lcLo)
                });
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

            // 페이지네이션 업데이트
            let totalPage = Math.ceil(dataList.length / itemsPerPage); // 전체 페이지 수 계산
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


// // 페이지네이션 업데이트 함수
// function updatePagination(totalPages) {
//     const pagination = $("#pagination");
//     pagination.empty();
//
//     if (currentPage > 1) {
//         pagination.append("<button id='first'>First</button>");
//         pagination.append("<button id='prev'>Previous</button>");
//     }
//
//     // 최대 5개까지만 버튼 표시
//     let startPage = Math.max(1, currentPage - 2);
//     let endPage = Math.min(startPage + 4, totalPages);
//
//     for (let i = startPage; i <= endPage; i++) {
//         if (i === currentPage) {
//             pagination.append("<button class='active'>" + i + "</button>");
//         } else {
//             pagination.append("<button>" + i + "</button>");
//         }
//     }
//
//     if (currentPage < totalPages) {
//         pagination.append("<button id='next'>Next</button>");
//         pagination.append("<button id='last'>Last</button>");
//     }
//
//     // 페이지 버튼 클릭 이벤트
//     $("#pagination button").on("click", function () {
//         const btnText = $(this).text();
//         if (btnText === "First") {
//             currentPage = 1;
//         } else if (btnText === "Last") {
//             currentPage = totalPages;
//         } else if (btnText === "Previous") {
//             if (currentPage > 1) {
//                 currentPage--;
//             }
//         } else if (btnText === "Next") {
//             if (currentPage < totalPages) {
//                 currentPage++;
//             }
//         } else {
//             currentPage = parseInt(btnText);
//         }
//         doPark(currentPage, itemsPerPage);
//     });
// }