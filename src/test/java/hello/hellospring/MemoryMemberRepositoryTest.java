package hello.hellospring;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MemoryMemberRepositoryTest {
    MemoryMemberRepository repository = new MemoryMemberRepository();

    @Test
    public void save12(){
        Member member1 = new Member();
        member1.setName("Spring");

        repository.save(member1);

       Member result = repository.findById(member1.getId()).get();
        Assertions.assertThat(result).isEqualTo(member1);

    }
}
