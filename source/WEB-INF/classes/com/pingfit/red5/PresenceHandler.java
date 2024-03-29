package com.pingfit.red5;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.IClient;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.io.utils.ObjectMap;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;

import com.pingfit.util.Num;
import com.pingfit.util.Util;


public class PresenceHandler extends MultiThreadedApplicationAdapter {

    //public IScope scope;

    public PresenceHandler(){
    }

    public void setFriends(String friendsCommaSep){
        Logger logger = Logger.getLogger(this.getClass().getName());
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        IScope scope = conn.getScope();
        logger.debug("-----setFriends()---------------------");
        logger.debug(scope);
        logger.debug(client);
        logger.debug(client.getId());
        logger.debug(IClient.ID);
        logger.debug(client.getConnections());
        logger.debug(client.getScopes());
        logger.debug(client.getCreationTime());
        logger.debug(conn);
        logger.debug("current client.getAttribute(\"friends\")="+client.getAttribute("friends"));
        logger.debug("incoming friendsCommaSep="+friendsCommaSep);
        logger.debug("-------------------------------------");
        if (friendsCommaSep==null){
            friendsCommaSep = "";
        }
        conn.getClient().setAttribute("friends", friendsCommaSep);
    }

    public void setFriendsFacebook(String friendsFacebookCommaSep){
        Logger logger = Logger.getLogger(this.getClass().getName());
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        IScope scope = conn.getScope();
        logger.debug("-----setFriendsFacebook()---------------------");
        logger.debug(scope);
        logger.debug(client);
        logger.debug(client.getId());
        logger.debug(IClient.ID);
        logger.debug(client.getConnections());
        logger.debug(client.getScopes());
        logger.debug(client.getCreationTime());
        logger.debug(conn);
        logger.debug("current client.getAttribute(\"facebookfriends\")="+client.getAttribute("facebookfriends"));
        logger.debug("incoming friendsFacebookCommaSep="+friendsFacebookCommaSep);
        logger.debug("-------------------------------------");
        if (friendsFacebookCommaSep==null){
            friendsFacebookCommaSep = "";
        }
        conn.getClient().setAttribute("facebookfriends", friendsFacebookCommaSep);
    }

    public void setStatus(String userstatus){
        Logger logger = Logger.getLogger(this.getClass().getName());
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        IScope scope = conn.getScope();
        String oldStatus = String.valueOf(client.getAttribute("userstatus"));
        if (oldStatus==null || oldStatus.equals("null")){
            oldStatus = "";
        }
        if (userstatus==null || userstatus.equals("null")){
            userstatus = "";
        }
        //Set the status
        conn.getClient().setAttribute("userstatus", userstatus);
        //Broadcast if status has changed
        if (!oldStatus.equals(userstatus)){
            broadcastStatus();
        }
    }

    public void setRoom(String roomid, String roomname){
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("setRoom() called roomid="+roomid+" roomname="+roomname);
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        IScope scope = conn.getScope();
        boolean hasChanged = false;
        if (1==1){
            String old = String.valueOf(client.getAttribute("roomid"));
            if (old==null || old.equals("null")){ old = ""; }
            if (roomid==null || roomid.equals("null")){ roomid = ""; }
            conn.getClient().setAttribute("roomid", roomid);
            if (!old.equals(roomid)){ hasChanged = true; logger.debug("setRoom() roomid has changed");}
        }
        if (1==1){
            String old = String.valueOf(client.getAttribute("roomname"));
            if (old==null || old.equals("null")){ old = ""; }
            if (roomname==null || roomname.equals("null")){ roomname = ""; }
            conn.getClient().setAttribute("roomname", roomname);
            if (!old.equals(roomname)){ hasChanged = true; logger.debug("setRoom() roomname has changed");}
        }
        //Broadcast if has changed
        if (hasChanged){ broadcastStatus(); logger.debug("setRoom() hasChanged, status broadcast");} else {logger.debug("setRoom() !hasChanged");}
    }

    public void heartbeat(String msg){
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("heartbeat() msg="+msg);
    }

    public void setRoomname(String roomname){
        Logger logger = Logger.getLogger(this.getClass().getName());
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        IScope scope = conn.getScope();
        String old = String.valueOf(client.getAttribute("roomname"));
        if (old==null || old.equals("null")){
            old = "";
        }
        if (roomname==null || roomname.equals("null")){
            roomname = "";
        }
        conn.getClient().setAttribute("roomname", roomname);
        //Broadcast if status has changed
        if (!old.equals(roomname)){
            broadcastStatus();
        }
    }

