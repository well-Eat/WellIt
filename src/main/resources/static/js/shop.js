/********* shop_list : 상품 리스트 페이지 *************/
/********* shop_list : 상품 리스트 페이지 *************/
/********* shop_list : 상품 리스트 페이지 *************/

$(function() {
    // 카테고리, 정렬, 페이지, 아이템 보기 개수
    let currentCategory = new URLSearchParams(window.location.search).get('category') || 'all';
    let currentOrder = new URLSearchParams(window.location.search).get('order') || 'default';
    let currentPage = new URLSearchParams(window.location.search).get('page') || 1;
    let currentSize = new URLSearchParams(window.location.search).get('size') || 20;
    let currentSearch = new URLSearchParams(window.location.search).get('search') || ''; // 검색어 추가

    if (currentSearch != null){
        $("#search-input").val(currentSearch);
    }

    // 카테고리 링크 클릭 이벤트 처리
    $('.prodCateLink').on('click', function(event) {
        event.preventDefault();
        const selectedCategory = $(this).data('category');
        const newUrl = `/shop/list?category=${selectedCategory}&order=${currentOrder}&page=${currentPage}&size=${currentSize}&search=${encodeURIComponent(currentSearch)}`;
        window.location.href = newUrl;
    });

    // 정렬 링크 클릭 이벤트 처리
    $('.sortLink').on('click', function(event) {
        event.preventDefault();
        const selectedOrder = $(this).data('order');
        const newUrl = `/shop/list?category=${currentCategory}&order=${selectedOrder}&page=${currentPage}&size=${currentSize}&search=${encodeURIComponent(currentSearch)}`;
        window.location.href = newUrl;
    });

    // 보기 개수 링크 클릭 이벤트 처리
    $('.pageSizeLink').on('click', function(event) {
        event.preventDefault();
        const selectedSize = $(this).data('size');
        const newUrl = `/shop/list?category=${currentCategory}&order=${currentOrder}&page=1&size=${selectedSize}&search=${encodeURIComponent(currentSearch)}`; // 새 사이즈 선택 시 1페이지로 이동
        window.location.href = newUrl;
    });

    // 페이지 번호 링크 클릭 이벤트 처리
    $('.pageLink').on('click', function(event) {
        event.preventDefault();
        const selectedPage = $(this).data('page');
        const newUrl = `/shop/list?category=${currentCategory}&order=${currentOrder}&page=${selectedPage}&size=${currentSize}&search=${encodeURIComponent(currentSearch)}`;
        window.location.href = newUrl;
    });

    // 검색 기능 추가
    $('#search-input').on('keypress', function(event) {
        if (event.which == 13) { // Enter 키가 눌렸을 때
            event.preventDefault();
            $('#searchBtn').trigger("click");
        }
    });
    $('#searchBtn').on('click', function (event){
        event.preventDefault();
        const searchKeyword = $('#search-input').val().trim(); // 입력된 검색어
        const newUrl = `/shop/list?category=${currentCategory}&order=${currentOrder}&page=1&size=${currentSize}&search=${encodeURIComponent(searchKeyword)}`;
        window.location.href = newUrl;
    })
});

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
    const $prodInfo = $("section.prodInfo");
    if ($prodInfo.length) {
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
                showMessage("quantityMessage", "최소 구매 수량은 1개 입니다.");
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
                showMessage("quantityMessage", "최대 구매 수량을 초과하였습니다.(재고수량 이내 & 최대 10개 구매)");
            }
            updateTotalPrice(finalPrice, $input.val());
        });


        function updateTotalPrice(finalPrice, quantity) {
            totalPrice = finalPrice * quantity;
            $(".prodTotalPrice").text(totalPrice.toLocaleString());
        }
    }
}); // $(function(){}); jQuery END
/** shop_detail : section.prodInfo : 총 구매 금액 calculate : **/
/** shop_detail : section.prodInfo : 총 구매 금액 calculate : **/


