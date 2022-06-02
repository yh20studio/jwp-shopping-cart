package woowacourse.auth.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static woowacourse.fixture.PasswordFixture.rowBasicPassword;
import static woowacourse.fixture.TokenFixture.BEARER;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import woowacourse.auth.dto.TokenRequest;
import woowacourse.auth.dto.TokenResponse;
import woowacourse.shoppingcart.acceptance.AcceptanceTest;
import woowacourse.shoppingcart.dto.CustomerRequest.UserNameAndPassword;
import woowacourse.shoppingcart.dto.CustomerResponse;
import woowacourse.shoppingcart.dto.ErrorResponse;

@DisplayName("인증 관련 기능")
public class AuthAcceptanceTest extends AcceptanceTest {

    @DisplayName("등록된 회원이 로그인을 하면 토큰을 발급받는다.")
    @Test
    void loginReturnBearerToken() {
        // given
        UserNameAndPassword request = new UserNameAndPassword("기론", rowBasicPassword);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/customers")
                .then().log().all()
                .extract();

        // when
        final TokenRequest tokenRequest = new TokenRequest("기론", rowBasicPassword);
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/api/login")
                .then().log().all()
                .extract();
        final TokenResponse tokenResponse = extract.as(TokenResponse.class);

        // then
        assertThat(tokenResponse.getAccessToken()).isNotNull();
    }

    @DisplayName("Bearer Auth 로그인 성공하고, 내 정보 조회를 요청하면 내 정보가 조회된다.")
    @Test
    void myInfoWithBearerAuth() {
        // given
        // 회원이 등록되어 있고
        UserNameAndPassword request = new UserNameAndPassword("기론", rowBasicPassword);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/customers")
                .then().log().all()
                .extract();

        // id, password를 사용해 토큰을 발급받고
        final TokenRequest tokenRequest = new TokenRequest("기론", rowBasicPassword);
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/api/login")
                .then().log().all()
                .extract();
        final TokenResponse tokenResponse = extract.as(TokenResponse.class);

        // when
        // 발급 받은 토큰을 사용하여 내 정보 조회를 요청하면
        final ExtractableResponse<Response> responseMe = RestAssured
                .given().log().all()
                .header("Authorization", BEARER + tokenResponse.getAccessToken())
                .when().get("/api/customers/me")
                .then().log().all()
                .extract();
        final CustomerResponse customerResponse = responseMe.as(CustomerResponse.class);
        // then
        // 내 정보가 조회된다
        assertThat(customerResponse.getUserName()).isEqualTo("기론");
    }

    @DisplayName("Bearer Auth 로그인 실패 - 유저 이름이 잘못된 경우 404-NOT_FOUND를 반환한다.")
    @Test
    void loginFailureWithWrongUserName() {
        // given
        // 회원이 등록되어 있고
        UserNameAndPassword request = new UserNameAndPassword("기론", rowBasicPassword);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/customers")
                .then().log().all()
                .extract();
        // when
        // 잘못된 id, password를 사용해 토큰을 요청하면
        final TokenRequest tokenRequest = new TokenRequest("티키", rowBasicPassword);
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/api/login")
                .then().log().all()
                .extract();

        final ErrorResponse errorResponse = extract.as(ErrorResponse.class);
        // then
        // 토큰 발급 요청이 거부된다
        assertAll(
                () -> assertThat(extract.header(HttpHeaders.AUTHORIZATION)).isNull(),
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(errorResponse.getMessage()).isEqualTo("존재하지 않는 유저입니다.")
        );
    }

    @DisplayName("Bearer Auth 로그인 실패 - 비밀번호가 잘못된 경우 404-NOT_FOUND를 반환한다.")
    @Test
    void loginFailureWithWrongPassword() {
        // given
        // 회원이 등록되어 있고
        UserNameAndPassword request = new UserNameAndPassword("기론", rowBasicPassword);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/customers")
                .then().log().all()
                .extract();
        // when
        // 잘못된 id, password를 사용해 토큰을 요청하면
        final TokenRequest tokenRequest = new TokenRequest("기론", "wrongPassword");
        final ExtractableResponse<Response> extract = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenRequest)
                .when().post("/api/login")
                .then().log().all()
                .extract();
        final ErrorResponse errorResponse = extract.as(ErrorResponse.class);
        // then
        // 토큰 발급 요청이 거부된다
        assertAll(
                () -> assertThat(extract.header(HttpHeaders.AUTHORIZATION)).isNull(),
                () -> assertThat(extract.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(errorResponse.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.")
        );
    }

    @DisplayName("Bearer Auth 유효하지 않은 토큰으로 접근했을 때 - 401 UNAUTHORIZED를 반환한다.")
    @Test
    void myInfoWithWrongBearerAuth() {
        // given
        // 회원이 등록되어 있고
        UserNameAndPassword request = new UserNameAndPassword("기론", rowBasicPassword);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/customers")
                .then().log().all()
                .extract();

        // when
        // 유효하지 않은 토큰을 사용하여 내 정보 조회를 요청하면
        final ExtractableResponse<Response> responseMe = RestAssured
                .given().log().all()
                .header("Authorization", BEARER + "wrongAccessToken")
                .when().get("/api/customers/me")
                .then().log().all()
                .extract();
        final ErrorResponse errorResponse = responseMe.as(ErrorResponse.class);

        // then
        // 내 정보 조회 요청이 거부된다
        assertAll(
                () -> assertThat(responseMe.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()),
                () -> assertThat(errorResponse.getMessage()).isEqualTo("유효하지 않은 토큰입니다.")
        );
    }
}
