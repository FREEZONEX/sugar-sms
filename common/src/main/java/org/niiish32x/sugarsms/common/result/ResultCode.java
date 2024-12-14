package org.niiish32x.sugarsms.common.result;

/**
 * ResultCodeEnum
 *
 * @author shenghao ni
 * @date 2024.12.09 10:25
 */


/**
 * 统一返回结果状态信息类
 */
public enum ResultCode {

    Suc(200),

    Parameter_Err(400),

    Not_Exist(404),

    Un_Support(406),

    Internal_Err(500),

    K8s_Clt_Err(510),
    ;

    ResultCode(final int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }
}



