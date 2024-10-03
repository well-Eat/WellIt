/****** mypage : order_history *****/
/****** mypage : order_history *****/
/****** mypage : order_history *****/
// 버튼 클릭 시, 이벤트
$(function () {
    // $('.viewReviewBtn').trigger("click");
})
$(document).on("click", ".openReviewFormBtn", function () {
    let clickedBtn = $(this);
    let prodId = clickedBtn.closest(".itemBlock").attr("data-product-id");
    let orderItemId = clickedBtn.closest(".itemBlock").attr("data-orderitem-id");
    let reviewed = clickedBtn.closest(".itemBlock").attr("data-reviewed");


    console.log(clickedBtn);
    console.log(prodId);
    console.log(orderItemId);
    console.log(reviewed);
    let prodReviewForm;

    // 리뷰 쓰기 버튼 클릭 시 => 새로운 리뷰 쓰기 폼 열기
    if (clickedBtn.hasClass('newReviewBtn')) {
        renderForm(null, clickedBtn);
    }
    //내 리뷰 보기 버튼 클릭 시
    else if (clickedBtn.hasClass('viewReviewBtn')) {
        loadReview(orderItemId, clickedBtn);

    } else if (clickedBtn.hasClass('openEditForm')) { //수정하기 버튼 클릭 시
        loadReview(orderItemId, clickedBtn);
    } else if (clickedBtn.hasClass('submitReviewForm')) {
        submitForm();
    }


    const sumFinalPrice = $(this).parents('.calcItem').find('.sumFinalPrice').attr('data-nums');


    // 리뷰 내용 불러오기 함수
    async function loadReview(orderItemId, clickedBtn) {
        try {
            const response = await fetch(`/shop/review/get/${orderItemId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            // 응답을 JSON으로 변환
            const data = await response.json();
            console.log("======= loadReview() : 리뷰 내용 =======")
            console.log(data);

            //내 리뷰 버튼 클릭 시 리뷰 랜더링 함수 호출
            if (clickedBtn.hasClass('viewReviewBtn')) {
                renderView(data, clickedBtn);
            } else if (clickedBtn.hasClass('openEditForm')) { //수정하기 버튼 클릭 시
                renderForm(data, clickedBtn);
            } else if (clickedBtn.hasClass('submitReviewForm')) {
                renderView(data, clickedBtn);
            }


        } catch (error) {
            console.error('리뷰 불러오기 실패 : ', error);
        }

    }

    //리뷰 보기 렌더링
    function renderView(data, clickedBtn) {
        // 리뷰 보기 폼을 동적으로 생성
        prodReviewForm = `
            <div class="card-text reviewWrap">
                <div class="reviewViewWrap">
                    <div class="row">
                        <div class="col-6 modalColLeft">
                            <div class="revImgGallery d-flex gap-1" style="height: 120px;">
        `;

        for (let i = 0; i < data.prodRevImgList.length; i++) {
            prodReviewForm += `
                                <div class="imgBox col ratio-1x1 overflow-hidden border d-block rounded-2" style="background-image: url('${data.prodRevImgList[i]}'); background-size: cover; background-position: center; background-repeat: no-repeat;"></div>
            `;
        }

        prodReviewForm += `
                            </div>
                        </div>

                        <div class="col-6 modalColRight">
                            <div class="view-star-rating mb-3" data-rating="${data.rating}">
                                <i class="fa-solid fa-star" id="star5"></i>
                                <i class="fa-solid fa-star" id="star4"></i>
                                <i class="fa-solid fa-star" id="star3"></i>
                                <i class="fa-solid fa-star" id="star2"></i>
                                <i class="fa-solid fa-star" id="star1"></i>
                            </div>
                            <p class="py-2 px-1" style="min-height: 72px;">
                                <span class="f14 c444 fw600">${data.revText}</span>
                            </p>
                            
                            <button type="button" class="btn badge rounded-pill border-success text-success f14 px-5 py-2 d-inline-block flex-grow-1 float-end openReviewFormBtn openEditForm"><i class="fa-regular fa-pen-to-square"></i>&nbsp;&nbsp;&nbsp;리뷰 수정하기</button>
                        </div>
                        <div id="drop-area" style="visibility: hidden;"></div>
                        <div id="gallery" style="visibility: hidden;"></div>
                        <div id="prodRevImg" style="visibility: hidden;"></div>
                    </div>
                </div>
            </div>
        `;
        $(".reviewWrap").remove(); // 기존에 열린 폼이 있으면 닫고,
        $('.itemBtnLine').show(); // 다른 버튼라인들은 모두 나타나도록
        clickedBtn.parents('.itemBtnLine').hide(); // 현재 열린 버튼라인만 hide

        clickedBtn.parent().after(prodReviewForm);

        // 평점 view 추가
        $(".view-star-rating i").removeClass("check");
        $(`#star${data.rating}`).addClass("check");
        console.log($(`#star${data.rating}`));

    }


    let existImageSrc = []; // 기존 이미지 URL을 저장하는 배열
    let filesArray = []; // 새로 업로드된 파일을 저장하는 배열

    //리뷰 폼 렌더링
    function renderForm(data, clickedBtn) {
        // 리뷰 쓰기 폼을 동적으로 생성
        prodReviewForm = `
            <div class="card-text reviewWrap">
                <form id="prodReviewForm" enctype="multipart/form-data" name="prodReviewForm">
                    <div class="row">
                        <div class="col-6 modalColLeft border">
                            <div id="drop-area">
                                <p class="c666 fw700">리뷰 이미지 등록</p>
                                <input type="file" class="form-control form-control-sm" name="prodRevImgList" id="prodRevImg" accept="image/*" multiple><div>드래그앤드롭 또는 </div>
                                <label class="button inputFileBtn rounded-pill bg-light border mt-3 ms-1 d-block" for="prodRevImg">파일 선택</label>
                                <div id="gallery"></div>
                            </div>
                        </div>

                        <div class="col-6 modalColRight">
                            <p class="mb-0">
                                <div class="star-rating">
                                    <input type="radio" id="star5" name="rating" value="5" checked/>
                                    <label for="star5" title="5 stars"><i class="fa-solid fa-star"></i></label>
                                    <input type="radio" id="star4" name="rating" value="4" />
                                    <label for="star4" title="4 stars"><i class="fa-solid fa-star"></i></label>
                                    <input type="radio" id="star3" name="rating" value="3" />
                                    <label for="star3" title="3 stars"><i class="fa-solid fa-star"></i></label>
                                    <input type="radio" id="star2" name="rating" value="2" />
                                    <label for="star2" title="2 stars"><i class="fa-solid fa-star"></i></label>
                                    <input type="radio" id="star1" name="rating" value="1" />
                                    <label for="star1" title="1 star"><i class="fa-solid fa-star"></i></label>
                                </div>
                            </p>
                            <p>
                                <textarea class="form-control overflow-y-scroll" id="revText" name="revText" style="resize: none;"></textarea>
                            </p>

                            <input type="hidden" id="prodId" name="prodId" value="${prodId}" />
                            <input type="hidden" id="paid" name="paid"/>
                            <input type="hidden" id="orderItemId" name="orderItemId" value="${orderItemId}"/>
                            
                            <button type="button" id="submitReviewForm" class="btn badge rounded-pill border-success text-success f14 px-5 py-2 d-inline-block flex-grow-1 float-end submitReviewForm"><i class="fa-solid fa-square-pen"></i>&nbsp;&nbsp;&nbsp;리뷰 제출하기</button>
                        </div>
                    </div>
                </form>
            </div>
        `;

        // let existImageSrc = []; // 기존 리뷰 이미지 주소를 저장

        if (data == null) {
            $(".reviewWrap").remove(); // 기존에 열린 폼이 있으면 닫고,
            $('.itemBtnLine').show(); // 다른 버튼라인들은 모두 나타나도록
            //  동적으로 생성된 폼을 DOM에 추가
            clickedBtn.closest('.itemBtnLine').after(prodReviewForm);
            clickedBtn.parents('.itemBtnLine').hide(); // 현재 열린 버튼라인만 hide
        } else if (data != null) {
            //  동적으로 생성된 폼을 DOM에 추가
            $(".reviewWrap").empty(); // 기존에 열린 폼이 있으면 닫고,
            //clickedBtn.closest('.calcItem').after(prodReviewForm);
            console.log(data);
            $(".reviewWrap").append(prodReviewForm);
            $(`input[name="rating"][value="${data.rating}"]`).prop('checked', true);
            $("#revText").val(data.revText);

            // 기존 이미지를 갤러리에 추가하고 배열에 저장
            if (data.prodRevImgList && data.prodRevImgList.length > 0) {
                data.prodRevImgList.forEach((imgUrl) => {
                    existImageSrc.push(imgUrl);
                    addExistingImage(imgUrl); // 기존 이미지 추가
                });
            }
        }

        /**** 리뷰 이미지 드래그앤드롭 등록 START****/
        // 이미지 드래그 앤 드롭 영역
        const dropArea = document.getElementById("drop-area");
        const gallery = document.getElementById("gallery");
        const fileInput = document.getElementById("prodRevImg");
        // let filesArray = []; // 업로드할 파일들을 저장할 배열


        // 기본 동작 방지
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropArea.addEventListener(eventName, preventDefaults, false);
            document.body.addEventListener(eventName, preventDefaults, false);
        });

        function preventDefaults(e) {
            e.preventDefault();
            e.stopPropagation();
        }

        // 드래그 오버시 하이라이트 주기
        ['dragenter', 'dragover'].forEach(eventName => {
            dropArea.addEventListener(eventName, () => dropArea.classList.add('highlight'), false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            dropArea.addEventListener(eventName, () => dropArea.classList.remove('highlight'), false);
        });

        // 파일 드롭 시 처리
        dropArea.addEventListener('drop', handleDrop, false);
        fileInput.addEventListener('change', handleFiles, false);

        function handleDrop(e) {
            const dt = e.dataTransfer;
            const files = dt.files;
            handleFiles({target: {files: files}});
        }

        function handleFiles(e) {
            const newFiles = Array.from(e.target.files);

            if (existImageSrc.length + filesArray.length + newFiles.length > 3) {
                alert('최대 3개의 이미지만 업로드할 수 있습니다.');
                return;
            }

            filesArray = filesArray.concat(newFiles).slice(0, 3 - existImageSrc.length); // 최대 3개까지만 허용
            updateGallery();
        }

        // 파일 추가 후 갤러리 업데이트
        function previewFile(file, index) {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onloadend = function () {
                const imgWrap = document.createElement('div');
                imgWrap.className = 'imgWrap';
                imgWrap.style.position = 'relative';
                imgWrap.style.display = 'inline-block';

                const img = document.createElement('img');
                img.src = reader.result;
                img.style.width = '100px';
                img.style.margin = '10px';

                const removeBtn = document.createElement('button');
                removeBtn.innerText = 'ㅡ';
                removeBtn.className = 'remove-btn';


                removeBtn.addEventListener('click', function () {
                    imgWrap.remove();
                    filesArray.splice(index, 1); // filesArray에서 해당 파일 제거
                    updateGallery(); // 갤러리 업데이트
                });

                imgWrap.appendChild(img);
                imgWrap.appendChild(removeBtn);
                gallery.appendChild(imgWrap);
            }
        }

        //삭제 후 업데이트
        function updateGallery() {
            gallery.innerHTML = '';

            // 기존 이미지 렌더링
            existImageSrc.forEach((imgUrl) => {
                addExistingImage(imgUrl);
            });

            // 새로 추가된 이미지 렌더링
            filesArray.forEach((file, index) => previewFile(file, index));
        }


        /**** 리뷰 이미지 드래그앤드롭 등록 END ****/

        $("#submitReviewForm").on("click", function () {
            submitForm(filesArray, existImageSrc, $(this));
        });

    }

    // 기존 이미지를 갤러리에 추가하는 함수
    function addExistingImage(imgUrl) {
        const imgWrap = document.createElement('div');
        imgWrap.className = 'imgWrap';
        imgWrap.style.position = 'relative';
        imgWrap.style.display = 'inline-block';
        imgWrap.setAttribute("data-img-url", imgUrl);

        const img = document.createElement('img');
        img.src = imgUrl;
        img.style.width = '100px';
        img.style.margin = '10px';

        const removeBtn = document.createElement('button');
        removeBtn.innerText = 'ㅡ';
        removeBtn.className = 'remove-btn';


        removeBtn.addEventListener('click', function () {
            imgWrap.remove();
            const index = existImageSrc.indexOf(imgUrl);
            if (index !== -1) {
                existImageSrc.splice(index, 1); // 배열에서 기존 이미지 제거
            }
        });

        imgWrap.appendChild(img);
        imgWrap.appendChild(removeBtn);
        gallery.appendChild(imgWrap);
    }


    // 리뷰 제출 버튼 클릭 이벤트
    function submitForm(filesArray, existImageSrc, clickedBtn) {
        let formData = new FormData(document.getElementById('prodReviewForm'));

        // filesArray에 있는 파일들을 FormData에 추가
        filesArray.forEach(file => {
            formData.append('prodRevImgList', file); // 새로 추가된 이미지 파일들
        });

        // existImageSrc에 있는 이미지 URL들을 FormData에 추가
        if (existImageSrc && existImageSrc.length > 0) {
            existImageSrc.forEach(url => {
                formData.append('existingImgUrls', url); // 기존 이미지 URL들
            });
        }

        formData.set('paid', sumFinalPrice);
        formData.set('reviewed', true);
        formData.set('orderItemId', orderItemId);
        formData.set('prodId', prodId);

        console.log("prodId", prodId);

        // fetch API로 폼 데이터 전송
        fetch(`/shop/review/save/${prodId}`, {
            method: 'POST',
            body: formData,  // FormData는 자동으로 multipart/form-data로 처리됨
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                } else {
                    throw new Error('리뷰 저장 실패');
                }
            })
            .then(data => {
                console.log(data);
                alert('리뷰가 성공적으로 저장되었습니다.');
                location.reload();
            })
            .catch(error => {
                console.log(error.text());
                alert('리뷰 저장에 실패했습니다.');
                location.reload();
            });

    }

});
