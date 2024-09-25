/** order_cart : totalPriceCard : 총 구매 금액 calculate **/
/** order_cart : totalPriceCard : 총 구매 금액 calculate **/
$(function () {
    calcTotalPrice();
    updateAddr();
    updateItemPriceInput();


    // 구매수량 - 버튼
    $(".minus").on("click", function (e) {
        e.preventDefault();
        let $input = $(this).next("input");
        let minValue = parseInt($input.attr("min"));
        if (parseInt($input.val()) > minValue) {
            $input.val(parseInt($input.val()) - 1);
        } else {
            showMessage("quantityMessage", "최소 구매 수량은 1개 입니다.");
        }
        let $orgPrice = $(this).parents('.calcItem').find('.orgPrice').val();
        let $finalPrice = $(this).parents('.calcItem').find('.finalPrice').val();
        let $sumOrgPrice = $(this).parents('.calcItem').find('.sumOrgPrice');
        let $sumFinalPrice = $(this).parents('.calcItem').find('.sumFinalPrice');

        $sumOrgPrice.val($orgPrice * $input.val());
        $sumFinalPrice.val($finalPrice * $input.val());

        let $sumDiscPrice = $sumFinalPrice.val() - $sumOrgPrice.val();
        $(this).parents('.calcItem').find('.sumDiscPrice').val($sumDiscPrice);  //아이템당 총 할인금액

        $(this).parents('.calcItem').find('.prodTotalPrice').text(
            Number($sumFinalPrice.val()).toLocaleString() // 1000단위 콤마 추가
        );
        calcTotalPrice();
    });

    // 구매수량 + 버튼
    $(".plus").on("click", function (e) {
        e.preventDefault();
        let $input = $(this).prev("input");
        let maxValue = parseInt($input.attr("max"));
        if (parseInt($input.val()) < maxValue) {
            $input.val(parseInt($input.val()) + 1);
        } else {
            showMessage("quantityMessage", "최대 구매 수량을 초과하였습니다.(재고수량 이내 & 최대 10개 구매)");
        }
        let $orgPrice = $(this).parents('.calcItem').find('.orgPrice').val();
        let $finalPrice = $(this).parents('.calcItem').find('.finalPrice').val();
        let $sumOrgPrice = $(this).parents('.calcItem').find('.sumOrgPrice');
        let $sumFinalPrice = $(this).parents('.calcItem').find('.sumFinalPrice');
        $sumOrgPrice.val($orgPrice * $input.val());
        $sumFinalPrice.val($finalPrice * $input.val());

        let $sumDiscPrice = $sumFinalPrice.val() - $sumOrgPrice.val();
        $(this).parents('.calcItem').find('.sumDiscPrice').val($sumDiscPrice);  //아이템당 총 할인금액

        $(this).parents('.calcItem').find('.prodTotalPrice').text(
            Number($sumFinalPrice.val()).toLocaleString() // 1000단위 콤마 추가
        );
        $(this).parents('.calcItem').find('.prodOrgPrice').text(
            Number($sumOrgPrice.val()).toLocaleString() // 1000단위 콤마 추가
        );
        calcTotalPrice();
    });


}); // $(function(){}); jQuery END
/** order_cart : totalPriceCard : 총 구매 금액 calculate **/
/** order_cart : totalPriceCard : 총 구매 금액 calculate **/

function updateItemPriceInput() {
    $(".calcItem").each(function () {
        let $input = $(this).find('.quantity')
        let $orgPrice = $(this).find('.orgPrice').val();
        let $finalPrice = $(this).find('.finalPrice').val();
        let $sumOrgPrice = $(this).find('.sumOrgPrice');
        let $sumFinalPrice = $(this).find('.sumFinalPrice');
        $sumOrgPrice.val($orgPrice * $input.val());
        $sumFinalPrice.val($finalPrice * $input.val());

        let $sumDiscPrice = $sumFinalPrice.val() - $sumOrgPrice.val();
        $(this).find('.sumDiscPrice').val($sumDiscPrice);  //아이템당 총 할인금액

        $(this).find('.prodTotalPrice').text(
            Number($sumFinalPrice.val()).toLocaleString() // 1000단위 콤마 추가
        );
        $(this).find('.prodOrgPrice').text(
            Number($sumOrgPrice.val()).toLocaleString() // 1000단위 콤마 추가
        );
        calcTotalPrice();
    })
}

