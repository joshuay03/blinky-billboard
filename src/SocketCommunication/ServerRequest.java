package SocketCommunication;

/**
 * Enum holds all request made to Server from Viewer and Control Panel.
 */
public enum ServerRequest {
    VIEWER_CURRENTLY_SCHEDULED,
    LOGIN,
    LIST_BILLBOARD,
    GET_BILL_INFO_REQ,
    CREATE_BILL_REQ,
    EDIT_BILL_REQ,
    DELETE_BILL_REQ,
    VIEW_SCHEDULED_BILL_REQ,
    scheduleBillboardReq,
    removeScheduledReq,
    listUserReq,
    createUserReq,
    getUserPermReq,
    setUserPermReq,
    setUserPasswordReq,
    deleteUserReq,
    logoutReq
}
