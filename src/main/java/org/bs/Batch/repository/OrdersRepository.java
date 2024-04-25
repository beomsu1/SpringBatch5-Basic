package org.bs.Batch.repository;

import org.bs.Batch.domain.orders.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