    public void sendMeStatusOfAllFriends(){
        Logger logger = Logger.getLogger(this.getClass().getName());
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        IScope scope = conn.getScope();
        logger.debug("-----sendMeStatusOfAllFriends()---------------------------");
//        logger.debug(scope);
//        logger.debug(client);
//        logger.debug(client.getId());
//        logger.debug(IClient.ID);
//        logger.debug(client.getConnections());
//        logger.debug(client.getScopes());
//        logger.debug(client.getCreationTime());
//        logger.debug(conn);
//        logger.debug("client.getAttribute(\"friends\")="+client.getAttribute("friends"));
        //Create an IServiceCapableConnection
        IServiceCapableConnection myIConn = (IServiceCapableConnection)conn;
        //Get global scope
        IScope globalScope = scope.getContext().getGlobalScope();
        //Iterate connections in global scope
        Collection<Set<IConnection>> conns = globalScope.getConnections();
        for (Iterator<Set<IConnection>> iterator = conns.iterator(); iterator.hasNext();) {
            Set<IConnection> iConnections = iterator.next();
            for (Iterator<IConnection> iConnectionIterator = iConnections.iterator(); iConnectionIterator.hasNext();) {
                IConnection iConnOfFriend = iConnectionIterator.next();
                if (Num.isinteger(String.valueOf(iConnOfFriend.getClient().getAttribute("userid")))){
                    int useridOfThisClient = Integer.parseInt(String.valueOf(iConnOfFriend.getClient().getAttribute("userid")));
                    String facebookuidOfThisClient = String.valueOf(iConnOfFriend.getClient().getAttribute("facebookuid"));
                    boolean isFriend = PresenceHandler.isFriend(useridOfThisClient, String.valueOf(client.getAttribute("friends")));
                    boolean isFacebookFriend = PresenceHandler.isFacebookFriend(facebookuidOfThisClient, String.valueOf(client.getAttribute("facebookfriends")));
                    if (isFriend || isFacebookFriend){
                        logger.debug("calling presenceChange on my own conn");
                        notifyOfPresenceChange(myIConn, iConnOfFriend.getClient());
                    }
                }
            }
        }
//        Iterator it = globalScope.getConnections();
//        while (it.hasNext()){
//            IConnection iConnOfFriend = (IConnection)it.next( );
//            if (Num.isinteger(String.valueOf(iConnOfFriend.getClient().getAttribute("userid")))){
//                int useridOfThisClient = Integer.parseInt(String.valueOf(iConnOfFriend.getClient().getAttribute("userid")));
//                String facebookuidOfThisClient = String.valueOf(iConnOfFriend.getClient().getAttribute("facebookuid"));
//                boolean isFriend = PresenceHandler.isFriend(useridOfThisClient, String.valueOf(client.getAttribute("friends")));
//                boolean isFacebookFriend = PresenceHandler.isFacebookFriend(facebookuidOfThisClient, String.valueOf(client.getAttribute("facebookfriends")));
//                if (isFriend || isFacebookFriend){
//                    logger.debug("calling presenceChange on my own conn");
//                    notifyOfPresenceChange(myIConn, iConnOfFriend.getClient());
//                }
//            }
//        }
        doneSendingAllStatuses(myIConn);
        logger.debug("---------------------------------sendMeStatusOfAllFriends()----");
    }


