package woowacourse.shoppingcart.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import woowacourse.shoppingcart.domain.Customer;

@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CustomerDaoTest {

    private final CustomerDao customerDao;

    public CustomerDaoTest(JdbcTemplate jdbcTemplate) {
        customerDao = new CustomerDao(jdbcTemplate);
    }

    @DisplayName("username을 통해 아이디를 찾으면, id를 반환한다.")
    @Test
    void findIdByUserNameTest() {

        // given
        final String userName = "puterism";

        // when
        final Long customerId = customerDao.findIdByUserName(userName);

        // then
        assertThat(customerId).isEqualTo(1L);
    }

    @DisplayName("대소문자를 구별하지 않고 username을 통해 아이디를 찾으면, id를 반환한다.")
    @Test
    void findIdByUserNameTestIgnoreUpperLowerCase() {

        // given
        final String userName = "gwangyeol-iM";

        // when
        final Long customerId = customerDao.findIdByUserName(userName);

        // then
        assertThat(customerId).isEqualTo(16L);
    }

    @DisplayName("유저를 저장한다.")
    @Test
    void save() {

        // given
        final String userName = "tiki";
        final String password = "password";

        // when
        Long id = customerDao.save(userName, password);

        // then
        assertThat(id).isNotNull();
    }

    @DisplayName("주어진 이름으로 대소문자 관계없이 존재하는 유저가 있으면 참을 반환한다.")
    @Test
    void existsByUserName() {

        // given
        final String alreadyExistsName = "puterisM";

        // when
        final boolean result = customerDao.existsByUserName(alreadyExistsName);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("id를 통해서 customer정보를 가져온다.")
    @Test
    void findById() {

        // given
        final Long id = 1L;

        // when
        final Customer customer = customerDao.findById(id).get();

        // then
        assertAll(
                () -> assertThat(customer.getId()).isEqualTo(id),
                () -> assertThat(customer.getUserName()).isEqualTo("puterism"),
                () -> assertThat(customer.getPassword()).isEqualTo("123")
        );
    }
}
