package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void order_item_test() throws Exception {
        Member member = createMember();

        Book book = createBook("old book", 10, 10000);
        int orderCount = 3;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 수는 정확");
        assertEquals(orderCount * 10000, getOrder.getTotalPrice(), "가격 테스트");
        assertEquals(7, book.getStockQuantity(), "재고 테스트");
    }

    @Test
    public void cancel_item_test() throws Exception {
        Member member = createMember();
        Book book = createBook("old book", 10, 10000);
        int orderCount = 3;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        orderService.cancelOrder(orderId);

        assertEquals(10, book.getStockQuantity());
    }

    @Test
    public void order_item_over_quantity_test() throws Exception {
        Member member = createMember();
        Book book = createBook("old book", 10, 10000);
        int orderCount = 11;

        try {
            Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }


    private Book createBook(String bookName, int stockQuantity, int price) {
        Book book = new Book();
        book.setName(bookName);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("abc", "가나다", "123"));
        em.persist(member);
        return member;
    }
}