    public static void broadcastStatus(){
        Logger logger = Logger.getLogger(PresenceHandler.class);
//        logger.debug("-----");
//        logger.debug("-----");
//        logger.debug("-----broadcastStatus()---------------------------");
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        logger.debug("client="+client);
        IScope scope = conn.getScope();
//        //Iterate client's connections
//        logger.debug("Iterating client.getConnections()");
//        for (Iterator<IConnection> iterator=client.getConnections().iterator(); iterator.hasNext();) {
//            IConnection iConnection=iterator.next();
//            logger.debug("-iConnection.getScope()="+iConnection.getScope());
//            if (iConnection.getScope().getName().equals("presence")){
//                logger.debug("-it's a presence scope!");
//                scope = iConnection.getScope();
//            } else {
//                logger.debug("-not a presence scope... iConnection.getScope().getName()="+iConnection.getScope().getName());
//            }
//        }
//        //Iterate client's scopes to find presence
//        if (scope==null){
//            logger.debug("Iterating client.getScopes()");
//            for (Iterator<IScope> itSc=client.getScopes().iterator(); itSc.hasNext();) {
//                IScope iScope=itSc.next();
//                logger.debug("-iScope="+iScope);
//                if (iScope.getName().equals("presence")){
//                    logger.debug("-it's a presence scope!");
//                    scope = iScope;
//                } else {
//                    logger.debug("-not a presence scope... iScope.getName()="+iScope.getName());
//                }
//            }
//        }
//        //If still no scope named presence, return
//        if (scope==null){
//            logger.debug("scope==null so returning");
//            logger.debug("--------------------------broadcastStatus()----");
//            logger.debug("-----");
//            logger.debug("-----");
//            return;
//        }

        //Go get the global scope
        IScope globalScope = scope.getContext().getGlobalScope();
        //Debug
//        logger.debug("scope="+scope);
//        logger.debug("globalScope="+globalScope);
//        logger.debug("client="+client);
//        logger.debug("client.getId()="+client.getId());
//        logger.debug("IClient.ID="+IClient.ID);
//        logger.debug("client.getConnections()="+client.getConnections());
//        logger.debug("client.getScopes()="+client.getScopes());
//        logger.debug("client.getCreationTime()="+client.getCreationTime());
//        logger.debug("conn="+conn);
//        logger.debug("client.getAttribute(\"friends\")="+client.getAttribute("friends"));
//        logger.debug("client.getAttribute(\"status\")="+client.getAttribute("status"));
        //Create the record

        //Iterate connections
        Collection<Set<IConnection>> conns = globalScope.getConnections();
        for (Iterator<Set<IConnection>> iterator = conns.iterator(); iterator.hasNext();) {
            Set<IConnection> iConnections = iterator.next();
            for (Iterator<IConnection> iConnectionIterator = iConnections.iterator(); iConnectionIterator.hasNext();) {
                IConnection iConnection = iConnectionIterator.next();
                if (Num.isinteger(String.valueOf(iConnection.getClient().getAttribute("userid")))){
                    int useridOfThisClient = Integer.parseInt(String.valueOf(iConnection.getClient().getAttribute("userid")));
                    String facebookuidOfThisClient = String.valueOf(iConnection.getClient().getAttribute("facebookuid"));
                    boolean isFriend = PresenceHandler.isFriend(useridOfThisClient, String.valueOf(client.getAttribute("friends")));
                    boolean isFacebookFriend = PresenceHandler.isFacebookFriend(facebookuidOfThisClient, String.valueOf(client.getAttribute("facebookfriends")));
                    if (isFriend || isFacebookFriend){
                        //This is a friend, update status
                        //logger.debug("-is friend, calling presenceChange");
                        IServiceCapableConnection iConnToNotify = (IServiceCapableConnection)iConnection;
                        notifyOfPresenceChange(iConnToNotify, client);
                    } else {
                        //logger.debug("-is not friend");
                    }
                } else {
                    //logger.debug("-userid not an int");
                }
            }
        }


//        Iterator it = globalScope.getConnections();
//        while (it.hasNext()){
//            IConnection iConnection = (IConnection)it.next( );
////            logger.debug("---iConnection="+iConnection);
////            logger.debug("---iConnection.getClient()="+iConnection.getClient());
////            logger.debug("---iConnection.getClient().getAttribute(\"userid\")="+iConnection.getClient().getAttribute("userid"));
////            logger.debug("---iConnection.getClient().getAttribute(\"status\")="+iConnection.getClient().getAttribute("status"));
////            logger.debug("---client.getAttribute(\"friends\")="+client.getAttribute("friends"));
//            //Only broadcast to friends
//            if (Num.isinteger(String.valueOf(iConnection.getClient().getAttribute("userid")))){
//                int useridOfThisClient = Integer.parseInt(String.valueOf(iConnection.getClient().getAttribute("userid")));
//                String facebookuidOfThisClient = String.valueOf(iConnection.getClient().getAttribute("facebookuid"));
//                boolean isFriend = PresenceHandler.isFriend(useridOfThisClient, String.valueOf(client.getAttribute("friends")));
//                boolean isFacebookFriend = PresenceHandler.isFacebookFriend(facebookuidOfThisClient, String.valueOf(client.getAttribute("facebookfriends")));
//                if (isFriend || isFacebookFriend){
//                    //This is a friend, update status
//                    //logger.debug("-is friend, calling presenceChange");
//                    IServiceCapableConnection iConnToNotify = (IServiceCapableConnection)iConnection;
//                    notifyOfPresenceChange(iConnToNotify, client);
//                } else {
//                    //logger.debug("-is not friend");
//                }
//            } else {
//                //logger.debug("-userid not an int");
//            }
//        }
//        logger.debug("--------------------------broadcastStatus()----");
//        logger.debug("-----");
//        logger.debug("-----");
    }

