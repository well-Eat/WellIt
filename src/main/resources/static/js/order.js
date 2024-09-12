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

function updateItemPriceInput(){
    $(".calcItem").each(function (){
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
    if (totalFinalPrice < 50000 && totalFinalPrice>0) deliveryFee = 3000;
    else deliveryFee = 0;

    totalFinalCalcedPrice = totalFinalPrice + deliveryFee;

    //출력
    $(".totalFinalPrice").text(Number(totalFinalPrice).toLocaleString()); //카드푸터
    $(".totalOrgPrice").text(Number(totalOrgPrice).toLocaleString()); //
    if (totalDiscPrice == 0){
        $(".totalDiscPrice").text("-");
        $(".totalDiscPrice").next('.won').text('');
    } else {
        $(".totalDiscPrice").text(Number(totalDiscPrice).toLocaleString());
        $(".totalDiscPrice").next('.won').text('원');
    }

    if (deliveryFee == 0){
        $(".deliveryFee").text("-");
        $(".deliveryFee").next('.won').text('');
    } else{
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
            updateCartBadge();
            calcTotalPrice();
        })


}


//카트 아이템 dom 제거
function removeItemDOM(prodId) {
    $(".itemId").each(function () {
        if ($(this).val() == prodId) {
            $(this).parents(".card-body").remove();
        }
    })
    console.log($(".cardItemTable").find(".card-body"))
    if ($(".cardItemTable").find(".card-body").length == 0){
        var vacantCartDOM = `
            <div class="card-body border-bottom d-flex gap-3 justify-content-center " >
                <div class="row">
                    <div class="col-auto align-items-center justify-content-center d-flex flex-column">
                        <div class="vacantCart "></div>
                        <p class="mt-3 f24 fw700 c555">장바구니에 상품을 추가해주세요</p>
                    </div>
                </div>
            </div>
        `;
        $(".cartItemTable .card-header").after(vacantCartDOM);
    }
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

/*// 전역 변수로 스크롤 위치를 저장할 변수를 선언
var savedScrollPosition = 0;

// 팝업을 열기 전에 현재 스크롤 위치를 저장
savedScrollPosition = window.pageYOffset || document.documentElement.scrollTop;

// 팝업이 닫힌 후 원래의 스크롤 위치로 이동
window.scrollTo(0, savedScrollPosition);*/

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
            setTimeout(function() {
                document.getElementById('sample4_detailAddress').value = '';
            document.getElementById('sample4_detailAddress').focus();
            }, 0);
        }
    }).open();
}

// cart -> order페이지로 submit시 : 체크되지 않은 아이템 리스트는 서버 전송 x
document.querySelector('#orderForm').addEventListener('submit', function(event) {
    document.querySelectorAll('.itemCheck:not(:checked)').forEach(function(checkbox) {
        const parent = checkbox.closest('.card-body');
        parent.querySelectorAll('input').forEach(function(input) {
            input.disabled = true;
        });
    });
});

/*마일리지 버튼*/
$(function (){

    $("#milePay").on("change", function (){

        let totalPrice = parseInt( $("#totalPrice").val());

        //마일리지 입력금액이 구매할 금액보다 크면 마일리지 적용금액 재계산
        if ( parseInt($("#milePay").val()) > totalPrice ){
            $("milePay").val(totalPrice);
        }


        //적용 공통 계산
        $("span.milePay").attr("data-nums", $("#milePay").val()*(-1));
        console.log($("span.milePay").attr("data-nums"));




        $("span.milePay").text(formatNumberWithComma( $("span.milePay").attr("data-nums") ));
        console.log($("span.milePay").text());


        var totalPayValue = parseInt( $("#totalPrice").val() ) + parseInt( $("span.milePay").attr("data-nums") )
        console.log(totalPayValue);

        $("#totalPay").val(totalPayValue);
        $("#totalPay").attr("data-nums", totalPayValue);

        $("span.totalPay").attr("data-nums", totalPayValue);
        $("span.totalPay").text(formatNumberWithComma( $("span.totalPay").attr("data-nums") ));



        $("span.totalPay").val(totalPayValue);


        $("#totalPay").attr("data-nums", $("#milePay").val()*(-1));
        console.log($("span.milePay").attr("data-nums"));
        $("span.milePay").text(formatNumberWithComma( $("span.milePay").attr("data-nums") ));
        console.log($("span.milePay").text());

    })
})

function useAllMileage(){
    let finalPrice = $('#finalPrice').attr('data-nums');
    let mileage = $("#mileage").attr('data-nums');

    console.log("-====================")
    console.log("finalPrice : ")
    console.log(parseInt( finalPrice));
    console.log("-====================")
    console.log("mileage : ")
    console.log( parseInt(mileage));
    console.log("-====================")


    let milePay = $("#milePay");

    console.log(milePay);
    // 할인된 상품 가격 내에서 마일리지 사용가능
    if (parseInt(mileage) > parseInt( finalPrice)) {
        console.log("트루다")
        milePay.val(parseInt( finalPrice));
    } else {
        milePay.val(parseInt( mileage));
        console.log("트루아님")
    }


    $("#milePay").change();


}















