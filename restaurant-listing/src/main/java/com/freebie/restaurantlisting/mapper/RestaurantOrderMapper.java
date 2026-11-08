package com.freebie.restaurantlisting.mapper;

import com.freebie.restaurantlisting.dto.RestaurantOrderResponse;
import com.freebie.restaurantlisting.entity.OrderLine;
import com.freebie.restaurantlisting.entity.RestaurantOrder;
import com.freebie.restaurantlisting.event.OrderCreatedEvent;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RestaurantOrderMapper {

    public RestaurantOrder toEntity(OrderCreatedEvent e) {
        List<OrderLine> lines = e.items().stream()
                .map(i -> new OrderLine(i.name(), i.quantity())).toList();
        return new RestaurantOrder(e.orderId(), e.restaurantId(), e.customerId(), e.total(), lines);
    }

    public RestaurantOrderResponse toResponse(RestaurantOrder o) {
        List<RestaurantOrderResponse.Line> lines = o.getLines().stream()
                .map(l -> new RestaurantOrderResponse.Line(l.getItemName(), l.getQuantity())).toList();
        return new RestaurantOrderResponse(o.getId(), o.getRestaurantId(), o.getCustomerId(), o.getTotal(), o.getStatus(),
                o.getRejectionReason(), lines, o.getCreatedAt(), o.getUpdatedAt());
    }
}
