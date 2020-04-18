package com.gm.authorization.server.custom.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.gm.authorization.server.custom.domain.GlobalConstant;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 自定义OAuth2异常信息序列化
 * 
 * @author Administrator
 *
 */
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

	private static final long serialVersionUID = 1L;

	public CustomOauthExceptionSerializer() {
		super(CustomOauthException.class);
	}

	@Override
	public void serialize(CustomOauthException value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();

		gen.writeStartObject();
		gen.writeNumberField("status", GlobalConstant.ERROR);
		gen.writeStringField("error", String.valueOf(value.getHttpErrorCode()));
		gen.writeStringField("message", value.getMessage());
		gen.writeStringField("path", request.getServletPath());
		gen.writeNumberField("timestamp", System.currentTimeMillis());
		if (value.getAdditionalInformation() != null) {
			for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
				String key = entry.getKey();
				String add = entry.getValue();
				gen.writeStringField(key, add);
			}
		}
		gen.writeEndObject();
	}
}