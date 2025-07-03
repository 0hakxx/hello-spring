package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, member.getName());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception e) { // try 블록 바로 뒤에 catch 블록 위치
            throw new IllegalStateException(e);
        } finally { // catch 블록 바로 뒤에 finally 블록 위치
            close(conn, pstmt, rs);
        }
    } // save 메소드 닫는 중괄호

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) { // try 블록 바로 뒤에 catch 블록 위치
            throw new IllegalStateException(e);
        } finally { // catch 블록 바로 뒤에 finally 블록 위치
            close(conn, pstmt, rs);
        }
    } // findById 메소드 닫는 중괄호


    @Override
    public List<Member> findAll() {
        String sql = "select * from member";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            List<Member> members = new ArrayList<>();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                members.add(member);
            }
            return members; // members 반환은 try 블록 안에서 이루어져야 함
        } catch (Exception e) { // try 블록 바로 뒤에 catch 블록 위치
            throw new IllegalStateException(e);
        } finally { // catch 블록 바로 뒤에 finally 블록 위치
            close(conn, pstmt, rs);
        }
    } // findAll 메소드 닫는 중괄호

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from member where name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            }
            // else { // Optional.empty()는 if 블록 외부에서 반환
            //    return Optional.empty();
            // }
        } catch (Exception e) { // try 블록 바로 뒤에 catch 블록 위치
            throw new IllegalStateException(e);
        } finally { // catch 블록 바로 뒤에 finally 블록 위치
            close(conn, pstmt, rs);
        }
        return Optional.empty(); // 찾지 못했을 경우 여기에서 반환
    } // findByName 메소드 닫는 중괄호


    // --- 헬퍼 메소드들 ---

    private Connection getConnection() {
        // 스프링 트랜잭션 동기화를 위한 DataSourceUtils.getConnection 사용
        return DataSourceUtils.getConnection(dataSource);
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                // 스프링 트랜잭션 동기화를 위한 DataSourceUtils.releaseConnection 사용
                close(conn); // 이 close는 아래의 오버로드된 close(Connection conn)를 호출합니다.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Connection을 닫는 오버로드된 메소드
    private void close(Connection conn) throws SQLException {
        // 스프링 트랜잭션 동기화된 커넥션을 닫을 때 사용
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
} // 클래스 닫는 중괄호