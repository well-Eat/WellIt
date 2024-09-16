
// 구매자 정보

const final_pay = parseInt( $("input#totalPay").val() );




var IMP = window.IMP;

var today = new Date();
var hours = today.getHours(); // 시
var minutes = today.getMinutes();  // 분
var seconds = today.getSeconds();  // 초
var milliseconds = today.getMilliseconds();
var makeMerchantUid = `${hours}` + `${minutes}` + `${seconds}` + `${milliseconds}`;


// 결제창 함수 넣어주기
const user_email = $("#memberEmail").text();
const username = $("#memberName").text();
const buyButton = document.getElementById('payment')
buyButton.setAttribute('onclick', `kakaoPay('${user_email}', '${username}')`)


async function kakaoPay(useremail, username) {
    const orderId = $("#orderId").val();

    if (confirm("구매 하시겠습니까?")) {
        IMP.init("imp51662258"); // 가맹점 식별코드
        IMP.request_pay({
            pg: 'kakaopay.TC0ONETIME',
            pay_method: 'card',
            merchant_uid: "IMP" + makeMerchantUid,
            name: 'Wellit Shop',
            amount: final_pay,
            buyer_email: useremail,
            buyer_name: username,
        }, async function (rsp) {
            if (rsp.success) {
                console.log("결제 성공", rsp);
                console.log(orderId);

                // 결제 성공시 서버로 데이터 전송
                try {
                    const response = await fetch(`/api/payment/save/${orderId}`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Access-Control-Allow-Origin': '*',
                            crossDomain : true,
                        },
                        body: JSON.stringify({
                            imp_uid: rsp.imp_uid, // 아임포트에서 제공한 거래 고유 번호
                            merchant_uid: rsp.merchant_uid, // 주문 번호
                            paid_amount: rsp.paid_amount, // 결제 금액
                            buyer_email: useremail, // 구매자 이메일
                            buyer_name: username, // 구매자 이름
                            status: rsp.status, // 결제 상태
                            success: rsp.success, // 결제 성공 여부
                            pay_method: rsp.pay_method,
                            pg_provider: rsp.pg_provider,
                        }),
                        credentials: 'same-origin'
                    });

                    if (response.ok) { // DB 저장 성공시
                        alert('결제가 완료되었습니다!');
                        document.getElementById("poForm").submit();
                    } else {
                        const errorData = await response.json();
                        alert(`결제는 성공했지만, 서버에 저장 중 오류가 발생했습니다: ${errorData.message}`);
                    }
                } catch (error) {
                    alert('결제 처리 중 문제가 발생했습니다. 관리자에게 문의하세요.');
                }
            } else {
                alert(`결제 실패: ${rsp.error_msg}`);
            }
        });
    } else {
        return false; // 결제 취소
    }
}


async function cancelPayment(impUid, merchantUid, reason) {
    if (confirm("결제를 취소하시겠습니까?")) {
        try {
            const response = await fetch(`/api/payment/cancel`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    imp_uid: impUid, // 아임포트 거래 고유 번호
                    merchant_uid: merchantUid, // 주문 번호
                    reason: reason // 취소 사유
                })
            });

            if (!response.ok) {
                throw new Error('결제 취소 중 오류 발생');
            }

            const result = await response.json();
            alert('결제가 성공적으로 취소되었습니다!');

            // 필요시 추가 작업 수행 (예: 페이지 새로고침)
            // location.reload();

        } catch (error) {
            alert('결제 취소 처리 중 문제가 발생했습니다. 관리자에게 문의하세요.');
        }
    } else {
        return false; // 취소 거부
    }
}

// 예시로 결제 취소 버튼에 이벤트 추가
const cancelButton = document.getElementById('cancelPaymentButton');
cancelButton.addEventListener('click', () => {
    const impUid = 'imp_864279579878';  // 실제 imp_uid 값
    const merchantUid = 'IMP215435871'; // 실제 merchant_uid 값
    const reason = prompt("결제 취소 사유를 입력해 주세요.");
    if (reason) {
        cancelPayment(impUid, merchantUid, reason);
    }
});








