package org.bs.Batch.repository;

import org.bs.Batch.domain.accounts.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Accounts, Long> {
}
