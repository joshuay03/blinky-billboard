package SocketCommunication;

import java.io.Serializable;
import java.util.HashMap;


public class Request implements Serializable {
    private ServerRequest requestType;
    private Session session; //can be null
    private HashMap<String, String> data; // can be null

    public ServerRequest getRequestType() {
        return requestType;
    }

    public Session getSession() {
        return session;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    //make hash map more generic or leave it as HashMap, Hash map works for login might need to make it more dynamic e.g Object later
    public Request(ServerRequest requestType, HashMap<String, String> data, Session session) {
        this.requestType = requestType;
        this.data = data;
        this.session = session;
    }

    // this needs to be done in client handler because you need to pass around the objects and process them through the streams
//
//        switch(requestType) {
//            case RequestType.LOGIN_REQ:
//                //the username and password will come from login class
//                credentials = new Credentials(args[1],args[2]);
//                Response.serverResponse(credentials,server);
//                break;
//
//            case RequestType.LIST_BILL_REQ:
//                //this will get the list of billboards from the server
//                Response.serverResponse();
//
//                break;
//            case RequestType.GET_BILL_INFO_REQ:
//               Response.serverResponse();
//                break;
//            case RequestType.CREATE_BILL_REQ
//                ;
//                Response.serverResponse();
//        }
//    }
}