    public static void notifyOfPresenceChange(IServiceCapableConnection iConnToNotify, IClient clientWithChangedStatus){
        Object[] out = new Object[1];
        ObjectMap oneRow = new ObjectMap( );
        oneRow.put( "userid" , clientWithChangedStatus.getAttribute("userid") );
        oneRow.put( "nickname" , clientWithChangedStatus.getAttribute("nickname") );
        oneRow.put( "roomid" , clientWithChangedStatus.getAttribute("roomid") );
        oneRow.put( "roomname" , clientWithChangedStatus.getAttribute("roomname") );
        oneRow.put( "userstatus" , clientWithChangedStatus.getAttribute("userstatus") );
        oneRow.put( "facebookuid" , clientWithChangedStatus.getAttribute("facebookuid") );
        out[0] = oneRow;
        iConnToNotify.invoke("presenceChange" , new Object[]{out} );
    }

    public static void doneSendingAllStatuses(IServiceCapableConnection iConnToNotify){
        Object[] out = new Object[1];
        ObjectMap oneRow = new ObjectMap( );
        out[0] = oneRow;
        iConnToNotify.invoke("doneSendingAllStatuses" , new Object[]{out} );
    }


    private Element nameValueElement(String name, String value){
        Element element = new Element(name);
        element.setContent(new Text(value));
        return element;
    }

