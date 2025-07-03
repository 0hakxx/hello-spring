package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Test
    void 회원가입() {
        //given
        Member member = new Member();
        member.setName("Spring1");
        //when
        Long result = memberService.join(member);
        //then
        Member member1 = memberService.findOne(result).get();
        Assertions.assertThat(member1.getName()).isEqualTo(member.getName());
        System.out.println(member1.getName());
    }

    @Test
    void 중복회원검증(){
        //given
        Member member1 = new Member();
        member1.setName("test");
        Member member2 = new Member();
        member2.setName("test");
        //when
        memberService.join(member1);
        try {
            memberService.join(member2);
            fail(); // catch에서 해당 부분이 실행되면 예외 검증 실패
        }catch (IllegalStateException e){
            Assertions.assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }

        //then

    }

}