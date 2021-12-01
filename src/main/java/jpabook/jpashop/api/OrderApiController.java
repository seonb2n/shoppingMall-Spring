package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.simplequery.OrderFlatDto;
import jpabook.jpashop.repository.simplequery.OrderQueryDto;
import jpabook.jpashop.repository.simplequery.OrderQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> allByCriteria = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : allByCriteria) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return allByCriteria;
    }

    @GetMapping("/api/v2/orders")
    public Result ordersV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/v3/orders")
    public Result ordersV3() {
        List<Order> allWithItem = orderRepository.findAllWithItem();
        List<OrderDto> collect = allWithItem.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/v3.1/orders")
    public Result ordersV3_page(
            @RequestParam(value = "offset", defaultValue =  "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> allWithItem = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> collect = allWithItem.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/v4/orders")
    public Result ordersV4() {
        List<OrderQueryDto> list = orderQueryRepository.findOrderQueryDtos();
        return new Result(list);
    }

    @GetMapping("/api/v5/orders")
    public Result ordersV5() {
        List<OrderQueryDto> list = orderQueryRepository.findAllByDto_optimization();
        return new Result(list);
    }

    @GetMapping("/api/v6/orders")
    public Result ordersV6() {
        List<OrderFlatDto> list = orderQueryRepository.findAllByDto_flat();
        
        return new Result(list);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        T data;
    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderITemDto> orderItems;

        public OrderDto(Order o) {
            orderId = o.getId();
            name = o.getMember().getName();
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            address = o.getDelivery().getAddress();
            orderItems = o.getOrderItems().stream().map(orderItem -> new OrderITemDto(orderItem)).collect(Collectors.toList());
        }
    }

    @Data
    static class OrderITemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderITemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}

