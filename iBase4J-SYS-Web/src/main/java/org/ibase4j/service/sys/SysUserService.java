package org.ibase4j.service.sys;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ibase4j.core.base.BaseService;
import org.ibase4j.core.support.Assert;
import org.ibase4j.core.support.login.LoginHelper;
import org.ibase4j.core.support.login.ThirdPartyUser;
import org.ibase4j.core.util.WebUtil;
import org.ibase4j.model.sys.SysUser;
import org.ibase4j.model.sys.ext.SysUserBean;
import org.ibase4j.provider.sys.ISysUserProvider;

import com.baomidou.mybatisplus.plugins.Page;

/**
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:47:21
 */
@Service
public class SysUserService extends BaseService<ISysUserProvider, SysUser> {
	@Autowired
	public void setProvider(ISysUserProvider provider) {
		this.provider = provider;
	}

	/** 修改用户信息 */
	public void updateUserInfo(SysUser sysUser) {
		Assert.notNull(sysUser.getId(), "USER_ID");
		SysUser user = this.queryById(sysUser.getId());
		Assert.notNull(user, "USER", sysUser.getId());
		if (StringUtils.isBlank(sysUser.getPassword())) {
			sysUser.setPassword(user.getPassword());
		}
		if (StringUtils.isEmpty(sysUser.getAvatar())) {
			sysUser.setAvatar(user.getAvatar());
		}
		sysUser.setUpdateBy(WebUtil.getCurrentUser());
		provider.update(sysUser);
	}

	public Page<SysUserBean> queryBeans(Map<String, Object> params) {
		return provider.queryBeans(params);
	}

	/** 修改密码 */
	public void updatePassword(Long id, String oldPassword, String password) {
		Assert.notNull(id, "USER_ID");
		Assert.isNotBlank(oldPassword, "OLDPASSWORD");
		Assert.isNotBlank(password, "PASSWORD");
		SysUser sysUser = provider.queryById(id);
		Assert.notNull(sysUser, "USER", id);
		Long userId = WebUtil.getCurrentUser();
		if (!id.equals(userId)) {
			SysUser user = provider.queryById(userId);
			if (user.getUserType() == 1) {
				throw new UnauthorizedException("您没有权限修改用户密码.");
			}
		} else {
			if (!sysUser.getPassword().equals(encryptPassword(oldPassword))) {
				throw new UnauthorizedException("原密码错误.");
			}
		}
		sysUser.setPassword(encryptPassword(password));
		sysUser.setUpdateBy(WebUtil.getCurrentUser());
		provider.update(sysUser);
	}

	public String encryptPassword(String password) {
		return provider.encryptPassword(password);
	}

	public void thirdPartyLogin(ThirdPartyUser thirdUser) {
		SysUser sysUser = null;
		// 查询是否已经绑定过
		Long userId = provider.queryUserIdByThirdParty(thirdUser.getOpenid(), thirdUser.getProvider());
		if (userId == null) {
			sysUser = insertThirdPartyUser(thirdUser);
		} else {
			sysUser = queryById(userId);
		}
		LoginHelper.login(sysUser.getAccount(), sysUser.getPassword());
	}

	public SysUser insertThirdPartyUser(ThirdPartyUser thirdUser) {
		return provider.insertThirdPartyUser(thirdUser);
	}
}
