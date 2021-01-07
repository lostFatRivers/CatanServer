package com.jokerbee.support;

/**
 * TODO description
 *
 * @author: Joker
 * @date: Created in 2020/10/31 15:47
 * @version: 1.0
 */
public interface MessageCode {

    int SC_ERROR = 101;

    int CS_ACCOUNT_LOGIN = 1001;
    int SC_ACCOUNT_LOGIN = 2001;

    int CS_SYNC_DATA = 1002;







    // ***************** 服务器之间消息 ******************
    // A: Account, G: Gateway, P: Player

    int AP_ACCOUNT_DISCONNECT = 90001;

    int AP_ACCOUNT_DESTROY = 90002;
}
