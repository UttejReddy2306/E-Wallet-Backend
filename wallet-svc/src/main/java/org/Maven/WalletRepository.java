package org.Maven;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,String> {


    Wallet findByUserId(Integer id);

    @Modifying
    @Transactional
    @Query("update Wallet w set w.balance = w.balance + :amount  where w.userId = :userId")
    void updateWalletBalance(Integer userId, Long amount);
}
