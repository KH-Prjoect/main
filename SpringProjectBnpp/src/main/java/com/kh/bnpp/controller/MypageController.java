
package com.kh.bnpp.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kh.bnpp.clova.Clova_temp;
import com.kh.bnpp.model.biz.BillBiz;
import com.kh.bnpp.model.biz.ClassBiz;
import com.kh.bnpp.model.biz.FileUploadBiz;
import com.kh.bnpp.model.biz.FoodBiz;
import com.kh.bnpp.model.biz.MemberBiz;
import com.kh.bnpp.model.biz.PayBiz;
import com.kh.bnpp.model.dto.BillDto;
import com.kh.bnpp.model.dto.ClassDetailDto;
import com.kh.bnpp.model.dto.ClassDto;
import com.kh.bnpp.model.dto.FoodDto;
import com.kh.bnpp.model.dto.FoodListDto;
import com.kh.bnpp.model.dto.MemberDto;
import com.kh.bnpp.model.dto.PayDto;
import com.kh.bnpp.sms.SMS;

@Controller
public class MypageController {
	
	private Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private MemberBiz m_biz;
	
	@Autowired
	private PayBiz p_biz;
	
	@Autowired
	private ClassBiz c_biz;
	
	@Autowired
	private FoodBiz f_biz;
	
	@Autowired
	private BillBiz b_biz;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Value("${imgfile.Uploadpath}")
	private String imgUploadPath;	

	@RequestMapping("/receiptupload.do")
	public String receiptupload(Model model, String member_id) {
		List<BillDto> b_list = b_biz.selectList(member_id);
		model.addAttribute("member_id", member_id);
		model.addAttribute("b_list", b_list);
		return "receiptupload";
	}
	
