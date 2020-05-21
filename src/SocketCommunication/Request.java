package SocketCommunication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

    public byte[] serialise(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    // I initially began to create each response in the response object but
    // this needs to be done in client handler because you need to pass around the objects and process them through the streams
//
//        switch(requestType) {
//            case RequestType.LOGIN:
//                //the username and password will come from login class
//                credentials = new Credentials(args[1],args[2]);
//                Response.serverResponse(credentials,server);
//                break;
//
//            case RequestType.LIST_BILLBOARD:
//                //this will get the list of billboards from the server
//                Response.serverResponse();
//
//                break;
//            case RequestType.GET_BILLBOARD_INFO:
//               Response.serverResponse();
//                break;
//            case RequestType.CREATE_BILLBOARD
//                ;
//                Response.serverResponse();
//        }
//    }
}