/** order_cart : item input checkbox 전체선택 연동 **/
/** order_cart : item input checkbox 전체선택 연동 **/
$(function () {

    //체크박스 : 전체 선택,해제
    $('#checkAll').on('change', function () {
        $(".cartItemTable input[type='checkbox']").prop("checked", this.checked)
        calcTotalPrice();
    })

    //체크박스 : 부분 선택,해제
    $('.cartItemTable .form-check-input').on('change', function () {
        // 나머지 체크박스가 모두 체크되었는지 확인
        let allChecked = $('.cartItemTable .itemCheck:checked').length === $('.cartItemTable .itemCheck').length;
        $('#checkAll').prop('checked', allChecked);
        calcTotalPrice();
    });


    if ($('#quantity').val() == 1) {
        const minusBtnIcon = $('.minus i');
        minusBtnIcon.removeClass('fa-minus fa-solid ');
        minusBtnIcon.addClass('fa-trash-can  fa-regular opacity-50')
    }

    // 수량이 1이면 -버튼이 휴지통 아이콘
    $('.minus').on('click', function () {
        const minusBtnIcon = $('.minus i');
        if ($('#quantity').val() == 1) {
            minusBtnIcon.toggleClass('fa-minus fa-trash-can fa-solid fa-regular opacity-50');
        } else if ($(this).find(".fa-trash-can")) {

        }
    })

    $('.plus').on('click', function () {
        const minusBtnIcon = $('.minus i');
        if ($('#quantity').val() == 2) {
            minusBtnIcon.toggleClass('fa-minus fa-trash-can fa-solid fa-regular opacity-50');
        }
    })

})


/** order_cart : 총 금액 재계산 **/
function calcTotalPrice() {
    let totalOrgPrice = 0; //상품금액
    let totalFinalPrice = 0; //합계금액(최종상품금액)
    let totalDiscPrice = 0; //할인금액
    let deliveryFee = 0; //배송비
    let totalFinalCalcedPrice = 0; //최종결제금액

    $("input.itemCheck:checked").each(function () {
        let $itemId = $(this).parents(".card-body").find(".itemId");
        let $quantity = $(this).parents(".card-body").find(".quantity").val();

        totalOrgPrice += (Number($itemId.attr('data-org-price')) * $quantity);
        totalFinalPrice += ($itemId.attr('data-final-price') * $quantity);
    })

    totalDiscPrice = totalFinalPrice - totalOrgPrice; // 할인금액

    //50000원 이상 배송비 무료
    if (totalFinalPrice < 50000 && totalFinalPrice > 0) deliveryFee = 3000;
    else deliveryFee = 0;

    totalFinalCalcedPrice = totalFinalPrice + deliveryFee;

    //출력
    $(".totalFinalPrice").text(Number(totalFinalPrice).toLocaleString()); //카드푸터
    $(".totalOrgPrice").text(Number(totalOrgPrice).toLocaleString()); //
    if (totalDiscPrice == 0) {
        $(".totalDiscPrice").text("-");
        $(".totalDiscPrice").next('.won').text('');
    } else {
        $(".totalDiscPrice").text(Number(totalDiscPrice).toLocaleString());
        $(".totalDiscPrice").next('.won').text('원');
    }

    if (deliveryFee == 0) {
        $(".deliveryFee").text("-");
        $(".deliveryFee").next('.won').text('');
    } else {
        $(".deliveryFee").text(Number(deliveryFee).toLocaleString()); //
        $(".deliveryFee").next('.won').text('원');
    }
    $(".totalFinalCalcedPrice").text(Number(totalFinalCalcedPrice).toLocaleString()); //

    updateCheckedItem();
}


$(function () {

    //카트 아이템 x버튼 클릭
    $("i.removeItem").on("click", function () {
        let prodId = Number($(this).siblings(".itemId").val());
        removeAtCart(prodId);
    })

})

//장바구니 상품 제거
function removeAtCart(prodId) {
    const removeItem = {
        prodId: prodId
    };
    fetch('/cart/data/delete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(removeItem)
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            removeItemDOM(prodId);
            updateCartBadge(); // 아이템 개수 업데이트 호출
            calcTotalPrice();
        })
        .catch(error => console.error('Error:', error));
}

