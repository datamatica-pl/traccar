/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.traccar;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.traccar.model.CommandResponse;

/**
 *
 * @author Lukasz
 */
public class BaseVolatileDataHandler extends SimpleChannelHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();
        if(isVolatile(message)) {
            CommandResponse cr = (CommandResponse) message;
            cr.getActiveDevice().onCommandResponse(cr.toString());
        } else
            super.messageReceived(ctx, e);
    }

    private boolean isVolatile(Object message) {
        return message instanceof CommandResponse;
    }
    
}
