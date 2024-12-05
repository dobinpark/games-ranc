package jp.games_ranc.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jp.games_ranc.DTO.token.TokenResponse;
import jp.games_ranc.DTO.user.LoginRequest;
import jp.games_ranc.DTO.user.SignUpRequest;
import jp.games_ranc.DTO.user.UserApiResponse;
import jp.games_ranc.DTO.user.UserUpdateRequest;
import jp.games_ranc.entity.User;
import jp.games_ranc.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    // 회원가입
    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserApiResponse> signup(@RequestBody @Valid SignUpRequest request) {
        try {
            userService.signup(request);
            return ResponseEntity.ok(new UserApiResponse(true, "회원가입이 완료되었습니다."));
        } catch (Exception e) {
            log.error("회원가입 실패", e);
            return ResponseEntity.badRequest()
                    .body(new UserApiResponse(false, "회원가입에 실패했습니다: " + e.getMessage()));
        }
    }

    // 로그인
    @Operation(summary = "로그인", description = "사용자 인증을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 인증 정보")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        try {
            TokenResponse tokenResponse = userService.login(request);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                    .body(new UserApiResponse(true, "로그인에 성공했습니다."));
        } catch (Exception e) {
            log.error("로그인 실패", e);
            return ResponseEntity.badRequest()
                    .body(new UserApiResponse(false, "로그인에 실패했습니다: " + e.getMessage()));
        }
    }

    // 회원 정보 조회
    @Operation(summary = "회원 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{email}")
    public ResponseEntity<?> getUser(@Parameter(description = "사용자 이메일") @PathVariable String email) {
        try {
            User user = userService.findByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new UserApiResponse(false, "사용자 조회에 실패했습니다: " + e.getMessage()));
        }
    }

    // 회원 정보 수정
    @Operation(summary = "회원 정보 수정", description = "사용자 정보를 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{email}")
    public ResponseEntity<?> updateUser(@Parameter(description = "사용자 이메일") @PathVariable String email,
                                        @RequestBody @Valid UserUpdateRequest request) {
        try {
            userService.updateUser(email, request);
            return ResponseEntity.ok(new UserApiResponse(true, "회원 정보가 수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new UserApiResponse(false, "회원 정보 수정에 실패했습니다: " + e.getMessage()));
        }
    }

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{email}")
    public ResponseEntity<?> deleteUser(@Parameter(description = "사용자 이메일") @PathVariable String email) {
        try {
            userService.deleteUser(email);
            return ResponseEntity.ok(new UserApiResponse(true, "회원이 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new UserApiResponse(false, "회원 삭제에 실패했습니다: " + e.getMessage()));
        }
    }
}