    public static boolean isFriend(int userid, String friends){
        if (userid==0){
            return false;
        }
        if (friends==null || friends.equals("null") || friends.equals("")){
            return false;
        }
        //@todo optimize isFriend... will be called a lot... hold String[] in client attribute?
        String[] split = friends.split(",");
        if (split.length>0){
            for (int i=0; i<split.length; i++) {
                String friendUserid = split[i];
                if (friendUserid.equals(String.valueOf(userid))){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isFacebookFriend(String facebookuid, String facebookfriends){
        if (facebookuid==null || facebookuid.equals("")){
            return false;
        }
        if (facebookfriends==null || facebookfriends.equals("null") || facebookfriends.equals("")){
            return false;
        }
        //@todo optimize isFriend... will be called a lot... hold String[] in client attribute?
        String[] split = facebookfriends.split(",");
        if (split.length>0){
            for (int i=0; i<split.length; i++) {
                String friendUserid = split[i];
                if (friendUserid.equals(facebookuid)){
                    return true;
                }
            }
        }
        return false;
    }


    public static void callIncomingDispatchEvent(IServiceCapableConnection iConnToNotify, IClient clientWithChangedStatus, String eventtype, String arg1, String arg2, String arg3, String arg4, String arg5){
        Object[] out = new Object[1];
        ObjectMap oneRow = new ObjectMap( );
        oneRow.put( "userid" , clientWithChangedStatus.getAttribute("userid") );
        oneRow.put( "nickname" , clientWithChangedStatus.getAttribute("nickname") );
        oneRow.put( "roomid" , clientWithChangedStatus.getAttribute("roomid") );
        oneRow.put( "roomname" , clientWithChangedStatus.getAttribute("roomname") );
        oneRow.put( "userstatus" , clientWithChangedStatus.getAttribute("userstatus") );
        oneRow.put( "eventtype" , eventtype );
        oneRow.put( "arg1" , arg1 );
        oneRow.put( "arg2" , arg2 );
        oneRow.put( "arg3" , arg3 );
        oneRow.put( "arg4" , arg4 );
        oneRow.put( "arg5" , arg5 );
        out[0] = oneRow;
        iConnToNotify.invoke("incomingRemoteEvent" , new Object[]{out} );
    }





    public static void dispatchEventToCommaSepListOfUsers(String useridscommasep, String eventtype, String arg1, String arg2, String arg3, String arg4, String arg5){
        Logger logger = Logger.getLogger(PresenceHandler.class);
        logger.debug("-----");
        logger.debug("-----");
        logger.debug("-----dispatchEventToCommaSepListOfUsers()---------------------------");
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        logger.debug("client="+client);
        IScope scope = conn.getScope();
        //Go get the global scope
        IScope globalScope = scope.getContext().getGlobalScope();
        //Debug
//        logger.debug("scope="+scope);
//        logger.debug("globalScope="+globalScope);
//        logger.debug("client="+client);
//        logger.debug("client.getId()="+client.getId());
//        logger.debug("IClient.ID="+IClient.ID);
//        logger.debug("client.getConnections()="+client.getConnections());
//        logger.debug("client.getScopes()="+client.getScopes());
//        logger.debug("client.getCreationTime()="+client.getCreationTime());
//        logger.debug("conn="+conn);
//        logger.debug("client.getAttribute(\"friends\")="+client.getAttribute("friends"));
//        logger.debug("client.getAttribute(\"status\")="+client.getAttribute("status"));
        //Iterate connections
        Collection<Set<IConnection>> conns = globalScope.getConnections();
        for (Iterator<Set<IConnection>> iterator = conns.iterator(); iterator.hasNext();) {
            Set<IConnection> iConnections = iterator.next();
            for (Iterator<IConnection> iConnectionIterator = iConnections.iterator(); iConnectionIterator.hasNext();) {
                IConnection iConnection = iConnectionIterator.next();
                if (Num.isinteger(String.valueOf(iConnection.getClient().getAttribute("userid")))){
                    int useridOfThisClient = Integer.parseInt(String.valueOf(iConnection.getClient().getAttribute("userid")));
                    String facebookuidOfThisClient = String.valueOf(iConnection.getClient().getAttribute("facebookuid"));
                    boolean isFriend = PresenceHandler.isFriend(useridOfThisClient, useridscommasep);
                    boolean isFacebookFriend = PresenceHandler.isFacebookFriend(facebookuidOfThisClient, String.valueOf(client.getAttribute("facebookfriends")));
                    if (isFriend || isFacebookFriend){
                        //This is a friend, update status
                        logger.debug("-is friend, calling remoteDispatchEvent");
                        IServiceCapableConnection iConnToNotify = (IServiceCapableConnection)iConnection;
                        callIncomingDispatchEvent(iConnToNotify, client, eventtype, arg1, arg2, arg3, arg4, arg5);
                    } else {
                        logger.debug("-is not in comma sep list of users");
                    }
                } else {
                    logger.debug("-userid not an int");
                }
            }
        }


//        Iterator it = globalScope.getConnections();
//        while (it.hasNext()){
//            IConnection iConnection = (IConnection)it.next( );
////            logger.debug("---iConnection="+iConnection);
////            logger.debug("---iConnection.getClient()="+iConnection.getClient());
////            logger.debug("---iConnection.getClient().getAttribute(\"userid\")="+iConnection.getClient().getAttribute("userid"));
////            logger.debug("---iConnection.getClient().getAttribute(\"status\")="+iConnection.getClient().getAttribute("status"));
////            logger.debug("---client.getAttribute(\"friends\")="+client.getAttribute("friends"));
//            //Only broadcast to friends
//            if (Num.isinteger(String.valueOf(iConnection.getClient().getAttribute("userid")))){
//                int useridOfThisClient = Integer.parseInt(String.valueOf(iConnection.getClient().getAttribute("userid")));
//                String facebookuidOfThisClient = String.valueOf(iConnection.getClient().getAttribute("facebookuid"));
//                boolean isFriend = PresenceHandler.isFriend(useridOfThisClient, useridscommasep);
//                boolean isFacebookFriend = PresenceHandler.isFacebookFriend(facebookuidOfThisClient, String.valueOf(client.getAttribute("facebookfriends")));
//                if (isFriend || isFacebookFriend){
//                    //This is a friend, update status
//                    logger.debug("-is friend, calling remoteDispatchEvent");
//                    IServiceCapableConnection iConnToNotify = (IServiceCapableConnection)iConnection;
//                    callIncomingDispatchEvent(iConnToNotify, client, eventtype, arg1, arg2, arg3, arg4, arg5);
//                } else {
//                    logger.debug("-is not in comma sep list of users");
//                }
//            } else {
//                logger.debug("-userid not an int");
//            }
//        }
        logger.debug("--------------------------dispatchEventToCommaSepListOfUsers()----");
        logger.debug("-----");
        logger.debug("-----");
    }



    public static void dispatchEventToUser(String useridtodispatchto, String eventtype, String arg1, String arg2, String arg3, String arg4, String arg5){
        Logger logger = Logger.getLogger(PresenceHandler.class);
        logger.debug("-----");
        logger.debug("-----");
        logger.debug("-----dispatchEventToUser()---------------------------");
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        logger.debug("client="+client);
        IScope scope = conn.getScope();
        //Go get the global scope
        IScope globalScope = scope.getContext().getGlobalScope();
        //Debug
//        logger.debug("scope="+scope);
//        logger.debug("globalScope="+globalScope);
//        logger.debug("client="+client);
//        logger.debug("client.getId()="+client.getId());
//        logger.debug("IClient.ID="+IClient.ID);
//        logger.debug("client.getConnections()="+client.getConnections());
//        logger.debug("client.getScopes()="+client.getScopes());
//        logger.debug("client.getCreationTime()="+client.getCreationTime());
//        logger.debug("conn="+conn);
//        logger.debug("client.getAttribute(\"friends\")="+client.getAttribute("friends"));
//        logger.debug("client.getAttribute(\"status\")="+client.getAttribute("status"));
        //Iterate connections
        Collection<Set<IConnection>> conns = globalScope.getConnections();
        for (Iterator<Set<IConnection>> iterator = conns.iterator(); iterator.hasNext();) {
            Set<IConnection> iConnections = iterator.next();
            for (Iterator<IConnection> iConnectionIterator = iConnections.iterator(); iConnectionIterator.hasNext();) {
                IConnection iConnection = iConnectionIterator.next();
                if (Num.isinteger(String.valueOf(iConnection.getClient().getAttribute("userid")))){
                    int useridOfThisClient = Integer.parseInt(String.valueOf(iConnection.getClient().getAttribute("userid")));
                    boolean ispersontodispatchto = false;
                    if (Num.isinteger(useridtodispatchto) && Integer.parseInt(useridtodispatchto)==useridOfThisClient){
                        ispersontodispatchto = true;
                    }
                    if (ispersontodispatchto){
                        logger.debug("-ispersontodispatchto, calling remoteDispatchEvent");
                        IServiceCapableConnection iConnToNotify = (IServiceCapableConnection)iConnection;
                        callIncomingDispatchEvent(iConnToNotify, client, eventtype, arg1, arg2, arg3, arg4, arg5);
                    } else {
                        logger.debug("-is not persontodispatchto");
                    }
                } else {
                    logger.debug("-userid not an int");
                }
            }
        }
//        Iterator it = globalScope.getConnections();
//        while (it.hasNext()){
//            IConnection iConnection = (IConnection)it.next( );
////            logger.debug("---iConnection="+iConnection);
////            logger.debug("---iConnection.getClient()="+iConnection.getClient());
////            logger.debug("---iConnection.getClient().getAttribute(\"userid\")="+iConnection.getClient().getAttribute("userid"));
////            logger.debug("---iConnection.getClient().getAttribute(\"status\")="+iConnection.getClient().getAttribute("status"));
////            logger.debug("---client.getAttribute(\"friends\")="+client.getAttribute("friends"));
//            //Only broadcast to friends
//            if (Num.isinteger(String.valueOf(iConnection.getClient().getAttribute("userid")))){
//                int useridOfThisClient = Integer.parseInt(String.valueOf(iConnection.getClient().getAttribute("userid")));
//                boolean ispersontodispatchto = false;
//                if (Num.isinteger(useridtodispatchto) && Integer.parseInt(useridtodispatchto)==useridOfThisClient){
//                    ispersontodispatchto = true;
//                }
//                if (ispersontodispatchto){
//                    logger.debug("-ispersontodispatchto, calling remoteDispatchEvent");
//                    IServiceCapableConnection iConnToNotify = (IServiceCapableConnection)iConnection;
//                    callIncomingDispatchEvent(iConnToNotify, client, eventtype, arg1, arg2, arg3, arg4, arg5);
//                } else {
//                    logger.debug("-is not persontodispatchto");
//                }
//            } else {
//                logger.debug("-userid not an int");
//            }
//        }
        logger.debug("--------------------------dispatchEventToUser()----");
        logger.debug("-----");
        logger.debug("-----");
    }


    public static void dispatchEventToRoom(String roomidtodispatchto, String eventtype, String arg1, String arg2, String arg3, String arg4, String arg5){
        Logger logger = Logger.getLogger(PresenceHandler.class);
        logger.debug("-----");
        logger.debug("-----");
        logger.debug("-----dispatchEventToRoom()---------------------------");
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        logger.debug("client="+client);
        IScope scope = conn.getScope();
        //Go get the global scope
        IScope globalScope = scope.getContext().getGlobalScope();
        //Debug
//        logger.debug("scope="+scope);
//        logger.debug("globalScope="+globalScope);
//        logger.debug("client="+client);
//        logger.debug("client.getId()="+client.getId());
//        logger.debug("IClient.ID="+IClient.ID);
//        logger.debug("client.getConnections()="+client.getConnections());
//        logger.debug("client.getScopes()="+client.getScopes());
//        logger.debug("client.getCreationTime()="+client.getCreationTime());
//        logger.debug("conn="+conn);
//        logger.debug("client.getAttribute(\"friends\")="+client.getAttribute("friends"));
//        logger.debug("client.getAttribute(\"status\")="+client.getAttribute("status"));
        //Iterate connections
        Collection<Set<IConnection>> conns = globalScope.getConnections();
        for (Iterator<Set<IConnection>> iterator = conns.iterator(); iterator.hasNext();) {
            Set<IConnection> iConnections = iterator.next();
            for (Iterator<IConnection> iConnectionIterator = iConnections.iterator(); iConnectionIterator.hasNext();) {
                IConnection iConnection = iConnectionIterator.next();
                    if (Num.isinteger(String.valueOf(iConnection.getClient().getAttribute("roomid")))){
                    int roomidOfThisClient = Integer.parseInt(String.valueOf(iConnection.getClient().getAttribute("roomid")));
                    boolean isinroomtodispatchto = false;
                    if (Num.isinteger(roomidtodispatchto) && Integer.parseInt(roomidtodispatchto)==roomidOfThisClient){
                        isinroomtodispatchto = true;
                    }
                    if (isinroomtodispatchto){
                        logger.debug("-isinroomtodispatchto, calling remoteDispatchEvent");
                        IServiceCapableConnection iConnToNotify = (IServiceCapableConnection)iConnection;
                        callIncomingDispatchEvent(iConnToNotify, client, eventtype, arg1, arg2, arg3, arg4, arg5);
                    } else {
                        logger.debug("-is not inroomtodispatchto");
                    }
                } else {
                    logger.debug("-roomid not an int");
                }
            }
        }
//
//        Iterator it = globalScope.getConnections();
//        while (it.hasNext()){
//            IConnection iConnection = (IConnection)it.next( );
////            logger.debug("---iConnection="+iConnection);
////            logger.debug("---iConnection.getClient()="+iConnection.getClient());
////            logger.debug("---iConnection.getClient().getAttribute(\"userid\")="+iConnection.getClient().getAttribute("userid"));
////            logger.debug("---iConnection.getClient().getAttribute(\"status\")="+iConnection.getClient().getAttribute("status"));
////            logger.debug("---client.getAttribute(\"friends\")="+client.getAttribute("friends"));
//            //Only broadcast to friends
//            if (Num.isinteger(String.valueOf(iConnection.getClient().getAttribute("roomid")))){
//                int roomidOfThisClient = Integer.parseInt(String.valueOf(iConnection.getClient().getAttribute("roomid")));
//                boolean isinroomtodispatchto = false;
//                if (Num.isinteger(roomidtodispatchto) && Integer.parseInt(roomidtodispatchto)==roomidOfThisClient){
//                    isinroomtodispatchto = true;
//                }
//                if (isinroomtodispatchto){
//                    logger.debug("-isinroomtodispatchto, calling remoteDispatchEvent");
//                    IServiceCapableConnection iConnToNotify = (IServiceCapableConnection)iConnection;
//                    callIncomingDispatchEvent(iConnToNotify, client, eventtype, arg1, arg2, arg3, arg4, arg5);
//                } else {
//                    logger.debug("-is not inroomtodispatchto");
//                }
//            } else {
//                logger.debug("-roomid not an int");
//            }
//        }
        logger.debug("--------------------------dispatchEventToRoom()----");
        logger.debug("-----");
        logger.debug("-----");
    }


}