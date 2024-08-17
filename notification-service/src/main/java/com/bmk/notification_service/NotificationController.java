package com.bmk.notification_service;

import io.dapr.Topic;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.CloudEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



record Order(String itemId, String customerId, String orderId) {}

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final DaprClient daprClient;

    public NotificationController() {
        this.daprClient = new DaprClientBuilder().build();
    }



    @PostMapping("/orderNotificationSubscriber")
    @Topic(pubsubName = "pubsub", name="orders")
    void getNotificationDetails(@RequestBody CloudEvent<Order> cloudEvent){
        System.out.println("Order Notification Received.....................");
        System.out.println("OrderId: " + cloudEvent.getData().orderId());
        System.out.println("CustomerId:  " + cloudEvent.getData().customerId());
        System.out.println("ItemId:" + cloudEvent.getData().itemId());
    }
}
