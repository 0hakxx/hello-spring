package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTemplateMemberRepository implements MemberRepository {
    private final JdbcTemplate jdbcTemplate; // JDBC 작업을 위한 핵심 객체

    // DataSource를 주입받아 JdbcTemplate을 초기화합니다.
    // 스프링이 데이터베이스 연결 정보를 가진 DataSource 빈을 자동으로 주입해줍니다.
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        // SimpleJdbcInsert: INSERT 쿼리를 보다 쉽게 작성하고 자동 생성된 키를 얻을 수 있도록 돕는 유틸리티 클래스입니다.
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        // INSERT할 테이블 이름을 "member"로 지정합니다.
        jdbcInsert.withTableName("member")
                // 데이터베이스에서 자동 생성되는 'id' 컬럼의 값을 가져오도록 설정합니다.
                .usingGeneratedKeyColumns("id");

        // INSERT 쿼리에 사용할 파라미터들을 Map 형태로 준비합니다.
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", member.getName()); // 'name' 컬럼에 Member 객체의 이름을 매핑합니다.

        // executeAndReturnKey() 메소드를 실행하여 INSERT 쿼리를 날리고, 자동 생성된 'id' 값을 반환받습니다.
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        // 반환받은 'id' 값을 Member 객체에 설정합니다.
        member.setId(key.longValue());
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        // jdbcTemplate.query(): SQL 쿼리를 실행하고 결과를 RowMapper를 통해 객체로 매핑합니다.
        // 첫 번째 인자는 SQL 쿼리, 두 번째 인자는 RowMapper, 세 번째 인자부터는 쿼리에 바인딩될 파라미터입니다.
        List<Member> result = jdbcTemplate.query("select * from member where id = ?", memberRowMapper(), id);
        // 결과를 Optional로 감싸서 반환하여, 결과가 없을 경우를 안전하게 처리합니다.
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        // 모든 회원을 조회하는 쿼리를 실행하고, 각 행을 Member 객체로 매핑하여 리스트로 반환합니다.
        return jdbcTemplate.query("select * from member", memberRowMapper());
    }

    @Override
    public Optional<Member> findByName(String name) {
        // 이름으로 회원을 조회하는 쿼리를 실행하고, 결과를 Optional로 반환합니다.
        List<Member> result = jdbcTemplate.query("select * from member where name = ?", memberRowMapper(), name);
        return result.stream().findAny();
    }

    // RowMapper<Member>: 데이터베이스 ResultSet의 각 행(row)을 Member 객체로 변환하는 방법을 정의합니다.
    // 이 람다식은 ResultSet에서 'id'와 'name' 컬럼의 값을 가져와 새로운 Member 객체를 생성하고 반환합니다.
    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> { // rs: ResultSet (쿼리 결과), rowNum: 현재 행 번호
            Member member = new Member();
            member.setId(rs.getLong("id"));     // ResultSet에서 'id' 컬럼의 값을 가져와 Member 객체에 설정
            member.setName(rs.getString("name")); // ResultSet에서 'name' 컬럼의 값을 가져와 Member 객체에 설정
            return member;
        };
    }
}