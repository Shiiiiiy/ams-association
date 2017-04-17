//package com.uws.association.controller;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import net.sf.json.JSONObject;
//
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.uws.apw.model.ApproveResult;
//import com.uws.apw.model.Approver;
//import com.uws.apw.service.IFlowInstanceService;
//import com.uws.association.service.IAssociationService;
//import com.uws.association.util.AssociationConstants;
//import com.uws.association.util.AssociationUtils;
//import com.uws.common.service.IBaseDataService;
//import com.uws.common.service.ICommonRoleService;
//import com.uws.common.service.IStudentCommonService;
//import com.uws.common.util.AmsDateUtil;
//import com.uws.common.util.CYLeagueUtil;
//import com.uws.common.util.ChineseUtill;
//import com.uws.common.util.Constants;
//import com.uws.common.util.JsonUtils;
//import com.uws.core.base.BaseController;
//import com.uws.core.hibernate.dao.support.Page;
//import com.uws.core.session.SessionFactory;
//import com.uws.core.session.SessionUtil;
//import com.uws.core.util.DataUtil;
//import com.uws.core.util.DateUtil;
//import com.uws.core.util.IdUtil;
//import com.uws.domain.association.AssociationAdvisorModel;
//import com.uws.domain.association.AssociationApplyModel;
//import com.uws.domain.association.AssociationAttacheModel;
//import com.uws.domain.association.AssociationBaseinfoModel;
//import com.uws.domain.association.AssociationMemberModel;
//import com.uws.domain.association.AssociationTempUserModel;
//import com.uws.domain.base.BaseAcademyModel;
//import com.uws.domain.base.BaseTeacherModel;
//import com.uws.domain.orientation.StudentInfoModel;
//import com.uws.log.Logger;
//import com.uws.log.LoggerFactory;
//import com.uws.sys.model.Dic;
//import com.uws.sys.model.UploadFileRef;
//import com.uws.sys.service.DicUtil;
//import com.uws.sys.service.FileUtil;
//import com.uws.sys.service.IDicService;
//import com.uws.sys.service.impl.DicFactory;
//import com.uws.sys.service.impl.DicServiceImpl;
//import com.uws.sys.service.impl.FileFactory;
//import com.uws.user.model.User;
//import com.uws.user.service.IUserService;
//import com.uws.util.ProjectConstants;
//import com.uws.util.ProjectSessionUtils;
//
///** 
//* AssociationApplyController
//* @Description:社团申请审核控制类Controller
//* @author liuyang
//* @date	   2015-12-02
//*/
//@Controller
//public class AssociationApplyController extends BaseController{
//	
//	@Autowired
//	private IAssociationService   associationService;
//	
//	@Autowired
//	private IBaseDataService baseDataService;
//	
//	@Autowired
//	private IStudentCommonService stuService;
//	
//  	@Autowired
//  	private ICommonRoleService commonRoleService;
//	
//	@Autowired
//	private IUserService userService;
//	
//	@Autowired
//	private IDicService dicService;
//	
//	@Autowired
//	private IFlowInstanceService flowInstanceService;
//
//	//数据字典工具类
//	private DicUtil dicUtil = DicFactory.getDicUtil();
//	
// 	//附件工具类
// 	private FileUtil fileUtil=FileFactory.getFileUtil();
// 	
//	//session工具类
//	private SessionUtil sessionUtil = SessionFactory.getSession(AssociationConstants.NAMESPACE);
//	
//	//日志工具类
//	private Logger logger = new LoggerFactory(AssociationApplyController.class);
//	
//	/**
//	 * 获得社团申请列表
//	 * @param model			页面数据加载器
//	 * @param request			页面请求
//	 * @param aam				社团申请实体
//	 * @return							指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList")
//	public String getAssociationApplyList(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam){
//		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
//		String curUserId = this.sessionUtil.getCurrentUserId();
//		Page page = new Page();
//		boolean isManagerRole = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
//		boolean isTeacherRole = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_TEACHER.toString());
//		if(isManagerRole){//负责人查看申请
//			
//			page = this.associationService.pageQueryAssociationApplyByMember(aam,pageNo,Page.DEFAULT_PAGE_SIZE);
//		}else if(isTeacherRole){
//			
//			page = this.associationService.pageQueryAssociationApplyByAdvisor(aam, pageNo, Page.DEFAULT_PAGE_SIZE);
//		}
//		List<AssociationApplyModel> resultList = (List<AssociationApplyModel>)page.getResult();
//		List<AssociationApplyModel> newResult = new ArrayList<AssociationApplyModel>();
//		for(AssociationApplyModel param:resultList){
//			String associationId = (DataUtil.isNotNull(param.getAssociationPo()))?param.getAssociationPo().getId():"";
//
//			//关联获取社团人数
//			int memberNums = this.associationService.getAssociationMemberNums(associationId);
//			param.setMemberNums(memberNums);
//			
//			//当前用户是否社团指导老师
//			boolean isCurAssociationAdvisor = this.validateCurAdvisor(associationId, param, curUserId);
//			param.setIsCurAA(String.valueOf(isCurAssociationAdvisor));
//			
//			//关联获取指导老师
//			String advisors = this.getCurApplyAdvisors(param);
//			param.setAdvisors(advisors);
//			
//			//是否当前社团的负责人
//			boolean isCurAssociationManager = 
//					this.associationService.getAssociationMemberByUserId(associationId,curUserId);
//			param.setIsCurAM(String.valueOf(isCurAssociationManager));
//			
//			newResult.add(param);
//		}
//		page.setResult(newResult);
//		
//		// 下拉列表 学院
//		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//		model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
//	    model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//		model.addAttribute("aam", aam);
//		model.addAttribute("page", page);
//		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
//		return AssociationConstants.NAMESPACE_APPLY+"/associationApplyList";
//	}
//	
//	/**
//	 * 获取当前申请的社团指导老师
//	 * @param curAam	
//	 * @return 指导老师信息
//	 */
//	private String getCurApplyAdvisors(AssociationApplyModel curAam) {
//		if(curAam!=null && curAam.getApplyTypeDic()!=null){
//			String applyTyle = curAam.getApplyTypeDic().getCode();
//			String modifyItem= (curAam.getModifyItem()!=null)?curAam.getModifyItem():"";
//			boolean isAdvisorModify = modifyItem.indexOf(AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR.toString())>-1;
//			if(applyTyle.equals(AssociationConstants.APPLY_STATUS.MODIFY.toString()) && isAdvisorModify){
//				String userType = AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString();
//				String curApplyAdvisors = this.associationService.getCurApplyAdvisors(curAam.getAssociationPo().getId(),userType);
//				if(curApplyAdvisors!=null && StringUtils.isNotBlank(curApplyAdvisors)){
//					return curApplyAdvisors;
//				}else{
//					return this.associationService.getAssociationAdvisors(curAam);
//				}
//			}else{
//				
//				return this.associationService.getAssociationAdvisors(curAam);
//			}
//		}else{
//			return this.associationService.getAssociationAdvisors(curAam);
//		}
//	}
//
//	/**
//	 * 验证当前用户是否社团指导老师
//	 * @param associationId			社团主键
//	 * @param curAam						当前申请
//	 * @param currentUserId		当前用户
//	 */
//	private boolean validateCurAdvisor(String associationId,AssociationApplyModel curAam, String currentUserId) {
//		if(curAam!=null && DataUtil.isNotNull(curAam.getApplyTypeDic())){
//			String applyTyle = curAam.getApplyTypeDic().getCode();
//			String modifyItem= (curAam.getModifyItem()!=null)?curAam.getModifyItem():"";
//			boolean isAdvisorModify = modifyItem.indexOf(AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR.toString())>-1;
//			if(applyTyle.equals(AssociationConstants.APPLY_STATUS.MODIFY.toString()) && isAdvisorModify){
//				return this.associationService.curUserIsModifyAdvisor(associationId,currentUserId);
//			}else{
//				
//				return this.associationService.isCurAssociationAdvisor(associationId,this.sessionUtil.getCurrentUserId());
//			}
//		}else{
//			
//			return this.associationService.isCurAssociationAdvisor(associationId,this.sessionUtil.getCurrentUserId());
//		}
//	}
//
//	/**
//	 * 获得社团申请列表【异步加载】
//	 * @param model			页面数据加载器
//	 * @param request			页面请求
//	 * @param aam				社团申请实体
//	 * @return							指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/nsm/asynLoadApplyList")
//	public String asynLoadApplyList(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam){
//		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
//		String curUserId = this.sessionUtil.getCurrentUserId();
//		Page page = new Page();
//		boolean isManagerRole = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
//		boolean isTeacherRole = this.commonRoleService.checkUserIsExist(curUserId, CYLeagueUtil.CYL_ROLES.HKY_TEACHER.toString());
//		if(isManagerRole){//负责人查看申请
//			
//			page = this.associationService.pageQueryAssociationApplyByMember(aam,pageNo,Page.DEFAULT_PAGE_SIZE);
//		}else if(isTeacherRole){
//			
//			page = this.associationService.pageQueryAssociationApplyByAdvisor(aam, pageNo, Page.DEFAULT_PAGE_SIZE);
//		}
//		List<AssociationApplyModel> resultList = (List<AssociationApplyModel>)page.getResult();
//		List<AssociationApplyModel> newResult = new ArrayList<AssociationApplyModel>();
//		for(AssociationApplyModel param:resultList){
//			String associationId = (DataUtil.isNotNull(param.getAssociationPo()))?param.getAssociationPo().getId():"";
//			//关联获取社团人数
//			int memberNums = this.associationService.getAssociationMemberNums(associationId);
//			param.setMemberNums(memberNums);
//			
//			//当前用户是否社团指导老师
//			boolean isCurAssociationAdvisor = this.validateCurAdvisor(associationId, param, curUserId);
//			param.setIsCurAA(String.valueOf(isCurAssociationAdvisor));
//			
//			//关联获取指导老师
//			String advisors = this.getCurApplyAdvisors(param);
//			param.setAdvisors(advisors);
//			
//			//是否当前社团的负责人
//			boolean isCurAssociationManager = 
//					this.associationService.getAssociationMemberByUserId(associationId,curUserId);
//			param.setIsCurAM(String.valueOf(isCurAssociationManager));
//			
//			newResult.add(param);
//		}
//		page.setResult(newResult);
//		
//		// 下拉列表 学院
//		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//		model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
//		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//		model.addAttribute("aam", aam);
//		model.addAttribute("page", page);
//		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
//		return AssociationConstants.NAMESPACE_APPLY+"/associationApplyLoadList";
//	}
//	   
//   /**
//     * 获取社团信息列表
//	 * @param model			页面数据加载器
//	 * @param request			页面请求
//     * @param amm				社团申请对象
//     * @param applyType	申请类型【注册、变更、注销】
//	 * @return							指定视图
//    */
//   @RequestMapping({AssociationConstants.NAMESPACE_MAINTAIN+"/nsm/getAssociationRadioList"})
//   public String getAssociationRadioList(ModelMap model,HttpServletRequest request,AssociationMemberModel amm,String applyType){
//	   int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
//	   Page page = this.associationService.pageQueryAssociationByMember(amm,pageNo,AssociationConstants.DEFALT_PAGE_SIZE);
//	   // 下拉列表 学院
//	   List<BaseAcademyModel> collegeList = this.baseDataService.listBaseAcademy();
//	   model.addAttribute("amm", amm);
//	   model.addAttribute("page", page);
//	   model.addAttribute("page", page);
//	   model.addAttribute("collegeList", collegeList);
//	   model.addAttribute("applyType", applyType);
//	   model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
//	   model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//	   return AssociationConstants.NAMESPACE_MAINTAIN+"/queryAssocitionRadio4CurAM";
//   }
//   
//   /**
//    * 获取社团信息列表
//	* @param model					页面数据加载器
//	* @param request				页面请求
//    * @param associationId	社团主键
//    * @param applyType		申请类型【注册、变更、注销】
//    */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/isModifyApplyFinish"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//   public String isModifyApplyFinish(ModelMap model,HttpServletRequest request,String associationId,String applyType){
//		try {
//			
//			List<AssociationApplyModel> aamList = this.associationService.getApprovingApply(associationId,applyType);
//			if(DataUtil.isNotNull(aamList) && aamList.size()>0){
//				return "{\"flag\":\"no\"}";
//			}else{
//				return "{\"flag\":\"yes\"}";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "{\"flag\":\"error\"}";
//		}
//		
//   }
//	
//	/**
//	 * 编辑社团注册申请
//	 * @param model			页面数据加载器
//	 * @param request			页面请求
//	 * @param applyId			业务主键
//	 * @return							指定视图
//	 */
//	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-add/addAssociationRegisterApply",
//		AssociationConstants.NAMESPACE_APPLY+"/opt-edit/editAssociationRegisterApply"})
//	public String editAssociationRegisterApply(ModelMap model,HttpServletRequest request,String applyId){
//		AssociationApplyModel  newAam = this.getNewAam(applyId);
//		newAam.setApplyTypeCode(AssociationConstants.APPLY_STATUS.REGISTER.toString());
//		String associationId = (null!=newAam.getAssociationPo())?newAam.getAssociationPo().getId():"";
//		BaseAcademyModel curCollege = this.getCurUserCollege(this.sessionUtil.getCurrentUserId());
//		//获取社团指导老师
//		Page teacherPage = this.associationService.pageQueryAssociationAdvisor(
//				associationId, 1, AssociationConstants.DEFALT_PAGE_SIZE);
//		//获取社团负责人
//		Page stuPage = this.getManagerPage(newAam,AssociationConstants.APPLY_STATUS.REGISTER.toString(), request);
//		//是否当前社团的负责人
//		boolean isCurAssociationManager = 
//				this.associationService.getAssociationMemberByUserId(associationId,this.sessionUtil.getCurrentUserId());
//		//当前用户是否社团指导老师
//		String curUserIsAdvisor = 
//				String.valueOf(this.associationService.isCurAssociationAdvisor(associationId,this.sessionUtil.getCurrentUserId()));
//		//获取社团指导老师列表
//	   List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//	   //获取社团负责人列表
//	   List<AssociationMemberModel> ammList = this.associationService.getAssociationMembers(associationId);
//	   //获取社团申请附件
//	   List<UploadFileRef> fileList=this.fileUtil.getFileRefsByObjectId(applyId);
//		
//	   /**
//	    * 申请信息
//	    */
//	   model.addAttribute("applyId", applyId);
//	   model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
//	   model.addAttribute("applyTypeId", AssociationConstants.registerDic.getId());
//	   
//		//获取社团注销申请附件
//		List<UploadFileRef> registerfileList=this.getAssociationAttache(applyId,AssociationConstants.ATTACHE_TYPE.REGISTER.toString());
//	   /**
//	    * 附件信息
//	    */
//	   model.addAttribute("fileList", registerfileList);
//	   
//	   /**
//	    * 社团基本信息
//	    */
//	   model.addAttribute("associationId",(DataUtil.isNotNull(applyId)?associationId:IdUtil.getUUIDHEXStr()));
//	   model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//       model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//	   model.addAttribute("curCollegeName", curCollege.getName());
//	   model.addAttribute("curCollegeId", curCollege.getId());
//	   model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//	   model.addAttribute("aam", newAam);
//	   model.addAttribute("aam_", new AssociationAdvisorModel());
//	   //社团指导老师信息
//	   request.getSession().setAttribute("aamList", aamList);
//	   model.addAttribute("aamList", aamList);
//	   model.addAttribute("teacherPage", teacherPage);
//	   model.addAttribute("hiddenTeacherIds", this.getTeaderIds(newAam));
//	   model.addAttribute("isCurAdvisor", curUserIsAdvisor);
//	   //社团负责人信息
//	   request.getSession().setAttribute("associationManager", newAam.getAssociationPo().getProprieter());
//	   request.getSession().setAttribute("ammList", ammList);
//	   model.addAttribute("stuPage", stuPage);
//	   model.addAttribute("hasStuData", this.isStuHasData(stuPage));
//	   model.addAttribute("hiddenManagerIds", this.getManagerIds(newAam));
//	   model.addAttribute("isCurManager",isCurAssociationManager?"true":"false");
//	   model.addAttribute("proprieterId",this.getPropieter(newAam));
//	   model.addAttribute("proprieterRegister",this.getRegisterPropieterByAssociation(associationId));
//	   //当前系统用户
//	   model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
//	   //当前操作【新增、修改】
//	   model.addAttribute("optType",this.getOptType(applyId));
//	   model.addAttribute("operateStatus",newAam.getOperateStatus());
//	   model.addAttribute("applyTypeCode",AssociationConstants.APPLY_STATUS.REGISTER.toString());
//
//	   return AssociationConstants.NAMESPACE_APPLY+"/associationRegisterApplyEdit";
//	}
//	
//	/**
//	 * 学生集合中是否有数据
//	 * @param stuPage		分页信息
//	 */
//	private String isStuHasData(Page stuPage) {
//		return String.valueOf(stuPage.getResult().size()>0);
//	}
//
//	/**
//	 * 封装社团负责人列表
//	 * @param newAam  		社团申请实体
//	 * @param applyType  申请类型
//	 * @return 分页对象
//	 */
//	private Page getManagerPage(AssociationApplyModel newAam,String applyType,HttpServletRequest request) {
//		Page stuPage  = this.packageManagerInfo(newAam,applyType, request);
//		if(DataUtil.isNotNull(stuPage)){
//			List<AssociationMemberModel> newResultList = new ArrayList<AssociationMemberModel>();
//			List<AssociationMemberModel> resultList = (List<AssociationMemberModel>)stuPage.getResult();
//			String proprieterId = (newAam.getAssociationPo()!=null && newAam.getAssociationPo().getProprieter()!=null)?
//					newAam.getAssociationPo().getProprieter().getId():this.sessionUtil.getCurrentUserId();
//			for(AssociationMemberModel amm:resultList){
//				String managerId = (amm.getMemberPo()!=null)?amm.getMemberPo().getId():"";
//				if(proprieterId.equals(managerId)){
//					amm.setLeaguePosition(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER);
//				}
//				newResultList.add(amm);
//			}
//			stuPage.setResult(newResultList);
//		}
//		return stuPage;
//	}
//
//	/**
//	 * 封装社团负责人列表
//	 * @param newAam		社团申请对象
//	 * @param applyType	申请类型
//	 * @return	负责人列表
//	 */
//	private Page packageManagerInfo(AssociationApplyModel newAam,String applyType,HttpServletRequest request) {
//		String associationId = (newAam.getAssociationPo()!=null)?newAam.getAssociationPo().getId():"";
//		if(isManagerModify(associationId, applyType,"")==false){
//			if(AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(applyType)){
//				List<AssociationTempUserModel> atumList = 
//						this.associationService.getTempUserInfo(associationId, AssociationConstants.ASSOCIATION_USER_TYPE.MANAGER.toString());
//				if(DataUtil.isNotNull(atumList) && atumList.size()>0){
//					Page page = new Page();
//					page.setStart(0);
//					page.setPageSize(AssociationConstants.DEFALT_PAGE_SIZE);
//					List<AssociationMemberModel>  newResultList = new ArrayList<AssociationMemberModel>();
//					String propireterId = (newAam.getAssociationPo()!=null && newAam.getAssociationPo().getId()!=null)?
//							newAam.getAssociationPo().getId():"";
//							int pageSize = AssociationConstants.DEFALT_PAGE_SIZE<atumList.size()?AssociationConstants.DEFALT_PAGE_SIZE:atumList.size();
//							for (int i = 0; i < pageSize; i++){
//								AssociationMemberModel  amm = this.getInitManagerInfo(associationId, atumList.get(i).getUserId(),propireterId, request);
//								newResultList.add(amm);
//							}
//							page.setResult(newResultList);
//							page.setTotalCount(atumList.size());
//							return page;
//				}
//			}
//		}
//		return this.associationService.pageQueryAssociationMember(
//				newAam.getAssociationPo(),1, AssociationConstants.DEFALT_PAGE_SIZE);
//	}
//
//	/**
//	 * 获取社长
//	 * @param newAam	社团申请
//	 * @return	当前社长
//	 */
//	private String getPropieter(AssociationApplyModel newAam) {
//		if(newAam.getAssociationPo()!=null && 
//			 newAam.getAssociationPo().getProprieter()!=null){
//			return DataUtil.isNotNull(newAam.getAssociationPo().getProprieter().getId())?
//							newAam.getAssociationPo().getProprieter().getId():this.sessionUtil.getCurrentUserId();
//		}
//		return this.sessionUtil.getCurrentUserId();
//	}
//
//	/**
//	 * 编辑社团变更申请
//	 * @param model				页面数据加载器
//	 * @param request				页面请求
//	 * @param applyId				业务主键
//	 * @param associationId	社团主键
//	 * @return								指定视图
//	 */
//	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-edit/addAssociationModifyApply",
//		AssociationConstants.NAMESPACE_APPLY+"/opt-edit/editAssociationModifyApply"})
//	public String editAssociationModifyApply(ModelMap model,HttpServletRequest request,
//			      String applyId,String associationId){
//		Boolean flag = false;
//		AssociationApplyModel  newAam = this.getNewAam_(applyId,associationId);
//		if(newAam!=null && newAam.getApplyTypeDic()!=null && StringUtils.isNotBlank(newAam.getApplyTypeDic().getId()) 
//				&& !"审核通过".equals(newAam.getProcessstatus()) && newAam.getModifyItem()!=null && newAam.getModifyItem().contains("ASSOCIATION_MANAGER"))
//		{
//			Dic dic = dicService.getDic(newAam.getApplyTypeDic().getId());
//			flag=AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(dic.getCode());
//		}
//		newAam.setApplyTypeCode(AssociationConstants.APPLY_STATUS.MODIFY.toString());
//		String curUserId  =this.sessionUtil.getCurrentUserId();
//		if(DataUtil.isNull(associationId)){
//			associationId = (null!=newAam.getAssociationPo())?newAam.getAssociationPo().getId():"";
//		}
//		BaseAcademyModel curCollege = this.getCurUserCollege(this.sessionUtil.getCurrentUserId());
//		//获取社团指导老师
//		Page teacherPage = this.getAdvisorPage(associationId,AssociationConstants.APPLY_STATUS.MODIFY.toString(),newAam.getModifyItem());
//		//获取社团负责人
//		Page stuPage = this.getManagerPage(newAam,AssociationConstants.APPLY_STATUS.MODIFY.toString(), request);
//		//是否当前社团的负责人
//		boolean isCurAssociationManager = 
//				this.associationService.getAssociationMemberByUserId(associationId,curUserId);
//		//当前用户是否社团指导老师
//		String curUserIsAdvisor = String.valueOf(this.validateCurAdvisor(associationId, newAam, curUserId));
//		//获取社团负责人列表
//		List<AssociationMemberModel> ammList = this.associationService.getAssociationMembers(associationId);
//		
//		/**
//		 * 指导老师信息
//		 */
//		String hiddenAdvisor = this.associationService.getHiddenAssociationAdvisor(associationId);
//		model.addAttribute("hiddenAdvisor", hiddenAdvisor);
//		
//		/**
//		 * 社团负责人信息
//		 */
//		String hiddenManager = this.associationService.getHiddenAssociationAdvisors(associationId);
//		model.addAttribute("hiddenManager", hiddenManager);
//		
//		/**
//		 * 申请信息
//		 */
//		model.addAttribute("applyId", applyId);
//		model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
//		model.addAttribute("applyTypeId", AssociationConstants.changeDic.getId());
//		
//		//获取社团注销申请附件
//		List<UploadFileRef> modifyfileList=this.getAssociationAttache(applyId,AssociationConstants.ATTACHE_TYPE.MODIFY.toString());
//		/**
//		 * 附件信息
//		 */
//		model.addAttribute("fileList", modifyfileList);
//		
//		/**
//		 * 社团基本信息
//		 */
//		AssociationMemberModel ammPo = new AssociationMemberModel();
//		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//		model.addAttribute("curCollegeName", curCollege.getName());
//		model.addAttribute("curCollegeId", curCollege.getId());
//		model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//		model.addAttribute("aam", newAam);
//		model.addAttribute("modifyItemInfo", newAam.getModifyItem());
//		model.addAttribute("aam_", new AssociationAdvisorModel());
//		model.addAttribute("amm", ammPo);
//		//社团指导老师信息
//	    List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//		model.addAttribute("aamList", this.getModifyApplyAdvisors(associationId,aamList,newAam));
//		model.addAttribute("teacherPage", teacherPage);
//		model.addAttribute("hiddenTeacherIds", this.getTeaderIds(newAam));//原始表
//		model.addAttribute("teacherIds", this.getNewManagerIds(newAam, AssociationConstants.APPLY_STATUS.MODIFY.toString(),AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString()));//临时
//		model.addAttribute("isCurAdvisor", curUserIsAdvisor);
//		//社团负责人信息
//		model.addAttribute("stuPage", stuPage);
//	    model.addAttribute("hasStuData", this.isStuHasData(stuPage));
//	    if(flag){
//	    	model.addAttribute("hiddenManagerIds", this.getNewManagerIds(newAam, AssociationConstants.APPLY_STATUS.MODIFY.toString(),AssociationConstants.ASSOCIATION_USER_TYPE.MANAGER.toString()));
//		}else{
//			model.addAttribute("hiddenManagerIds", this.getManagerIds(newAam));
//		}
//		model.addAttribute("isCurManager",String.valueOf(isCurAssociationManager));
//	    model.addAttribute("proprieterId",this.getPropieter(newAam));
//	    model.addAttribute("proprieterRegister",this.getRegisterPropieterByAssociation(associationId));
//		//当前系统用户
//		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
//		//当前操作【新增、修改】
//		model.addAttribute("optType",this.getOptType(applyId));
//		model.addAttribute("operateStatus",newAam.getOperateStatus());
//		model.addAttribute("applyTypeCode",AssociationConstants.APPLY_STATUS.MODIFY.toString());
//		model.addAttribute("oldManagerIds", this.getManagerIds(newAam));
//		return AssociationConstants.NAMESPACE_APPLY+"/associationModifyApplyEdit";
//	}
//	
//	/**
//	 * 获取社团变更指导老师列表
//	 * @param newAam		社团申请实体
//	 * @param applyType	社团申请类型
//	 * @return
//	 */
//	private Page getAdvisorPage(String associationId,String applyType,String modifyItem) {
//		if(AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(applyType)){
//			if(isAdvisorModify(associationId, modifyItem,"")){
//				List<AssociationTempUserModel> atumList = 
//						this.associationService.getTempUserInfo(associationId, AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString());
//				if(DataUtil.isNotNull(atumList) && atumList.size()>0){
//					Page page = new Page();
//					page.setStart(0);
//					page.setPageSize(AssociationConstants.DEFALT_PAGE_SIZE);
//					List<AssociationAdvisorModel>  aamList = new ArrayList<AssociationAdvisorModel>();
//					for(AssociationTempUserModel atum: atumList){
//						AssociationAdvisorModel  aam = this.getInitAdvisorInfo(associationId, atum.getUserId());
//						aam.setId(atum.getId());
//						aamList.add(aam);
//					}
//					page.setResult(aamList);
//					page.setTotalCount(aamList.size());
//					return page;
//				}
//			}
//		}
//		return this.associationService.pageQueryAssociationAdvisor(
//				associationId, 1, AssociationConstants.DEFALT_PAGE_SIZE);
//	}
//
//	/**
//	 * 编辑社团注销申请
//	 * @param model				页面数据加载器
//	 * @param request				页面请求
//	 * @param applyId				业务主键
//	 * @param associationId	社团主键
//	 * @return								指定视图
//	 */
//	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-edit/addAssociationCancelApply",
//		AssociationConstants.NAMESPACE_APPLY+"/opt-edit/editAssociationCancelApply"})
//	public String editAssociationCancelApply(ModelMap model,HttpServletRequest request,String applyId,String associationId){
//		AssociationApplyModel  newAam = this.getNewAam_(applyId,associationId);
//		newAam.setApplyTypeCode(AssociationConstants.APPLY_STATUS.CANCEL.toString());
//		if(DataUtil.isNull(associationId)){
//			associationId = (null!=newAam.getAssociationPo())?newAam.getAssociationPo().getId():"";
//		}
//		BaseAcademyModel curCollege = this.getCurUserCollege(this.sessionUtil.getCurrentUserId());
//		//获取社团指导老师
//		Page teacherPage = this.associationService.pageQueryAssociationAdvisor(
//				associationId, 1, AssociationConstants.DEFALT_PAGE_SIZE);
//		//获取社团负责人
//		Page stuPage = this.getManagerPage(newAam,AssociationConstants.APPLY_STATUS.CANCEL.toString(), request);
//		//是否当前社团的负责人
//		boolean isCurAssociationManager = 
//				this.associationService.getAssociationMemberByUserId(associationId,this.sessionUtil.getCurrentUserId());
//		//当前用户是否社团指导老师
//		String curUserIsAdvisor = 
//				String.valueOf(this.associationService.isCurAssociationAdvisor(associationId,this.sessionUtil.getCurrentUserId()));
//		//获取社团指导老师列表
//		List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//		//获取社团负责人列表
//		List<AssociationMemberModel> ammList = this.associationService.getAssociationMembers(associationId);
//		
//		/**
//		 * 申请信息
//		 */
//		model.addAttribute("applyId", applyId);
//		model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
//		model.addAttribute("applyTypeId", AssociationConstants.cancelDic.getId());
//		
//		/**
//		 * 社团注销申请附件
//		 */
//		List<UploadFileRef> cancelfileList=this.getAssociationAttache(applyId,AssociationConstants.ATTACHE_TYPE.CANCEL.toString());
//		model.addAttribute("fileList", cancelfileList);
//		/**
//		 * 社团财务清算附件
//		 */
//		List<UploadFileRef> financefileList=this.getAssociationAttache(applyId,AssociationConstants.ATTACHE_TYPE.FINANCE.toString());
//		model.addAttribute("financefileList", financefileList);
//		
//		/**
//		 * 社团基本信息
//		 */
//		AssociationMemberModel ammPo = new AssociationMemberModel();
//		model.addAttribute("associationId",(DataUtil.isNotNull(applyId)?associationId:IdUtil.getUUIDHEXStr()));
//		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//		model.addAttribute("curCollegeName", curCollege.getName());
//		model.addAttribute("curCollegeId", curCollege.getId());
//		model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//		model.addAttribute("aam", newAam);
//		model.addAttribute("aam_", new AssociationAdvisorModel());
//		model.addAttribute("amm", ammPo);
//		//社团指导老师信息
//		request.getSession().setAttribute("aamList", aamList);
//		model.addAttribute("aamList", aamList);
//		model.addAttribute("teacherPage", teacherPage);
//		model.addAttribute("hiddenTeacherIds", this.getTeaderIds(newAam));
//		model.addAttribute("isCurAdvisor", curUserIsAdvisor);
//		//社团负责人信息
//		request.getSession().setAttribute("ammList", ammList);
//		model.addAttribute("stuPage", stuPage);
//		model.addAttribute("hasStuData", this.isStuHasData(stuPage));
//		model.addAttribute("hiddenManagerIds", this.getManagerIds(newAam));
//		model.addAttribute("isCurManager",String.valueOf(isCurAssociationManager));
//		//当前系统用户
//		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
//		//当前操作【新增、修改】
//		model.addAttribute("optType",this.getOptType(applyId));
//		model.addAttribute("operateStatus",newAam.getOperateStatus());
//		model.addAttribute("applyTypeCode",AssociationConstants.APPLY_STATUS.CANCEL.toString());
//		
//		return AssociationConstants.NAMESPACE_APPLY+"/associationCancelApplyEdit";
//	}
//	
//	/**
//	 * 获取社团附件
//	 * @param applyId				申请id
//	 * @param attacheType	附件类型
//	 */
//	private List<UploadFileRef> getAssociationAttache(String applyId,String attacheType) {
//		
//		return this.associationService.getAssociationAttache(applyId,attacheType);
//	}
//
//	/**
//	 * 删除当前申请
//	 * @param model				页面数据加载器
//	 * @param request				页面请求
//	 * @param session				当前会话
//	 * @param applyId				业务主键
//	 * @param applyType		申请类型【注册、变更、注销】
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-delete/deleteAssociationApply"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String deleteAssociationApply(ModelMap model,HttpServletRequest request,HttpServletResponse response,
//				   String applyId,String applyType){
//		try {
//					this.associationService.deleteAssociationApplyInfo(applyId,applyType);
//					return "{\"flag\":\"success\"}";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "{\"flag\":\"error\"}";
//		}
//	}
//	
//	/**
//	 * 回滚社团申请
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param associationId				社团id
//	 * @param advisors						指导老师
//	 * @param stuIds							学生id集合
//	 * @param advisorComment_	指导老师简介
//	 */
//	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/opt-delete/rollbackTempInfo"})
//	@SuppressWarnings("all")
//	public String rollbackTempInfo(ModelMap model,HttpServletRequest request,String associationId,
//			String advisors,String stuIds,String [] advisorComment_){
//		AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
//		//获取社团指导老师列表
//		List<AssociationAdvisorModel> aamList =(List<AssociationAdvisorModel>)request.getSession().getAttribute("aamList");
//		//获取社团负责人列表
//		List<AssociationMemberModel> ammList =(List<AssociationMemberModel>)request.getSession().getAttribute("ammList");
//		//社团负责人[社长]
//		StudentInfoModel proprieter =(StudentInfoModel)request.getSession().getAttribute("associationManager");
//		
//			//回滚指导老师
//			this.rollbackAdvisors(aamList,associationId);
//			request.getSession().removeAttribute("aamList");
//		
//			//回滚社团人数
//			if(DataUtil.isNotNull(abm)&&DataUtil.isNotNull(abm.getId())){
//				abm.setMemberNums(ammList.size());
//				this.associationService.updateAssociationInfo(abm);
//		        this.rollbackProprieter(associationId,proprieter);
//			}
//			
//			//回滚社团负责人
//			this.rollbackMembers(ammList,associationId);
//			request.getSession().removeAttribute("ammList");
//
//	    
//		return "redirect:"+AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList.do";
//	}
//	
//	/**
//	 * 回滚社团负责人 
//	 * @param associationId	社团主键
//	 * @param proprieter		社团负责人【社长】
//	 */
//	private void rollbackProprieter(String associationId,StudentInfoModel proprieter) {
//		AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
//		abm.setProprieter(proprieter);
//		this.associationService.updateAssociationInfo(abm);
//	}
//
//	/**
//	 * 判断社团负责人是否变动
//	 * @param ammList		原社团负责人
//	 * @param stuIds			选中的社团负责人
//	 * @return	[true/false]
//	 */
//	private boolean checkManagerChange(List<AssociationMemberModel> ammList,String stuIds) {
//		for(AssociationMemberModel amm:ammList){
//			String managerId = (amm.getMemberPo()!=null)?amm.getMemberPo().getId():"";
//			if((DataUtil.isNotNull(managerId)) && (stuIds.indexOf(managerId)==-1)){
//				return true;
//			}
//			
//			String leaguePosition= (amm.getLeaguePosition()!=null)?amm.getLeaguePosition().getId():"";
//			String associationId = (amm.getAssociationPo()!=null)?amm.getAssociationPo().getId():"";
//			AssociationMemberModel curAmm = this.associationService.getAssociationMember_(associationId, managerId);
//			String curLeaguePosition =  (curAmm.getLeaguePosition()!=null)?curAmm.getLeaguePosition().getId():"";
//			if(!leaguePosition.equals(curLeaguePosition)){
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 判断社团指导人是否变动
//	 * @param aamList				原社团指导人
//	 * @param advisors			选中的指导人
//	 * @return [true/false]
//	 */
//	private boolean checkADvisorChange(List<AssociationAdvisorModel> aamList,String advisors,String [] advisorComment_) {
//		for(int i=0;i<aamList.size();i++){
//			AssociationAdvisorModel aam=aamList.get(i);
//			String advisorId = (aam.getAdvisorPo()!=null)?aam.getAdvisorPo().getCode():"";
//			if((DataUtil.isNotNull(advisorId)) && (advisors.indexOf(advisorId)==-1)){
//				return true;
//			}
//			if(this.sessionUtil.getCurrentUserId().equals(advisorId)){
//				String  advisorInfo = aam.getComments();
//				String  advisorInfo_ = advisorComment_[i];
//				if(DataUtil.isNotNull(advisorInfo)&&DataUtil.isNotNull(advisorInfo_)){
//					if(!advisorInfo.equals(advisorInfo_)){
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * 回滚社团成员
//	 * @param ammList			社团成员列表
//	 * @param associationId	社团主键
//	 */
//	private void rollbackMembers(List<AssociationMemberModel> ammList,String associationId) {
//		if(DataUtil.isNotNull(associationId)){
//			this.associationService.truncateManagerInfo(associationId);
//			for(AssociationMemberModel amm:ammList){
//				this.associationService.addAssociationMember(amm);
////			String userId = amm.getMemberPo().getId();
////			this.associationService.saveUserRole(userId, CYL_ROLES.HKY_ASSOCIATION_MANAGER.toString());
//			}
//		}
//	}
//
//	/**
//	 * 回滚社团指导老师
//	 * @param aamList				社团指导老师列表
//	 * @param associationId	社团主键
//	 */
//	private void rollbackAdvisors(List<AssociationAdvisorModel> aamList,String associationId) {
//		if(DataUtil.isNotNull(associationId)){
//			this.associationService.truncateAdvisorInfo(associationId);
//			for(AssociationAdvisorModel aam:aamList){
//				this.associationService.addAssociationAdvisor(aam);
//			}
//		}
//	}
//
//	/**
//	 * 获取格式化后的申请对象
//	 * @param applyId		社团申请id
//	 */
//	private AssociationApplyModel getNewAam(String applyId) {
//		AssociationApplyModel aam = new AssociationApplyModel();
//		if(DataUtil.isNotNull(applyId)){
//			aam.setId(applyId);
//			aam = this.associationService.getAssociationApplyInfo(aam);
//		}else{
//			aam.setAssociationPo(new AssociationBaseinfoModel());
//		}
//		return aam;
//	}
//	
//	/**
//	 * 获取格式化后的申请对象
//	 * @param applyId					社团申请id
//	 * @param associationId		社团id
//	 */
//	private AssociationApplyModel getNewAam_(String applyId,String associationId) {
//		AssociationApplyModel aam = new AssociationApplyModel();
//		if(DataUtil.isNotNull(applyId)){
//			aam.setId(applyId);
//			aam = this.associationService.getAssociationApplyInfo(aam);
//		}else{
//			aam.setAssociationPo(this.associationService.getAssociationInfo(associationId));
//		}
//		return aam;
//	}
//
//	/**
//	 * 获取操作类型
//	 * @param pk		业务主键
//	 */
//	   private String getOptType(String pk) {
//		   if(DataUtil.isNotNull(pk)){
//			   return AssociationConstants.OPT_TYPE.UPDATE.toString();
//		   }else{
//			   return AssociationConstants.OPT_TYPE.ADD.toString();
//		   }
//	}
//
//	/**
//	 * 编辑指导老师简介
//	 * @param model			页面数据加载器
//	 * @param request			页面请求
//	 * @param aamId			社团指导老师主键
//	 * @return							 指定视图
//	 */
//	   @RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/nsm/editAdvisorInfo")
//	   public String editAdvisorInfo(ModelMap model,HttpServletRequest request,String associationId,String teacherId,String isAdvisorChange){
//		   if("true".equals(isAdvisorChange)){
//			   AssociationTempUserModel atumPo = 
//			   this.associationService.getAssociationTempUser(associationId, teacherId, AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString());
//			   model.addAttribute("aam_",  this.getInitAdvisorInfo(associationId, atumPo.getUserId()));
//		   }else{
//			   AssociationAdvisorModel aam = this.associationService.getAssociationAdvisor(associationId, teacherId);
//			   model.addAttribute("aam_", aam);
//		   }
//		   return AssociationConstants.NAMESPACE_APPLY+"/advisorInfoEdit";
//	   }
//	   
//	   /**
//	    * 指导老师异步加载指导老师简介
//	    * @param model					页面数据加载器
//	    * @param request				页面请求
//	    * @param associationId	社团主键
//	    * @return							 	指定视图
//	    */
//	   @RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/nsm/advisorAsynLoadAdvisorInfo")
//	   public String advisorAsynLoadAdvisorInfo(ModelMap model,HttpServletRequest request,
//			   String associationId,String applyType,String applyId,String teacherIds,String modifyItem){
//		   List<AssociationAdvisorModel>  aamList = new ArrayList<AssociationAdvisorModel>();
//		   if(AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(applyType)){
//			   if(isAdvisorModify(associationId, modifyItem, teacherIds)){
//				   aamList = this.getTempAdvisorInfo(associationId);
//			   }else{
//				   aamList = this.associationService.getAssociationAdvisors(associationId);
//			   }
//		   }else{
//			   aamList = this.associationService.getAssociationAdvisors(associationId);
//		   }
//		   model.addAttribute("aamList", aamList);
//		   return AssociationConstants.NAMESPACE_APPLY+"/advisorInfoList";
//	   }
//	   
//	   /**
//	    * 获取变更的临时指导老师简介
//	    * @param associationId	社团主键
//	    * @return 指导老师列表
//	    */
//	   private List<AssociationAdvisorModel> getTempAdvisorInfo(String associationId) {
//		   List<AssociationAdvisorModel> aamList = new ArrayList<AssociationAdvisorModel>();
//		   List<AssociationTempUserModel> atumList =
//		   this.associationService.getTempUserInfo(associationId, AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString());
//		   for(AssociationTempUserModel atum:atumList){
//			   AssociationAdvisorModel aam = this.getInitAdvisorInfo(associationId, atum.getUserId());
//			   aam.setComments(atum.getComments());
//			   aamList.add(aam);
//		   }
//		   return aamList;
//	}
//
//	/**
//	    * 负责人异步加载指导老师简介
//	    * @param model					页面数据加载器
//	 	* @param request				页面请求
//	    * @param associationId	社团主键
//	    * @return							 	指定视图
//	    */
//	   @RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/nsm/managerAsynLoadAdvisorInfo")
//	   public String managerAsynLoadAdvisorInfo(ModelMap model,HttpServletRequest request,
//			   String associationId,String applyType,String applyId,String teacherIds,String modifyItem){
//		   List<AssociationAdvisorModel>  aamList = new ArrayList<AssociationAdvisorModel>();
//		   if(AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(applyType)){
//			   if(modifyItemCheck(modifyItem,AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR.toString())){
//				   aamList = this.managerGetNewAdvisorInfo(associationId,aamList,teacherIds);
//			   }else{
//				   aamList = this.associationService.getAssociationAdvisors(associationId);
//			   }
//		   }else{
//			   aamList = this.associationService.getAssociationAdvisors(associationId);
//		   }
//		   model.addAttribute("aamList", aamList);
//		   return AssociationConstants.NAMESPACE_APPLY+"/advisorInfoList";
//	   }
//	   
//	   /**
//	    * 获取变更后指导老师后的简介列表
//	    * @param associationId	社团id
//	    * @param aamList				指导老师列表
//	    * @param teacherIds			指导老师信息
//	    * @return 指导老师列表
//	    */
//	   private List<AssociationAdvisorModel> managerGetNewAdvisorInfo(String associationId,List<AssociationAdvisorModel> aamList, String teacherIds) {
//		   if(DataUtil.isNotNull(teacherIds)){
//			  String teacherArray [] = teacherIds.split(",");
//			  for(String teacherId:teacherArray){
//				  AssociationAdvisorModel aam = this.associationService.getAssociationAdvisor(associationId,teacherId);
//				  if(DataUtil.isNotNull(aam) && DataUtil.isNotNull(aam.getId())){
//					  aamList.add(aam);
//				  }else{
//					  aamList.add(this.getInitAdvisorInfo(associationId, teacherId));
//				  }
//			  }
//		   }
//		   return aamList;
//	}
//
//	/**
//	    * 保存指导老师简介
//	    * @param model					页面数据加载器
//	 	* @param request				页面请求
//	    * @param aam						指导老师对象
//	    */
//		@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-edit/saveAdvisorInfo"},produces={"text/plain;charset=UTF-8"})
//		@ResponseBody
//	   public String saveAdvisorInfo(ModelMap model,HttpServletRequest request,AssociationAdvisorModel aam){
//		   String returnValue="success";
//		   try {
//			   String ammId = (aam!=null && DataUtil.isNotNull(aam.getId()))?aam.getId():"";
//			   AssociationAdvisorModel newAam = this.associationService.getAssociationAdvisor(ammId);
//			   newAam.setComments(aam.getComments().trim());
//			   this.associationService.updateAdvisor(newAam);
//			} catch (Exception e) {
//				returnValue="failed";
//			}
//		   return returnValue;
//	   }
//		
//		/**
//		 * 保存指导老师简介
//		 * @param model							页面数据加载器
//		 * @param request							页面请求
//		 * @param associationId				社团主键
//		 * @param advisorId						指导老师id
//		 * @param advisorComments	指导老师简介
//		 */
//		@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-edit/saveAdvisorTempInfo"},produces={"text/plain;charset=UTF-8"})
//		@ResponseBody
//		public String saveAdvisorTempInfo(ModelMap model,HttpServletRequest request,
//				       String associationId,String advisorId,String advisorComments){
//			String returnValue="success";
//			try {
//				AssociationTempUserModel atumPo =this.associationService.getAssociationTempUser(
//						associationId,advisorId,AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString());
//				atumPo.setComments(advisorComments);
//				this.associationService.updateAssociationTempUser(atumPo);
//			} catch (Exception e) {
//				returnValue="failed";
//			}
//			return returnValue;
//		}
//
//	/**
//	 * 获得当前登录人的学院
//	 * @param currentUserId
//	 */
//	private BaseAcademyModel getCurUserCollege(String currentUserId) {
//		StudentInfoModel sim = this.stuService.queryStudentById(currentUserId);
//		if(DataUtil.isNotNull(sim) && DataUtil.isNotNull(sim.getCollege())){
//			return sim.getCollege();
//		}else{
//			return new BaseAcademyModel();
//		}
//	}
//
//	/**
//	 * 查看社团注册申请
//	 * @param model				页面数据加载器
//	 * @param request				页面请求
//	 * @param applyId				社团申请id
//	 * @return								指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/opt-edit/viewAssociationRegisterApply")
//	public String viewAssociationRegisterApply(ModelMap model,HttpServletRequest request,String applyId){
//		AssociationApplyModel  newAam = this.getNewAam(applyId);
//		newAam.setApplyTypeCode(AssociationConstants.APPLY_STATUS.REGISTER.toString());
//		String associationId = (null!=newAam.getAssociationPo())?newAam.getAssociationPo().getId():"";
//		BaseAcademyModel curCollege = this.getCurUserCollege(this.sessionUtil.getCurrentUserId());
//		//获取社团指导老师
//		Page teacherPage = this.associationService.pageQueryAssociationAdvisor(
//				associationId, 1, AssociationConstants.DEFALT_PAGE_SIZE);
//		//获取社团负责人
//		Page stuPage = this.getManagerPage(newAam,AssociationConstants.APPLY_STATUS.REGISTER.toString(),request);
//		//获取社团指导老师列表
//	   List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//	   //获取社团负责人列表
//	   List<AssociationMemberModel> ammList = this.associationService.getAssociationMembers(associationId);
//		/**
//		 * 社团注册申请附件
//		 */
//		List<UploadFileRef> registerfileList=this.getAssociationAttache(applyId,AssociationConstants.ATTACHE_TYPE.REGISTER.toString());
//	   
//	   model.addAttribute("associationId",(DataUtil.isNotNull(applyId)?associationId:IdUtil.getUUIDHEXStr()));
//	   model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//       model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//	   model.addAttribute("curCollegeName", curCollege.getName());
//	   model.addAttribute("curCollegeId", curCollege.getId());
//	   model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//	   model.addAttribute("teacherPage", teacherPage);
//	   model.addAttribute("stuPage", stuPage);
//	   model.addAttribute("hasStuData", this.isStuHasData(stuPage));
//	   model.addAttribute("aamList", aamList);
//	   model.addAttribute("ammList", ammList);
//	   model.addAttribute("fileList", registerfileList);
//	   model.addAttribute("aam", newAam);
//	   model.addAttribute("hiddenManagerIds", this.getManagerIds(newAam));
//		return AssociationConstants.NAMESPACE_APPLY+"/associationRegisterApplyView";
//	}
//	
//	/**
//	 * 查看社团变更申请
//	 * @param model				页面数据加载器
//	 * @param request				页面请求
//	 * @param applyId				社团申请id
//	 * @return								指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/opt-edit/viewAssociationModifyApply")
//	public String viewAssociationModifyApply(ModelMap model,HttpServletRequest request,String applyId){
//		AssociationApplyModel  newAam = this.getNewAam(applyId);
//		Boolean flag = false;
//		if(newAam!=null && newAam.getApplyTypeDic()!=null && StringUtils.isNotBlank(newAam.getApplyTypeDic().getId())
//				&& !"审核通过".equals(newAam.getProcessstatus()) && newAam.getModifyItem()!=null && newAam.getModifyItem().contains("ASSOCIATION_MANAGER"))
//		{
//			Dic dic = dicService.getDic(newAam.getApplyTypeDic().getId());
//			flag=AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(dic.getCode());
//		}
//		newAam.setApplyTypeCode(AssociationConstants.APPLY_STATUS.MODIFY.toString());
//		String associationId = (null!=newAam.getAssociationPo())?newAam.getAssociationPo().getId():"";
//		BaseAcademyModel curCollege = this.getCurUserCollege(this.sessionUtil.getCurrentUserId());
//		//获取社团指导老师
//		Page teacherPage = this.getAdvisorPage(associationId,AssociationConstants.APPLY_STATUS.MODIFY.toString(),newAam.getModifyItem());
//		//获取社团负责人
//		Page stuPage = this.getManagerPage(newAam,AssociationConstants.APPLY_STATUS.MODIFY.toString(), request);
//		//获取社团指导老师列表
//		List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//		//获取社团负责人列表
//		List<AssociationMemberModel> ammList = this.associationService.getAssociationMembers(associationId);
//		/**
//		 * 社团变更申请附件
//		 */
//		List<UploadFileRef> modifyfileList=this.getAssociationAttache(applyId,AssociationConstants.ATTACHE_TYPE.MODIFY.toString());
//		
//		model.addAttribute("modifyItemInfo", newAam.getModifyItem());
//		model.addAttribute("associationId",(DataUtil.isNotNull(applyId)?associationId:IdUtil.getUUIDHEXStr()));
//		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//		model.addAttribute("curCollegeName", curCollege.getName());
//		model.addAttribute("curCollegeId", curCollege.getId());
//		model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//		model.addAttribute("teacherPage", teacherPage);
//		model.addAttribute("stuPage", stuPage);
//		model.addAttribute("hasStuData", this.isStuHasData(stuPage));
//		model.addAttribute("fileList", modifyfileList);
//		String applyType = (newAam.getApplyTypeDic()!=null)?newAam.getApplyTypeDic().getCode():"";
//		model.addAttribute("aamList", this.getModifyApplyAdvisors(associationId, aamList,newAam));
//		model.addAttribute("ammList", ammList);
//		model.addAttribute("aam", newAam);
//		 if(flag){
//		    	model.addAttribute("hiddenManagerIds", this.getNewManagerIds(newAam, AssociationConstants.APPLY_STATUS.MODIFY.toString(),AssociationConstants.ASSOCIATION_USER_TYPE.MANAGER.toString()));
//		}else{
//			model.addAttribute("hiddenManagerIds", this.getManagerIds(newAam));
//		}
//		return AssociationConstants.NAMESPACE_APPLY+"/associationModifyApplyView";
//	}
//	
//	/**
//	 * 获取变更后的指导老师信息
//	 * @param associationId	社团主键
//	 * @param aamList				指导老师列表
//	 * @return	指导老师列表
//	 */
//	private List<AssociationAdvisorModel> getModifyApplyAdvisors(String associationId,List<AssociationAdvisorModel> aamList,AssociationApplyModel  newAam) {
//		String applyType = newAam.getApplyTypeCode();	
//		if(applyType.equals(AssociationConstants.APPLY_STATUS.MODIFY.toString())){
//				List<AssociationAdvisorModel>  newResultList = new ArrayList<AssociationAdvisorModel>();
//				List<AssociationTempUserModel> atumList = 
//						this.associationService.getTempUserInfo(associationId, AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString());
//				AssociationApplyModel curAam = this.associationService.getAssociationCurApply(newAam.getId());
//				String modifyItem = DataUtil.isNotNull(curAam.getModifyItem())?curAam.getModifyItem():"";
//				boolean isModifyFlag = modifyItem.indexOf(AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR.toString())>-1;
//				if(isModifyFlag && DataUtil.isNotNull(atumList) && atumList.size()>0){
//					for(AssociationTempUserModel atum: atumList){
//						AssociationAdvisorModel  aam = this.getInitAdvisorInfo(associationId, atum.getUserId());
//						aam.setComments(atum.getComments());
//						newResultList.add(aam);
//					}
//					return newResultList;
//				}
//			}
//			return aamList;
//	}
//
//	/**
//	 * 查看社团注销申请
//	 * @param model				页面数据加载器
//	 * @param request				页面请求
//	 * @param applyId				社团申请id
//	 * @return								指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPLY+"/opt-edit/viewAssociationCancelApply")
//	public String viewAssociationCancelApply(ModelMap model,HttpServletRequest request,String applyId){
//		AssociationApplyModel  newAam = this.getNewAam(applyId);
//		newAam.setApplyTypeCode(AssociationConstants.APPLY_STATUS.CANCEL.toString());
//		String associationId = (null!=newAam.getAssociationPo())?newAam.getAssociationPo().getId():"";
//		BaseAcademyModel curCollege = this.getCurUserCollege(this.sessionUtil.getCurrentUserId());
//		//获取社团指导老师
//		Page teacherPage = this.associationService.pageQueryAssociationAdvisor(
//				associationId, 1, AssociationConstants.DEFALT_PAGE_SIZE);
//		//获取社团负责人
//		Page stuPage = this.getManagerPage(newAam,AssociationConstants.APPLY_STATUS.CANCEL.toString(), request);
//		//获取社团指导老师列表
//		List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//		//获取社团负责人列表
//		List<AssociationMemberModel> ammList = this.associationService.getAssociationMembers(associationId);
//		
//		/**
//		 * 社团注销申请附件
//		 */
//		List<UploadFileRef> cancelfileList=this.getAssociationAttache(applyId,AssociationConstants.ATTACHE_TYPE.CANCEL.toString());
//		model.addAttribute("fileList", cancelfileList);
//		/**
//		 * 社团财务清算附件
//		 */
//		List<UploadFileRef> financefileList=this.getAssociationAttache(applyId,AssociationConstants.ATTACHE_TYPE.FINANCE.toString());
//		model.addAttribute("financefileList", financefileList);
//		model.addAttribute("associationId",(DataUtil.isNotNull(applyId)?associationId:IdUtil.getUUIDHEXStr()));
//		model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//		model.addAttribute("curCollegeName", curCollege.getName());
//		model.addAttribute("curCollegeId", curCollege.getId());
//		model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//		model.addAttribute("teacherPage", teacherPage);
//		model.addAttribute("stuPage", stuPage);
//		model.addAttribute("hasStuData", this.isStuHasData(stuPage));
//		model.addAttribute("aamList", aamList);
//		model.addAttribute("ammList", ammList);
//		model.addAttribute("aam", newAam);
//		model.addAttribute("hiddenManagerIds", this.getManagerIds(newAam));
//		return AssociationConstants.NAMESPACE_APPLY+"/associationCancelApplyView";
//	}
//
//	/**
//	 * 获取社团负责人列表
//	 * @param newAam	社团申请对象
//	 * @return	社团负责人列表
//	 */
//	private String getManagerIds(AssociationApplyModel newAam) {
//		StringBuffer managerIds=new StringBuffer("\"");
//		AssociationBaseinfoModel associationPo=(DataUtil.isNotNull(newAam))?newAam.getAssociationPo():null;
//		String associationId = DataUtil.isNotNull(associationPo)?associationPo.getId():"";
//		List<AssociationMemberModel> resultList = this.associationService.getAssociationManagers(associationId);
//		for(int i=0;i<resultList.size();i++){
//			AssociationMemberModel amm = resultList.get(i);
//			if(i==resultList.size()-1){
//				managerIds.append(amm.getMemberPo().getId());
//			}else{
//				managerIds.append(amm.getMemberPo().getId()).append(",");
//			}
//		}
//		managerIds.append("\"");
//		return managerIds.toString();
//	}
//
//	/**
//	 * 获取社团指导老师列表
//	 * @param newAam	社团申请对象
//	 * @return	社团指导老师列表
//	 */
//	private String getTeaderIds(AssociationApplyModel newAam) {
//		StringBuffer teacherIds=new StringBuffer("\"");
//		String associationId = (newAam!=null && newAam.getAssociationPo()!=null)?newAam.getAssociationPo().getId():"";
//		List<AssociationAdvisorModel> resultList = 
//				(List<AssociationAdvisorModel>)this.associationService.getAssociationAdvisors(associationId);
//		for(int i=0;i<resultList.size();i++){
//			AssociationAdvisorModel aampo = resultList.get(i);
//			if(i==resultList.size()-1){
//				teacherIds.append(aampo.getAdvisorPo().getId());
//			}else{
//				teacherIds.append(aampo.getAdvisorPo().getId()).append(",");
//			}
//		}
//		teacherIds.append("\"");
//		return teacherIds.toString();
//	}
//	
//	/**
//	 * 设置成员社团职务
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param associationId				社团主键
//	 * @param pk									业务主键
//	 * @param associationPosition	社团职务
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-modify/setMemberPosition"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String setMemberPosition(ModelMap model,HttpServletRequest request,String associationId,String memberPoId,String associationPosition){
//		String returnValue="{\"flag\":\"success\"}";
//		try {
//			String manager = AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER.getId();
//			if(DataUtil.isNotNull(associationPosition) && associationPosition.equals(manager)){//设置社长
//				boolean flag = this.associationService.isAssociationProprieter(associationId);
//				if(flag){
//					returnValue = "{\"flag\":\"onlyone\"}";
//				}
//			}else{//设置其他团内职务
//				this.associationService.setMemberPosition(memberPoId,associationPosition);
//			}
//		} catch (Exception e) {
//				returnValue = "{\"flag\":\"failed\"}";
//		}
//		
//		return returnValue;
//	}
//	
//	/**
//	 * 社团职务校验
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param associationId				社团主键
//	 * @param pk									业务主键
//	 * @param associationPosition	社团职务
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-modify/checkMemberPosition"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String checkMemberPosition(ModelMap model,HttpServletRequest request,String associationId,String memberPoId,String associationPosition){
//		String returnValue="{\"flag\":\"success\"}";
//		try {
//			String manager = AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER.getId();
//			if(DataUtil.isNotNull(associationPosition) && associationPosition.equals(manager)){//设置社长
//				boolean flag = this.associationService.isAssociationProprieter(associationId);
//				if(flag){
//					returnValue = "{\"flag\":\"onlyone\"}";
//				}
//			}
//		} catch (Exception e) {
//			returnValue = "{\"flag\":\"failed\"}";
//		}
//		
//		return returnValue;
//	}
//	
//	/**
//	 * 回滚社团临时用户
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param associationId				社团主键
//	 * @param userType						业务主键
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-delete/rollBackTemUsers"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String rollBackTemUsers(ModelMap model,HttpServletRequest request,String associationId,String userType){
//		String returnValue="{\"flag\":\"success\"}";
//		try {
//			
//			this.associationService.deleteAssociationTempUser(associationId, userType);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			returnValue = "{\"flag\":\"error\"}";
//		}
//		
//		return returnValue;
//	}
//	
//	/**
//	 * 负责人保存社团申请
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param aam								社团申请视图
//	 * @param applyFileId					申请附件集合
//	 * @param financeFileId				财务附件集合
//	 * @return											指定视图
//	 */
//    @RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-edit/managerSaveApply"})
//	public String managerSaveApply(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam,
//			String [] applyFileId,String [] financeFileId,String hiddenTeacherIds,String hiddenManagerIds,String proprieterId){
//    	String associationId = (aam.getAssociationPo()!=null)?aam.getAssociationPo().getId():"";
//    	String applyType = (aam.getApplyTypeDic()!=null)?aam.getApplyTypeDic().getCode():"";
//    	aam.setApplyStatus(Constants.OPERATE_STATUS.SAVE.toString());
//    	aam.setOperateStatus(AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());
//    	String modifyItem = aam.getModifyItem();
//    	//先初始指导老师
//    	this.initAdvisorInfo(associationId, hiddenTeacherIds,applyType,modifyItem);
//    	//再初始负责人
//    	this.initManagerInfo(associationId, hiddenManagerIds,proprieterId,applyType,request);
//    	//最后初始社团信息【保证有了指导老师，负责人后再保存社团】
//    	this.saveCurOperation(aam,applyType,AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());
//    	//初始化附件信息
//    	this.saveAssociationAttach(applyType,aam,applyFileId,financeFileId);
//		return "redirect:"+AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList.do";
//	}
//    
//    /**
//     * 负责人提交申请
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param aam								社团申请视图
//	 * @param applyFileId					申请附件集合
//	 * @param financeFileId				财务附件集合
//     * @param hiddenTeacherIds		指导老师集合
//     * @param hiddenManagerIds	负责人集合
//     * @param leaguePositions			负责人职位集合
//     * @return											指定视图
//     */
//    @RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-edit/managerSubmitApply"})
//    public String managerSubmitApply(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam,
//    		String [] applyFileId,String [] financeFileId,String hiddenTeacherIds,String hiddenManagerIds,String proprieterId){
//    	String associationId = (aam.getAssociationPo()!=null)?aam.getAssociationPo().getId():"";
//    	String applyType = (aam.getApplyTypeDic()!=null)?aam.getApplyTypeDic().getCode():"";
//    	aam.setApplyStatus(Constants.OPERATE_STATUS.SAVE.toString());
//    	String modifyItem = aam.getModifyItem();
//    	this.initAdvisorInfo(associationId, hiddenTeacherIds,applyType,modifyItem);
//    	this.initManagerInfo(associationId, hiddenManagerIds,proprieterId,applyType,request);
//    	this.saveCurOperation(aam,applyType,AssociationConstants.OPERATE_STATUS.MANAGER_SUBMIT.toString());
//    	this.saveAssociationAttach(applyType,aam,applyFileId,financeFileId);
//    	return "redirect:"+AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList.do";
//    }
//    
//    /**
//     * 指导老师保存社团申请
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param aam								社团申请视图
//	 * @param applyFileId					申请附件集合
//	 * @param financeFileId				财务附件集合
//	 * @return											指定视图
//     */
//    @RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-edit/advisorSaveApply"})
//    public String advisorSaveApply(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam,
//    		String [] applyFileId,String [] financeFileId){
//    	String applyType = (aam.getApplyTypeDic()!=null)?aam.getApplyTypeDic().getCode():"";
//    	aam.setOperateStatus(AssociationConstants.OPERATE_STATUS.ADVISOR_SAVE.toString());
//    	this.saveCurOperation(aam,applyType,AssociationConstants.OPERATE_STATUS.ADVISOR_SAVE.toString());
//    	return "redirect:"+AssociationConstants.NAMESPACE_APPLY+"/opt-query/getAssociationApplyList.do";
//    }
//	
//	/**
//	 * 提交社团申请【指导老师】
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param aam								社团申请实体
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-edit/submitAssociationApply"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String submitAssociationApply(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam){
//    	aam.setApplyStatus(Constants.OPERATE_STATUS.SUBMIT.toString());
//    	aam.setOperateStatus(AssociationConstants.OPERATE_STATUS.ADVISOR_SUBMIT.toString());
//    	String applyType = (aam!=null && aam.getApplyTypeDic()!=null)?aam.getApplyTypeDic().getCode():"";
//    	this.saveCurOperation(aam, applyType,AssociationConstants.OPERATE_STATUS.ADVISOR_SUBMIT.toString());
//		return "{\"flag\":\"success\"}";
//	}
//	
//    /**
//     * 保存当前执行的操作
//     * @param aam								社团申请对象
//     * @param applyType					社团申请类型
//     * @param operateStatus				操作类型
//     */
//    public void saveCurOperation(AssociationApplyModel aam,String applyType,String operateStatus){
//    	if(DataUtil.isNotNull(applyType)){
//    		this.associationService.addAssociationApplyInfo(aam,applyType,operateStatus);
//    	}else{
//    		logger.error("申请类型不确定，当前申请保存失败，请联系管理员.");
//    	}
//    }
//	
//	/**
//	 * 发起流程后，回写业务中的流程信息【流程状态、下一节点办理人】
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//	 * @param objectId						业务主键
//	 * @param nextApproverId		下一节点办理人
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-init/initCurProcess"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String initCurProcess(ModelMap model,HttpServletRequest request,String objectId,String nextApproverId){
//		ApproveResult result = new ApproveResult();
//		result.setResultFlag("success");
//		if(ProjectConstants.IS_APPROVE_ENABLE){
//			try {
//				User initiator = new User(this.sessionUtil.getCurrentUserId());
//				User nextApprover = new User(nextApproverId);
//				result = this.flowInstanceService.initProcessInstance(objectId,"ASSOCIATION_APPLY_APPROVE", 
//						initiator,nextApprover,ProjectConstants.IS_APPROVE_ENABLE);
//				this.saveProcessInfo(objectId,nextApprover);
//			} catch (Exception e) {
//				result.setResultFlag("error");
//			}
//		}else{
//			result.setResultFlag("deprecated");
//	    }
//		JSONObject json=JsonUtils.getJsonObject(result);
//		return JsonUtils.jsonObject2Json(json);
//	}
//	
//	/**
//	 * 回写提交后返回的流程信息
//	 * @param objectId					业务主键
//	 * @param nextApproverId	下一节点办理人
//	 */
//	private void saveProcessInfo(String objectId,User nextApprover) {
//		AssociationApplyModel newAam = this.associationService.getAssociationApplyInfo(objectId);
//		if(DataUtil.isNotNull(newAam)){
//			newAam.setApplyStatus(Constants.OPERATE_STATUS.SUBMIT.toString());
//			newAam.setNextapprover(nextApprover);
//			newAam.setInitiator(new User(this.sessionUtil.getCurrentUserId()));
//			newAam.setProcessstatus(ProjectSessionUtils.getApproveProcessStatusByCode("CURRENT_APPROVE"));
//			this.associationService.modifyAssociationApplyInfo(newAam);
//		}
//	}
//    
//    /**
//     * 保存社团附件
//     * @param applyType		申请类型
//     * @param aam					申请对象
//     * @param applyFileId		申请附件
//     * @param financeFileId	财务附件
//     */
//    private void saveAssociationAttach(String applyType,AssociationApplyModel aam,
//    																		 String [] applyFileId,String [] financeFileId) {
//		String applyId = (DataUtil.isNotNull(aam))?aam.getId():"";
//		//保存社团申请附件
//		this.saveApplyAttacheMent(applyId,applyType,applyFileId);
//		if(applyType.equals(AssociationConstants.ATTACHE_TYPE.CANCEL.toString())){
//			//保存财务结算附件
//			this.saveFinanceAttacheMent(applyId,financeFileId);
//		}
//	}
//
//	/**
//     * 保存社团申请附件
//     * @param applyId				社团申请id
//     * @param applyType		社团申请类型
//     * @param applyFileIds	附件id数组
//     */
//    private void saveApplyAttacheMent(String applyId, String applyType,String[] applyFileIds) {
//    	if(DataUtil.isNotNull(applyFileIds)){
//    		String applyAttachId = IdUtil.getUUIDHEXStr();
//    		List<UploadFileRef> urList = this.associationService.getAssociationAttache(applyId, applyType);
//    		if(DataUtil.isNotNull(urList) && urList.size()>0){
//    			this.associationService.deleteAssociationAttach(applyId,applyType);
//    		}
//    		//生成社团申请业务附件ID
//    		//分布式保存物理附件
//    		this.associationService.saveAttacheMent(applyAttachId,applyFileIds);
//    		//保存业务附件信息
//    		for(String fileId:applyFileIds){
//    			AssociationAttacheModel aam_ = this.formateAttacheMent(applyAttachId,applyId,applyType,fileId);
//    			this.associationService.saveAssociationAttach(aam_);
//    		}
//    	}
//	}
//
//	/**
//     * 保存财务结算附件
//     * @param applyId				社团申请id
//     * @param fileIds				附件id数组
//     */
//	private void saveFinanceAttacheMent(String applyId,String[] financeFileIds) {
//    	if(DataUtil.isNotNull(financeFileIds)){
//    		String applyType = AssociationConstants.ATTACHE_TYPE.FINANCE.toString();
//    		List<UploadFileRef> urList = this.associationService.getAssociationAttache(applyId, applyType);
//    		if(DataUtil.isNotNull(urList) && urList.size()>0){
//    			this.associationService.deleteAssociationAttach(applyId,applyType);
//    		}
//    		//生成社团申请业务附件ID
//    		String applyAttachId = IdUtil.getUUIDHEXStr();
//    		//分布式保存物理附件
//			this.associationService.saveAttacheMent(applyAttachId,financeFileIds);
//	    	//保存业务附件信息
//	    	for(String fileId:financeFileIds){
//	    		AssociationAttacheModel aam_ = this.formateAttacheMent(applyAttachId,applyId,applyType,fileId);
//	    		this.associationService.saveAssociationAttach(aam_);
//	    	}
//    	}
//	}
//	
//    /**
//     * 封装社团管理附件对象
//     * @param applyAttachId	业务主键
//     * @param applyId					申请主键
//     * @param applyType			附件类型
//     * @param fileId						附件 id
//     */
//    private AssociationAttacheModel formateAttacheMent(String applyAttachId,String applyId,
//			String applyType, String fileId) {
//    	AssociationAttacheModel aam_ = new AssociationAttacheModel();
//    	aam_.setId(applyAttachId);
//    	aam_.setApplyPo(this.associationService.getAssociationApplyInfo(applyId));
//    	aam_.setAttacheType(applyType);
//    	aam_.setAttachePo(this.associationService.getFileUploadRef(applyAttachId,fileId));
//    	aam_.setCreateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//    	aam_.setUpdateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//    	aam_.setDeleteStatus(Constants.STATUS_NORMAL);
//		return aam_;
//	}
//    
//    /**
//     * 社团名称重复性验证
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//     * @param associationId				社团主键
//     * @param associationName		社团名称
//     */
//    @RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/isAssociationNameRepeat"},produces={"text/plain;charset=UTF-8"})
//    @ResponseBody
//    public String isAssociationNameRepeat(ModelMap model,HttpServletRequest request,String associationId,String associationName){
//    	
//    	 if (this.associationService.isAssociationNameRepeat(associationId,associationName)) {
//  	       return "";
//  	     }
//  	     return "true";
//    }
//    
//    /**
//     * 社团编号重复性验证
//	 * @param model							页面数据加载器
//	 * @param request							页面请求
//     * @param associationId				社团主键
//     * @param associationCode		社团编码
//     */
//    @RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/isAssociationCodeRepeat"},produces={"text/plain;charset=UTF-8"})
//    @ResponseBody
//    public String isAssociationCodeRepeat(ModelMap model,HttpServletRequest request,String associationId,String associationCode){
//    	
//    	AssociationBaseinfoModel  abm = this.associationService.getAssociationInfoByCode(associationCode);
//    	String exitId = (DataUtil.isNotNull(abm))?abm.getId():"";
//    	if(DataUtil.isNotNull(exitId)){
//    		 if(DataUtil.isNotNull(exitId) && exitId.equals(associationId)){
//    			 return "true";
//    		 }else{
//    			 return "";
//    		 }
//    	}
//    	    return "true";
//    }
//
//	/**
//	 * 获得社团审核列表
//	 * @param model				页面数据加载器
//	 * @param request				页面请求
//	 * @param aam					社团申请对象
//	 * @return								指定页面
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-query/getAssociationApproveList")
//	public String getAssociationApproveList(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam){
//		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
//		aam.setApplyStatus(Constants.OPERATE_STATUS.SUBMIT.toString());
//		Page page = this.associationService.pageQueryAssociationApplyInfo(aam,pageNo,Page.DEFAULT_PAGE_SIZE);
//		List<AssociationApplyModel> resultList = (List<AssociationApplyModel>)page.getResult();
//		List<AssociationApplyModel> newResult = new ArrayList<AssociationApplyModel>();
//		for(AssociationApplyModel param:resultList){
//			//关联获取指导老师
//			String advisors = this.associationService.getAssociationAdvisors(param);
//			param.getAssociationPo().setAdvisors(advisors);
//			//关联获取社团人数
//			int memberNums = this.associationService.getAssociationMemberNums(param.getAssociationPo().getId());
//			param.getAssociationPo().setMemberNums(memberNums);
//			newResult.add(param);
//		}
//		page.setResult(newResult);
//		// 下拉列表 学院
//		model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//		model.addAttribute("applyTypeList", AssociationConstants.applyTypeList);
//	    model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//		model.addAttribute("page", page);
//		model.addAttribute("aam", aam);
//		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
//		return AssociationConstants.NAMESPACE_APPROVE+"/associationApproveList";
//	}
//	
//	/**
//	 * 社团注册审核页面
//	 * @param model				页面数据加载器
//	 * @param request				页面请求
//	 * @param aam					社团申请对象
//	 * @param applyType		申请类型【注册、变更、注销】
//	 * @return								指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-edit/editAssociationApprove")
//	public String editAssociationApprove(ModelMap model,HttpServletRequest request,AssociationApplyModel  aam,String applyType){
//		AssociationApplyModel  newAam = this.getNewAam(aam.getId());
//		Boolean flag = false;
//		if(newAam!=null && newAam.getApplyTypeDic()!=null && StringUtils.isNotBlank(newAam.getApplyTypeDic().getId())
//				&& !"审核通过".equals(newAam.getProcessstatus()) && newAam.getModifyItem()!=null && newAam.getModifyItem().contains("ASSOCIATION_MANAGER"))
//		{
//			Dic dic = dicService.getDic(newAam.getApplyTypeDic().getId());
//			flag=AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(dic.getCode());
//		}
//		newAam.setApplyTypeCode(applyType);
//		String associationId = (null!=newAam.getAssociationPo())?newAam.getAssociationPo().getId():"";
//		BaseAcademyModel curCollege = this.getCurUserCollege(this.sessionUtil.getCurrentUserId());
//		//获取社团指导老师
//		Page teacherPage = this.getAdvisorPage(associationId,applyType,newAam.getModifyItem());
//		//获取社团负责人
//		Page stuPage = this.getManagerPage(newAam,applyType, request);
//		//获取社团指导老师列表
//	   List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//	   //获取社团负责人列表
//	   List<AssociationMemberModel> ammList = this.associationService.getAssociationMembers(associationId);
//	   
//		/**
//		 * 社团申请附件
//		 */
//		List<UploadFileRef> cancelfileList=this.getAssociationAttache(newAam.getId(),applyType);
//		model.addAttribute("fileList", cancelfileList);
//		/**
//		 * 社团财务清算附件
//		 */
//		List<UploadFileRef> financefileList=this.getAssociationAttache(newAam.getId(),AssociationConstants.ATTACHE_TYPE.FINANCE.toString());
//		model.addAttribute("financefileList", financefileList);
//	   
//	   model.addAttribute("associationId",(DataUtil.isNotNull(aam.getId())?associationId:IdUtil.getUUIDHEXStr()));
//	   model.addAttribute("associationTypeDicList", AssociationConstants.associationTypeList);
//       model.addAttribute("collegeList", this.baseDataService.listBaseAcademy());
//	   model.addAttribute("curCollegeName", curCollege.getName());
//	   model.addAttribute("curCollegeId", curCollege.getId());
//	   model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//	   model.addAttribute("teacherPage", teacherPage);
//	   model.addAttribute("stuPage", stuPage);
//	   model.addAttribute("hasStuData", this.isStuHasData(stuPage));
//	   model.addAttribute("aamList", this.getModifyApplyAdvisors(associationId, aamList,newAam));
//	   model.addAttribute("ammList", ammList);
//	   model.addAttribute("aam", newAam);
//	   if(flag){
//	    	model.addAttribute("hiddenManagerIds", this.getNewManagerIds(newAam, AssociationConstants.APPLY_STATUS.MODIFY.toString(),AssociationConstants.ASSOCIATION_USER_TYPE.MANAGER.toString()));
//		}else{
//			model.addAttribute("hiddenManagerIds", this.getManagerIds(newAam));
//		}
//	   
//	   String url = this.getApproveEditPage(applyType);
//	   return url;
//	}
//	
//	/**
//	 * 获取审批返回页面
//	 * @param applyType	申请类型
//	 * @return	审批返回页面
//	 */
//	private String getApproveEditPage(String applyType) {
//		   String url="";
//		   String register_ = AssociationConstants.APPLY_STATUS.REGISTER.toString();
//		   String modify_ = AssociationConstants.APPLY_STATUS.MODIFY.toString();
//		   String cancel_ = AssociationConstants.APPLY_STATUS.CANCEL.toString();
//		   if(DataUtil.isNotNull(applyType) && register_.equals(applyType)){
//			   
//			   url = AssociationConstants.NAMESPACE_APPROVE+"/associationRegisterApproveEdit";
//		   }else if(DataUtil.isNotNull(applyType) && modify_.equals(applyType)){
//			   
//			   url =  AssociationConstants.NAMESPACE_APPROVE+"/associationModifyApproveEdit";
//		   }else if(DataUtil.isNotNull(applyType) && cancel_.equals(applyType)){
//			   
//			   url =  AssociationConstants.NAMESPACE_APPROVE+"/associationCancelApproveEdit";
//		   }
//		   
//		   return url;
//	}
//
//	/**
//	 * 审批社团申请
//	 * @param model								页面数据加载器
//	 * @param request								页面请求
//	 * @param applyType						申请类型【注册、变更、注销】
//	 * @param objectId							业务主键
//	 * @param nextApproverId			下一节点办理人
//	 * @param approveStatus				流程审核状态
//	 * @param processStatusCode		审核状态编码
//	 * @param approveKey					审核操作【通过，拒绝】
//	 * @return												指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-apporve/approveAssociationApply")
//	public String approveAssociationApply(ModelMap model,HttpServletRequest request,String applyType,
//		String objectId,String nextApproverId,String approveStatus,String processStatusCode,String approveKey){
//		approveStatus = ChineseUtill.toChinese(approveStatus);
//		AssociationApplyModel newAam = this.associationService.getAssociationApplyInfo(objectId);
//		boolean isFinalTask = this.flowInstanceService.isFinalTask_(newAam.getId(),this.sessionUtil.getCurrentUserId());
//		if(DataUtil.isNotNull(newAam)){
//			if(DataUtil.isNotNull(approveKey) && approveKey.equals("REJECT")){
//				newAam.setApplyStatus(Constants.OPERATE_STATUS.SAVE.toString());
//				newAam.setNextapprover(null);
//				this.rejectCurApply(newAam,isFinalTask);
//			}else if(DataUtil.isNotNull(approveKey) && approveKey.equals("PASS")){
//				newAam.setNextapprover(new User(nextApproverId));
//				this.activiteAssociation(applyType,newAam,approveKey,isFinalTask, request);
//			}
//			
//			if(isFinalTask){
//				newAam.setProcessstatus(ProjectSessionUtils.getApproveProcessStatusByCode(processStatusCode));
//				//获取社团编码
//				String collegeId = newAam.getAssociationPo().getCollege().getId();
//				Dic associationType = newAam.getAssociationPo().getAssociationType();
//				Dic isMajor = newAam.getAssociationPo().getIsMajor();
//				String associationCode = AssociationUtils.generateAssociationCode(collegeId, associationType, isMajor);
//				if(approveKey.equals("PASS") && !associationCode.equals(newAam.getAssociationPo().getAssociationCode()))
//				{  
//					AssociationBaseinfoModel associationPo = associationService.getAssociationInfo(newAam.getAssociationPo().getId());
//					associationPo.setAssociationCode(associationCode);
//					newAam.setAssociationPo(associationPo);
//				}
//			}else{
//				newAam.setProcessstatus(ProjectSessionUtils.getApproveProcessStatusByCode(CYLeagueUtil.APPLY_APPROVE_STATUS.CURRENT_APPROVE.toString()));
//			}
//			this.associationService.modifyAssociationApplyInfo(newAam);
//		}
//		return "redirect:"+AssociationConstants.NAMESPACE_APPROVE+"/opt-query/getAssociationApproveList.do";
//	}
//	
//	/**
//	 * 流程拒绝后的业务操作
//	 * @param newAam		社团申请实体
//	 */
//	private void rejectCurApply(AssociationApplyModel newAam,boolean isFinalTask) {
//		String associationId = (DataUtil.isNotNull(newAam.getAssociationPo()))?newAam.getAssociationPo().getId():"";
//		AssociationBaseinfoModel associationPo = this.associationService.getAssociationInfo(associationId);
//		if(DataUtil.isNotNull(associationPo) && DataUtil.isNotNull(associationPo.getId())){
//			if(isFinalTask){
//					String applyType = newAam.getApplyTypeDic().getCode();
//					this.rollBackApplyResult(newAam,applyType);//回滚社团申请信息
//					this.rollBackAssociationResult(associationPo,applyType);//回滚社团信息
//			}
//		}
//	}
//	
//	/**
//	 * 回滚社团申请信息--zhangmx
//	 * @param newAam
//	 * @param applyType
//	 */
//	private void rollBackApplyResult(AssociationApplyModel newAam,String applyType) {
//		newAam.setApplyStatus(Constants.OPERATE_STATUS.SAVE.toString());//申请状态变为保存
//		newAam.setOperateStatus(AssociationConstants.OPERATE_STATUS.MANAGER_SAVE.toString());//操作状态变为负责人保存
//		this.associationService.modifyAssociationApplyInfo(newAam);
//	}
//
//	/**
//	 * 回滚社团信息
//	 * @param associationPo		社团基础实体
//	 * @param applyType			社团申请类型
//	 */
//	private void rollBackAssociationResult(AssociationBaseinfoModel associationPo,String applyType) {
//		associationPo.setIsCancel(this.dicUtil.getDicInfo("Y&N", "N"));
//		this.associationService.updateAssociationInfo(associationPo);
//		/*if(DataUtil.isNotNull(applyType) && 
//			 applyType.equals(AssociationConstants.APPLY_STATUS.MODIFY.toString())){//变更审批驳回时，删除临时用户信息
//			//this.associationService.deleteAssociationTempUser(associationPo.getId());
//		}*/
//	}
//
//	/**
//	 * 社团申请后审批通过，设置社团标志
//	 * @param applyType		社团申请类型
//	 * @param newAam			社团申请对象
//	 * @param approveKey	社团申请审批标识
//	 */
//	private void activiteAssociation(String applyType,AssociationApplyModel newAam,String approveKey,boolean isFinalTask,HttpServletRequest request) {
//		String associationId = (DataUtil.isNotNull(newAam.getAssociationPo()))?newAam.getAssociationPo().getId():"";
//		AssociationBaseinfoModel associationPo = this.associationService.getAssociationInfo(associationId);
//		if(DataUtil.isNotNull(associationPo) && DataUtil.isNotNull(associationPo.getId())){
//			if(isFinalTask){
//				if(applyType.equals(AssociationConstants.APPLY_STATUS.REGISTER.toString())){//社团注册成功
//					this.saveRegisterApproveResult(associationPo);
//				}else if(applyType.equals(AssociationConstants.APPLY_STATUS.MODIFY.toString())){//社团变更成功
//					this.saveModifyApproveResult(associationPo);
//					this.saveModifyAdvisorInfo(associationId);
//					this.saveModifyManagerInfo(associationId,associationPo, request);
//				}else if(applyType.equals(AssociationConstants.APPLY_STATUS.CANCEL.toString())){//社团注销成功
//					this.saveCancelApproveResult(associationPo);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 保存变更审批成功后的指导老师信息
//	 * @param associationId		社团id
//	 */
//	private void saveModifyAdvisorInfo(String associationId) {
//		
//		//获取变更后的社团负责人列表
//		String userType = AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString();
//		List<AssociationTempUserModel> atumList =  this.associationService.getTempUserInfo(associationId,userType);
//		if(atumList!=null && atumList.size()>0){
//				//获取当前社团指导老师列表
//				List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//				//删除社团指导老师
//				for(AssociationAdvisorModel aam:aamList){
//					String advisorId = (aam !=null && aam.getAdvisorPo()!=null)?aam.getAdvisorPo().getId():"";
//					this.associationService.deleteAssociationAdvisor(associationId, advisorId);
//				}
//				
//				//更新变更的指导老师
//				for(AssociationTempUserModel atum:atumList){
//					AssociationAdvisorModel aam = this.getInitAdvisorInfo(associationId, atum.getUserId());
//					aam.setComments(atum.getComments());
//					this.associationService.addAssociationAdvisor(aam);
//				}
//				
//		}
//	}
//
//	/**
//	 * 保存变更审批成功后的负责人信息
//	 * @param associationId		社团id
//	 * @param associationPo		社团对象
//	 */
//	private void saveModifyManagerInfo(String associationId,AssociationBaseinfoModel associationPo,HttpServletRequest request) {
//		
//		//获取变更后的社团负责人列表
//		String userType = AssociationConstants.ASSOCIATION_USER_TYPE.MANAGER.toString();
//		List<AssociationTempUserModel> atumList =  this.associationService.getTempUserInfo(associationId,userType);
//		if(atumList!=null && atumList.size()>0){
//			String proprieterId = (associationPo.getProprieter()!=null)?associationPo.getProprieter().getId():"";
//			//获取当前社团负责人列表
//			List<AssociationMemberModel> ammList = this.associationService.getAssociationManagers(associationId);
//			//删除社团负责人
//			for(AssociationMemberModel amm:ammList){
//				String memberId = (amm !=null && amm.getMemberPo()!=null)?amm.getMemberPo().getId():"";
//				this.associationService.truncateManagerInfo(associationId,memberId);
//			}
//			
//			//更新变更后的负责人
//			for(AssociationTempUserModel atum:atumList){
//				AssociationMemberModel amm = this.getInitManagerInfo(associationId, atum.getUserId(), proprieterId, request);
//				this.associationService.addAssociationManager(amm);
//			}
//			//删除变更后的社团负责人临时信息
//			this.associationService.deleteAssociationTempUser(associationId, userType);
//		}
//	}
//
//	/**
//	 * 变更审核通过后，设置社团基础信息
//	 * @param associationPo		社团基础实体
//	 */
//	private void saveModifyApproveResult(AssociationBaseinfoModel associationPo) {
//		//设置社团负责人【社长】
//		associationPo.setProprieterRegister(associationPo.getProprieter());
//		//设置社团注册名称
//		associationPo.setAssociationRegisterName(associationPo.getAssociationName());
//		//设置社团注册类型
//		associationPo.setAssociationRegisterType(associationPo.getAssociationType());
//		//设置社团注册性质
//		associationPo.setIsMajorRegister(associationPo.getIsMajor());
//		//保存社团变更后的基本信息
//		this.associationService.updateAssociationInfo(associationPo);
//	}
//
//	/**
//	 * 注销审核通过后，设置社团状态
//	 * @param associationPo		社团基础实体
//	 */
//	private void saveCancelApproveResult(AssociationBaseinfoModel associationPo) {
//		associationPo.setIsValid(this.dicUtil.getDicInfo("Y&N", "N"));
//		this.associationService.updateAssociationInfo(associationPo);
//	}
//
//	/**
//	 * 注册审批成功后，设置社团可见状态
//	 * @param associationPo		社团基本信息
//	 */
//	private void saveRegisterApproveResult(AssociationBaseinfoModel associationPo) {
//		associationPo.setIsValid(this.dicUtil.getDicInfo("Y&N", "Y"));
//		this.associationService.updateAssociationInfo(associationPo);
//	}
//
//	/**
//	 *  获取批量审批社团申请
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param selectedBox			批量审核主键集合
//	 * @return									指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-apporve/getMulAAPage")
//	public String getMulAAPage(ModelMap model,HttpServletRequest request,String selectedBox){
//
//		String applyIds = CYLeagueUtil.getCondition(selectedBox);
//		List<AssociationApplyModel> selecteAamList = new ArrayList<AssociationApplyModel>();
//		List<AssociationApplyModel> aamList = this.associationService.getAssociationApplyByIds(applyIds);
//		for(AssociationApplyModel param:aamList){
//			//关联获取指导老师
//			String advisors = this.associationService.getAssociationAdvisors(param);
//			param.getAssociationPo().setAdvisors(advisors);
//			selecteAamList.add(param);
//		}
//		model.addAttribute("aamList", selecteAamList);
//	     model.addAttribute("objectIds", selectedBox);
//		return AssociationConstants.NAMESPACE_APPROVE+"/mulAssociationApprove";
//	}
//	
//	/**
//	 * 批量审批社团申请
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param mulResults			批量审核结果集
//	 * @return									指定视图
//	 */
//	@RequestMapping(AssociationConstants.NAMESPACE_APPROVE+"/opt-apporve/mulAssociationApprove")
//	public String mulAssociationApprove(ModelMap model,HttpServletRequest request,String mulResults){
//		
//		List<ApproveResult> list  = this.flowInstanceService.getFormatedResult(mulResults,ProjectConstants.IS_APPROVE_ENABLE);
//		if(DataUtil.isNotNull(list) && list.size()>0){
//			for(ApproveResult result:list){
//				String objectId = result.getObjectId();
//				String approveKey = result.getApproveKey();
//				
//				Approver nextApprover = (result.getNextApproverList()!=null && result.getNextApproverList().size()>0)?
//																	    result.getNextApproverList().get(0):null;
//				String nextApproverId = (DataUtil.isNotNull(nextApprover))?nextApprover.getUserId():"";
//		        User nextUser = new User(nextApproverId);
//		        String processStatusCode = result.getProcessStatusCode();
//		        this.saveMulApproveResult(objectId,approveKey,nextUser,processStatusCode, request);
//			}
//		}
//		return "redirect:"+AssociationConstants.NAMESPACE_APPROVE+"/opt-query/getAssociationApproveList.do";
//	}
//	
//	/**
//	 * 保存批量审批结果到业务表
//	 * @param objectId						业务主键
//	 * @param approveKey				审批操作【PASS、NOT_PASS、REJECT】
//	 * @param nextUser						下一节点审核人
//	 * @param processStatusCode	 当前节点审批结果
//	 */
//	private void saveMulApproveResult(String objectId, String approveKey,
//		User nextUser, String processStatusCode,HttpServletRequest request) {
//		AssociationApplyModel newAam = this.associationService.getAssociationApplyInfo(objectId);
//		boolean isFinalTask = this.flowInstanceService.isFinalTask_(newAam.getId(),this.sessionUtil.getCurrentUserId());
//		if(DataUtil.isNotNull(newAam)){
//			if(DataUtil.isNotNull(approveKey) && approveKey.equals("REJECT")){
//				newAam.setApplyStatus(Constants.OPERATE_STATUS.SAVE.toString());
//				newAam.setNextapprover(newAam.getInitiator());
//				this.rejectCurApply(newAam,isFinalTask);
//			}else if(DataUtil.isNotNull(approveKey) && approveKey.equals("PASS")){
//				newAam.setNextapprover(nextUser);
//				String applyType = (newAam.getApplyTypeDic()!=null)?newAam.getApplyTypeDic().getCode():"";
//				this.activiteAssociation(applyType,newAam,approveKey,isFinalTask, request);
//			}
//			
//			if(isFinalTask){
//				newAam.setProcessstatus(ProjectSessionUtils.getApproveProcessStatusByCode(processStatusCode));
//			}else{
//				newAam.setProcessstatus(ProjectSessionUtils.getApproveProcessStatusByCode(CYLeagueUtil.APPLY_APPROVE_STATUS.CURRENT_APPROVE.toString()));
//			}
//			this.associationService.modifyAssociationApplyInfo(newAam);
//		}
//	}
//
//	/**
//	 * 保存社团指导员信息
//	 * @param associationId		社团主键
//	 * @param teacherIds			指导老师集合
//	 * @param applyType			申请类型
//	 * @param modifyItem			变更项
//	 */
//	public void initAdvisorInfo(String associationId,String teacherIds,String applyType,String modifyItem){
//		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(teacherIds)){
//			//页面选中的指导老师信息
//			String [] idArray = teacherIds.split(",");
//			List<String> IdList = new ArrayList<String>();
//			for(String id:idArray){
//				IdList.add(id);
//			}
//			if(AssociationConstants.APPLY_STATUS.REGISTER.toString().equalsIgnoreCase(applyType)){
//				
//				this.setAdvisorInfo(associationId,IdList,teacherIds);
//			}else if(AssociationConstants.APPLY_STATUS.MODIFY.toString().equalsIgnoreCase(applyType)){
//				if(modifyItemCheck(modifyItem, AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR.toString())){
//					String userType = AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString();
//					this.setAssociationTempInfo(associationId,idArray,userType);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * 社团指导老师是否发生变更
//	 * @param associationId	社团主键
//	 * @param applyId 			申请主键
//	 * @param advisorIds 指导老师
//	 * @return [true、false]
//	 */
//	private boolean isAdvisorModify(String associationId, String modifyItem,String advisorIds) {
//		List<AssociationAdvisorModel> ammList = this.associationService.getAssociationAdvisors(associationId);
//		boolean isAdvisorChange = this.validateAdvisorChange(associationId,ammList,advisorIds);
//		if(DataUtil.isNotNull(modifyItem) && 
//			 modifyItem.indexOf(AssociationConstants.MODIFY_TYPE.ASSOCIATION_ADVISOR.toString())>-1){
//			return true;
//		}
//		return false;
//	}
//	
//	/**
//	 * 检查变更内容
//	 * @param modifyItem		变更内容
//	 * @param checkedItem	变更项
//	 * @return [true/false]
//	 */
//	private boolean modifyItemCheck(String modifyItem,String checkedItem) {
//		if(DataUtil.isNotNull(modifyItem) && 
//				modifyItem.indexOf(checkedItem)>-1){
//			return true;
//		}
//		return false;
//	}
//	
//	/**
//	 * 验证指导老师是否发生变更
//	 * @param ammList			变更前指导老师
//	 * @param advisorIds		变更后指导老师
//	 * @return [true、false]
//	 */
//	private boolean validateAdvisorChange(String associationId,List<AssociationAdvisorModel> ammList, String advisorIds) {
//		List<AssociationTempUserModel> tempUsers = 
//				this.associationService.getTempUserInfo(associationId, AssociationConstants.ASSOCIATION_USER_TYPE.ADVISOR.toString());
//		if(DataUtil.isNotNull(advisorIds)){
//			for(AssociationAdvisorModel aam:ammList){
//				String advisorId = (aam.getAdvisorPo()!=null)?aam.getAdvisorPo().getId():"";
//				if(advisorIds.indexOf(advisorId)==-1){
//					return true;
//				}
//			}
//		}else if(tempUsers!=null && tempUsers.size()>0){
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 社团负责人是否发生变更
//	 * @param associationId	社团主键
//	 * @param applyType 		申请类型
//	 * @param managerIds 	申请类型
//	 * @return [true、false]
//	 */
//	private boolean isManagerModify(String associationId, String applyId,String managerIds) {
//		AssociationApplyModel aam = this.associationService.getAssociationCurApply(applyId);
//		String modifyItem = DataUtil.isNotNull(aam)?aam.getModifyItem():null;
//		List<AssociationMemberModel> ammList = this.associationService.getAssociationManagers(associationId);
//		boolean isManagerChange = this.validateManagerChange(associationId,ammList,managerIds);
//		if(DataUtil.isNotNull(modifyItem) && 
//			 modifyItem.indexOf(AssociationConstants.MODIFY_TYPE.ASSOCIATION_MANAGER.toString())>-1){
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 验证负责人是否发生变更
//	 * @param ammList			变更前负责人列表
//	 * @param managerIds		变更后负责人列表
//	 * @return	[true、false]
//	 */
//	private boolean validateManagerChange(String associationId,List<AssociationMemberModel> ammList,String managerIds) {
//		List<AssociationTempUserModel> tempUsers = 
//				this.associationService.getTempUserInfo(associationId, AssociationConstants.ASSOCIATION_USER_TYPE.MANAGER.toString());
//		if(DataUtil.isNotNull(managerIds)){
//			for(AssociationMemberModel amm:ammList){
//				String managerId = amm.getMemberPo().getId();
//				if(DataUtil.isNotNull(managerId) && (managerIds.indexOf(managerId)==-1))
//					return true;
//			}
//		}else if(tempUsers!=null && tempUsers.size()>0){
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 设置社团指导老师信息【临时表】
//	 * @param associationId	社团主键
//	 * @param idArray 			页面获取的社团指导老师集合
//	 * @param userType			社团用户类型【指导老师，负责人】
//	 */
//	private void setAssociationTempInfo(String associationId, String[] idArray,String userType) {
//		
//		this.associationService.deleteAssociationTempUser(associationId,userType);
//		List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//		for(String advisorId:idArray){
//			AssociationTempUserModel atumPo = this.packageAtumPo(associationId,userType,advisorId);
//			atumPo.setComments(this.getAdvisorComments(aamList,advisorId));
//			this.associationService.saveAtumpo(atumPo);
//		}
//	}
//
//	/**
//	 * 获取未发生变更指导老师的简介
//	 * @param aamList			变更前指导老师列表
//	 * @param advisorId		变更后指导老师id
//	 * @return 变更后指导老师简介
//	 */
//	private String getAdvisorComments(List<AssociationAdvisorModel> aamList,String advisorId) {
//		if(DataUtil.isNotNull(aamList)){
//			for(AssociationAdvisorModel aam:aamList){
//				String  userId = (DataUtil.isNotNull(aam.getAdvisorPo()))?aam.getAdvisorPo().getCode():"";
//				if(DataUtil.isNotNull(userId) && userId.equals(advisorId))
//					return aam.getComments();
//			}
//		}
//		return "";
//	}
//
//	/**
//	 * 封装社团临时用户对象
//	 * @param associationId		社团id
//	 * @param userType				用户类型
//	 * @param userId				用户id
//	 * @return
//	 */
//	private AssociationTempUserModel packageAtumPo(String associationId,String userType, String userId) {
//		AssociationTempUserModel atumPo = new AssociationTempUserModel();
//		AssociationBaseinfoModel abm = new AssociationBaseinfoModel();
//		abm.setId(associationId);
//		atumPo.setAssociationPo(abm);
//		atumPo.setUserType(userType);
//		atumPo.setUserId(userId);
//		atumPo.setCreateTime(AmsDateUtil.toTime(DateUtil.getCurDate()));
//		atumPo.setUpdateTime(AmsDateUtil.toTime(DateUtil.getCurDate()));
//		return atumPo;
//	}
//
//	/**
//	 * 设置社团指导老师信息
//	 * @param associationId	社团主键
//	 * @param IdList					页面获取的社团指导老师集合
//	 * @param teacherIds		页面获取的指导老师id字符串
//	 */
//	private void setAdvisorInfo(String associationId,List<String> IdList,String teacherIds) {
//		//清除没有选择的教师数据
//		List<AssociationAdvisorModel> aamList = this.associationService.getAssociationAdvisors(associationId);
//		for(int i=0;i<aamList.size();i++){
//			AssociationAdvisorModel amm=aamList.get(i);
//			String advisorId = (amm.getAdvisorPo()!=null)?amm.getAdvisorPo().getId():"";
//			if(DataUtil.isNotNull(advisorId)&&teacherIds.indexOf(advisorId)==-1){
//				BaseTeacherModel btm = this.baseDataService.findTeacherById(advisorId);
//				String advisorId_ = DataUtil.isNotNull(btm)?btm.getId():"";
//				this.associationService.deleteAssociationAdvisor(associationId,advisorId_);
//			}else{
//				IdList.remove(advisorId);
//			}
//		}
//		
//		//保存新增的指导老师信息
//		for(String teacherId:IdList){
//			if(DataUtil.isNotNull(teacherId)){
//				AssociationAdvisorModel aam_ = this.associationService.getAssociationAdvisor(teacherId);
//				if(!DataUtil.isNotNull(aam_)){
//					AssociationAdvisorModel aam = this.getInitAdvisorInfo(associationId,teacherId);
//					this.associationService.addAssociationAdvisor(aam);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 判断当前指导老师，是否已经存在
//	 * @param aamList			已存在的社团指导老师
//	 * @param teacherId		选择的当前老师
//	 */
//	private AssociationAdvisorModel isExistAdvisor(List<AssociationAdvisorModel> aamList,String teacherId) {
//		if(DataUtil.isNotNull(aamList)){
//			for(AssociationAdvisorModel aam:aamList){
//				String curTeacherId = (aam.getAdvisorPo()!=null)?aam.getAdvisorPo().getCode():"";
//				if(curTeacherId.equals(teacherId)){
//					return aam;
//				}
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 删除指导员信息
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param id								业务主键
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-delete/deleteAdvisorInfo"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String deleteAdvisorInfo(ModelMap model,HttpServletRequest request,String id){
//		try {
//			this.associationService.deleteAdvisorInfo(id);
//			return "success";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "error";
//		}
//	}
//	
//	/**
//	 * 删除社团成员信息
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param id								业务主键
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-delete/deleteMemberInfo"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String deleteMemberInfo(ModelMap model,HttpServletRequest request,String id){
//		try {
//			this.associationService.deleteAssociationMemberInfo(id);
//			return "success";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "error";
//		}
//	}
//	
//	/**
//	 * 检查是否本学院指导员
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param associationId		社团主键
//	 * @param teacherIds			指导老师集合
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/checkAdvisor"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String checkAdvisor(ModelMap model,HttpServletRequest request,String associationId,String teacherIds){
//		String [] IdArray = teacherIds.split(",");
//		StudentInfoModel sim = this.stuService.queryStudentById(this.sessionUtil.getCurrentUserId());
//		StringBuffer errorTeachers=new StringBuffer();
//		for(int i=0;i<IdArray.length;i++){
//			String teacherId=IdArray[i];
//			if(!this.isCurCollegeTeacher(teacherId,sim)){
//				if(i==IdArray.length-1){
//					errorTeachers.append(teacherId);
//				}else{
//					errorTeachers.append(teacherId).append("|");
//				}
//			}
//		}
//		return "{\"success\":\"success\",\"errorTeachers\":\""+errorTeachers.toString()+"\"}";
//	}
//	
//	/**
//	 * 检查是否本学院学生
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param associationId		社团主键
//	 * @param stuIds					学生集合
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/checkCurCollegeStudent"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String checkCurCollegeStudent(ModelMap model,HttpServletRequest request,String associationId,String stuIds){
//		String [] IdArray = stuIds.split(",");
//		StudentInfoModel sim = this.stuService.queryStudentById(this.sessionUtil.getCurrentUserId());
//		StringBuffer errorStudents=new StringBuffer();
//		for(int i=0;i<IdArray.length;i++){
//			String stuId=IdArray[i];
//			if(!this.isCurCollegeStudent(stuId,sim)){
//				if(i==IdArray.length-1){
//					errorStudents.append(stuId);
//				}else{
//					errorStudents.append(stuId).append("|");
//				}
//			}
//		}
//		return "{\"success\":\"success\",\"errorStudents\":\""+errorStudents.toString()+"\"}";
//	}
//	
//	/**
//	 * 判断当前教师是否本学院学生
//	 * @param stuId				学生Id
//	 * @param sim					登录人学院信息
//	 * @return							[true/false]
//	 */
//	private boolean isCurCollegeStudent(String stuId, StudentInfoModel sim) {
//		boolean returnValue=false;
//		if(DataUtil.isNotNull(sim) && DataUtil.isNotNull(sim.getCollege())){
//			String collegeId = sim.getCollege().getId();
//			StudentInfoModel stuModel = this.stuService.queryStudentById(stuId);
//			String selectedStuCollege = (stuModel!=null && stuModel.getCollege()!=null)?stuModel.getCollege().getId():"";
//			if(DataUtil.isNotNull(collegeId) && DataUtil.isNotNull(selectedStuCollege)){
//				if(selectedStuCollege.equals(collegeId)){
//					returnValue =  true;
//				}
//			}
//		}
//		
//		return returnValue;
//	}
//
//	/**
//	 * 判断当前教师是否本学院教师
//	 * @param teacherId		教师Id
//	 * @param sim					登录人学院信息
//	 * @return							[true/false]
//	 */
//	private boolean isCurCollegeTeacher(String teacherId, StudentInfoModel sim) {
//		boolean returnValue=false;
//		if(DataUtil.isNotNull(sim) && DataUtil.isNotNull(sim.getCollege())){
//			String collegeId = sim.getCollege().getId();
//			BaseTeacherModel btm = this.baseDataService.findTeacherById(teacherId);
//			if(DataUtil.isNotNull(btm) && DataUtil.isNotNull(btm.getOrg())){
//				String teacherCollege = btm.getOrg().getId();
//				if(DataUtil.isNotNull(collegeId) && DataUtil.isNotNull(teacherCollege) && teacherCollege.equals(collegeId)){
//					returnValue =  true;
//				}
//			}
//		}
//		
//		return returnValue;
//	}
//
//	/**
//	 * 初始化社团指导员信息
//	 * @param associationId	社团id
//	 * @param teacherId	老师id
//	 */
//	private AssociationAdvisorModel getInitAdvisorInfo(String associationId,String teacherId) {
//		AssociationAdvisorModel aam = new AssociationAdvisorModel();
//		AssociationBaseinfoModel associationPo = this.associationService.getAssociationInfo(associationId);
//		if(DataUtil.isNotNull(associationId)){
//			associationPo.setId(associationId);
//		}
//		aam.setAssociationPo(associationPo);
//		BaseTeacherModel btm = this.baseDataService.findTeacherById(teacherId);
//		aam.setAdvisorPo(btm);
//		User user = this.userService.getUserById(teacherId);
//		if(DataUtil.isNotNull(user)){
//			aam.setPhone(user.getPhone());
//		}
//		AssociationAdvisorModel aam_ = this.associationService.findAssociationAdvisor(associationId,teacherId);
//		aam.setComments(DataUtil.isNotNull(aam_)?aam_.getComments():"");
//		aam.setCreateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//		aam.setUpdateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//		aam.setDeleteStatus(this.dicUtil.getStatusNormal());
//		return aam;
//	}
//	
//	/**
//	 * 保存社团负责人信息
//	 * @param associationId		 社团主键
//	 * @param managerIds			 社团负责人集合
//	 * @param proprieterId	 	 社长id
//	 * @param applyType	 	 	 申请类型
//	 */
//	public void initManagerInfo(String associationId,String managerIds,String proprieterId,String applyType,HttpServletRequest request){
//		if(DataUtil.isNotNull(associationId) && DataUtil.isNotNull(managerIds)){
//			try {
//				
//				String [] idArray = managerIds.split(",");
//				if(AssociationConstants.APPLY_STATUS.REGISTER.toString().equalsIgnoreCase(applyType)){
//					
//					this.setAssociationManagerInfo(idArray,associationId,proprieterId,request);
//				}else if(AssociationConstants.APPLY_STATUS.MODIFY.toString().equalsIgnoreCase(applyType)){
//					if(isManagerModify(associationId,AssociationConstants.APPLY_STATUS.MODIFY.toString(),managerIds)==false){
//						String userType = AssociationConstants.ASSOCIATION_USER_TYPE.MANAGER.toString();
//						this.setAssociationTempInfo(associationId,idArray,userType);
//					}
//				}
//			} catch (Exception e) {
//				logger.error("社团负责人初始化错误："+e.getMessage());
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	
//	private void setAssociationManagerInfo(String [] idArray,String associationId,String proprieterId,HttpServletRequest request) {
//		
//		//获取社团负责人列表【更新前】
//		List<AssociationMemberModel> ammList = this.associationService.getAssociationManagers(associationId);
//		
//		//删除社团负责人
//		for(AssociationMemberModel amm:ammList){
//			String memberId = (amm !=null && amm.getMemberPo()!=null)?amm.getMemberPo().getId():"";
//			this.associationService.truncateManagerInfo(associationId,memberId);
//		}
//
//		//更新社团负责人
//		for(int i=0;i<idArray.length;i++){
//			String managerId=idArray[i];
//			AssociationMemberModel amm = this.getInitManagerInfo(associationId,managerId,proprieterId,request);
//			this.associationService.addAssociationManager(amm);
//		}
//		
//		//更新社团人数
//		if(DataUtil.isNotNull(ammList) && DataUtil.isNotNull(idArray)){
//			int memberOffs = idArray.length-ammList.size();
//			if(memberOffs>=0){
//				this.associationService.synAssociationMemberNums(associationId, (memberOffs), CYLeagueUtil.OPERATOR_FLAG.PLUS.toString());
//			}else{
//				this.associationService.synAssociationMemberNums(associationId, (memberOffs), CYLeagueUtil.OPERATOR_FLAG.MINUS.toString());
//			}
//		}
//	}
//
//	/**
//	 * 获取页面获取的负责人职务信息
//	 * @param index			负责人id集合当前索引
//	 * @param idArray		负责人id集合
//	 * @param leaguePositionArray	负责人职务集合
//	 * @return 当前负责人职务
//	 */
//	private Dic getLeaguePosition(int index,String[] idArray, String[] leaguePositionArray) {
//		Dic leaguePostionDic = new Dic();
//		if(idArray.length==leaguePositionArray.length){
//			String leaguePosition = leaguePositionArray[index];
//			if(DataUtil.isNotNull(leaguePosition)){
//				leaguePostionDic.setId(leaguePosition);
//			}
//		}
//		return leaguePostionDic;
//	}
//
//	/**
//	 * 是否删除社团负责人
//	 * @param managerIds		选择的负责人id
//	 * @param amm					社团成员对象
//	 * @return [true/false]
//	 */
//	private boolean checkManagerDelete(String managerIds,AssociationMemberModel amm) {
//		String managerId = (amm !=null && amm.getMemberPo()!=null)?amm.getMemberPo().getId():DateUtil.getCurTime();
//		return managerIds.indexOf(managerId)==-1;
//	}
//
//	/**
//	 * 是否新增社团负责人
//	 * @param managerId		社团负责人
//	 * @param ammList			负责人列表
//	 * @return[true/false]
//	 */
//	private boolean checkManagerAdd(String managerId,List<AssociationMemberModel> ammList) {
//		for(AssociationMemberModel amm:ammList){
//			String memberId = (amm !=null && amm.getMemberPo()!=null)?amm.getMemberPo().getId():"";
//			if(memberId.equals(managerId)){
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * 校验社团是否设置社长
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param associationId		社团主键
//	 * @param managerIds			负责人集合
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/checkManager"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String checkManager(ModelMap model,HttpServletRequest request,String associationId,String managerIds){
//		String returnValue="{\"flag\":\"success\"}";
//		try {
//			//获取社长实体类
//			AssociationMemberModel proprieter = this.associationService.getAssociationProprieter(associationId);
//			String propreterId = (DataUtil.isNotNull(proprieter)&&
//					DataUtil.isNotNull(proprieter.getMemberPo()))?proprieter.getMemberPo().getId():"";
//			//未设置社长
//			boolean flag = (DataUtil.isNotNull(managerIds)  && DataUtil.isNotNull(propreterId))?true:false;
//			if(DataUtil.isNull(managerIds) ||  
//				 flag && (managerIds.indexOf(propreterId)==-1) || 
//				 (DataUtil.isNotNull(managerIds)&&DataUtil.isNull(propreterId))){
//				returnValue="{\"flag\":\"noproprieter\"}";
//			}
//		} catch (Exception e) {
//			returnValue="{\"flag\":\"error\"}";
//			logger.error("社团负责人验证失败："+e.getMessage());
//			e.printStackTrace();
//		}
//		
//		return returnValue;
//	}
//
//	/**
//	 * 封装社团负责人初始值
//	 * @param associationId	社团主键
//	 * @param managerId		负责人id
//	 * @param proprieterId	社长id
//	 */
//	private AssociationMemberModel getInitManagerInfo(String associationId,String managerId,String proprieterId,HttpServletRequest request) {
//		AssociationMemberModel amm = new AssociationMemberModel();
//		AssociationBaseinfoModel associationPo = this.associationService.getAssociationInfo(associationId);
//		if(DataUtil.isNotNull(associationId)){
//			associationPo.setId(associationId);
//		}
//		//社团对象
//		amm.setAssociationPo(associationPo);
//		//学生对象
//		StudentInfoModel sim = this.stuService.queryStudentById(managerId);
//		amm.setMemberPo(sim);
//		//是否负责人
//		amm.setIsManager(this.dicUtil.getDicInfo("Y&N", "Y"));
//		//社团职务
//		String str=request.getParameter(managerId);
//		Dic dic=this.dicService.getDic(str);
//		amm.setLeaguePosition(dic);
//		/*if(DataUtil.isNotNull(proprieterId) && proprieterId.equals(managerId)){
//			amm.setLeaguePosition(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER);
//		}else{
//			amm.setLeaguePosition(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER_DEPUTY);
//		}*/
//		//默认社团负责人是社团成员，不需要审批
//		amm.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
//		//加入社团时间
//		amm.setJoinTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//		//创建时间
//		amm.setCreateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//		//修改时间
//		amm.setUpdateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//		//删除状态
//		amm.setDeleteStatus(this.dicUtil.getStatusNormal());
//		return amm;
//	}
//	
//	/**
//	 * 异步封装指导老师列表
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param response				当前会话
//	 */
//	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/nsm/asynPackageAdvisorList"})
//	public String asynPackageAdvisorList(ModelMap model,HttpServletRequest request,
//				  String associationId,String teacherIds,String applyTypeCode){
//		Page page = new Page();
//		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
//		int pageSize_ = AssociationConstants.DEFALT_PAGE_SIZE;
//		page.setPageSize(pageSize_);
//		int startIndex = (pageNo-1)*AssociationConstants.DEFALT_PAGE_SIZE;
//		page.setStart(startIndex);
//		
//		String [] IdArray = teacherIds.split(",");
//		List<AssociationAdvisorModel> aamList = new ArrayList<AssociationAdvisorModel>();
//		for(int index=0;index<IdArray.length;index++){
//				if(index>=startIndex && index<(startIndex+pageSize_)){
//					String teacherId = IdArray[index];
//					if(DataUtil.isNotNull(teacherId)){
//						AssociationAdvisorModel aam_ = this.getInitAdvisorInfo(associationId, teacherId);
//						aamList.add( aam_);
//					}
//				}
//		}
//		page.setResult(aamList);
//		page.setTotalCount(IdArray.length);
//		
//		model.addAttribute("curUserId", this.sessionUtil.getCurrentUserId());
//		model.addAttribute("applyTypeCode", applyTypeCode);
//		model.addAttribute("teacherPage", page);
//		model.addAttribute("hiddenTeacherIds", teacherIds);
//		return "/association/advisorSelectedList";
//	}
//	
//	/**
//	 * 异步加载教师列表1
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param response				当前会话
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/opt-query/advisorQueryOnPage"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String advisorQueryOnPage(ModelMap model,HttpServletRequest request,HttpServletResponse response,String teacherIds){
//		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
//		model.addAttribute("pageNo", pageNo);
//		return "{\"success\":\"success\",\"listType\":\""+AssociationConstants.LIST_STYPE.LIST_ADVISOR+"\",\"pageNo\":\""+pageNo+"\"}";
//	}
//	
//	/**
//	 * 异步加载教师列表2
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param response				当前会话
//	 */
//	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/nsm/asynLoadAdvisorList"})
//	public String asynLoadAdvisorList(ModelMap model,HttpServletRequest request,
//				  String objectId,String teacherIds,String applyTypeCode,String returnFlag){
//		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
//		String teacherIdsConditon = CYLeagueUtil.getCondition(teacherIds);
//		Page page = this.associationService.pageQueryAssociationAdvisor(objectId, teacherIdsConditon,pageNo, AssociationConstants.DEFALT_PAGE_SIZE);
//		//是否当前社团的负责人
//		boolean isCurAssociationManager = 
//				this.associationService.getAssociationMemberByUserId(objectId,this.sessionUtil.getCurrentUserId());
//		model.addAttribute("isCurManager", isCurAssociationManager?"true":"false");
//		model.addAttribute("applyTypeCode", applyTypeCode);
//		model.addAttribute("teacherPage", page);
//		model.addAttribute("hiddenTeacherIds", teacherIds);
//		if(DataUtil.isNotNull(returnFlag) && returnFlag.equals("view")){
//			return "/association/advisorViewList";
//		}else{
//			return "/association/advisorSelectedList";
//		}
//	}
//	
//	/**
//	 * 异步封装社团负责人列表
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param response				当前会话
//	 */
//	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/nsm/asynPackageManagerList"})
//	public String asynPackageManagerList(ModelMap model,HttpServletRequest request,
//			       String associationId,String managerIds,String applyTypeCode,String proprieterId,
//			       String operateStatus,String modifyItemInfo, String dicId){
//		Page page = new Page();
//		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
//		int pageSize_ = AssociationConstants.DEFALT_PAGE_SIZE;
//		page.setPageSize(pageSize_);
//		int startIndex = (pageNo-1)*pageSize_;
//		page.setStart(startIndex);
//		String [] IdArray = managerIds.split(",");
//		String[] str=request.getParameterValues("leaguePosition");
//		List<AssociationMemberModel> ammList = new ArrayList<AssociationMemberModel>();
//		for(int index=0;index<IdArray.length;index++){
//				if(index>=startIndex && index<(startIndex+pageSize_)){
//					String managerId = IdArray[index];
//					AssociationMemberModel amm = this.getNewInitManagerInfo(associationId,managerId,proprieterId,dicId);
//					ammList.add(amm);
//				}
//		}
//		page.setTotalCount(IdArray.length);
//		page.setResult(ammList);
//		model.addAttribute("stuPage", page);
//		model.addAttribute("ammList", ammList);
//		model.addAttribute("hasStuData", this.isStuHasData(page));
//	    model.addAttribute("proprieterId",this.getPropieterByAssociation(associationId));
//	    model.addAttribute("proprieterRegister",this.getRegisterPropieterByAssociation(associationId));
//		model.addAttribute("operateStatus",operateStatus);
//		model.addAttribute("modifyItemInfo",modifyItemInfo);
//		model.addAttribute("hiddenManagerIds", managerIds);
//		model.addAttribute("applyTypeCode", applyTypeCode);
//		model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//		
//		return "/association/managerSelectedList";
//	}
//	
//	/**
//	 * 根据社团主键获取社团负责人【社长】
//	 * @param associationId		社团主键
//	 * @return 社团负责人
//	 */
//	private String getPropieterByAssociation(String associationId) {
//		AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
//		if(DataUtil.isNotNull(abm) && abm.getProprieter()!=null){
//			return (DataUtil.isNotNull(abm.getProprieter().getId()))?
//							abm.getProprieter().getId():this.sessionUtil.getCurrentUserId();
//		}
//		return this.sessionUtil.getCurrentUserId();
//	}
//	
//	/**
//	 * 根据社团主键获取原社团负责人【原社长】
//	 * @param associationId		社团主键
//	 * @return 社团原负责人
//	 */
//	private String getRegisterPropieterByAssociation(String associationId) {
//		AssociationBaseinfoModel abm = this.associationService.getAssociationInfo(associationId);
//		if(DataUtil.isNotNull(abm) && abm.getProprieterRegister()!=null){
//			return (DataUtil.isNotNull(abm.getProprieterRegister().getId()))?
//					abm.getProprieterRegister().getId():"";
//		}
//		return "";
//	}
//
//	/**
//	 * 异步加载社团负责人列表1
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param response				当前会话
//	 */
//	@RequestMapping(value={AssociationConstants.NAMESPACE_APPLY+"/managerQueryOnPage"},produces={"text/plain;charset=UTF-8"})
//	@ResponseBody
//	public String managerQueryOnPage(ModelMap model,HttpServletRequest request,HttpServletResponse response,String managerIds){
//		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
//		model.addAttribute("pageNo", pageNo);
//		return "{\"success\":\"success\",\"listType\":\""+AssociationConstants.LIST_STYPE.LIST_MANAGER+"\",\"pageNo\":\""+pageNo+"\"}";
//	}
//	
//	/**
//	 * 异步加载社团负责人列表2
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 * @param response				当前会话
//	 */
//	@RequestMapping({AssociationConstants.NAMESPACE_APPLY+"/nsm/asynLoadManagerList"})
//	public String asynLoadManagerList(ModelMap model,HttpServletRequest request,
//			       String objectId,String managerIds,String applyTypeCode,String returnFlag){
//		//int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
//		AssociationBaseinfoModel abm = new AssociationBaseinfoModel();
//		abm.setId(objectId);
//		//Page page = this.associationService.pageQueryAssociationMember(abm,pageNo, AssociationConstants.DEFALT_PAGE_SIZE);
//		
//		String proprieterId = null;
//		AssociationBaseinfoModel associationInfo = associationService.getAssociationInfo(objectId);
//		if(associationInfo!=null && associationInfo.getProprieter()!=null)
//		{
//			proprieterId = associationInfo.getProprieter().getId();
//		}
//		Page page = new Page();
//		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
//		int pageSize_ = AssociationConstants.DEFALT_PAGE_SIZE;
//		page.setPageSize(pageSize_);
//		int startIndex = (pageNo-1)*pageSize_;
//		page.setStart(startIndex);
//		String [] IdArray = managerIds.split(",");
//		List<AssociationMemberModel> ammList = new ArrayList<AssociationMemberModel>();
//		for(int index=0;index<IdArray.length;index++){
//				if(index>=startIndex && index<(startIndex+pageSize_)){
//					String managerId = IdArray[index];
//					AssociationMemberModel amm = this.getInitManagerInfo(objectId,managerId,proprieterId,request);
//					ammList.add(amm);
//				}
//		}
//		page.setTotalCount(IdArray.length);
//		page.setResult(ammList);
//		model.addAttribute("stuPage", page);
//		
//		
//		
//		
//		model.addAttribute("hasStuData", this.isStuHasData(page));
//		model.addAttribute("hiddenManagerIds", managerIds);
//		model.addAttribute("applyTypeCode", applyTypeCode);
//		model.addAttribute("associationManagerList", AssociationConstants.associationManagerList);
//		if(DataUtil.isNotNull(returnFlag) && returnFlag.equals("view")){
//			return "/association/managerViewList";
//		}else{
//			return "/association/managerSelectedList";
//		}
//	}
//
//	/**
//	 * 异步分页展现
//	 * @param model					页面数据加载器
//	 * @param request					页面请求
//	 */
//   @RequestMapping(value={AssociationConstants.NAMESPACE+"/opt-query/ajaxGetAssociationApplyList"},produces={"text/plain;charset=UTF-8"})
//   @ResponseBody
//	public String ajaxGetAssociationApplyList(ModelMap model,HttpServletRequest request){
//		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
//		
//		return "{\"success\":\"success\",\"pageNo\":\""+pageNo+"\"}";
//	}
//   
//   /**
// 	 * 隐藏ids
// 	 */
// 	private String getNewManagerIds(AssociationApplyModel newAam, String applyType,String type) {
// 		String associationId = (newAam.getAssociationPo()!=null)?newAam.getAssociationPo().getId():"";
//		if(isManagerModify(associationId, applyType,"")==false){
//			if(AssociationConstants.APPLY_STATUS.MODIFY.toString().equals(applyType)){
//				List<AssociationTempUserModel> atumList = 
//						this.associationService.getTempUserInfo(associationId, type);
//				StringBuffer managerIds=new StringBuffer("\"");
//		 		for(int i=0;i<atumList.size();i++){
//		 			if(i==atumList.size()-1){
//		 				managerIds.append(atumList.get(i).getUserId());
//		 			}else{
//		 				managerIds.append(atumList.get(i).getUserId()).append(",");
//		 			}
//		 		}
//		 		managerIds.append("\"");
//		 		return managerIds.toString();
//			}
//		}
//		return "";
// 	}
// 	
// 	/** jiang   从写
//	 * 封装社团负责人初始值
//	 * @param associationId	社团主键
//	 * @param managerId		负责人id
//	 * @param proprieterId	社长id
//	 */
//	private AssociationMemberModel getNewInitManagerInfo(String associationId, String managerId, String proprieterId, String dicId) {
//		AssociationMemberModel amm = new AssociationMemberModel();
//		AssociationBaseinfoModel associationPo = this.associationService.getAssociationInfo(associationId);
//		if(DataUtil.isNotNull(associationId)){
//			associationPo.setId(associationId);
//		}
//		//社团对象
//		amm.setAssociationPo(associationPo);
//		//学生对象
//		StudentInfoModel sim = this.stuService.queryStudentById(managerId);
//		amm.setMemberPo(sim);
//		//是否负责人
//		amm.setIsManager(this.dicUtil.getDicInfo("Y&N", "Y"));
//		//社团职务
//		if(StringUtils.isNotEmpty(dicId)){
//			if(DataUtil.isNotNull(proprieterId) && proprieterId.equals(managerId)){
//				amm.setLeaguePosition(this.dicService.getDic(dicId));
//			}
//		}else{//默认社员
//			if(DataUtil.isNotNull(proprieterId) && proprieterId.equals(managerId)){
//				amm.setLeaguePosition(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER);
//			}else{
//				amm.setLeaguePosition(AssociationConstants.ASSOCIATION_MEMBER);
//			}
//		}
//		/*if(DataUtil.isNotNull(proprieterId) && proprieterId.equals(managerId)){
//			amm.setLeaguePosition(AssociationConstants.ASSOCIATION_MANAGER_PROPRIETER);
//		}else{
//			
//		}*/
//		//默认社团负责人是社团成员，不需要审批
//		amm.setMemberStatus(CYLeagueUtil.APPROVE_PASS);
//		//加入社团时间
//		amm.setJoinTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//		//创建时间
//		amm.setCreateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//		//修改时间
//		amm.setUpdateTime(AmsDateUtil.toTime(DateUtil.getCurTime()));
//		//删除状态
//		amm.setDeleteStatus(this.dicUtil.getStatusNormal());
//		return amm;
//	}
//}
