package com.example.order_service;

import io.dapr.Topic;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.CloudEvent;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


record Order(String itemId, String customerId, String orderId) {}

@RestController
@RequestMapping("/api/order")
public class OrderController {


    private final DaprClient daprClient;

    public OrderController() {
        this.daprClient = new DaprClientBuilder().build();
    }


    @PostMapping("/book")
    public Order book(@RequestBody Order order) {
        Order updateOrder = new Order(order.itemId(), order.customerId(), UUID.randomUUID().toString());
        daprClient.saveState("statestore", updateOrder.orderId(), updateOrder).block();
        return daprClient.getState("statestore", updateOrder.orderId(), Order.class).block().getValue();
    }

    @GetMapping("/orderDetails")
    public Order orderDetails(@RequestParam("orderId") String orderId) {
        return daprClient.getState("statestore", orderId, Order.class).block().getValue();
    }

    @PostMapping("/notification")
    public void sendNotification(@RequestBody Order order){
        daprClient.publishEvent("pubsub", "orders", order).block();
    }
}



