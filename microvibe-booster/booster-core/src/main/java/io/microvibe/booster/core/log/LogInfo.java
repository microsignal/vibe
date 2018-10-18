package io.microvibe.booster.core.log;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;


@Data
public class LogInfo {
	private String uuid = UUID.randomUUID().toString().replace("-", "");
	private Log.Level level = Log.Level.INFO;
	private Log.Level errorLevel = Log.Level.ERROR;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private int pointcut = 0;
	private String module;
	private String content;
	private String requestIp;
	private String requestUri;
	private String requestMethod;
	private MethodSignature signature;
	private String signatureString;
	private Class<?> withinType;
	private String methodName;
	private Object[] methodArgs;
	private String methodArgsString;
	private Class<?> methodReturnType;
	private Object methodReturnValue;
	private String methodReturnValueString;
	private Throwable throwable;
	private String throwableTraceString;
	private Object attachment;

	@Log(content = "yyy")
	public static void main(String[] args) throws NoSuchMethodException {
		Log log = AnnotationUtils.findAnnotation(LogInfo.class.getDeclaredMethod("main", String[].class), Log.class);
		System.out.println(log);
	}

	public boolean hasPointcut(Log.Pointcut pointcut) {
		return (this.pointcut & pointcut.mask()) > 0;
	}

	public void addPointcut(Log.Pointcut... pointcuts) {
		for (Log.Pointcut e : pointcuts) {
			this.pointcut |= e.mask();
		}
	}

	public void setSignature(MethodSignature signature) {
		this.signature = signature;
		this.signatureString = signature.toString();
	}

	public void setMethodArgs(Object[] methodArgs) {
		this.methodArgs = methodArgs;
		this.methodArgsString = _toMethodArgsString();
	}

	public String getSignatureString() {
		return signatureString != null ? signatureString : (signatureString = signature.toString());
	}


	public String getMethodArgsString() {
		return methodArgsString != null ? methodArgsString : (methodArgsString = _toMethodArgsString());
	}

	public String _toMethodArgsString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < methodArgs.length; i++) {
			sb.append("Arg-").append(i).append(": ").append((methodArgs[i]));
			sb.append("\n");
		}
		return sb.toString();
	}

	public String getMethodReturnValueString() {
		if (methodReturnValue != null) {
			return methodReturnValueString != null ? methodReturnValueString : (methodReturnValueString = _toMethodReturnValueString());
		} else {
			return null;
		}
	}

	private String _toMethodReturnValueString() {
//		return JSON.toJSONString(methodReturnValue);
		return String.valueOf(methodReturnValue);
	}

	public String getThrowableTraceString() {
		if (throwable != null) {
			return throwableTraceString != null ? throwableTraceString : (throwableTraceString = _toThrowableTraceString());
		} else {
			return null;
		}
	}

	private String _toThrowableTraceString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);
		return stringWriter.toString();
	}
}
