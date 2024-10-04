package com.wellit.project.store;

import com.wellit.project.store.StoreReservation;
import com.wellit.project.store.StoreReservationRepository; // 예약 저장소 인터페이스
import com.wellit.project.email.EmailService;
import com.wellit.project.member.Member; // 회원 엔티티
import com.wellit.project.member.MemberRepository; // 회원 저장소 인터페이스
import com.wellit.project.store.AllStore; // 가게 엔티티
import com.wellit.project.store.AllStoreRepository; // 가게 저장소 인터페이스

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/store")
public class StoreReservationController {

    @Autowired
    private StoreReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AllStoreRepository allStoreRepository;
    
    @Autowired
    private EmailService emailService; // 이메일 서비스 주입

    
    @PostMapping("/reservations")
    public ResponseEntity<Map<String, String>> makeReservation(@RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");

        // stoId를 String으로 가져온 후 Long으로 변환
        Long stoId;
        try {
            // stoId를 String으로 가져온 후 Long으로 변환
            stoId = Long.valueOf(String.valueOf(request.get("stoId"))); 
        } catch (NumberFormatException e) {
            throw new RuntimeException("stoId 형식이 유효하지 않습니다."); // 예외 처리
        }

        String reservationTime = (String) request.get("reservationTime");

        // 회원 정보 가져오기
        Member member = memberRepository.findByMemberId(userId);
        if (member == null) {
            throw new RuntimeException("로그인되지 않았습니다.");
        }

        // 가게 정보 가져오기
        AllStore allStore = allStoreRepository.findById(stoId)
                .orElseThrow(() -> new RuntimeException("가게가 존재하지 않습니다."));

        // 동일한 회원과 가게에 대한 기존 예약 확인
        boolean exists = reservationRepository.existsByMemberAndAllStore(member, allStore);
        if (exists) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "이미 예약된 장소입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict 상태 코드
        }

        // 예약 생성
        StoreReservation reservation = new StoreReservation();
        reservation.setReserveTime(reservationTime);
        reservation.setAccepted(false);
        reservation.setMember(member);
        reservation.setAllStore(allStore);

        // 예약 저장
        reservationRepository.save(reservation);
        String memberName = member.getMemberName();
     // 이메일 전송
        String subject = "예약 확인";
        String body = String.format("안녕하세요! %s님, 예약 신청이 완료되었습니다. 예약 시간: %s", 
        		memberName, reservationTime);
        emailService.sendSimpleMessage(member.getMemberEmail(), subject, body); // 회원 이메일로 전송
        
        // JSON 형태로 응답 생성
        Map<String, String> response = new HashMap<>();
        response.put("message", "예약 신청이 완료되었습니다.");
        
        

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/reservations")
    public ResponseEntity<List<Map<String, Object>>> getReservations(@RequestParam("memberId") String memberId) {
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new RuntimeException("회원이 존재하지 않습니다.");
        }

        List<StoreReservation> reservations = reservationRepository.findByMember(member);

