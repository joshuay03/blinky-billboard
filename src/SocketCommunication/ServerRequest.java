package SocketCommunication;

/**
 * Enum holds all request made to Server from Viewer and Control Panel.
 */
public enum ServerRequest {
    VIEWER_CURRENTLY_SCHEDULED,
    LOGIN,
    LIST_BILLBOARDS,
    GET_BILLBOARD_INFO,
    CREATE_BILLBOARD,
    EDIT_BILLBOARD,
    DELETE_BILLBOARD,
    VIEW_SCHEDULED_BILLBOARDS,
    SCHEDULE_BILLBOARD,
    REMOVE_SCHEDULED,
    LIST_USERS,
    CREATE_USER,
    GET_USER_PERMISSION,
    SET_USER_PERMISSION,
    SET_USER_PASSWORD,
    DELETE_USER,
    LOGOUT
}