//카트 아이템 DOM 제거
function removeItemDOM(prodId) {
    $(".itemId").each(function () {
        if ($(this).val() == prodId) {
            $(this).parents(".card-body").remove();
        }
    });

    checkCartEmpty();
}

// 장바구니 비었는지 확인 후 DOM 업데이트
function checkCartEmpty() {
    fetch('/cart/data/item-count', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.cartItemCount == 0) {
                // 장바구니 비었을 때 처리
                var vacantCartDOM = `
                <div class="card-body border-bottom d-flex gap-3 justify-content-center " >
                    <div class="row">
                        <div class="col-auto align-items-center justify-content-center d-flex flex-column">
                            <div class="vacantCart "></div>
                            <p class="my-3 f24 fw700 c555">장바구니에 상품을 추가해주세요</p>
                            <p><a href="/shop/list" class="text-success text-decoration-underline">쇼핑하러가기</a></p>
                        </div>
                    </div>
                </div>
            `;
                $(".cartItemTable .card-header").after(vacantCartDOM);
            }
        })
        .catch(error => console.error('Error:', error));
}


//카트 아이템 리스트 카드헤더 : 선택개수 / 전체개수 업데이트
function updateCheckedItem() {
    $(".printCheckedNumber #checkedItem").text($(".itemCheck:checked").length);
    $(".printCheckedNumber #allItem").text($(".itemCheck").length);
}

//배송지
$('input.addr').on('change', function () {
    updateAddr();
});

function updateAddr() {
    $("div.addr").text($("input.addr").val());
}

function sample4_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 도로명 주소의 노출 규칙에 따라 주소를 표시한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var roadAddr = data.roadAddress; // 도로명 주소 변수
            var extraRoadAddr = ''; // 참고 항목 변수

            // 법정동명이 있을 경우 추가한다. (법정리는 제외)
            // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
            if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                extraRoadAddr += data.bname;
            }
            // 건물명이 있고, 공동주택일 경우 추가한다.
            if (data.buildingName !== '' && data.apartment === 'Y') {
                extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
            }
            // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
            if (extraRoadAddr !== '') {
                extraRoadAddr = ' (' + extraRoadAddr + ')';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('sample4_postcode').value = data.zonecode;
            document.getElementById("sample4_roadAddress").value = roadAddr;
            document.getElementById("sample4_jibunAddress").value = data.jibunAddress;

            // 참고항목 문자열이 있을 경우 해당 필드에 넣는다.
            if (roadAddr !== '') {
                document.getElementById("sample4_extraAddress").value = extraRoadAddr;
            } else {
                document.getElementById("sample4_extraAddress").value = '';
            }

            var guideTextBox = document.getElementById("guide");
            // 사용자가 '선택 안함'을 클릭한 경우, 예상 주소라는 표시를 해준다.
            if (data.autoRoadAddress) {
                var expRoadAddr = data.autoRoadAddress + extraRoadAddr;
                guideTextBox.innerHTML = '(예상 도로명 주소 : ' + expRoadAddr + ')';
                guideTextBox.style.display = 'block';

            } else if (data.autoJibunAddress) {
                var expJibunAddr = data.autoJibunAddress;
                guideTextBox.innerHTML = '(예상 지번 주소 : ' + expJibunAddr + ')';
                guideTextBox.style.display = 'block';
            } else {
                guideTextBox.innerHTML = '';
                guideTextBox.style.display = 'none';
            }
        }
    }).open();
}

function sample4_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            document.getElementById('sample4_postcode').value = data.zonecode;
            document.getElementById('sample4_roadAddress').value = data.roadAddress;
            document.getElementById('sample4_jibunAddress').value = data.jibunAddress;
            setTimeout(function () {
                document.getElementById('sample4_detailAddress').value = '';
                document.getElementById('sample4_detailAddress').focus();
            }, 0);
        }
    }).open();
}

// cart -> order페이지로 submit시 : 체크되지 않은 아이템 리스트는 서버 전송 x
const orderForm = document.querySelector('#orderForm');