        // 예약 정보를 Map 형태로 변환
        List<Map<String, Object>> response = reservations.stream()
                .map(reservation -> {
                    Map<String, Object> reservationInfo = new HashMap<>();
                    reservationInfo.put("id", reservation.getId()); // 예약 ID 추가
                    reservationInfo.put("storeName", reservation.getAllStore().getStoName()); // 가게 이름
                    reservationInfo.put("reserveTime", reservation.getReserveTime());       // 예약 시간
                    reservationInfo.put("accepted", reservation.isAccepted());               // 수락 여부
                    reservationInfo.put("memberId", reservation.getMember().getMemberId());
                    reservationInfo.put("stoId", reservation.getAllStore().getStoId()); // 가게 ID 추가
                    return reservationInfo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cancelReservation")
    public ResponseEntity<Map<String, String>> cancelReservation(@RequestBody Map<String, Object> request) {
        String memberId = (String) request.get("memberId");
        Long reservationId = ((Number) request.get("reservationId")).longValue(); // 예약 ID를 Long으로 변환

        // 회원 정보 가져오기
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new RuntimeException("회원이 존재하지 않습니다.");
        }
        
        // 예약 찾기
        Optional<StoreReservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            StoreReservation reservation = optionalReservation.get();
            // 예약의 회원 정보가 요청한 회원과 일치하는지 확인
            if (!reservation.getMember().getMemberId().equals(memberId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "예약을 취소할 권한이 없습니다."));
            }
            // 예약 삭제
            reservationRepository.delete(reservation);
            return ResponseEntity.ok(Map.of("message", "예약이 성공적으로 취소되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "예약을 찾을 수 없습니다."));
        }
    }
    
    @GetMapping("/myStoreReservation")
    public ResponseEntity<Map<String, Object>> mypage(@RequestParam("memberId") String memberId) {
        // 회원 정보 조회
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Member not found"));
        }

        String businessName = member.getBusinessName();
        System.out.println("businessName = "+businessName);
        // 가게 예약 내역 조회
        List<StoreReservation> reservations = reservationRepository
                .findByMember_BusinessNameAndAllStore_StoName(businessName, businessName);

        // 예약 정보를 Map 형태로 변환
        List<Map<String, Object>> reservationList = reservations.stream()
                .map(reservation -> {
                    Map<String, Object> reservationInfo = new HashMap<>();
                    reservationInfo.put("id", reservation.getId());
                    reservationInfo.put("storeName", reservation.getAllStore().getStoName());
                    reservationInfo.put("reserveTime", reservation.getReserveTime());
                    reservationInfo.put("accepted", reservation.isAccepted());
                    reservationInfo.put("memberId", reservation.getMember().getMemberId());
                    return reservationInfo;
                })
                .collect(Collectors.toList());

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("member", member);
        response.put("reservations", reservationList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/allReservations")
    public ResponseEntity<Map<String, Object>> getAllReservations() {
        // 모든 예약 조회
        List<StoreReservation> reservations = reservationRepository.findAll();

        // 예약 정보를 Map 형태로 변환
        List<Map<String, Object>> reservationList = reservations.stream()
                .map(reservation -> {
                    Map<String, Object> reservationInfo = new HashMap<>();
                    reservationInfo.put("id", reservation.getId());
                    reservationInfo.put("storeName", reservation.getAllStore().getStoName());
                    reservationInfo.put("reserveTime", reservation.getReserveTime());
                    reservationInfo.put("accepted", reservation.isAccepted());
                    reservationInfo.put("memberId", reservation.getMember().getMemberId());
                    return reservationInfo;
                })
                .collect(Collectors.toList());

        // 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("reservations", reservationList);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/confirmReservation")
    public ResponseEntity<Map<String, Object>> confirmReservation(@RequestParam("reservationId") Long reservationId, @RequestBody Map<String, String> request) {
        // 예약 조회
        Optional<StoreReservation> optionalReservation = reservationRepository.findById(reservationId);
        
        if (!optionalReservation.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "예약을 찾을 수 없습니다."));
        }

        StoreReservation reservation = optionalReservation.get(); // Optional에서 값 가져오기

        // 수락 상태로 변경
        reservation.setAccepted(true);
        reservationRepository.save(reservation); // 데이터베이스에 저장
     // 이메일 전송
        String subject = "예약 확정";
        String body = String.format(
        	    "안녕하세요, %s님!\n\n" +
        	    "고객님께서 요청하신 예약이 성공적으로 확정되었습니다.\n" +
        	    "예약 내용은 다음과 같습니다:\n" +
        	    "- 가게 이름: %s\n" +
        	    "- 주소: %s\n" + // 주소 추가
        	    "- 예약 시간: %s\n\n" +
        	    "저희 레스토랑에서 고객님을 뵙게 되어 매우 기쁩니다. " +
        	    "예약 시간에 맞춰 방문해 주시기 바랍니다. " +
        	    "더 좋은 서비스를 위해 최선을 다하겠습니다.\n\n" +
        	    "감사합니다!\n" +
        	    "레스토랑 팀 드림",
        	    reservation.getMember().getMemberName(),
        	    reservation.getAllStore().getStoName(), // 가게 이름
        	    reservation.getAllStore().getStoAddress(), // 가게 주소
        	    reservation.getReserveTime() // 예약 시간
        	);        emailService.sendSimpleMessage(reservation.getMember().getMemberEmail(), subject, body); // 회원 이메일로 전송

        return ResponseEntity.ok(Map.of("success", true));
    }
}