	@RequestMapping("/scan.do")
	public String scan(@RequestParam("member_id") String member_id, @RequestParam("file") MultipartFile fm, HttpServletRequest request) {
		//파일을 realPath에 저장하기
		String uploadRealPath = request.getSession().getServletContext().getRealPath("/resources/img/receipts");
		String paths = "/resources/img/receipts/";
		
		//위에 설정한 경로의 폴더가 없을 경우 생성
		File dir = new File(uploadRealPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
				
		//파일 업로드
		if (!fm.getOriginalFilename().isEmpty()) {
			// 파일명
			String originalFile = fm.getOriginalFilename();
			System.out.println("파일명이 뭔데? : " + originalFile);
				
			String fullPathName = request.getSession().getServletContext().getRealPath(paths + originalFile);	
			try {
				fm.transferTo(new File(fullPathName));
				List<String> list = Clova_temp.OCR(fullPathName);
				
				List<BillDto> listres = Clova_temp.input_bill(list, member_id);
				
				int res = 0;
				int i = 0;
				
				for (BillDto dto : listres) {
					res += b_biz.insert(dto);
					if (b_biz.insert(dto) > 0) {
						logger.info(i + "번째 bill 삽입 성공");
					}
					i++;
				}
				if (res == listres.size()) {
					return "redirect:receiptupload.do?member_id="+member_id;
				} else {
					logger.info("내역 몇개 빠짐");
					return "redirect:receiptupload.do?member_id="+member_id;
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			logger.info("빈 파일")	;
		}	
		return "redirect:receiptupload.do?member_id="+member_id;
	}
	
	@RequestMapping("/mypage.do")
	public String mypage(String member_id) {
		
		List<FoodDto> foods = f_biz.selectList();
		String f_life, phone, content;
		MemberDto alarm_dto = new MemberDto();
		
		for (FoodDto f_dto : foods) {
			if (f_dto.getFood_alarm_yn().equals("N")) {
				f_life = f_dto.getFood_life();
				if (!f_life.equals("미설정")) {
					try {
						if (!SMS.compareDate(f_life).equals("0")) {
							alarm_dto = m_biz.selectOne(f_dto.getMember_id());
							phone = alarm_dto.getMember_phone().replace("-", "");
							content = "내 냉장고 안의 " + f_dto.getFood_name() + "의 " + SMS.compareDate(f_life);
							try {
								SMS.sendSMS(phone, content);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							if (f_biz.updateAlarm(f_dto.getFood_num()) > 0) {
								logger.info("알람 수정 완료");
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		MemberDto dto = m_biz.selectOne(member_id);
		
		if (dto.getMember_role().equals("M")) {
			return "redirect:mypage_student.do?member_id="+member_id;
		} else if (dto.getMember_role().equals("T")) {
			return "redirect:mypage_teacher.do?member_id="+member_id;
		} else {
			return "redirect:mypage_admin.do?member_id="+member_id;
		}
	}
	
	@RequestMapping("/mypage_student.do")
	public String mypage_student(Model model, String member_id) {
		MemberDto m_dto = m_biz.selectOne(member_id);
		List<PayDto> p_list = p_biz.selectMyList(member_id);
		List<ClassDto> c_list = new ArrayList<ClassDto>();
		ClassDto c_dto = null;
		for (PayDto p_dto : p_list) {
			c_dto = c_biz.selectOne(p_dto.getClass_num());
			c_list.add(c_dto);
		}
		List<FoodDto> f_list = f_biz.selectMyList(member_id);
		model.addAttribute("c_list", c_list);
		model.addAttribute("f_list", f_list);
		model.addAttribute("m_dto", m_dto);
		return "mypage_student";
	}
	
	@RequestMapping("/mypage_teacher.do")
	public String mypage_teacher(Model model, String member_id) {
		MemberDto m_dto = m_biz.selectOne(member_id);
		List<ClassDetailDto> c_list = c_biz.selectList(member_id);
		List<FoodDto> f_list = f_biz.selectMyList(member_id);
		model.addAttribute("c_list", c_list);
		model.addAttribute("f_list", f_list);
		model.addAttribute("m_dto", m_dto);
		return "mypage_teacher";
	}
	
	@RequestMapping("/studentupdateres.do")
	public String studentupdateres(MemberDto dto) {
		if (m_biz.updatestudent(dto) > 0) {
			return "redirect:mypage.do?member_id="+dto.getMember_id();
		}
		return "redirect:mypage.do?member_id="+dto.getMember_id();
	}
	
	@RequestMapping("/teacherupdateres.do")
	public String teacherupdateres(MemberDto dto) {
		if (m_biz.updateteacher(dto) > 0) {
			return "redirect:mypage.do?member_id="+dto.getMember_id();
		}
		return "redirect:mypage.do?member_id="+dto.getMember_id();
	}
	
	@RequestMapping(value = "/foodlifeupdateres.do", method = RequestMethod.POST)
	public String lifeupdateres(FoodListDto food_list, String member_id) {
		int num = 0;
		int k;
		List<FoodDto> f_list = food_list.getFood_list();
		for (FoodDto dto : f_list) {
			k = f_biz.updateLife(dto);
			num += k;
		}
			
		if (num > f_list.size()) {
			return "redirect:mypage.do?member_id="+member_id;
		}
		return "redirect:mypage.do?member_id="+member_id;
	}
	
	@RequestMapping("/memberdelete.do")
	public String delete(String member_id, String member_pw) {
		MemberDto dto = m_biz.selectOne(member_id);
		if (passwordEncoder.matches(member_pw, dto.getMember_pw())) {
			if (m_biz.delete(dto) > 0) {
				logger.info("회원 삭제 성공");
				return "index.do";
			}
			logger.info("회원 삭제 실패");
			return "redirect:mypage.do?member_id="+member_id;
		}
		logger.info("비밀번호 불일치");
		return "redirect:mypage.do?member_id="+member_id;
	}
	
	@RequestMapping("/updatepw.do")
	public String updatepw(Model model, String member_id) {
		model.addAttribute("member_id", member_id);
		return "updatepw";
	}
	
	@RequestMapping("/updatepwres.do")
	@ResponseBody
	public int updatepwres(@RequestBody MemberDto dto) {
		dto.setMember_pw(passwordEncoder.encode(dto.getMember_pw()));
		int res = m_biz.updatepw(dto);
		return res;
	}
	
	@RequestMapping("/tests.do")
	public void ocrtest(HttpServletRequest request) {
		List<String> list = Clova_temp.OCR("/resources/img/receipts/receipt4.jpg");
		System.out.println("receipt4");
		List<BillDto> listres = Clova_temp.input_bill(list, "asd123");
	}
	
}



