package com.wellit.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.wellit.project.store.Store;
import com.wellit.project.store.StoreRepository;

@Component
public class DataLoader implements CommandLineRunner {
	private final StoreRepository storeRepository;

	public DataLoader(StoreRepository storeRepository) {
		this.storeRepository = storeRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		long count = storeRepository.count();
		/*
		if (count < 20) {
			// 비건 카페 A
			storeRepository.save(createStore("비건 카페 A", "비건 디저트 전문점", "100% 비건 재료로 만든 다양한 디저트를 제공합니다.", "카페", "서울특별시",
					"강남구", "02-1234-5678", "서울특별시 강남구 테헤란로 123", "/imgs/main/goods01.jpg", "10:00 - 22:00", "매주 일요일",
					"비건 초콜릿 케이크", "주차 가능", 37.4979, 127.0276, "Y"));

			// 비건 레스토랑 B
			storeRepository.save(createStore("비건 레스토랑 B", "건강한 비건 요리", "신선한 재료로 만든 비건 요리를 제공합니다.", "레스토랑", "서울특별시",
					"마포구", "02-2345-6789", "서울특별시 마포구 홍익로 45", "/imgs/main/goods02.jpg", "11:00 - 21:00", "매주 월요일",
					"비건 파스타", "주차 불가", 37.5456, 126.9490, "Y"));

			// 비건 스무디 바 C
			storeRepository.save(createStore("비건 스무디 바 C", "신선한 스무디와 주스", "비건 스무디와 주스를 전문으로 합니다.", "카페", "서울특별시", "서초구",
					"02-3456-7890", "서울특별시 서초구 반포대로 67", "/imgs/main/goods03.jpg", "09:00 - 19:00", "매주 화요일", "그린 스무디",
					"주차 가능", 37.4872, 127.0055, "Y"));

			// 비건 패스트푸드 D
			storeRepository.save(createStore("비건 패스트푸드 D", "비건 패스트푸드", "빠르고 건강한 비건 패스트푸드를 제공합니다.", "패스트푸드", "서울특별시",
					"송파구", "02-4567-8901", "서울특별시 송파구 올림픽로 123", "/imgs/main/goods04.jpg", "10:00 - 22:00", "매주 수요일",
					"비건 버거", "주차 가능", 37.5112, 127.1060, "Y"));

			// 비건 베이커리 E
			storeRepository.save(createStore("비건 베이커리 E", "비건 빵과 케이크", "비건 재료로 만든 다양한 빵과 케이크를 제공합니다.", "베이커리", "서울특별시",
					"용산구", "02-5678-9012", "서울특별시 용산구 이태원로 45", "/imgs/main/goods05.jpg", "08:00 - 20:00", "매주 목요일",
					"비건 브라우니", "주차 불가", 37.5349, 126.9940, "Y"));

			// 비건 레스토랑 F
			storeRepository.save(createStore("비건 레스토랑 F", "비건 한식 전문점", "비건 한식을 전문으로 하는 레스토랑입니다.", "레스토랑", "서울특별시",
					"종로구", "02-6789-0123", "서울특별시 종로구 종로 123", "/imgs/main/goods06.jpg", "11:00 - 22:00", "매주 금요일",
					"비건 비빔밥", "주차 가능", 37.5700, 126.9980, "Y"));

			// 비건 카페 G
			storeRepository.save(createStore("비건 카페 G", "비건 커피와 스낵", "비건 커피와 스낵을 제공합니다.", "카페", "서울특별시", "영등포구",
					"02-7890-1234", "서울특별시 영등포구 여의도동 45", "/imgs/main/goods07.jpg", "09:00 - 21:00", "매주 토요일", "비건 쿠키",
					"주차 가능", 37.5260, 126.9250, "Y"));

			// 비건 레스토랑 H
			storeRepository.save(createStore("비건 레스토랑 H", "비건 이탈리안", "비건 이탈리안 요리를 전문으로 합니다.", "레스토랑", "서울특별시", "관악구",
					"02-8901-2345", "서울특별시 관악구 관악로 123", "/imgs/main/goods01.jpg", "12:00 - 22:00", "매주 일요일", "비건 피자",
					"주차 불가", 37.4780, 126.9510, "Y"));

			// 비건 카페 I
			storeRepository.save(createStore("비건 카페 I", "비건 아침식사 전문점", "비건 아침식사를 제공합니다.", "카페", "서울특별시", "서대문구",
					"02-9012-3456", "서울특별시 서대문구 연세로 45", "/imgs/main/goods02.jpg", "08:00 - 14:00", "매주 월요일", "비건 오트밀",
					"주차 가능", 37.5636, 126.9380, "Y"));

			// 비건 레스토랑 J
			storeRepository.save(createStore("비건 레스토랑 J", "비건 중식 전문점", "비건 중식을 전문으로 하는 레스토랑입니다.", "레스토랑", "서울특별시",
					"동대문구", "02-0123-4567", "서울특별시 동대문구 왕산로 123", "/imgs/main/goods03.jpg", "11:00 - 22:00", "매주 화요일",
					"비건 짜장면", "주차 불가", 37.5740, 127.0240, "Y"));

			// 비건 카페 K
			storeRepository.save(createStore("비건 카페 K", "비건 스낵바", "비건 스낵과 음료를 제공합니다.", "카페", "서울특별시", "중구",
					"02-1234-5678", "서울특별시 중구 명동 45", "/imgs/main/goods04.jpg", "10:00 - 20:00", "매주 수요일", "비건 팝콘",
					"주차 가능", 37.5630, 126.9820, "Y"));

			// 비건 레스토랑 L
			storeRepository.save(createStore("비건 레스토랑 L", "비건 지중해 요리", "비건 지중해 요리를 전문으로 합니다.", "레스토랑", "서울특별시", "강서구",
					"02-2345-6789", "서울특별시 강서구 화곡로 123", "/imgs/main/goods05.jpg", "11:00 - 21:00", "매주 목요일", "비건 파에야",
					"주차 가능", 37.5500, 126.8490, "Y"));

			// 비건 카페 M
			storeRepository.save(createStore("비건 카페 M", "비건 차 전문점", "다양한 비건 차를 제공합니다.", "카페", "서울특별시", "금천구",
					"02-3456-7890", "서울특별시 금천구 시흥대로 45", "/imgs/main/goods06.jpg", "09:00 - 19:00", "매주 금요일", "비건 허브차",
					"주차 불가", 37.4540, 126.9000, "Y"));

			// 비건 레스토랑 N
			storeRepository.save(createStore("비건 레스토랑 N", "비건 퓨전 요리", "비건 퓨전 요리를 제공합니다.", "레스토랑", "서울특별시", "노원구",
					"02-4567-8901", "서울특별시 노원구 상계로 123", "/imgs/main/goods07.jpg", "12:00 - 22:00", "매주 토요일", "비건 타코",
					"주차 가능", 37.6550, 127.0560, "Y"));

			// 비건 카페 O
			storeRepository.save(createStore("비건 카페 O", "비건 아이스크림 전문점", "비건 아이스크림을 제공합니다.", "카페", "서울특별시", "양천구",
					"02-5678-9012", "서울특별시 양천구 신정로 45", "/imgs/main/goods01.jpg", "10:00 - 20:00", "매주 일요일", "비건 아이스크림",
					"주차 가능", 37.5160, 126.8660, "Y"));

			// 비건 레스토랑 P
			storeRepository.save(createStore("비건 레스토랑 P", "비건 스시 전문점", "비건 스시를 전문으로 합니다.", "레스토랑", "서울특별시", "성북구",
					"02-6789-0123", "서울특별시 성북구 정릉로 123", "/imgs/main/goods02.jpg", "11:00 - 21:00", "매주 월요일", "비건 스시",
					"주차 불가", 37.6100, 127.0240, "Y"));

			// 비건 카페 Q
			storeRepository.save(createStore("비건 카페 Q", "비건 브런치 카페", "비건 브런치를 제공합니다.", "카페", "서울특별시", "광진구",
					"02-7890-1234", "서울특별시 광진구 자양로 45", "/imgs/main/goods03.jpg", "09:00 - 15:00", "매주 화요일", "비건 팬케이크",
					"주차 가능", 37.5380, 127.0820, "Y"));

			// 비건 레스토랑 R
			storeRepository.save(createStore("비건 레스토랑 R", "비건 태국 요리", "비건 태국 요리를 전문으로 합니다.", "레스토랑", "서울특별시", "중랑구",
					"02-8901-2345", "서울특별시 중랑구 면목로 123", "/imgs/main/goods04.jpg", "11:00 - 22:00", "매주 수요일", "비건 팟타이",
					"주차 불가", 37.5960, 127.0950, "Y"));

			// 비건 레스토랑 S
			storeRepository.save(createStore("비건 레스토랑 S", "비건 지중해 요리", "신선한 재료로 만든 비건 지중해 요리를 제공합니다.", "레스토랑", "서울특별시",
					"성동구", "02-9012-3456", "서울특별시 성동구 왕십리로 123", "/imgs/main/goods05.jpg", "11:00 - 22:00", "매주 목요일",
					"비건 후무스", "주차 가능", 37.5630, 127.0420, "Y"));

			// 비건 카페 T
			storeRepository.save(createStore("비건 카페 T", "비건 스무디 전문점", "다양한 비건 스무디와 주스를 제공합니다.", "카페", "서울특별시", "서대문구",
					"02-3456-7890", "서울특별시 서대문구 연세로 45", "/imgs/main/goods06.jpg", "10:00 - 20:00", "매주 금요일",
					"비건 망고 스무디", "주차 불가", 37.5636, 126.9380, "Y"));

		}*/
	}

	private Store createStore(String name, String title, String content, String category, String regionProvince,
			String regionCity, String contact, String address, String image, String operatingHours, String closedDays,
			String recommendedMenu, String parkingInfo, double latitude, double longitude, String isOpen) {
		Store store = new Store();
		store.setStoName(name);
		store.setStoTitle(title);
		store.setStoContent(content);
		store.setStoCategory(category);
		store.setStoRegionProvince(regionProvince);
		store.setStoRegionCity(regionCity);
		store.setStoContact(contact);
		store.setStoAddress(address);
		store.setStoImage(image);
		store.setStoOperatingHours(operatingHours);
		store.setStoClosedDays(closedDays);
		store.setStoRecommendedMenu(recommendedMenu);
		store.setStoParkingInfo(parkingInfo);
		store.setStoLatitude(latitude);
		store.setStoLongitude(longitude);
		store.setStoIsOpen(isOpen);
		return store;
	}
	
}
