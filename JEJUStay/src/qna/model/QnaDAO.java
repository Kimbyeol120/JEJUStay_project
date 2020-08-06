package qna.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import mvc.database.DBConnection;

public class QnaDAO {

	private static QnaDAO instance; //멤버필드 변수 선언
	
	private QnaDAO() {} //기본생성자

	public static QnaDAO getInstance() {
		if (instance == null)
			instance = new QnaDAO();
		return instance;
	}	
	//Qna 테이블의 레코드(튜플,행) 개수
	public int getListCount(String items, String text) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;//레코드 개수

		int x = 0;

		String sql;
		
		if (items == null && text == null)
			sql = "SELECT count(*) from tbl_qna"; //count: 레코드 갯수 출력  
		else
			sql = "SELECT count(*) FROM tbl_qna where " + items + " like '%" + text + "%'";
		
		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) 
				x = rs.getInt(1);
			
		} catch (Exception ex) {
			System.out.println("getListCount() 에러: " + ex);
		} finally {			
			try {				
				if (rs != null) 
					rs.close();							
				if (pstmt != null) 
					pstmt.close();				
				if (conn != null) 
					conn.close();												
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}		
		}		
		return x;
	}
	//Qna 테이블의 레코드 가져오기
	public ArrayList<QnaDTO> getQnaList(int page, int limit, String items, String text) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int total_record = getListCount(items, text);
		int start = (page - 1) * limit;
		int index = start + 1;

		String sql;

		if (items == null && text == null)
			sql = "select * from tbl_qna ORDER BY num DESC";
		else
			sql = "SELECT * FROM tbl_qna where " + items + " like '%" + text + "%' ORDER BY num DESC ";

		ArrayList<QnaDTO> list = new ArrayList<QnaDTO>();

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.absolute(index)) {
				QnaDTO qna = new QnaDTO();
				qna.setNum(rs.getInt("num"));
				qna.setSubject(rs.getString("subject"));
				qna.setContent(rs.getString("content"));
				qna.setHit(rs.getInt("hit"));
				list.add(qna);

				if (index < (start + limit) && index <= total_record)
					index++;
				else
					break;
			}
			return list;
		} catch (Exception ex) {
			System.out.println("getQnaList() 에러 : " + ex);
		} finally {
			try {
				if (rs != null) 
					rs.close();							
				if (pstmt != null) 
					pstmt.close();				
				if (conn != null) 
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}			
		}
		return null;
	}

	//qna 테이블에 새로운 글 삽입히가
	public void insertQna(QnaDTO qna)  {
	
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBConnection.getConnection();		

			String sql = "insert into tbl_qna values(?, ?, ?, ?, ?, ?, ?)";
		
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, qna.getNum());
			pstmt.setString(2, qna.getSubject());
			pstmt.setString(3, qna.getContent());
			pstmt.setInt(4, qna.getHit());

			pstmt.executeUpdate();
		} catch (Exception ex) {
			System.out.println("insertQna() 에러 : " + ex);
		} finally {
			try {									
				if (pstmt != null) 
					pstmt.close();				
				if (conn != null) 
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}		
		}		
	} 
	//선택된 글의 조회수 증가하기
	public void updateHit(int num) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBConnection.getConnection();

			String sql = "select hit from tbl_qna where num = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			int hit = 0;

			if (rs.next())
				hit = rs.getInt("hit") + 1;
		
			sql = "update tbl_qna set hit=? where num=?";
			pstmt = conn.prepareStatement(sql);		
			pstmt.setInt(1, hit);
			pstmt.setInt(2, num);
			pstmt.executeUpdate();
		} catch (Exception ex) {
			System.out.println("updateHit() 에러 : " + ex);
		} finally {
			try {
				if (rs != null) 
					rs.close();							
				if (pstmt != null) 
					pstmt.close();				
				if (conn != null) 
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}			
		}
	}
	
	//선택된 글 상세 내용 가져오기
	public QnaDTO getQnaByNum(int num, int page) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		QnaDTO qna = null;

		updateHit(num);
		String sql = "select * from tbl_qna where num = ?";

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				qna = new QnaDTO();
				qna.setNum(rs.getInt("num"));
				qna.setSubject(rs.getString("subject"));
				qna.setContent(rs.getString("content"));
				qna.setHit(rs.getInt("hit"));
			}
			
			return qna;
		} catch (Exception ex) {
			System.out.println("getQnaByNum() 에러 : " + ex);
		} finally {
			try {
				if (rs != null) 
					rs.close();							
				if (pstmt != null) 
					pstmt.close();				
				if (conn != null) 
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}		
		}
		return null;
	}
	//선택된 글 내용 수정하기
	public void updateQna(QnaDTO qna) {

		Connection conn = null;
		PreparedStatement pstmt = null;
	
		try {
			String sql = "update tbl_qna set subject=?, content=? where num=?";

			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			
			conn.setAutoCommit(false);

			pstmt.setString(1, qna.getSubject());
			pstmt.setString(2, qna.getContent());
			pstmt.setInt(3, qna.getNum());

			pstmt.executeUpdate();			
			conn.commit();

		} catch (Exception ex) {
			System.out.println("updateQna() 에러 : " + ex);
		} finally {
			try {										
				if (pstmt != null) 
					pstmt.close();				
				if (conn != null) 
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}		
		}
	} 
	//선택된 글 삭제하기
	public void deleteQna(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;		

		String sql = "delete from tbl_qna where num=?";	

		try {
			conn = DBConnection.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();

		} catch (Exception ex) {
			System.out.println("deleteQna() 에러 : " + ex);
		} finally {
			try {										
				if (pstmt != null) 
					pstmt.close();				
				if (conn != null) 
					conn.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex.getMessage());
			}		
		}
	}	
}
