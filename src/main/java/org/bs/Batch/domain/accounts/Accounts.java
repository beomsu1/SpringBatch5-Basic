package org.bs.Batch.domain.accounts;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.bs.Batch.domain.orders.Orders;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderItem;
    private Integer price;
    private Date orderDate;
    private Date accountDate;

    public Accounts(Orders orders){
        this.id = orders.getId();
        this.orderItem = orders.getOrderItem();
        this.price = orders.getPrice();
        this.orderDate = orders.getOrderDate();
        this.accountDate = new Date();
    }
}
