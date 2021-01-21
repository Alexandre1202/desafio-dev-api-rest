package br.com.dockbank.bankaccount.repository;

import static br.com.dockbank.bankaccount.mother.CustomerMother.getCustomerBank;
import static br.com.dockbank.bankaccount.mother.TransactionMother.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.dockbank.bankaccount.domain.entity.AccountBank;
import br.com.dockbank.bankaccount.domain.entity.CustomerBank;
import br.com.dockbank.bankaccount.domain.entity.TransactionBank;
import br.com.dockbank.bankaccount.mother.AccountMother;
import br.com.dockbank.bankaccount.mother.TransactionMother;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private TransactionRepository repository;

    @Test
    void testTransactionHappyPath() {
        CustomerBank customerBank = getCustomerBank();
        customerBank = em.persist(customerBank);
        em.flush();
        em.clear();

        AccountBank accountBank = AccountMother.getAccountBank(customerBank);
        accountBank = em.persist(accountBank);
        em.flush();
        em.clear();

        TransactionBank result = getTransactionBank(accountBank);
        result = em.persist(result);
        em.flush();
        em.clear();

        assertThat(result.getAccountBank()).isEqualTo(accountBank);
        assertThat(result.getTransactionCreated()).isNotNull();
        assertThat(result.getTransactionValue()).isEqualTo(new BigDecimal(20300.55));
    }
}