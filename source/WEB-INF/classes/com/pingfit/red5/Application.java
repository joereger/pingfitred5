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
        appScope = app;
		return true;
	}


	public boolean appConnect(IConnection conn, Object[] params) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("appConnect() called");
        for (int i=0; i<params.length; i++) {
            Object param=params[i];
            logger.debug("appConnect() param="+param.toString());
        }
        //Set attributes that'll follow this IClient around the system
        int userid = 0;
        if (params!=null && params.length>=1 && params[0]!=null && Num.isinteger(String.valueOf(params[0]))){
            userid = Integer.parseInt(String.valueOf(params[0]));
        }
        conn.getClient().setAttribute("userid", userid);
        String name = "";
        if (params!=null && params.length>=2 && params[1]!=null && !String.valueOf(params[1]).equals("")){
            name = String.valueOf(params[1]);
        }
        conn.getClient().setAttribute("name", name);
        logger.debug("appConnect() by "+conn.getClient().getAttribute("name"));
        return super.appConnect(conn, params);
	}


	public void appDisconnect(IConnection conn) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("appDisconnect() called by "+conn.getClient().getAttribute("name"));
        //System.out.println("appDisconnect() called");
        //Do stuff before, apparently
        super.appDisconnect(conn);
	}

    public boolean roomConnect(IConnection conn, Object[] params) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("roomConnect()");
        IConnection conn2 = Red5.getConnectionLocal();
        IClient client = conn2.getClient();
        IScope scope = conn2.getScope();
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
        //Store the person who just entered room
        Object[] justEnteredRoom = new Object[1];
        ObjectMap oneRow = new ObjectMap( );
        oneRow.put( "name" , client.getAttribute("name") );
        oneRow.put( "userid" , client.getAttribute("userid") );
        justEnteredRoom[0] = oneRow;
        //Iterate connections in this scope
        Iterator it = scope.getConnections();
        logger.debug("scope.getConnections().hasNext()="+scope.getConnections().hasNext());
        while (it.hasNext()){
            IConnection iConnection = (IConnection)it.next( );
            //Notify existing users of this new person
            IServiceCapableConnection iConn = (IServiceCapableConnection)iConnection;
            iConn.invoke("personEnteredRoom" , new Object[] {justEnteredRoom} );
            //Notify person who just entered of people already here
            Object[] wasAlreadyInRoom = new Object[1];
            ObjectMap oneRow2 = new ObjectMap( );
            oneRow2.put( "name" , iConn.getClient().getAttribute("name") );
            oneRow2.put( "userid" , iConn.getClient().getAttribute("userid") );
            wasAlreadyInRoom[0] = oneRow2;
            IServiceCapableConnection iConn2 = (IServiceCapableConnection)conn2;
            iConn2.invoke("personEnteredRoom" , new Object[] {wasAlreadyInRoom} );
        }
        return true;
    }

    public void roomDisconnect(IConnection conn) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("roomDisconnect()");
        IConnection conn2 = Red5.getConnectionLocal();
        IClient client = conn2.getClient();
        IScope scope = conn2.getScope();
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
        oneRow.put( "name" , client.getAttribute("name") );
        oneRow.put( "userid" , client.getAttribute("userid") );
        justLeftRoom[0] = oneRow;
        //Iterate connections in this scope
        Iterator it = scope.getConnections();
        logger.debug("scope.getConnections().hasNext()="+scope.getConnections().hasNext());
        while (it.hasNext()){
            IConnection iConnection = (IConnection)it.next( );
            //Notify existing users of this new person
            IServiceCapableConnection iConn = (IServiceCapableConnection)iConnection;
            iConn.invoke("personLeftRoom" , new Object[] {justLeftRoom} );
        }
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
          IEventListener listener = new RoomListener();
          room.addEventListener(listener);
          //createSharedObject(room, "sampleSO", true);
          //ISharedObject so = getSharedObject(room, "sampleSO");
          return true;
      }



    




}
