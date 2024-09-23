/********* shop_list : 상품 리스트 페이지 *************/
/********* shop_list : 상품 리스트 페이지 *************/
/********* shop_list : 상품 리스트 페이지 *************/
$(function(){

    /* 카테고리 아이템 리스트 get */
    $("a.prodCateLink").on("click", function(e) {
        e.preventDefault();

        //카테고리 선택상자의 id와 아이템카드의 data-prodcate의 값이
        //같은 경우 display:block;
        let clickedCate = $(this).parent().attr('id');
        let allList = $(".prodItem");

        if (clickedCate === 'all') {
            // 모든 항목을 표시
            allList.removeClass('d-none');
        } else {
            // 선택된 카테고리만 표시하고, 나머지는 숨김
            allList.each(function() {
                let prod = $(this);
                let prodCate = prod.attr('data-prodcate');

                if (prodCate === clickedCate) {
                    prod.removeClass('d-none');
                } else {
                    prod.addClass('d-none');
                }
            });
        }

        // 모든 카테고리의 active 클래스 제거하고 클릭된 항목의 부모 li 요소에만 추가
        $(".prodCate").removeClass('active');
        $(this).parent().addClass('active');
    });
});

/***************** shop_detail : 상품 상세페이지 *****************/
/***************** shop_detail : 상품 상세페이지 *****************/
/***************** shop_detail : 상품 상세페이지 *****************/


/** shop_detail : section.prodInfo : 총 구매 금액 calculate : START **/
/** shop_detail : section.prodInfo : 총 구매 금액 calculate : START **/
$(function () {
    const finalPrice = parseFloat(document.getElementById('finalPrice').value);
    let totalPrice = finalPrice;

    // 구매수량 - 버튼
    $(".minus").on("click", function (e) {
        e.preventDefault();
        let $input = $(this).next("input");
        let minValue = parseInt($input.attr("min"));
        if (parseInt($input.val()) > minValue) {
            $input.val(parseInt($input.val()) - 1);
        } else {
            alert("최소 구매 수량은 1개 입니다.");
        }
        updateTotalPrice(finalPrice, $input.val());
        console.log(totalPrice);
    });

    // 구매수량 + 버튼
    $(".plus").on("click", function (e) {
        e.preventDefault();
        let $input = $(this).prev("input");
        let maxValue = parseInt($input.attr("max"));
        if (parseInt($input.val()) < maxValue) {
            $input.val(parseInt($input.val()) + 1);
        } else {
            alert("최대 구매 수량을 초과하였습니다.");
        }
        updateTotalPrice(finalPrice, $input.val());
    });

    function updateTotalPrice(finalPrice, quantity) {
        totalPrice = finalPrice * quantity;
        $(".prodTotalPrice").text(totalPrice.toLocaleString());
    }
}); // $(function(){}); jQuery END
/** shop_detail : section.prodInfo : 총 구매 금액 calculate : **/
/** shop_detail : section.prodInfo : 총 구매 금액 calculate : **/