if (orderForm) {
    orderForm.addEventListener('submit', function (event) {
        document.querySelectorAll('.itemCheck:not(:checked)').forEach(function (checkbox) {
            const parent = checkbox.closest('.card-body');
            parent.querySelectorAll('input').forEach(function (input) {
                input.disabled = true;
            });
        });
    });
}


/*마일리지 버튼*/
$(function () {

    $("#milePay").on("change", function () {

        let totalPrice = parseInt($("#totalPrice").val());

        //마일리지 입력금액이 구매할 금액보다 크면 마일리지 적용금액 재계산
        if (parseInt($("#milePay").val()) > totalPrice) {
            $("milePay").val(totalPrice);
        }


        //적용 공통 계산
        $("span.milePay").attr("data-nums", $("#milePay").val() * (-1));
        console.log($("span.milePay").attr("data-nums"));


        $("span.milePay").text(formatNumberWithComma($("span.milePay").attr("data-nums")));
        console.log($("span.milePay").text());


        var totalPayValue = parseInt($("#totalPrice").val()) + parseInt($("span.milePay").attr("data-nums"))
        console.log(totalPayValue);

        $("#totalPay").val(totalPayValue);
        $("#totalPay").attr("data-nums", totalPayValue);

        $("span.totalPay").attr("data-nums", totalPayValue);
        $("span.totalPay").text(formatNumberWithComma($("span.totalPay").attr("data-nums")));


        $("span.totalPay").val(totalPayValue);


        $("#totalPay").attr("data-nums", $("#milePay").val() * (-1));
        console.log($("span.milePay").attr("data-nums"));
        $("span.milePay").text(formatNumberWithComma($("span.milePay").attr("data-nums")));
        console.log($("span.milePay").text());

    })
})

/*마일리지 : 모두 사용 버튼*/
function useAllMileage() {
    let finalPrice = $('#finalPrice').attr('data-nums');
    let mileage = $("#mileage").attr('data-nums');

    console.log("-====================")
    console.log("finalPrice : ")
    console.log(parseInt(finalPrice));
    console.log("-====================")
    console.log("mileage : ")
    console.log(parseInt(mileage));
    console.log("-====================")


    let milePay = $("#milePay");

    console.log(milePay);
    // 할인된 상품 가격 내에서 마일리지 사용가능
    if (parseInt(mileage) > parseInt(finalPrice)) {
        console.log("트루다")
        milePay.val(parseInt(finalPrice));
    } else {
        milePay.val(parseInt(mileage));
        console.log("트루아님")
    }


    $("#milePay").change();
}


/*orderHistory : 주문 취소 */
function cancelRequest(cancelBtn) {
    const orderId = $(cancelBtn).attr("data-orderId")
    console.log("Order ID:", orderId);

    const cancelReason = prompt("주문 취소 사유를 입력해주세요.");

    if (!cancelReason) return false;


    const cancelRequest = {
        orderId: orderId,
        cancelReason: cancelReason,
    };
    fetch('/api/orders/cancelRequest', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(cancelRequest)
    }).then(response => {
        if (response.ok) {
            alert("취소 신청 승인 후 결제 취소가 진행됩니다.")
            location.reload();
            //return "redirect:/order/po/detail/"+orderId;
        }
        //
    })
}




/***************** admin : po_list ******************/
/***************** admin : po_list ******************/
/***************** admin : po_list ******************/
document.addEventListener("DOMContentLoaded", function () {
    // #orderTable이 문서에 있는지 확인
    if (document.querySelector('#orderTable')) {
        // 현재 달의 1일과 오늘 날짜를 기본값으로 설정
        const today = new Date();
        const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

        // 날짜를 'YYYY-MM-DD' 형식으로 변환하는 함수
        document.getElementById('startDate').value = formatDateForInput(firstDayOfMonth);
        document.getElementById('endDate').value = formatDateForInput(today);

        // 페이지 로드 시 주문 목록 불러오기
        fetchOrders();
    }
});



