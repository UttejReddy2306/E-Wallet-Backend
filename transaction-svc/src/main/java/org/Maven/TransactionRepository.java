package org.Maven;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {


    Transaction findByExternalTxnId(String externalTxnId);
}
