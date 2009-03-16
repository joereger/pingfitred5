package com.pingfit.red5;

import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IBandwidthConfigure;
import org.red5.server.api.IConnection;
import org.red5.server.api.IScope;
import org.red5.server.api.IClient;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.stream.IServerStream;
import org.red5.server.api.stream.IStreamCapableConnection;
import org.red5.server.api.stream.support.SimpleConnectionBWConfig;
import org.red5.server.api.stream.support.SimplePlayItem;
import org.red5.server.api.stream.support.StreamUtils;
import org.red5.io.utils.ObjectMap;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Set;


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
        //Do stuff before, apparently
        //System.out.println("appConnect() called");
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("appConnect() called");
        return super.appConnect(conn, params);
	}


	public void appDisconnect(IConnection conn) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("appDisconnect() called");
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
        //Iterate clients in this scope
//        Iterator it = this.getScope( ).getClients( ).iterator( );
//        while ( it.hasNext() ){
//            IClient client = (IClient)it.next( );
//            logger.debug("found a client... client.getId()="+client.getId());
//            //Iterate this client's connections
//            Set<IConnection> thisUsersConns = client.getConnections(this.getScope());
//            for (Iterator<IConnection> iConnectionIterator=thisUsersConns.iterator(); iConnectionIterator.hasNext();) {
//                IConnection iConnection=iConnectionIterator.next();
//                logger.debug("found a connection... iConnection.getHost()="+iConnection.getHost());
//                //Send them the message
//                IServiceCapableConnection iconn = (IServiceCapableConnection)iConnection;
//                iconn.invoke("messageInbound" , new Object[] {out} );
//            }
//        }
    }


    public String high5Red5(int how_many){
        String highFives = "";
        if(how_many==0){ return "I dont like you anyways, bro."; }
        for(int i=0;i<how_many;i++){ highFives += "*SmackBoomps*\n"; }
        return highFives;
    }


}
