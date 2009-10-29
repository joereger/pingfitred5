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

import java.util.Iterator;
import java.util.ArrayList;

import com.pingfit.util.Num;
import com.pingfit.util.Util;


public class RoomHandler extends MultiThreadedApplicationAdapter {

    public IScope scope;

    public RoomHandler(IScope scope){
        this.scope = scope;

    }

    public boolean roomConnect(IConnection conn, Object[] params) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("roomConnect() called inside RoomHandler!!!");
        return true;
    }

    public void say(String msg, String from, String messagetype){
        Logger logger = Logger.getLogger(this.getClass().getName());
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        IScope scope = conn.getScope();
        logger.debug("-----say()---------------------------");
        logger.debug(scope);
        logger.debug(client);
        logger.debug(client.getId());
        logger.debug(IClient.ID);
        logger.debug(client.getConnections());
        logger.debug(client.getScopes());
        logger.debug(client.getCreationTime());
        logger.debug(conn);
        logger.debug("-------------------------------------");
        if (msg!=null && !msg.equals("") && msg.length()>0){
            //Create the message to be sent
            Object[] out = new Object[1];
            ObjectMap oneRow = new ObjectMap( );
            oneRow.put( "nickname" , from );
            oneRow.put( "msg" , msg );
            oneRow.put( "messagetype" , messagetype );
            oneRow.put( "roomid" , client.getAttribute("roomid") );
            oneRow.put( "userid" , client.getAttribute("userid") );
            out[0] = oneRow;
            //Iterate connections in this scope
            Iterator it = scope.getConnections();
            while (it.hasNext()){
                IConnection iConnection = (IConnection)it.next( );
                logger.debug("sending msg to iConnection.getHost()="+iConnection.getHost());
                IServiceCapableConnection iConn = (IServiceCapableConnection)iConnection;
                iConn.invoke("messageToRoom" , new Object[] {out} );
            }
        }
    }

    public String getPeopleInRoom(){
        Logger logger = Logger.getLogger(this.getClass().getName());
        IConnection conn = Red5.getConnectionLocal();
        IClient client = conn.getClient();
        IScope scope = conn.getScope();
        logger.debug("-----getPeopleInRoom()---------------------------");
        logger.debug(scope);
        logger.debug(client);
        logger.debug(client.getId());
        logger.debug(IClient.ID);
        logger.debug(client.getConnections());
        logger.debug(client.getScopes());
        logger.debug(client.getCreationTime());
        logger.debug(conn);
        logger.debug("-------------------------------------");
        Element root = new Element("peopleinroom");
        org.jdom.Document outDoc = new org.jdom.Document(root);
        for (Iterator<IClient> it=scope.getClients().iterator(); it.hasNext();) {
            IClient iClient=it.next();
            //logger.debug("getPeopleInRoom() adding iClient.getId()="+iClient.getId());
            Element element = new Element("person");
            element.addContent(nameValueElement("nickname", String.valueOf(iClient.getAttribute("nickname"))));
            element.addContent(nameValueElement("userid", String.valueOf(iClient.getAttribute("userid"))));
            element.addContent(nameValueElement("facebookuid", String.valueOf(iClient.getAttribute("facebookuid"))));
            root.addContent(element);
        }
        return Util.jdomDocAsString(outDoc);
    }

    private Element nameValueElement(String name, String value){
        Element element = new Element(name);
        element.setContent(new Text(value));
        return element;
    }





}