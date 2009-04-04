package com.pingfit.red5;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.IClient;
import org.red5.server.api.Red5;
import org.red5.server.api.event.IEventListener;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.io.utils.ObjectMap;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;

import java.util.Iterator;
import java.util.ArrayList;

import com.pingfit.util.Num;
import com.pingfit.util.Util;


public class Application extends MultiThreadedApplicationAdapter {

	private IScope appScope;

	public boolean appStart(IScope app) {
        //System.out.println("pingFitRed5 Application.appStart() called");
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("pingFitRed5 Application.appStart() called");
        //Object handler = new PresenceHandler(app);
        //app.registerServiceHandler("presence", handler);
        appScope = app;
		return true;
	}


	public boolean appConnect(IConnection conn, Object[] params) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("appConnect() called");
        for (int i=0; i<params.length; i++) {
            Object param=params[i];
            logger.debug("appConnect() param["+i+"]="+param.toString());
        }
        //Set attributes that'll follow this IClient around the system
        int userid = 0;
        if (params!=null && params.length>=1 && params[0]!=null && Num.isinteger(String.valueOf(params[0]))){
            userid = Integer.parseInt(String.valueOf(params[0]));
        }
        conn.getClient().setAttribute("userid", userid);
        String nickname = "";
        if (params!=null && params.length>=2 && params[1]!=null && !String.valueOf(params[1]).equals("")){
            nickname = String.valueOf(params[1]);
        }
        conn.getClient().setAttribute("nickname", nickname);
        String friends = "";
        if (params!=null && params.length>=3 && params[2]!=null && !String.valueOf(params[2]).equals("")){
            friends = String.valueOf(params[2]);
        }
        conn.getClient().setAttribute("friends", friends);
        String userstatus = "Online";
        if (params!=null && params.length>=4 && params[3]!=null && !String.valueOf(params[3]).equals("")){
            userstatus = String.valueOf(params[3]);
        }
        conn.getClient().setAttribute("userstatus", userstatus);
        String roomid = "";
        if (params!=null && params.length>=5 && params[4]!=null && !String.valueOf(params[4]).equals("")){
            roomid = String.valueOf(params[4]);
        }
        conn.getClient().setAttribute("roomid", roomid);
        String roomname = "";
        if (params!=null && params.length>=6 && params[5]!=null && !String.valueOf(params[5]).equals("")){
            roomname = String.valueOf(params[5]);
        }
        conn.getClient().setAttribute("roomname", roomname);
        //Broadcast Status
        PresenceHandler.broadcastStatus();
        return super.appConnect(conn, params);
	}


	public void appDisconnect(IConnection conn) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("appDisconnect() called by "+conn.getClient().getAttribute("nickname"));
        //System.out.println("appDisconnect() called");
        //Broadcast Status
        conn.getClient().setAttribute("userstatus", "Offline");
        PresenceHandler.broadcastStatus();
        //Do stuff before, apparently
        super.appDisconnect(conn);
	}

    public boolean roomConnect(IConnection conn, Object[] params) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("-----roomConnect()---------------------------");
        for (int i=0; i<params.length; i++) {
            Object param=params[i];
            logger.debug("roomConnect() param["+i+"]="+param.toString());
        }
        IConnection conn2 = Red5.getConnectionLocal();
        IClient client = conn2.getClient();
        IScope scope = conn2.getScope();
        logger.debug("scope="+scope);
        logger.debug("client="+client);
        logger.debug("client.getId()="+client.getId());
        logger.debug("IClient.ID="+IClient.ID);
        logger.debug("client.getConnections()="+client.getConnections());
        logger.debug("client.getScopes()="+client.getScopes());
        logger.debug("client.getCreationTime()="+client.getCreationTime());
        logger.debug("conn="+conn);
        logger.debug("client.getAttribute(\"friends\")="+client.getAttribute("friends"));
        logger.debug("client.getAttribute(\"userstatus\")="+client.getAttribute("userstatus"));
        //Store the person who just entered room
        Object[] justEnteredRoom = new Object[1];
        ObjectMap oneRow = new ObjectMap( );
        oneRow.put( "nickname" , client.getAttribute("nickname") );
        oneRow.put( "userid" , client.getAttribute("userid") );
        justEnteredRoom[0] = oneRow;
        //Set userid of the connected user
        int useridOfJoiner = 0;
        if (Num.isinteger(String.valueOf(client.getAttribute("userid")))){
            useridOfJoiner = Integer.parseInt(String.valueOf(client.getAttribute("userid")));
        }
        //Iterate connections in this scope
        Iterator it = scope.getConnections();
        logger.debug("scope.getConnections().hasNext()="+scope.getConnections().hasNext());
        while (it.hasNext()){
            IConnection iConnection = (IConnection)it.next( );
            //Notify existing users of this new person
            IServiceCapableConnection iConn = (IServiceCapableConnection)iConnection;
            iConn.invoke("personEntersRoom" , new Object[] {justEnteredRoom} );
            //Notify person who just entered of people already here
            Object[] wasAlreadyInRoom = new Object[1];
            ObjectMap oneRow2 = new ObjectMap( );
            oneRow2.put( "nickname" , iConn.getClient().getAttribute("nickname") );
            oneRow2.put( "userid" , iConn.getClient().getAttribute("userid") );
            wasAlreadyInRoom[0] = oneRow2;
            IServiceCapableConnection iConn2 = (IServiceCapableConnection)conn2;
            iConn2.invoke("personEntersRoom" , new Object[] {wasAlreadyInRoom} );
        }
        //Broadcast Status
        conn.getClient().setAttribute("userstatus", "Online");
        PresenceHandler.broadcastStatus();
        logger.debug("-----------------------------roomConnect()--------");
        return true;
    }

    public void roomDisconnect(IConnection conn) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        IConnection conn2 = Red5.getConnectionLocal();
        IClient client = conn2.getClient();
        IScope scope = conn2.getScope();
        logger.debug("roomDisconnect() by client.getAttribute(\"nickname\")="+client.getAttribute("nickname")+" client.getAttribute(\"userid\")="+client.getAttribute("userid"));
