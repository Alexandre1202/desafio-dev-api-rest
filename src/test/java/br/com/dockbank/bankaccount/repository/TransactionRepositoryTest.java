package br.com.dockbank.bankaccount.repository;

import static br.com.dockbank.bankaccount.mother.CustomerMother.getCustomerBank;
import static br.com.dockbank.bankaccount.mother.TransactionMother.getTransactionBank;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.dockbank.bankaccount.domain.entity.AccountBank;
import br.com.dockbank.bankaccount.domain.entity.CustomerBank;
import br.com.dockbank.bankaccount.domain.entity.TransactionBank;
import br.com.dockbank.bankaccount.mother.AccountMother;
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

    @Test
    void testFindTransactionBanksByAccountBank_AccountIdAndAccountBank_AccountActiveAndTransactionCreatedEquals() {
        CustomerBank customerBank = getCustomerBank();
        customerBank = em.persist(customerBank);
        em.flush();
        em.clear();

        AccountBank accountBank = AccountMother.getAccountBank(customerBank);
        accountBank = em.persist(accountBank);
        em.flush();
        em.clear();

        TransactionBank transactionBank = getTransactionBank(accountBank);
        transactionBank = em.persist(transactionBank);
        em.flush();
        em.clear();

        Double result = repository
            .findTransactionBanksByAccountBank_AccountIdAndAccountBank_AccountActiveAndTransactionCreatedEquals(
                accountBank.getAccountId(), "A", LocalDate.now())
            .stream()
            .map(t -> t.getTransactionValue().doubleValue()).reduce(Double::sum).orElse(0D);

        assertThat(result).isEqualTo(20300.55D);
    }

    @Test
    void testFindTransactionBanksByAccountBank_AccountIdAndAccountBank_AccountActiveAndTransactionCreatedEqualsWithManyTransactions() {
        CustomerBank customerBank = getCustomerBank();
        customerBank = em.persist(customerBank);
        em.flush();
        em.clear();

        AccountBank accountBank = AccountMother.getAccountBank(customerBank);
        accountBank = em.persist(accountBank);
        em.flush();
        em.clear();

        TransactionBank transactionBank = getTransactionBank(accountBank);
        transactionBank = em.persist(transactionBank);
        em.flush();
        em.clear();

        transactionBank = getTransactionBank(accountBank);
        transactionBank.setTransactionValue(new BigDecimal(1200));
        transactionBank = em.persist(transactionBank);
        em.flush();
        em.clear();

        transactionBank = getTransactionBank(accountBank);
        transactionBank.setTransactionCreated(LocalDate.now().minusDays(1));
        transactionBank.setTransactionValue(new BigDecimal(2500));
        transactionBank = em.persist(transactionBank);
        em.flush();
        em.clear();

        Double result = repository
            .findTransactionBanksByAccountBank_AccountIdAndAccountBank_AccountActiveAndTransactionCreatedEquals(
                accountBank.getAccountId(), "A", LocalDate.now())
            .stream()
            .map(t -> t.getTransactionValue().doubleValue()).reduce(Double::sum).orElse(0D);

        assertThat(result).isEqualTo(21500.55D);
    }
}