async function fetchOrders() {
    // 검색어와 상태 필터 값 가져오기
    const search = document.getElementById("searchInput").value;
    const status = document.getElementById("statusSelect").value;

    // 시작 날짜와 끝 날짜 값 가져오기
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    // Fetch API를 사용하여 서버에서 데이터 가져오기
    const response = await fetch(`/api/orders?search=${search}&status=${status}&startDate=${startDate}&endDate=${endDate}`);
    const data = await response.json();

    // 테이블에 주문 목록 렌더링
    const orderTableBody = document.getElementById("orderTableBody");
    orderTableBody.innerHTML = ""; // 기존 내용 제거

    data.orders.forEach((order,index) => {
        // 상태에 따라 배경색 클래스를 동적으로 설정
        let rowClass = '';
        switch (order.status) {
            case '상품준비중':
                rowClass = 'table-light';  // 파란색
                statClass = 'text-primary fw700';
                break;
            case '배송중':
                rowClass = 'table-info';     // 밝은 파란색
                statClass = 'c333 fw700';
                break;
            case '배송완료':
                rowClass = 'table-success';  // 녹색
                statClass = 'c333 fw700';
                break;
            case '주문취소':
                rowClass = 'table-danger';   // 빨간색
                statClass = 'text-danger fw700';
                break;
            case '취소승인대기중':
                rowClass = 'table-warning';  // 노란색
                statClass = 'text-warning fw700';
                break;
            default:
                rowClass = ''; // 기본값 (특별한 배경색 없음)
        }
        console.log(order.status);

        const row = `
            <tr class="${rowClass} orderRow">
                <td>${index+1}</td>
                <td>${formatDateTime(order.createdAt)}</td>
                <td><a href="/order/admin/po/${order.orderId}" class="${statClass} tableLink">${order.orderId}</a></td>
                <td>${order.memberId}</td>
                <td class="${statClass}">${order.status}</td>
                <td class="text-end me-5">${formatCurrency(order.totalPay)}</td>
            </tr>
        `;
        orderTableBody.insertAdjacentHTML('beforeend', row);
    });
}
















//OrderStatus Enum 한글명 매핑 함수
function toKoreanOrderStatus(orderStatus) {
    const statusMap = {
        "PAYMENT_WAIT": "결제대기",
        "PRODUCT_PREPARE": "상품준비중",
        "DELIVERING": "배송중",
        "COMPLETED": "배송완료",
        "CANCELLED": "주문취소",
        "WAITING_CANCEL": "취소승인대기중"
    };

    return statusMap[orderStatus] || orderStatus;
}


//오류 메시지 박스 초기화
$(function (){
    const messageBox = $(".messageBox");
    if (messageBox){
        messageBox.css("visibility", "hidden")
    }
})

//오류 메시지 박스 show
function showMessage(id, message) {
    let $messageBox = $("#" + id);  // 전달받은 id로 메시지 박스를 선택
    $messageBox.text(message).css("visibility", "visible");  // 메시지 박스에 텍스트 추가 및 표시

    let timeoutId = $messageBox.data("timeoutId");

    // 기존 타이머가 있을 경우 취소
    if (timeoutId) {
        clearTimeout(timeoutId);
    }

    // 새로운 타이머 시작
    timeoutId = setTimeout(function() {
        $messageBox.css("visibility", "hidden");  // 2초 후에 메시지 박스를 사라지게 함
        $messageBox.removeData("timeoutId");  // 타이머 ID 삭제
    }, 2000);  // 2초(2000밀리초)

    // 타이머 ID를 메시지 박스에 저장
    $messageBox.data("timeoutId", timeoutId);
}




// 날짜 포맷팅 함수 (YYYY-MM-DD 형식으로 변환)
function formatDateForInput(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}




// 날짜 포맷팅 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}. ${month}. ${day}.`;
}

// 날짜 시간 포맷팅 함수
function formatDateTime(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}. ${month}. ${day}. ${hour}:${minute}:${seconds}`;
}

//통화 포맷팅 함수
function formatCurrency(amount){
    return `${amount.toLocaleString('ko-KR')}원`;
}

//연락처 포맷팅 함수
function formatPhone(phoneNumber){
    var formated = "";
    if (phoneNumber.length >= 11){
        formated = phoneNumber.slice(0,3)+"-"+phoneNumber.slice(3,7)+"-"+phoneNumber.slice(7);
    } else {
        formated = phoneNumber.slice(0,3)+"-"+phoneNumber.slice(3,6)+"-"+phoneNumber.slice(6);
    }
    return formated;
}
