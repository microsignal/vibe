package io.microvibe.booster.system.entity;

import io.microvibe.booster.core.base.entity.BaseAssignableAutoIncEntity;
import io.microvibe.booster.core.base.entity.BaseAssignableAutoUuidHexEntity;
import io.microvibe.booster.core.log.Log;
import io.microvibe.booster.system.enums.LogType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;


/**
 * 系统日志信息
 *
 * @author Q
 * @version 1.0
 * @since Jun 22, 2018
 */
@Entity
@Table(name = "sys_log")
@Getter
@Setter
@EqualsAndHashCode
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AttributeOverrides(
	@AttributeOverride(name = "id", column = @Column(name = "id"))
)
public class SysLog extends BaseAssignableAutoIncEntity {

	private static final long serialVersionUID = 1L;

	// region columns

	@Column(name = "user_id")
	private Long userId;//操作用户id

	@Column(name = "user_name")
	private String userName;//操作用户名

	@Column(name = "request_ip")
	private String requestIp;//登录ip地址
	@Column(name = "request_uri")
	private String requestUri;
	@Column(name = "request_method")
	private String requestMethod;

	@Column(name = "log_type")
	@Enumerated(EnumType.STRING)
	private LogType logType = LogType.SYSTEM;//日志类型

	@Column(name = "log_level")
	@Enumerated(EnumType.STRING)
	private Log.Level logLevel;//日志级别

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_time")
	private java.util.Date logTime;//日志时间

	@Column(name = "log_module")
	private String logModule;//功能模块

	@Column(name = "log_content")
	private String logContent;//日志内容

	@Column(name = "log_stacktrace")
	private String logStacktrace;//日志堆栈

	@Column(name = "class_name")
	private String className;//执行类名

	@Column(name = "method_name")
	private String methodName;//执行方法名

	@Column(name = "method_args")
	private String methodArgs;//方法入参

	@Column(name = "method_result")
	private String methodResult;//方法返回值

	// endregion columns

}