//        logger.debug("-----roomConnect()---------------------------");
//        logger.debug(scope);
//        logger.debug(client);
//        logger.debug(client.getId());
//        logger.debug(IClient.ID);
//        logger.debug(client.getConnections());
//        logger.debug(client.getScopes());
//        logger.debug(client.getCreationTime());
//        logger.debug(conn);
//        logger.debug("-------------------------------------");
        //Store the person who just left room
        Object[] justLeftRoom = new Object[1];
        ObjectMap oneRow = new ObjectMap( );
        oneRow.put( "nickname" , client.getAttribute("nickname") );
        oneRow.put( "userid" , client.getAttribute("userid") );
        justLeftRoom[0] = oneRow;
        //Iterate connections in this scope
        Iterator it = scope.getConnections();
        logger.debug("scope.getConnections().hasNext()="+scope.getConnections().hasNext());
        while (it.hasNext()){
            IConnection iConnection = (IConnection)it.next( );
            //Notify existing users of this new person
            IServiceCapableConnection iConn = (IServiceCapableConnection)iConnection;
            iConn.invoke("personLeavesRoom" , new Object[] {justLeftRoom} );
        }
        //Broadcast Status
        conn.getClient().setAttribute("status", "Offline");
        PresenceHandler.broadcastStatus();
    }


    public boolean roomStart(IScope room) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("roomStart() called");
          if (!super.roomStart(room)){
              logger.debug("!super.roomStart(room) so returning false");
              return false;
          }
          Object handler = new RoomHandler(room);
          room.registerServiceHandler("room", handler);
          Object presHand = new PresenceHandler();
          room.registerServiceHandler("presence", presHand);
          //IEventListener listener = new RoomListener();
          //room.addEventListener(listener);
          //createSharedObject(room, "sampleSO", true);
          //ISharedObject so = getSharedObject(room, "sampleSO");
          return true;
      }













    




}
