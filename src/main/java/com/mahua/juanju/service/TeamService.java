package com.mahua.juanju.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mahua.juanju.model.domain.User;
import com.mahua.juanju.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mahua.juanju.model.dto.TeamQuery;
import com.mahua.juanju.model.request.TeamJoinRequest;
import com.mahua.juanju.model.request.TeamUpdateRequest;
import com.mahua.juanju.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author mahua
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2024-02-06 17:56:53
*/
public interface TeamService extends IService<Team> {

	long addTeam(Team team, User loginUser);

	Page<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);

	/**
	 * 更新队伍
	 * @param teamUpdateRequest
	 * @param loginUser
	 * @return
	 */
	boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

	/**
	 *
	 * @param teamJoinRequest
	 * @param loginUser
	 * @return
	 */
	boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

	/**
	 * 退出队伍
	 * @param teamId
	 * @param request
	 * @return
	 */
	boolean quitTeam(Long teamId, HttpServletRequest request);

	/**
	 * 解散队伍
	 *
	 * @param id
	 * @param request
	 * @return
	 */
	boolean deleteTeam(long id, HttpServletRequest request);
}