/** shop_detail > shop_imgReview : section.imgReviewList : 이미지 리뷰 라인 : START **/
/** shop_detail > shop_imgReview : section.imgReviewList : 이미지 리뷰 라인 : START **/
$(function () {
        // Swiper 초기화
        var swiper = new Swiper(".imgRevSwiper", {
            lazy: true,
            centeredSlides: false,
            slidesPerView: 7,
            slidesPerGroup: 7,
            spaceBetween: 10,
            breakpoints: {
                768: {
                    slidesPerView: 6,
                    slidesPerGroup: 6,
                },
                1200: {
                    slidesPerView: 7,
                    slidesPerGroup: 7,
                    spaceBetween: 10,
                },
            },
            navigation: {
                nextEl: ".swiper-button-next",
                prevEl: ".swiper-button-prev",
            },
        });


    // 초기 이미지 리뷰 버튼 위치 설정
    updateImgRevPos();

    // 윈도우 리사이즈 시 이미지 리뷰 버튼 위치 업데이트
    $(window).on("resize", function () {
        updateImgRevPos();
    });

    // 이미지 리뷰 링크 클릭 이벤트 핸들러
    $('.revImgLink').on('click', function () {
        // 기존에 생성된 모달이 있으면 제거
        $('#dynamicModal').remove();

        // 이미지 및 리뷰 데이터를 가져옴
        const revImg = $(this).data('rev-img');
        const revText = $(this).data('rev-text');
        const revRating = convertStarRating($(this).data('rev-rating'));
        const revCreated = formatDate($(this).data('rev-created'));
        const revWriter = $(this).data('rev-writer');

        // 동적으로 모달 생성
        const modalHtml = `
            <div class="modal fade" id="dynamicModal" tabindex="-1" aria-labelledby="imgRevModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5 fw700" id="imgRevModalLabel">사진후기</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body container-fluid">
                            <div class="row">
                                <div class="col-6 modalColLeft text-center">
                                    <img src="${revImg}" alt="Review Image" class="img-fluid rounded-2">
                                </div>
                                <div class="col-6 modalColRight">
                                    <p class="revWriter">${revWriter}</p>
                                    <p class="revCreated">${revCreated}</p>
                                    <p class="revRating">${revRating}</p>
                                    <p class="revText">${revText}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>`;

        // body에 동적으로 모달 추가
        $('body').append(modalHtml);

        // 새로 생성된 모달 초기화
        const dynamicModal = new bootstrap.Modal(document.getElementById('dynamicModal'));
        dynamicModal.show();
    });


}); // $(function(){}); jQuery END

// 이미지 리뷰 버튼 위치 업데이트 함수
function updateImgRevPos() {
    var imgBoxHeight = $(".revImgBox").height();
    const controller = $(".shopImgRev.swiper-controller");
    controller.css("top", imgBoxHeight / 2);
}

/** shop_detail > shop_imgReview : section.imgReviewList : 이미지 리뷰 라인 :  END **/
/** shop_detail > shop_imgReview : section.imgReviewList : 이미지 리뷰 라인 :  END **/


/** shop_detail : section.prodReview : 리뷰리스트 출력 : START **/
/** shop_detail : section.prodReview : 리뷰리스트 출력 : START **/
$(function () {

    const prodId = document.getElementById('prodId').value;

    loadReviews(prodId, curRevPage);


    // 리뷰 페이지 버튼 클릭 이벤트 핸들러
    $('.pagePrev').click(function () {
        if (curRevPage > 0) {
            curRevPage--;
            loadReviews(prodId, curRevPage);
        }
    });

    $('.pageNext').click(function () {
        if (curRevPage < totalRevPages - 1) {
            curRevPage++;
            loadReviews(prodId, curRevPage);
        }
    });


    $(document).on('click', '.modal-footer .revImgBox', function () {
        const clickedRevImgBox = $(this);
        clickedRevImgBox.closest('.modal-content').find('.modalColLeft').find('img').attr('src', clickedRevImgBox.data('rev-img'));
        // 큰 이미지의 썸네일 이미지를 강조표시

        $('.modal-footer .revImgBox').removeClass('highlight');
        $(this).addClass('highlight');
        //highlightThumb();
    });

}); // $(function(){}); jQuery END

let curRevPage = 0;
let totalRevPages = 0;

