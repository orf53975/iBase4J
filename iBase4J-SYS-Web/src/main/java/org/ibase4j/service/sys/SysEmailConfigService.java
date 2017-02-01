package org.ibase4j.service.sys;

import org.springframework.stereotype.Service;
import org.ibase4j.core.base.BaseService;
import org.ibase4j.core.support.dubbo.spring.annotation.DubboReference;
import org.ibase4j.model.sys.SysEmailConfig;
import org.ibase4j.provider.sys.ISysEmailConfigProvider;

/**
 * 邮件配置管理
 * @author ShenHuaJie
 */
@Service
public class SysEmailConfigService extends BaseService<ISysEmailConfigProvider, SysEmailConfig> {
	@DubboReference
	public void setProvider(ISysEmailConfigProvider provider) {
		this.provider = provider;
	}
}
