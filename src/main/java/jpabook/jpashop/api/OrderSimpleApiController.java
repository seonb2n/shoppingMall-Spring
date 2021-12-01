package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.simplequery.SimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XToOne
 * Order 조회
 * Order -> Member 연관
 * Order -> Delivery 연관
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final SimpleQueryRepository simpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //getName 을 하는 순간, lazy loading 에서 서버로 데이터를 요청한다.
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public Result ordersV2() {
        //1 + N (회원 2개) + N (배송 2개)
        List<Order> list = orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDto> collect = list.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/v3/simple-orders")
    public Result ordersV3() {
        //1 + N (회원 2개) + N (배송 2개)
        List<Order> list = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = list.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/v4/simple-orders")
    public Result ordersV4() {
        return new Result(simpleQueryRepository.findOrderDtos());
    }


    @Data
    @AllArgsConstructor
    static class Result<T> {
        T data;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
