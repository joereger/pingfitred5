package com.pingfit.red5;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.IClient;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.io.utils.ObjectMap;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.ArrayList;

import com.pingfit.util.Num;


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
        if (params!=null && params.length>=2 && params[1]!=null && String.valueOf(params[1]).equals("")){
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
        logger.debug("roomConnect() called");

        //Whether user should be allowed in???
        return true;
    }

    public boolean roomStart(IScope room) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("roomStart() called");
          if (!super.roomStart(room)){
              logger.debug("!super.roomStart(room) so returning false");
              return false;
          }
          //createSharedObject(room, "sampleSO", true);
          //ISharedObject so = getSharedObject(room, "sampleSO");
          return true;
      }


    public void say(String msg, String from){
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("say() called... msg="+msg+" from="+from);
        //Create the message to be sent
        Object[] out = new Object[1];
        ObjectMap oneRow = new ObjectMap( );
        oneRow.put( "from" , from );
        oneRow.put( "msg" , msg );
        out[0] = oneRow;
        //Iterate connections in this scope
        Iterator it = this.getScope().getConnections();
        while (it.hasNext()){
            IConnection iConnection = (IConnection)it.next( );
            logger.debug("found a connection... iConnection.getHost()="+iConnection.getHost());
            IServiceCapableConnection iConn = (IServiceCapableConnection)iConnection;
            iConn.invoke("messageInbound" , new Object[] {out} );
        }
    }

    public ArrayList<Person> getPeopleInRoom(){
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("getPeopleInRoom() called");
        ArrayList<Person> out = new ArrayList<Person>();
        for (Iterator<IClient> it=this.getScope().getClients().iterator(); it.hasNext();) {
            IClient iClient=it.next();
            logger.debug("getPeopleInRoom() adding iClient.getId()="+iClient.getId());
            Person person = new Person();
            person.setName(String.valueOf(iClient.getAttribute("name")));
            person.setUserid(Integer.parseInt(String.valueOf(iClient.getAttribute("userid"))));
            out.add(person);
        }
        return out;
    }



}
