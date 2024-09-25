// Main section image handling
const maxImages = 3;
let mainImages = [];

// 전체 문서에서 기본 드래그 앤 드롭 동작 방지
$(document).on('dragover drop', function(e) {
	e.preventDefault(); // 기본 동작 방지
});

$(document).ready(function() {
	// 파일 선택창 열기
	$(".mainImgFrame").click(function() {
		//$(this).find("input.recpMainImg").click();
	});

	// 파일 선택 시 미리보기 이미지 표시
	$(".recpMainImg").change(function(event) {
		let input = $(this);
		let parentDiv = input.closest('.mainImgFrame');

		// 파일이 있으면 처리
		if (event.target.files && event.target.files[0]) {
			let reader = new FileReader();

			reader.onload = function(e) {
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
	$('.mainCookCard .drop-area').on('dragover', function(e) {
		e.preventDefault();
		e.stopPropagation();
		$(this).addClass('active');
	});

	$('.mainCookCard .drop-area').on('dragleave', function(e) {
		e.preventDefault();
		e.stopPropagation();
		$(this).removeClass('active');
	});

	$('.mainCookCard .drop-area').on('drop', function(e) {
	    e.preventDefault();
	    e.stopPropagation();
	    $(this).removeClass('active');

	    let files = e.originalEvent.dataTransfer.files;
	    console.log('Dropped files:', files); // 파일 정보 확인        

	    if (files.length > 0) {
	        let emptyInput = $(".recpMainImg").filter(function() {
	            return !this.files.length; // 비어 있는 파일 입력 찾기
	        }).first();

	        if (emptyInput.length) {
	            // DataTransfer 객체를 사용하여 파일 입력에 파일 추가
	            const dataTransfer = new DataTransfer();
	            for (let i = 0; i < files.length; i++) {
	                dataTransfer.items.add(files[i]);
	            }
	            emptyInput[0].files = dataTransfer.files; // 파일 입력에 설정

	            let parentDiv = emptyInput.closest('.mainImgFrame');
	            let reader = new FileReader();

	            reader.onload = function(e) {
	                // 기존 이미지 제거 후 새 이미지 추가
	                parentDiv.find('img').remove();
	                parentDiv.append('<img src="' + e.target.result + '" alt="Image Preview">');

	                // 삭제 버튼 보이기
	                parentDiv.find('.remove-btn').show();
	            };

	            // 첫 번째 파일에 대해 미리보기 생성
	            reader.readAsDataURL(files[0]); 
	        } else {
	            console.error('No empty input available.'); // 비어 있는 입력 필드가 없음
	        }
	    } else {
	        console.error('No files were dropped.'); // 드롭된 파일이 없음
	    }
	});



	// 이미지 삭제 버튼 클릭 시
	$(".mainImgFrame").on("click", ".remove-btn", function(e) {
		e.stopPropagation();

		let parentDiv = $(this).closest('.mainImgFrame');
		parentDiv.find('img').remove();
		parentDiv.find('.recpMainImg').val(''); // 파일 input 초기화
		$(this).hide(); // 삭제 버튼 숨기기
	});

	// jQuery UI sortable 기능으로 이미지 순서 변경 가능하게 설정
	$("#mainDropArea").sortable({
		items: ".mainImgFrame",
		cursor: "move",
		opacity: 0.7,
		placeholder: "sortable-placeholder"
	});

	$("#mainDropArea").disableSelection(); // 텍스트 선택 방지
});









// Cooking order image handling
// .cookingDropArea와 관련된 코드 수정

// Cooking order image handling
$(document).on('dragover', '.cookingDropArea', function(e) {
	e.preventDefault();
	e.stopPropagation();
	$(this).addClass('highlight');
});

$(document).on('dragleave', '.cookingDropArea', function(e) {
	e.preventDefault();
	e.stopPropagation();
	$(this).removeClass('highlight');
});

$(document).on('drop', '.cookingDropArea', function(e) {
	e.preventDefault();
	e.stopPropagation();
	$(this).removeClass('highlight');

	const file = e.originalEvent.dataTransfer.files[0];

	// handleCookingImage 함수 호출
	handleCookingImage(file, $(this));

	// 파일 입력 필드에도 파일 설정
	const fileInput = $(this).closest('.card-body').find('.cookingImgInput');

	// 파일 입력 필드 초기화
	fileInput.val('');

	const dataTransfer = new DataTransfer();
	dataTransfer.items.add(file);
	fileInput[0].files = dataTransfer.files;

	// 파일이 있을 때 삭제 버튼 표시
	showRemoveButton($(this));
});

$(document).on('change', '.cookingImgInput', function() {
	handleCookingImage(this.files[0], $(this).closest('.card-body').find('.cookingDropArea'));

	// 파일이 있을 때 삭제 버튼 표시
	showRemoveButton($(this).closest('.cookingDropArea'));
});

function handleCookingImage(file, dropArea) {
	if (file instanceof Blob) {
		const reader = new FileReader();
		reader.onload = function(e) {
			const preview = dropArea.find('.preview');
			preview.empty();
			preview.append($('<img>').attr('src', e.target.result));
			const rmvBtn = $('<button>').addClass('remove-btn').attr({ 'type': 'button' }).text('-').css('display', 'inline-block');
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
$(document).on('click', '.remove-btn', function() {
	const dropArea = $(this).closest('.cookingDropArea');
	const fileInput = dropArea.find('.cookingImgInput');

	// 이미지 및 파일 입력 필드 초기화
	dropArea.find('.preview').empty();
	fileInput.val('');

	// 삭제 버튼 숨기기
	$(this).hide();
});






//레시피카드 추가 제거
$(function() {
	// 페이지 로드 시 기본 카드가 없으면 하나 추가
	if ($('#cookOrderCardList .cookOrderCard').length === 0) {
		addCard();  // 기본 카드를 추가하는 함수 호출
	}

	// 카드 추가 버튼 클릭 시 새로운 카드 추가
	$(document).on('click', '.addCard', function() {
		addCard();
	});

	// 카드 제거 버튼 클릭 시 카드 제거 및 순서 번호 재정렬
	$(document).on('click', '.removeCard', function() {
		removeCard($(this));
	});
});

// 새로운 레시피 카드 추가 함수
function addCard() {
	const index = $('#cookOrderCardList .cookOrderCard').length; // 현재 카드 개수로 인덱스 설정
	const newCard = $('.cookOrderCard').first().clone(); // 첫 번째 카드를 복제

	// 새 카드에서 필드 초기화
	newCard.find('.cookingImgInput').val('');
	newCard.find('.preview').empty();
	newCard.find('textarea').val('');

	// 새 카드의 name 속성 업데이트
	newCard.find('.cookOrderNum').val(index + 1);
	newCard.find('.cookOrderNum').attr('name', `cookOrderCardList[${index}].cookOrderNum`);
	newCard.find('.cookingImgInput').attr('name', `cookOrderCardList[${index}].cookOrderImg`);
	newCard.find('textarea').attr('name', `cookOrderCardList[${index}].cookOrderText`);

	// 카드 리스트에 새 카드 추가
	$('#cookOrderCardList').append(newCard);

	updateCardOrderNumbers(); // 순서 번호 업데이트
}

// 레시피 카드 제거 함수
function removeCard(button) {
	if ($('.cookOrderCard').length > 1) {
		button.closest('.cookOrderCard').remove();
		updateCardOrderNumbers(); // 순서 번호 업데이트
	}
}

// 레시피 순서 번호 업데이트 함수
function updateCardOrderNumbers() {
	$('li.cookOrderCard').each(function(index, card) {
		$(card).find('.cookOrderNum').val(index + 1);
	});
}


/*재료 추가/제거*/
$(function() {
	// 페이지 로드 시 기본 행 추가
	//addIngredientRow();

	// 재료 추가 버튼 클릭 시 새로운 행 추가
	$(document).on('click', '#addIngredientBtn', function() {
		addIngredientRow();
	});

	// 재료 제거 버튼 클릭 시 행 제거 및 인덱스 재정렬
	$(document).on('click', '.removeRow', function() {
		if ($('#ingredientList tr').length <= 1) return false;
		$(this).closest('tr').remove();
		updateIngredientIndices(); // 인덱스를 다시 업데이트
	});

	// 재료 제거 버튼 클릭 시 행 제거 및 인덱스 재정렬
	$(document).on('click', '.addRow', function() {
		const newRow = `
        <tr>
          <td><input type="button" class="form-control removeRow" value="(-)"></td>
          <td><input type="text" class="form-control ingrName"  placeholder="재료"></td>
          <td><input type="text" class="form-control ingrAmount" placeholder="양"></td>
          <td><input type="button" class="form-control addRow" value="(+)"></td>
        </tr>`;
		$(this).closest('tr').after(newRow);
		updateIngredientIndices(); // 인덱스를 다시 업데이트
	});
});

// 새로운 재료 행 추가 함수
function addIngredientRow() {
	const index = $('#ingredientList tr').length; // 현재 행 개수로 인덱스 설정
	const newRow = `
        <tr>
          <td><input type="text" class="form-control ingrName" name="recpIngredientList[${index}].ingredientName" placeholder="재료"></td>
          <td><input type="text" class="form-control ingrAmount" name="recpIngredientList[${index}].amount" placeholder="양"></td>
          <td><input type="button" class="form-control removeRow" value="(-)"></td>
        </tr>`;
	$('#ingredientList').append(newRow);
}

// 재료 행 인덱스 업데이트 함수
function updateIngredientIndices() {
	$('#ingredientList tr').each(function(index, tr) {
		$(tr).find('input.ingrName').attr('name', 'recpIngredientList[' + index + '].ingredientName');
		$(tr).find('input.ingrAmount').attr('name', 'recpIngredientList[' + index + '].amount');
	});
}


const swiper = new Swiper('.mainImgContainer', {
	loop: true,
	pagination: {
		el: '.swiper-pagination',
	},
	navigation: {
		nextEl: '.swiper-button-next',
		prevEl: '.swiper-button-prev',
	},

});