/** shop_detail > shop_imgReview : section.imgReviewList : 이미지 리뷰 라인 : START **/
/** shop_detail > shop_imgReview : section.imgReviewList : 이미지 리뷰 라인 : START **/
$(function () {
    if ($(".imgRevSwiper").length) {
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
    }

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

    const $prodReview = $("section.prodReview");

    if ($prodReview.length) {
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
    }

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
    console.log(reviews.length);
    if (reviews.length==0 || reviews==null){
        const reviewHtml = `
            <div class="row border-bottom py-2">
                <div class="col-md-12 mb-3 text-center">
                    작성된 리뷰가 없습니다.
                </div>
            </div>`;
        reviewBlock.append(reviewHtml);
    }

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
/*상품정보 추가/제거*/
$(function () {


    $(document).on('click', '.removeRow', function () {
        $(this).closest('tr').remove();
        updateProdInfoIndices(); // 삭제 후 인덱스 업데이트
    });


    $(document).on('click', '.drop-area:not(.remove-btn)', function () {
        const dropArea = $(this).closest(".drop-area").not(".remove-btn");
        const fileInput = $(this).find(".prodImageInput");

        fileInput.click();

    });

});




// 상품정보 행 인덱스 업데이트 함수
function updateProdInfoIndices() {
    $('#prodInfoList tr').each(function (index) {
        $(this).find('.infoKey').attr('name', `prodInfoList[${index}].infoKey`);
        $(this).find('.infoValue').attr('name', `prodInfoList[${index}].infoValue`);
    });
}


/** shop_form : 상품 delete **/
/** shop_form : 상품 delete **/
/*function deleteProd() {
    if (prodName == null || prodName.length < 1) prodName = '해당';
    if (confirm(prodName + " 상품을 삭제하시겠습니까?")) {
        document.querySelector('#deleteForm').submit()
    };
}*/


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
    const newCard = $('.prodImageCard').first().clone(); // 첫 번째 카드를 복사하여 새로운 카드 생성

    newCard.find('.prodImageInput').val(''); // 입력 필드를 비움
    newCard.find('.preview').empty(); // 미리보기 영역을 비움

    // 새로운 카드를 현재 위치 바로 다음에 추가
    var pos = $(e.target.closest('li.prodImageCard'));
    pos.after(newCard);

    updateCardOrderNumbers(); // 전체 카드를 DOM 순서대로 업데이트
}

// 상세이미지 카드 제거 함수
function removeCard(button) {
    if ($('.prodImageCard').length > 1) {
        const removeBtn = button.closest('.prodImageCard').find('remove-btn');

        const imgSrc = button.closest('.prodImageCard').find('img').attr('src')
        const toBeDeletedInput = $('#toBeDeleted');

        // 기존 이미지 중 제거할 이미지 파일 목록을 hidden 으로 전송
        if (imgSrc != null && imgSrc.startsWith('/imgs/shop/product/')) {
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
        $(card).find('.prodImageInput').attr('data-image-num', index + 1);
    });
}


// admin : 상품 등록 & 수정
$(function () {
    const currentUrl = window.location.href;

    // 수정 페이지 일때 -> 이미지 순서 처리
    if (currentUrl.includes('/admin/edit')) {
        updateCardOrderNumbers();

        $('form.productForm').on('submit', function (e) {
            e.preventDefault();

            updateCardOrderNumbers();

            let formData = new FormData(this);

            // 이미지 카드  -> 기존 이미지와 새 이미지를 구분 & 순서 부여
            $('li.prodImageCard').each(function () {
                const imgSrc = $(this).find('.preview img').attr('src');
                const fileInput = $(this).find('.prodImageInput');
                const imageOrder = fileInput.attr('data-image-num'); // 이미지 순서(카드순서)

                if (imgSrc && imgSrc.startsWith('/imgs/shop/product/')) { //기존 이미지
                    formData.append('existingImages[]', imgSrc);
                    formData.append('existingImageOrders[]', imageOrder);
                } else if (fileInput[0].files.length > 0) { // 새 이미지
                    formData.append('newImages[]', fileInput[0].files[0]);
                    formData.append('newImageOrders[]', imageOrder);
                }
            });

            // 상품 상세 정보(prodInfoList) 리스트
            $('#prodInfoList tr').each(function (index) {
                const infoKey = $(this).find('.infoKey').val();
                const infoValue = $(this).find('.infoValue').val();

                if (infoKey && infoValue) {
                    formData.append(`prodInfoList[${index}].infoKey`, infoKey);
                    formData.append(`prodInfoList[${index}].infoValue`, infoValue);
                }
            });


            // Fetch API로 폼 데이터 전송
            fetch($(this).attr('action'), {
                method: $(this).attr('method'),
                body: formData,
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    window.location.href = '/shop/admin/list';
                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                });
        });
    } else {
        // 상품 신규 등록 : submit
        $('form.productForm').off('submit');
    }
});

























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
            renderFavoriteProducts(data); // 데이터 렌더링
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
        favoriteList.html(`
                     <div class="container w-100">
                            <div class="row">
                                <div class="col-12">
                                    <div class="no-favorites d-flex flex-column justify-content-center align-items-center">
                                        <div class="icon-heart my-5">
                                            <i class="fa-regular fa-heart fa-10x" style="color:#bbbbbb;"></i>
                                        </div>
                                        <div class="message mb-5">
                                            <p class="c333 f32 fw700 text-center mb-3">찜한 상품이 없습니다</p>
                                            <p class="c333 f20 text-center">상품 페이지에서 마음에 드는 상품을 골라 찜한 상품 리스트를 만들어보세요</p>
                                        </div>
                                        <a href="/shop/list" class="btn-back btn btn-success btn-lg">상품 보러 가기</a>
                                    </div>
                                </div>
                            </div>
                    </div>
                
                `);
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
            var cntFavorite = $(".prodItem").length;
            console.log(cntFavorite);

            if (cntFavorite==0 || cntFavorite ==null){
                const favoriteList = $(".favoriteList");
                // 찜 목록이 없을 때 처리
                favoriteList.html(`
                     <div class="container w-100">
                            <div class="row">
                                <div class="col-12">
                                    <div class="no-favorites d-flex flex-column justify-content-center align-items-center">
                                        <div class="icon-heart my-5">
                                            <i class="fa-regular fa-heart fa-10x" style="color:#bbbbbb;"></i>
                                        </div>
                                        <div class="message mb-5">
                                            <p class="c333 f32 fw700 text-center mb-3">찜한 상품이 없습니다</p>
                                            <p class="c333 f20 text-center">상품 페이지에서 마음에 드는 상품을 골라 찜한 상품 리스트를 만들어보세요</p>
                                        </div>
                                        <a href="/shop/list" class="btn-back btn btn-success btn-lg">상품 보러 가기</a>
                                    </div>
                                </div>
                            </div>
                    </div>
                
                `);
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



/***************** admin : productList ******************/
/***************** admin : productList ******************/
/***************** admin : productList ******************/
$(function() {
    if ($('#prodTable').length) {
        // 현재 달의 1일과 오늘 날짜를 기본값으로 설정
        const today = new Date();
        const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

        // 날짜 'YYYY-MM-DD' 형식으로 변환
        $('#startDate').val(formatDateForInput(firstDayOfMonth));
        $('#endDate').val(formatDateForInput(today));

        // 페이지 로드 시 상품 목록 불러오기
        fetchProducts();

        // 상품명 검색 input에서 enter 입력 시 검색 실행
        $('#searchInput').on('keydown', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                fetchProducts();
            }
        });

    }
});



