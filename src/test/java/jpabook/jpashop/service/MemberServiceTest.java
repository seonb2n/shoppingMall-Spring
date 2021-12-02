package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepositoryOld;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepositoryOld memberRepository;
    @Autowired
    EntityManager em;

    @Test
    public void join() throws Exception {
        Member member = new Member();
        member.setName("kim");

        Long savedId = memberService.join(member);

        em.flush();
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void check_member_exception() throws Exception {
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        memberService.join(member1);
        try {
            memberService.join(member2);
        } catch (IllegalStateException e) {
            return;
        }

        fail("예외가 발생해야 함");
    }
}