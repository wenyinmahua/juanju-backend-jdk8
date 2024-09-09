package com.mahua.juanju.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mahua.juanju.Exception.BusinessException;
import com.mahua.juanju.common.BaseResponse;
import com.mahua.juanju.common.ErrorCode;
import com.mahua.juanju.common.ResultUtils;
import com.mahua.juanju.model.domain.User;
import com.mahua.juanju.model.domain.Team;
import com.mahua.juanju.model.domain.UserTeam;
import com.mahua.juanju.model.dto.TeamQuery;
import com.mahua.juanju.model.request.TeamJoinRequest;
import com.mahua.juanju.model.request.TeamRequest;
import com.mahua.juanju.model.request.TeamUpdateRequest;
import com.mahua.juanju.model.vo.UserTeamVO;
import com.mahua.juanju.service.TeamService;
import com.mahua.juanju.service.UserService;
import com.mahua.juanju.service.UserTeamService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

	@Resource
	private UserService userService;

	@Resource
	private TeamService teamService;

	@Resource
	private UserTeamService userTeamService;

//	@Operation(summary = "创建队伍")
	@PostMapping("/add")
	public BaseResponse<Long> createTeam (@RequestBody TeamRequest teamRequest, HttpServletRequest request){
		if(teamRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Team team = new Team();
		BeanUtils.copyProperties(teamRequest,team);
		User loginUser = userService.getLoginUser(request);
		long teamId = teamService.addTeam(team,loginUser);
		return ResultUtils.success(teamId);
	}

//	@Operation(summary = "更新队伍")
	@PutMapping("/update")
	public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
		User loginUser = userService.getLoginUser(request);
		if (teamUpdateRequest == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
		if (!result){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
		}
		return ResultUtils.success(true);
	}

	/**
	 * 获得队伍详细描述
	 * @param id
	 * @return
	 */
//	@Operation(summary = "获得队伍")
	@GetMapping("/get")
	public BaseResponse<Team> getTeamListById(long id){
		if (id <= 0 ){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Team team = teamService.getById(id);
		if (team == null){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"获取队伍列表错误");
		}
		return ResultUtils.success(team);
	}

//	@Operation(summary = "分页查询队伍")
	@GetMapping("/list")
	public BaseResponse<Page<UserTeamVO>> teamList(TeamQuery teamQuery, HttpServletRequest request){
		if (teamQuery == null ){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		if (request == null){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);

		}
		boolean isAdmin = userService.isAdmin(request);
		Page<UserTeamVO> teamList = teamService.listTeams(teamQuery,isAdmin);
		if (teamList == null){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"获取队伍列表错误");
		}
		if (teamList.getRecords().size() == 0){
			return ResultUtils.success(null,"暂无队伍列表");
		}
		List<Long> teamIdList = teamList.getRecords().stream().map(UserTeamVO::getId).collect(Collectors.toList());
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper();
		try{
			User loginUser = userService.getLoginUser(request);
			queryWrapper.eq("user_id",loginUser.getId());
			queryWrapper.in("team_id",teamIdList);
			List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
			// 已加入队伍 id 集合
			Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
			teamList.getRecords().forEach(team->{
				boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
				team.setHasJoin(hasJoin);
			});
		}catch (Exception e){
			log.error("查询队伍失败");
		}

		return ResultUtils.success(teamList);
	}

//	@Operation(summary = "获取我创建的队伍")
	@GetMapping("/list/my/create")
	public BaseResponse<Page<UserTeamVO>> myCreateTeamList(TeamQuery teamQuery, HttpServletRequest request){
		if (teamQuery == null ){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		teamQuery.setUserId(loginUser.getId());
		return getPageBaseResponse(teamQuery);
	}

//	@Operation(summary = "获取我加入的队伍")
	@GetMapping("/list/my/join")
	public BaseResponse<Page<UserTeamVO>> myJoinTeamList(TeamQuery teamQuery, HttpServletRequest request){
		if (teamQuery == null ){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id",loginUser.getId());
		List <UserTeam> userTeamList = userTeamService.list(queryWrapper);
		Map<Long,List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
		List<Long> idList = new ArrayList<>(listMap.keySet());
		teamQuery.setIdList(idList);
		return getPageBaseResponse(teamQuery);
	}

	private BaseResponse<Page<UserTeamVO>> getPageBaseResponse(@RequestBody TeamQuery teamQuery) {
		Page<UserTeamVO> teamList = teamService.listTeams(teamQuery,true);
		if (teamList == null){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"获取队伍列表错误");
		}
		for(UserTeamVO userTeamVO : teamList.getRecords()){
			userTeamVO.setHasJoin(true);
		}
		return ResultUtils.success(teamList);
	}

//	@Operation(summary = "加入队伍")
	@PostMapping("/join")
	public BaseResponse<Boolean> joinTeam (@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
		User loginUser = userService.getLoginUser(request);
		boolean result = teamService.joinTeam(teamJoinRequest,loginUser);
		return ResultUtils.success(result);
	}

//	@Operation(summary = "退出队伍")
	@PostMapping("/quit/{id}")
	public BaseResponse quitTeam(@PathVariable("id") long id,HttpServletRequest request){
		teamService.quitTeam(id,request);
		return ResultUtils.success("退出队伍成功");
	}


//	@Operation(summary = "解散队伍")
	@PostMapping("/delete/{id}")
	public BaseResponse<Boolean> deleteTeam(@PathVariable("id") long id,HttpServletRequest request){
		if ( id <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = teamService.deleteTeam(id,request);
		if (!result){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
		}
		return ResultUtils.success(true);
	}

	@GetMapping("/info")
	public BaseResponse<UserTeamVO> getTeamInfoById(@RequestParam("id")Long id,HttpServletRequest request){
		UserTeamVO userTeamVO = teamService.getTeamInfoById(id,request);
		return ResultUtils.success(userTeamVO);
	}




}