//admin 상품 리스트 렌더링
async function fetchProducts() {
    // 검색어와 상태 필터 값 가져오기
    const search = document.getElementById("searchInput").value;
    const category = document.getElementById("categorySelect").value;
    const status = document.getElementById("statusSelect").value;
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    const response = await fetch(`/shop/api/products?search=${search}&category=${category}&status=${status}&startDate=${startDate}&endDate=${endDate}`);
    const data = await response.json();

    console.log(`/shop/api/products?search=${search}&category=${category}&status=${status}&startDate=${startDate}&endDate=${endDate}`)
    console.log(data);

    // 테이블에 주문 목록 렌더링
    const prodTableBody = document.getElementById("prodTableBody");
    prodTableBody.innerHTML = ""; // 기존 내용 제거
    let sumStock = 0;
    let sumSalesProd = 0;
    let sumSalesAmount = 0;

    data.products.forEach((product,index) => {
        // 상태에 따라 배경색 클래스를 동적으로 설정
        let rowClass = '';
        if (product.prodStock !=null) sumStock += product.prodStock;
        if (product.sumQuantity !=null) sumSalesProd += product.sumQuantity;
        if (product.totalFinalPrice !=null) sumSalesAmount += product.totalFinalPrice;

        switch (product.prodStatus) {
            case 'AVAILABLE':
                rowClass = 'table-info';  // 파란색
                statClass = 'text-primary fw700';
                break;
            case 'UNAVAILABLE':
                rowClass = 'table-danger';   // 빨간색
                statClass = 'text-danger fw700';
                break;
            case 'DISCONTINUED':
                rowClass = 'table-secondary';  // 회색
                statClass = 'c333 fw700 text-secondary';
                break;
            case 'OUT_OF_STOCK':
                rowClass = 'table-warning';  // 노란색
                statClass = 'text-warning fw700';
                break;
            default:
                rowClass = ''; // 기본값
        }

        const row = `
            <tr class="prodRow ${rowClass}">
                <td>${index + 1}</td>
                <td>${product.prodId}</td>
                <td class="${statClass}"><a href="/shop/detail/${product.prodId}">${product.prodName}</a>  </td>
                <td class="${statClass}">${toKoreanProdStatus(product.prodStatus)}</td>
                <td class="text-end pe-2">${product.prodStock==null? 0 :product.prodStock.toLocaleString('ko-KR') || 0}</td>
                <td class="text-end pe-2">${product.sumQuantity==null? 0 :product.sumQuantity.toLocaleString('ko-KR') || 0}</td>
                <td class="text-end pe-2">${product.totalFinalPrice==null? 0 : product.totalFinalPrice.toLocaleString('ko-KR')}</td>
                <td class="text-center"><a href="/shop/admin/edit/${product.prodId}" class="tableLink text-secondary text-center">수정</a></td>
            </tr>
        `;
        prodTableBody.insertAdjacentHTML('beforeend', row);

    });
    const totalRow = `
            <tr  class="table-light fw700">
                <td class="text-center" colspan="4">소&nbsp;&nbsp;계</td>
                <td class="text-end pe-2">${sumStock.toLocaleString('ko-KR') || 0}</td>
                <td class="text-end pe-2">${sumSalesProd.toLocaleString('ko-KR') || 0}</td>
                <td class="text-end pe-2">${sumSalesAmount.toLocaleString('ko-KR') || 0}</td>
                <td></td>
            </tr>
        `;
    prodTableBody.insertAdjacentHTML('afterbegin', totalRow);
}



/*<td>${formatDateTime(order.createdAt)}</td>
<td><a href="/order/admin/po/${order.orderId}" className="${statClass} tableLink">${order.orderId}</a></td>
<td>${order.memberId}</td>
<td className="${statClass}">${order.status}</td>
<td className="text-end me-5">${formatCurrency(order.totalPay)}</td>*/










/******* Common Util : END *********/
//통화 포맷팅 함수
function formatCurrency(amount){
    return `${amount.toLocaleString('ko-KR')}원`;
}

//ProdStatus Enum 한글명 매핑 함수
function toKoreanProdStatus(prodStatus) {
    const statusMap = {
        "AVAILABLE": "판매중",
        "UNAVAILABLE": "판매일시중단",
        "OUT_OF_STOCK": "일시품절",
        "DISCONTINUED": "단종"
    };

    return statusMap[prodStatus] || prodStatus;
}

//오류 메시지 박스 초기화
$(function (){
    const messageBox = $(".messageBox");
    if (messageBox){
        messageBox.css("visibility", "hidden")
    }
})

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



