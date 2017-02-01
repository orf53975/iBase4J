/**
 * 
 */
package org.ibase4j.web.sys;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.ibase4j.core.base.BaseController;
import org.ibase4j.core.support.Assert;
import org.ibase4j.core.support.HttpCode;
import org.ibase4j.core.util.UploadUtil;
import org.ibase4j.core.util.WebUtil;
import org.ibase4j.model.sys.SysUser;
import org.ibase4j.model.sys.ext.SysMenuBean;
import org.ibase4j.service.sys.SysAuthorizeService;
import org.ibase4j.service.sys.SysUserService;

import com.baomidou.mybatisplus.plugins.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 用户管理控制器
 * 
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:12:12
 */
@RestController
@Api(value = "用户管理", description = "用户管理")
@RequestMapping(value = "/user")
public class SysUserController extends BaseController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysAuthorizeService authorizeService;

	@ApiOperation(value = "修改用户信息")
	@RequiresPermissions("sys.user.update")
	@RequestMapping(method = RequestMethod.POST)
	public Object update(ModelMap modelMap, @RequestBody SysUser sysUser) {
		Assert.isNotBlank(sysUser.getAccount(), "ACCOUNT");
		Assert.length(sysUser.getAccount(), 3, 15, "ACCOUNT");
		sysUserService.updateUserInfo(sysUser);
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "修改个人信息")
	@RequiresPermissions("sys.user.update")
	@RequestMapping(value = "/update/person", method = RequestMethod.POST)
	public Object updatePerson(ModelMap modelMap, @RequestBody SysUser sysUser) {
		sysUser.setId(WebUtil.getCurrentUser());
		Assert.isNotBlank(sysUser.getAccount(), "ACCOUNT");
		Assert.length(sysUser.getAccount(), 3, 15, "ACCOUNT");
		sysUserService.updateUserInfo(sysUser);
		return setSuccessModelMap(modelMap);
	}

	@ApiOperation(value = "修改用户头像")
	@RequiresPermissions("sys.user.update")
	@RequestMapping(value = "/update/avatar", method = RequestMethod.POST)
	public Object updateAvatar(HttpServletRequest request, ModelMap modelMap) {
		List<String> fileNames = UploadUtil.uploadImage(request);
		if (fileNames.size() > 0) {
			SysUser sysUser = new SysUser();
			sysUser.setId(WebUtil.getCurrentUser());
			String filePath = UploadUtil.getUploadDir(request) + fileNames.get(0);
			// String avatar = UploadUtil.remove2DFS("sysUser", "user" +
			// sysUser.getId(), filePath).getRemotePath();
			// String avatar = UploadUtil.remove2Sftp(filePath, "user" +
			// sysUser.getId());
			sysUser.setAvatar(filePath);
			sysUserService.updateUserInfo(sysUser);
			return setSuccessModelMap(modelMap);
		} else {
			setModelMap(modelMap, HttpCode.BAD_REQUEST);
			modelMap.put("msg", "请选择要上传的文件！");
			return modelMap;
		}
	}

	// 修改密码
	@ApiOperation(value = "修改密码")
	@RequiresPermissions("sys.user.update")
	@RequestMapping(value = "/update/password", method = RequestMethod.POST)
	public Object updatePassword(ModelMap modelMap, @RequestBody SysUser sysUser) {
		sysUserService.updatePassword(WebUtil.getCurrentUser(), sysUser.getOldPassword(), sysUser.getPassword());
		return setSuccessModelMap(modelMap);
	}

	// 查询用户
	@ApiOperation(value = "查询用户")
	@RequiresPermissions("sys.user.read")
	@RequestMapping(value = "/read/list", method = RequestMethod.PUT)
	public Object get(ModelMap modelMap, @RequestBody Map<String, Object> sysUser) {
		Page<?> list = sysUserService.queryBeans(sysUser);
		return setSuccessModelMap(modelMap, list);
	}

	// 用户详细信息
	@ApiOperation(value = "用户详细信息")
	@RequiresPermissions("sys.user.read")
	@RequestMapping(value = "/read/detail", method = RequestMethod.PUT)
	public Object detail(ModelMap modelMap, @RequestBody SysUser sysUser) {
		sysUser = sysUserService.queryById(sysUser.getId());
		if (sysUser != null) {
			sysUser.setPassword(null);
		}
		return setSuccessModelMap(modelMap, sysUser);
	}

	// 用户详细信息
	@ApiOperation(value = "删除用户")
	@RequiresPermissions("sys.user.delete")
	@RequestMapping(method = RequestMethod.DELETE)
	public Object delete(ModelMap modelMap, @RequestBody SysUser sysUser) {
		sysUserService.delete(sysUser.getId());
		return setSuccessModelMap(modelMap);
	}

	// 当前用户
	@ApiOperation(value = "当前用户信息")
	@RequestMapping(value = "/read/promission", method = RequestMethod.GET)
	public Object promission(ModelMap modelMap) {
		Long id = getCurrUser();
		SysUser sysUser = sysUserService.queryById(id);
		if (sysUser != null) {
			sysUser.setPassword(null);
		}
		List<SysMenuBean> menus = authorizeService.queryAuthorizeByUserId(id);
		modelMap.put("user", sysUser);
		modelMap.put("menus", menus);
		return setSuccessModelMap(modelMap);
	}

	// 当前用户
	@ApiOperation(value = "当前用户信息")
	@RequestMapping(value = "/read/current", method = RequestMethod.GET)
	public Object current(ModelMap modelMap) {
		Long id = getCurrUser();
		SysUser sysUser = sysUserService.queryById(id);
		if (sysUser != null) {
			sysUser.setPassword(null);
		}
		return setSuccessModelMap(modelMap, sysUser);
	}
}
