$(document).ready(() => {

	const navbar = $("header")
	const menu = $(".custom-menu");
	const menu_main = $("ul.custom-main li a")
	const submenu = $(".custom-menu.custom-sub");
	const small_menu_btn = $(".menu-btn");
	const small_header = $("#small-header");
	const small_category = $(".small-category");
	const small_submenu = $(".small-submenu");
	const close_btn = $(".close-btn");
	const icons = $(".material-symbols-outlined")
	var hasClass = false;
	var isMouseOverPlace = false;
	const blackright = $(".blackright")
	// 하단 서브메뉴 슬라이드
	//submenu.css({display: "none"});
	small_header.css({ display: "none" });
	menu.mouseover(function() {
		isMouseOverPlace = true;
		//submenu.stop().slideDown();
		submenu.addClass('show');
		navbar.addClass("green-navbar");
		navbar.removeClass("difference")
		menu_main.css({ color: "black" })
		icons.addClass("black-icons")
		$(".custom-submenu").css({ display: "flex", backgroundColor: "#808080" })
	});
	menu.mouseout(function() {
		isMouseOverPlace = false;
		//submenu.stop().slideUp();
		submenu.removeClass('show');
		navbar.addClass("difference")
		navbar.removeClass("green-navbar");
		menu_main.css({ color: "white" })
		$(".right").css({ color: "white" });
		icons.removeClass("black-icons")
		//$(".custom-sub").css({display: "none"})
		if ($(window).scrollTop() > height) {
			navbar.addClass("green-navbar")
			navbar.removeClass("difference")
			menu_main.css({ color: "black" })
			$(".right").css({ color: "black" });
			icons.addClass("black-icons")
		}
	});

	submenu.mouseover(function() {
		isMouseOverPlace = true;
		//submenu.stop().slideDown();
		submenu.addClass('show');
		navbar.addClass("green-navbar");
		navbar.removeClass("difference")
		menu_main.css({ color: "black" })
		$(".right").css({ color: "black" });
		icons.addClass("black-icons")
		$(".custom-submenu").css({ display: "flex", backgroundColor: "#386641" })
	});

	submenu.mouseout(function() {
		isMouseOverPlace = false;
		//submenu.stop().slideUp();
		submenu.removeClass('show');
		navbar.removeClass("green-navbar");
		navbar.addClass("difference")
		menu_main.css({ color: "white" })
		$(".right").css({ color: "white" });
		icons.removeClass("black-icons")
		//$(".custom-sub").css({display: "none"})
		if ($(window).scrollTop() > height) {
			navbar.addClass("green-navbar")
			menu_main.css({ color: "black" })
			navbar.removeClass("difference")
			$(".right").css({ color: "black" });
			icons.addClass("black-icons")
		}
	});
	//작은 화면 메뉴 슬라이드
	small_menu_btn.click(function() {
		small_header.stop().slideDown();
		$("html").css("overflow", "hidden");
	});
	close_btn.click(function() {
		small_header.stop().slideUp();
		$("html").css("overflow", "scroll");
	});

	$(".small-submenu").hide();
	// small-category 또는 small-submenu에 마우스를 올렸을 때
	$(".small-category, .small-submenu").hover(
		function() {
			// 해당 small-category의 다음 형제인 small-submenu를 슬라이드다운
			$(this).closest("li").find(".small-submenu").stop().slideDown();
		},
		function() {
			// 마우스가 small-category와 small-submenu를 모두 벗어났을 때 슬라이드업
			$(this).closest("li").find(".small-submenu").stop().slideUp();
		}
	);
})