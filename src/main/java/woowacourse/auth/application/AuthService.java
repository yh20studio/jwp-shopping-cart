package woowacourse.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.auth.dto.LoginCustomer;
import woowacourse.auth.dto.TokenRequest;
import woowacourse.auth.dto.TokenResponse;
import woowacourse.auth.exception.LoginFailureException;
import woowacourse.auth.support.JwtTokenProvider;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.customer.Customer;
import woowacourse.shoppingcart.domain.customer.PasswordEncryptor;
import woowacourse.shoppingcart.exception.InvalidCustomerException;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncryptor passwordEncryptor;
    private final CustomerDao customerDao;

    public AuthService(final JwtTokenProvider jwtTokenProvider,
                       final PasswordEncryptor passwordEncryptor, final CustomerDao customerDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncryptor = passwordEncryptor;
        this.customerDao = customerDao;
    }

    public LoginCustomer getAuthenticatedCustomer(final String token) {
        Long id = Long.parseLong(jwtTokenProvider.getPayload(token));
        final Customer customer = customerDao.findById(id)
                .orElseThrow(InvalidCustomerException::new);
        return new LoginCustomer(customer.getId());
    }

    public TokenResponse login(final TokenRequest tokenRequest) {
        final Customer customer = customerDao.findByUserName(tokenRequest.getUserName())
                .orElseThrow(LoginFailureException::new);
        validatePassword(tokenRequest, customer);
        final String token = jwtTokenProvider.createToken(String.valueOf(customer.getId()));
        return new TokenResponse(token);
    }

    private void validatePassword(final TokenRequest tokenRequest, final Customer customer) {
        if (!customer.matchesPassword(passwordEncryptor, tokenRequest.getPassword())) {
            throw new LoginFailureException();
        }
    }
}
