package com.pingfit.red5;

import org.red5.server.api.event.IEventListener;
import org.red5.server.api.event.IEvent;
import org.red5.server.net.rtmp.event.BaseEvent;
import org.apache.log4j.Logger;

/**
 * User: Joe Reger Jr
 * Date: Mar 21, 2009
 * Time: 7:07:08 PM
 */
public class RoomListener implements IEventListener {

    public RoomListener(){
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("RoomListener instanciated");    
    }

    public void notifyEvent(IEvent iEvent) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.debug("iEvent.getType()="+iEvent.getType());
        if (iEvent instanceof BaseEvent){
            logger.debug("it's a BaseEvent");
            BaseEvent baseEvent = (BaseEvent)iEvent;
        }

    }
}
