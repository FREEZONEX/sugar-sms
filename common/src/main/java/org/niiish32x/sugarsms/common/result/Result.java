package org.niiish32x.sugarsms.common.result;

/**
 * Result
 *
 * @author shenghao ni
 * @date 2024.12.09 10:26
 */

import lombok.Data;

import java.io.Serializable;

/**
 * 全局统一返回结果类
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -5202065562748397103L;

    private static final int SUCCESS_CODE = 200;

    private static final int DEFAULT_ERROR_CODE = 500;

    private static final String SUCCESS = "SUCCESS";

    private static final String FAILED = "FAILED";

    private static final Result<Object> EMPTY = Result.success(null);

    /**
     * 结果码值
     */
    private int code;

    /**
     * 结果数据
     */
    private T data;

    /**
     * 结果消息
     */
    private String message;

    /**
     * debug信息，便于排查问题
     */
    private String debug;

    public Result() {
    }

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }

    public static <T> Result<T> empty() {
        return (Result<T>) EMPTY;
    }

    public static Result<String> success() {
        return success("OK");
    }

    public static <T> Result<T> success(T t) {
        return Result.newBuilder()
                .code(SUCCESS_CODE)
                .message(SUCCESS)
                .data(t)
                .build();
    }

    public static <T> Result<T> error(Result r) {
        return error(r.getCode(), r.getMessage());
    }

    public static <T> Result<T> error(ResultCode code, String message) {
        return Result.newBuilder()
                .code(code.getCode())
                .message(message)
                .build();
    }

    public static <T> Result<T> error(int code, String message) {
        return Result.newBuilder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> Result<T> error(String message) {
        return Result.newBuilder()
                .code(DEFAULT_ERROR_CODE)
                .message(message)
                .build();
    }
    public static <T> Result<T> error(ResultCode code, String msgPattern, Object... args) {
        return error(code.getCode(), String.format(msgPattern, args));
    }

    public static <T> Result<T> error(int code, String msgPattern, Object... args) {
        return error(code, String.format(msgPattern, args));
    }

    public static <T> Result<T> error(String msgPattern, Object... args) {
        return error(String.format(msgPattern, args));
    }


    private Result(Builder<T> builder) {
        setCode(builder.code);
        setData(builder.data);
        setMessage(builder.message);
        setDebug(builder.debug);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder<T> {
        private int code;
        private T data;
        private String message;
        private String debug;

        private Builder() {
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder data(T data) {
            this.data = data;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder debug(String debug) {
            this.debug = debug;
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }
}

