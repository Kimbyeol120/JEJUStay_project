package mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import qna.model.QnaDAO;
import qna.model.QnaDTO;

public class QnaController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static final int LISTCOUNT = 5; //한 페이지 최대 글 수

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String RequestURI = request.getRequestURI(); 
		String contextPath = request.getContextPath(); //http://localhost:9000/WebMarket/까지 주소의 길이 (뒤에 주소)QnaListAction.do?pageNum=1
		String command = RequestURI.substring(contextPath.length());//QnaListAction.do?pageNum=1 
		
		response.setContentType("text/html; charset=utf-8");
		request.setCharacterEncoding("utf-8");
	
		if (command.equals("/QnaListAction.do")) {//등록된 글 목록 페이지 출력하기.3
			requestQnaList(request);
			RequestDispatcher rd = request.getRequestDispatcher("./qna/list.jsp");
			rd.forward(request, response);
			
		} else if (command.equals("/QnaViewAction.do")) {//선택된 글 상세 페이지 가져오기
				requestQnaView(request);
				RequestDispatcher rd = request.getRequestDispatcher("/QnaView.do");
				rd.forward(request, response);						
		} else if (command.equals("/QnaView.do")) { //글 상세 페이지 출력하기
				RequestDispatcher rd = request.getRequestDispatcher("./qna/view.jsp");
				rd.forward(request, response);	
				
		} else if (command.equals("/QnaUpdateAction.do")) { //선택된 글의 조회수 증가하기
				requestQnaUpdate(request);
				RequestDispatcher rd = request.getRequestDispatcher("/QnaListAction.do");
				rd.forward(request, response);
				
		}else if (command.equals("/QnaDeleteAction.do")) { //선택된 글 삭제하기
				requestQnaDelete(request);
				RequestDispatcher rd = request.getRequestDispatcher("/QnaListAction.do");
				rd.forward(request, response);				
		} 
	}
	//등록된 글 목록 가져오기	
	public void requestQnaList(HttpServletRequest request){
			
		QnaDAO dao = QnaDAO.getInstance();
		List<QnaDTO> qnalist = new ArrayList<QnaDTO>();
		
	  	int pageNum=1;
		int limit=LISTCOUNT;
		
		if(request.getParameter("pageNum")!=null)
			pageNum=Integer.parseInt(request.getParameter("pageNum"));
				
		String items = request.getParameter("items");
		String text = request.getParameter("text");
		
		int total_record=dao.getListCount(items, text);
		qnalist = dao.getQnaList(pageNum,limit, items, text); 
		
		int total_page;
		
		if (total_record % limit == 0){     
	     	total_page =total_record/limit;
	     	Math.floor(total_page);  
		}
		else{
		   total_page =total_record/limit;
		   Math.floor(total_page); 
		   total_page =  total_page + 1; 
		}		
   
   		request.setAttribute("pageNum", pageNum);		  
   		request.setAttribute("total_page", total_page);   
		request.setAttribute("total_record",total_record); 
		request.setAttribute("qnalist", qnalist);								
	}
	
	
	//선택된 글 상세 페이지 가져오기
	public void requestQnaView(HttpServletRequest request){
					 
		QnaDAO dao = QnaDAO.getInstance();
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		QnaDTO qna = new QnaDTO();
		qna = dao.getQnaByNum(num, pageNum);		
		
		request.setAttribute("num", num);		 
   		request.setAttribute("page", pageNum); 
   		request.setAttribute("qna", qna);   									
	}
	//선택된 글 내용 수정하기
	public void requestQnaUpdate(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		QnaDAO dao = QnaDAO.getInstance();		
		
		QnaDTO qna = new QnaDTO();		
		qna.setNum(num);
		qna.setSubject(request.getParameter("subject"));
		qna.setContent(request.getParameter("content"));		
		
		 java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd(HH:mm:ss)");
		 String regist_day = formatter.format(new java.util.Date()); 
		 
		 qna.setHit(0);		
		
		 dao.updateQna(qna);								
	}
	//선택된 글 삭제하기
	public void requestQnaDelete(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		QnaDAO dao = QnaDAO.getInstance();
		dao.deleteQna(num);							
	}	
}
