package com.gm.authorization.server.custom.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private int status;// 请求状态是否成功
	private int errorCode;// 错误类型
	private String errorMessage;// 详细的信息
	private String path;// 请求对象
	private Long timestamp;// 时间戳
	private T data;// 返回对象

	public ResponseResult() {
		this.status = GlobalConstant.SUCCESS;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getTimestamp() {
		if (timestamp == null) {
			timestamp = System.currentTimeMillis();
		}
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