// 리뷰 로드를 위한 함수
async function loadReviews(prodId, revPage) {
    try {
        // 서버로 GET 요청 보내기
        const response = await fetch(`/shop/review/${prodId}/${revPage}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // 응답을 JSON으로 변환
        const data = await response.json();

        // 리뷰 리스트를 화면에 렌더링
        renderReviews(data.reviews);

        // 페이징 정보 업데이트
        totalRevPages = data.totalPages;
        updateButtons();
    } catch (error) {
        console.error('Failed to fetch reviews:', error);
    }
}

// 리뷰 페이지 버튼 상태 업데이트 함수
function updateButtons() {
    if (curRevPage <= 0) {
        $('.pagePrev').attr('disabled', true);
    } else {
        $('.pagePrev').attr('disabled', false);
    }

    if (curRevPage >= totalRevPages - 1) {
        $('.pageNext').attr('disabled', true);
    } else {
        $('.pageNext').attr('disabled', false);
    }
}

// 페이지에 리뷰 리스트를 렌더링하는 함수
function renderReviews(reviews) {
    const reviewBlock = $('.reviewBlock');
    reviewBlock.empty();

    reviews.forEach(function (review) {
        let imgHtml = '';

        // 이미지가 있는 경우 각 이미지를 80px × 80px 크기로 출력
        if (review.prodReviewImgList && review.prodReviewImgList.length > 0) {
            imgHtml = '<div class="row revImgRow justify-content-start my-3">';
            review.prodReviewImgList.forEach(function (imgData) {
                imgHtml += `
                    <div class="col-auto">
                        <div class="revImgBox rounded-2  border" 
                             style="width: 80px; height: 80px; background-image: url(${imgData}); background-size: cover; background-position: center;"
                             data-rev-img="${imgData}" data-rev-text="${review.revText}" data-rev-rating="${review.revRating}" data-rev-created="${review.createdAt}" data-rev-writer="${review.writer}">
                        </div>
                    </div>`;
            });
            imgHtml += '</div>';
        }

        const reviewHtml = `
            <div class="row border-bottom py-2">
                <div class="col-md-2 writer mt-3">${review.writer}</div>
                <div class="col-md-10">
                    ${imgHtml} <!-- 이미지가 있을 경우 표시 -->
                    <div class="row revText justify-content-start mb-3">
                        <div class="col">${review.revText}</div>
                    </div>
                    <div class="row createdAt justify-content-start mb-3">
                        <div class="col"><small>${formatDate(review.createdAt)}</small></div>
                    </div>
                </div>
            </div>`;
        reviewBlock.append(reviewHtml);
    });

    // 이미지 클릭 시 모달을 동적으로 생성하여 표시
    $('.reviewBlock .revImgBox').on('click', function (e) {
        const revImg = $(this).data('rev-img');
        const revText = $(this).data('rev-text');
        const revRating = convertStarRating($(this).data('rev-rating'));
        const revCreated = formatDate($(this).data('rev-created'));
        const revWriter = $(this).data('rev-writer');


        const revImgList = $(this).closest('.revImgRow').find('.revImgBox');

        console.log($(this).closest('.revImgRow').find('.revImgBox'));

        console.log(revImgList)
        console.log(revImgList.length);

        // 기존에 생성된 모달이 있으면 제거
        $('#dynamicModal').remove();

        let modalHtml = `
            <div class="modal fade" id="dynamicModal" tabindex="-1" aria-labelledby="imgRevModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h1 class="modal-title fs-5 fw700" id="imgRevModalLabel">사진후기</h1>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body container-fluid">
                            <div class="row">
                                <div class="col-6 modalColLeft text-center">
                                    <img src="${revImg}" alt="Review Image" class="img-fluid rounded-2">
                                </div>
                                <div class="col-6 modalColRight">
                                    <p class="revWriter">${revWriter}</p>
                                    <p class="revCreated">${revCreated}</p>
                                    <p class="revRating">${revRating}</p>
                                    <p class="revText">${revText}</p>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer justify-content-center">
                            <div class="row revImgRow justify-content-start my-3">`;

        revImgList.each(function () {

            const $item = $(this);
            modalHtml += `
                                    <div class="col-auto">
                                        <div class="revImgBox rounded-2 border" style="width: 80px; height: 80px; 
                                            background-clip: border-box;
                                            background-image: url(${$item.data('rev-img')}); 
                                            background-size: cover; 
                                            background-position: center;"
                                            data-rev-img="${$item.data('rev-img')}" 
                                            data-rev-text="${$item.data('rev-text')}" 
                                            data-rev-rating="${$item.data('rev-rating')}" 
                                            data-rev-created="${$item.data('rev-created')}" 
                                            data-rev-writer="${$item.data('rev-writer')}"> 
                                        </div>
                                    </div>
                                `;

        });

        modalHtml += `
                        </div>
                    </div>
                </div>
            </div>`;


        // 동적으로 생성한 모달을 body에 추가
        $('body').append(modalHtml);

        const renderedImg = $('.modal-content .modalColLeft img');


        // 열려 있는 이미지의 썸네일 이미지에 강조 표시
        const thumbList = $('.modal-footer .revImgBox');
        thumbList.each(function () {
            const $item = $(this);

            if ($item.data('rev-img') == renderedImg.attr('src')) {
                $item.addClass('highlight');
            }
        })

        // 생성된 모달을 열기
        const dynamicModal = new bootstrap.Modal(document.getElementById('dynamicModal'));
        dynamicModal.show();

    });
}


/** shop_detail : section.prodReview : 리뷰리스트 출력 : END **/
/** shop_detail : section.prodReview : 리뷰리스트 출력 : END **/



/** shop_create : 상품 상세정보 입력 **/
/** shop_create : 상품 상세정보 입력 **/

/*재료 추가/제거*/
$(function () {


    $(document).on('click', '.removeRow', function () {
        $(this).closest('tr').remove();
        updateProdInfoIndices(); // 삭제 후 인덱스 업데이트
    });


    $(document).on('click', '.addRow', function () {
        const curIndex = $(this).index();
        const rowCount = $('#prodInfoList tr').length;
        const newRow = `
    <tr>
      <td><input type="button" class="form-control form-control-sm removeRow" value="(-)"></td>
      <td><input type="text" class="form-control form-control-sm infoKey" name="prodInfoList[${rowCount}].infoKey" placeholder="재료"></td>
      <td><input type="text" class="form-control form-control-sm infoValue" name="prodInfoList[${rowCount}].infoValue" placeholder="양"></td>
      <td><input type="button" class="form-control form-control-sm addRow" value="(+)"></td>
    </tr>`;
        // + 클릭 시 바로 다음에 행추가 & 키로 포커스 이동
        $('#prodInfoList tr').eq(curIndex).after(newRow);
        $('.infoKey').eq(curIndex + 1).focus();
        updateProdInfoIndices(); // 인덱스를 다시 업데이트
    });


});


// 재료 행 인덱스 업데이트 함수
function updateProdInfoIndices() {
    $('#prodInfoList tr').each(function (index) {
        $(this).find('.infoKey').attr('name', `prodInfoList[${index}].infoKey`);
        $(this).find('.infoValue').attr('name', `prodInfoList[${index}].infoValue`);
    });
}


/** shop_form : 상품 delete **/
/** shop_form : 상품 delete **/
function deleteProd() {
    if (prodName == null || prodName.length < 1) prodName = '해당';
    if (confirm(prodName + " 상품을 삭제하시겠습니까?")) {
        document.querySelector('#deleteForm').submit()
    }
    ;
}


// 상품 메인 이미지
const maxImages = 1;
let mainImages = [];

// 전체 문서에서 기본 드래그 앤 드롭 동작 방지
$(document).on('dragover drop', function (e) {
    e.preventDefault(); // 기본 동작 방지
});

$(document).ready(function () {
    // 파일 선택창 열기
    $(".mainImgFrame").click(function () {
        //$(this).find("input.prodMainImg").click();
    });


    // 파일 선택 시 미리보기 이미지 표시
    $(".prodMainImg").change(function (event) {
        let input = $(this);
        let parentDiv = input.closest('.mainImgFrame');

        // 파일이 있으면 처리
        if (event.target.files && event.target.files[0]) {
            let reader = new FileReader();

            reader.onload = function (e) {
                // 기존 이미지 제거 후 새 이미지 추가
                parentDiv.find('img').remove();
                parentDiv.append('<img src="' + e.target.result + '" alt="Image Preview">');

                // 삭제 버튼 보이기
                parentDiv.find('.remove-btn').show();
            };

            reader.readAsDataURL(event.target.files[0]);
        }
    });

    // 드래그 앤 드롭 이벤트 처리
    $('.mainProdCard .drop-area').on('dragover', function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).addClass('active');
    });

    $('.mainProdCard .drop-area').on('dragleave', function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('active');
    });

    $('.mainProdCard .drop-area').on('drop', function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('active');

        let files = e.originalEvent.dataTransfer.files;
        let emptyInput = $(".prodMainImg").filter(function () {
            return !this.files.length;
        }).first();

        if (emptyInput.length && files.length > 0) {
            emptyInput[0].files = files;

            let parentDiv = emptyInput.closest('.mainImgFrame');
            let reader = new FileReader();

            reader.onload = function (e) {
                // 기존 이미지 제거 후 새 이미지 추가
                parentDiv.find('img').remove();
                parentDiv.append('<img src="' + e.target.result + '" alt="Image Preview">');

                // 삭제 버튼 보이기
                parentDiv.find('.remove-btn').show();
            };

            reader.readAsDataURL(files[0]);
        }
    });

    // 이미지 삭제 버튼 클릭 시
    $(".mainImgFrame").on("click", ".remove-btn", function (e) {
        e.stopPropagation();

        let parentDiv = $(this).closest('.mainImgFrame');
        parentDiv.find('img').remove();
        parentDiv.find('.prodMainImg').val(''); // 파일 input 초기화
        $(this).hide(); // 삭제 버튼 숨기기
    });


});


// .prodImageDropArea와 관련된 코드 수정
$(document).on('dragover', '.prodImageDropArea', function (e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).addClass('highlight');
});

$(document).on('dragleave', '.prodImageDropArea', function (e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).removeClass('highlight');
});

$(document).on('drop', '.prodImageDropArea', function (e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).removeClass('highlight');

    const file = e.originalEvent.dataTransfer.files[0];

    // handleProdImage 함수 호출
    handleProdImage(file, $(this));

    // 파일 입력 필드에도 파일 설정
    const fileInput = $(this).closest('.card-body').find('.prodImageInput');

    // 파일 입력 필드 초기화
    fileInput.val('');

    const dataTransfer = new DataTransfer();
    dataTransfer.items.add(file);
    fileInput[0].files = dataTransfer.files;

    // 파일이 있을 때 삭제 버튼 표시
    showRemoveButton($(this));
});

$(document).on('change', '.prodImageInput', function () {
    handleProdImage(this.files[0], $(this).closest('.card-body').find('.prodImageDropArea'));

    // 파일이 있을 때 삭제 버튼 표시
    showRemoveButton($(this).closest('.prodImageDropArea'));
});

function handleProdImage(file, dropArea) {
    if (file instanceof Blob) {
        const reader = new FileReader();
        reader.onload = function (e) {
            const preview = dropArea.find('.preview');
            preview.empty();
            preview.append($('<img>').attr('src', e.target.result));
            const rmvBtn = $('<button>').addClass('remove-btn').attr({'type': 'button'}).text('-').css('display', 'inline-block');
            preview.append(rmvBtn);
        };
        reader.readAsDataURL(file);
    } else {
        console.error('The provided file is not a Blob.');
    }
}

// 파일이 있으면 삭제 버튼을 보여주는 함수
function showRemoveButton(dropArea) {
    const removeBtn = dropArea.find('.remove-btn');
    removeBtn.show();
}


// 이미지 삭제 버튼 클릭 시
$(document).on('click', '.remove-btn', function () {
    const dropArea = $(this).closest('.prodImageDropArea');
    const fileInput = dropArea.find('.prodImageInput');
    const imgSrc = $('.preview').find('img').attr('src')
    const toBeDeletedInput = $('#toBeDeleted');

    if (imgSrc.startsWith('/imgs/shop/product/')) {

        let toBeDeletedList = toBeDeletedInput.val() ? toBeDeletedInput.val().split(',') : [];
        toBeDeletedList.push(imgSrc);
        toBeDeletedInput.val(toBeDeletedList.join(','));  // 리스트를 문자열로 변환하여 hidden input에 저장
    }

    dropArea.find('.preview').empty();
    fileInput.val('');

    $(this).hide();
});


//상세이미지카드 추가 제거
$(function () {
    $(document).on('click', '.addCard', function (e) {
        addCard(e);
    });
    $(document).on('click', '.removeCard', function () {
        removeCard($(this));
    });
});

// 새로운 상세이미지 카드 추가 함수
function addCard(e) {
    const index = $('#prodImageCardList .prodImageCard').length;
    const newCard = $('.prodImageCard').first().clone();

    newCard.find('.prodImageInput').val('');
    newCard.find('.preview').empty();

    newCard.find('.prodImageNum').val(index + 1);
    newCard.find('.prodImageNum').attr('name', ``);
    newCard.find('.prodImageInput').attr('name', `prodImages[]`);
    newCard.find('.prodImageInput').attr('data-image-num', index + 1);

    var pos = e.target.closest('li.prodImageCard')
    $('#prodImageCardList').append(newCard);

    updateCardOrderNumbers(); // 순서 번호 업데이트
}

// 상세이미지 카드 제거 함수
function removeCard(button) {
    if ($('.prodImageCard').length > 1) {
        const removeBtn = button.closest('.prodImageCard').find('remove-btn');

        const imgSrc = button.closest('.prodImageCard').find('img').attr('src')
        const toBeDeletedInput = $('#toBeDeleted');

        // 기존 이미지 중 제거할 이미지 파일 목록을 hidden 으로 전송
        if (imgSrc.startsWith('/imgs/shop/product/')) {
            let toBeDeletedList = toBeDeletedInput.val() ? toBeDeletedInput.val().split(',') : [];
            toBeDeletedList.push(imgSrc);
            toBeDeletedInput.val(toBeDeletedList.join(','));
        }
        button.closest('.prodImageCard').remove();
        updateCardOrderNumbers();
    }
}

// 상세이미지 순서 번호 업데이트 함수
function updateCardOrderNumbers() {
    $('li.prodImageCard').each(function (index, card) {
        $(card).find('.prodImageNum').val(index + 1);
    });
}


/** 장바구니 상품 추가 **/
function addToCart(prodId, quantity) {
    const cartItem = {
        prodId: prodId,
        quantity: quantity
    };

    fetch('/cart/data/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(cartItem)
    })
        .then(response => response.json())
        .then(data => {
            console.log('Cart updated successfully', data);
            updateCartBadge();

            Swal.fire({
                title: '상품이 장바구니에 추가되었습니다!',
                text: "장바구니로 이동하시겠습니까?",
                icon: 'success',
                showCancelButton: true,
                confirmButtonText: '장바구니로 이동',
                cancelButtonText: '상품 목록',
                reverseButtons: true
            }).then((result) => {
                if (result.isConfirmed) {
                    // '장바구니로 이동'을 선택한 경우
                    window.location.href = '/cart/list';
                } else if (result.dismiss === Swal.DismissReason.cancel) {
                    const currentPath = window.location.pathname;

                    // 현재 페이지가 /shop/detail/~~~이면 /shop/list로 이동
                    if (currentPath.startsWith('/shop/detail/')) {
                        window.location.href = '/shop/list';
                    } else {
                        // 그 외의 경우 현재 페이지를 리로드
                        window.location.reload();
                    }
                }
            });

        })
        .catch(error => {
            console.error('Error:', error);
            Swal.fire('오류 발생', '상품을 장바구니에 추가하는 중 오류가 발생했습니다.', 'error');
        });
}


//찜한 상품 목록 가져오기
$(function (){
    const favoriteProdElem = $(".favoriteProduct");
    const memberId = $(".pageTitle.favoriteProduct").attr("data-member-id");

    if (favoriteProdElem) {
        fetchFavoriteProductList(memberId)
    }
})

//찜한 상품 목록 데이터 get
function fetchFavoriteProductList(memberId){
    fetch(`/shop/favorite/list/${memberId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log(data); // 서버에서 받은 데이터를 출력하여 확인
            renderFavoriteProducts(data); // 데이터 렌더링 함수 호출
        })
        .catch(error => {
            console.error('Error fetching favorite products:', error);
        });
}

// 찜한 상품 목록 렌더링 함수
function renderFavoriteProducts(products) {
    const favoriteList = $(".favoriteList");

    if (products.length > 0) {
        // 찜 목록이 있을 때 DOM에 렌더링
        products.forEach(product => {
            const productHTML = `
                <div class="prodItem col mb-4" data-prod-id="${product.prodId}">
                    <div class="card">
                        
                        <!-- 상품이미지 -->
                        <div class="card-img-wrapper rounded-2 mb-3">
                            <a href="/shop/detail/${product.prodId}" class="d-block h-100">
                                <div class="card-img-box" style="background-image: url('${product.prodMainImg}');" alt="${product.prodName}"></div>
                            </a>
                        </div>
                        
                        <div class="d-flex gap-1">
                            <!--찜하기버튼-->
                            <a href="javascript:void(0);" class="btn btn-outline-success heart removeFavoriteProductBtn" ><i class="fa-regular fa-heart" style="color: #6A994E;"></i></a>
                            <!-- 담기버튼 -->
                            <a href="javascript:void(0);" class="addCartBtn btn btn-outline-secondary flex-grow-1" onclick="addToCart(${product.prodId}, 1);"><i class="fa-solid fa-cart-shopping" style="color: #666;"></i> 담기</a>
                        </div>
                        
                        <!-- 상품설명 -->
                        <div class="card-body">
                            <a href="/shop/detail/${product.prodId}" class="d-block">
                                <h5 class="prodName card-title">${product.prodName}</h5>
                            </a>
                        </div> <!-- //card-body -->
                    </div> <!-- //card -->
                </div> <!-- ///////// prodItem -->
            `;
            favoriteList.append(productHTML);
            const heartBtn = $(".heart");
            heartBtn.css("backgroundColor", "#198754");
            heartBtn.find(".fa-heart").css("color", "#ffffff");
        });
    } else {
        // 찜 목록이 없을 때 처리
        favoriteList.html("<p>찜한 상품이 없습니다.</p>");
    }
}

//찜목록 해제
$(document).on("click", ".removeFavoriteProductBtn", function () {
    const prodItem = $(this).closest(".prodItem");
    const prodId = prodItem.attr("data-prod-id");
    const memberId = $(".pageTitle.favoriteProduct").attr("data-member-id");

    Swal.fire({
        title: '해당 상품을 찜 리스트에서 해제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '찜상품 해제',
        cancelButtonText: '취소',
    }).then((result) => {
        if (result.isConfirmed) {
            myFavoriteProduct(prodId, memberId, true);
        }
    });

    async function myFavoriteProduct(prodId, memberId, favorite) {
        console.log(prodId);
        console.log(memberId);
        const response = await fetch(`/shop/favorite/change?prodId=${prodId}&memberId=${memberId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({prodId: prodId, memberId: memberId})
        });

        if (response.ok) {
            const message = await response.text();  // response body의 텍스트를 가져옴
            console.log(message);  // 응답 메시지를 사용자에게 보여줌
            prodItem.remove();
            var cntFavorite = $(".prodItem").count();
            console.log(cntFavorite);

            if (cntFavorite==0 || cntFavorite ==null){
                const favoriteList = $(".favoriteList");
                // 찜 목록이 없을 때 처리
                favoriteList.html("<p>찜한 상품이 없습니다.</p>");
            }
        } else {
            const errorMessage = await response.text();  // 오류 메시지 표시
            if (errorMessage === "로그인해주세요") {
                alert(errorMessage);
                window.location.href = '/member/login';  // 로그인 페이지로 리다이렉트
            } else {
                alert(errorMessage);  // 기타 오류 메시지 표시
            }
        }
    }
})












/******* Common Util : END *********/
// 작성자 ID 마스킹 함수
function maskWriter(writer) {
    let maskedWriter = writer.slice(0, 2);
    for (let i = 0; i < writer.length - 2; i++) {
        maskedWriter += '*';
    }
    return maskedWriter;
}

// 날짜 포맷팅 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}. ${month}. ${day}.`;
}

// 평점을 별점으로 변환하는 함수
function convertStarRating(numRating) {
    numRating *= 2; // 5점 -> 10점 만점으로 환산
    numRating = Math.round(numRating);

    let vacant = 0, half = 0, filled = 0;

    if (numRating === 0) {
        vacant = 5;
    } else if (numRating % 2 === 0) {
        filled = numRating / 2;
        vacant = 5 - filled;
    } else {
        filled = (numRating - 1) / 2;
        half = 1;
        vacant = 5 - filled - half;
    }

    let star = '<i class="fas fa-star"></i>'.repeat(filled);
    if (half === 1) {
        star += '<i class="fas fa-star-half-alt"></i>';
    }
    star += '<i class="far fa-star"></i>'.repeat(vacant);

    return star;
}

function formatPhone(phoneNumber){
    var formated = "";
    if (phoneNumber.length >= 11){
        formated = phoneNumber.slice(0,2)+"-"+phoneNumber.slice(3,6)+"-"+phoneNumber.slice(7);
    } else {
        formated = phoneNumber.slice(0,2)+"-"+phoneNumber.slice(3,5)+"-"+phoneNumber.slice(6);
    }
    return formated;
}

/******* Common Util : END *********/



