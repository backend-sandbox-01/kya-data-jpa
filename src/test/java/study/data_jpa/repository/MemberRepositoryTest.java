package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    void findByAge() throws Exception {

        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent(); // 조회된 데이터
        assertEquals(3, content.size()); // 조회된 데이터 수
        assertEquals(5, page.getTotalElements()); // 전체 데이터 수
        assertEquals(0, page.getNumber()); // 페이지 번호
        assertEquals(2,page.getTotalPages()); // 전체 페이지 번호
        assertTrue(page.isFirst()); // 첫번째 항목인가?
        assertTrue(page.hasNext()); // 다음 페이지가 있는가?

    }

    @Test
    void findUser() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));

        List<Member> findMember = memberRepository.findUser("member1", 10);

        assertEquals("member1",findMember.get(0).getUsername());
        assertEquals(10,findMember.get(0).getAge());
        assertEquals(1,findMember.size());
    }

    @Test
    void findMemberDto() {

    }

    @Test
    void bulkAgePlus() {
        memberRepository.save(new Member("memberA", 10));
        memberRepository.save(new Member("memberB", 19));
        memberRepository.save(new Member("memberC", 20));
        memberRepository.save(new Member("memberD", 21));
        memberRepository.save(new Member("memberE", 40));

        int resultCount = memberRepository.bulkAgePlus(20);

        assertEquals(3, resultCount);
    }

    @Test
    void findAllByEntityGraph() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            member.getTeam().getName();
        }
    }
}