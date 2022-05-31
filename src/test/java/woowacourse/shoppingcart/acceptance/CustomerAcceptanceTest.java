package woowacourse.shoppingcart.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import woowacourse.fixture.PasswordFixture;
import woowacourse.shoppingcart.dto.CustomerRequest;
import woowacourse.shoppingcart.dto.CustomerResponse;

@DisplayName("회원 관련 기능")
public class CustomerAcceptanceTest extends AcceptanceTest {

    @DisplayName("회원가입 성공하면 201 CREATED를 반환한다.")
    @Test
    void addCustomer() {
        // given
        CustomerRequest request = new CustomerRequest("기론", PasswordFixture.basicPassword);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/customers")
                .then().log().all()
                .extract();

        // then
        Long id = Long.parseLong(response.header("Location").split("/")[3]);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(id).isNotNull()
        );
    }

    @DisplayName("내 정보 조회하면 200 OK를 반환한다.")
    @Test
    void getMe() {
        // given
        final long id = 1L;

        // when
        ExtractableResponse<Response> extractableResponse = RestAssured
                .given().log().all()
                .when().get("/api/customers/" + id)
                .then().log().all()
                .extract();

        final CustomerResponse response = extractableResponse.as(CustomerResponse.class);

        // then
        assertAll(
                () -> assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.getUserName()).isEqualTo("puterism")
        );
    }

    @DisplayName("내 정보 수정하면 200 OK를 반환한다.")
    @Test
    void updateMe() {
        // given
        final long id = 1L;
        CustomerRequest request = new CustomerRequest("puterism", "321");

        // when
        ExtractableResponse<Response> extractableResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put("/api/customers/" + id)
                .then().log().all()
                .extract();

        final CustomerResponse response = extractableResponse.as(CustomerResponse.class);

        // then
        assertAll(
                () -> assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.getUserName()).isEqualTo("puterism")
        );
    }

    @DisplayName("회원탈퇴를 하면 204 NO_CONTENT를 반환한다.")
    @Test
    void deleteMe() {
        // given
        final long id = 1L;

        // when
        ExtractableResponse<Response> extractableResponse = RestAssured
                .given().log().all()
                .when().delete("/api/customers/" + id)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> findResponse = RestAssured
                .given().log().all()
                .when().get("/api/customers/" + id)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
        );
    }
}
