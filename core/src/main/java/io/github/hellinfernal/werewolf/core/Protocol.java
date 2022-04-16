package io.github.hellinfernal.werewolf.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Protocol {
    List<ProtocolPoint> _protocolPoints = new ArrayList<ProtocolPoint>();
    String _name;
    Logger LOGGER = LoggerFactory.getLogger(Protocol.class);
    public Protocol(String name){
        _name = new StringBuilder().append(name).append(generateDiscriminator()).toString();
        LOGGER.debug("Protocol generated: "+ toString());
    }

    public void addProtocolPoint(ProtocolPoint protocolPoint){
        _protocolPoints.add(protocolPoint);


    }
    public String printProtocol(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(_name);
        _protocolPoints.forEach(protocolPoint -> stringBuilder.append(protocolPoint._whatHappend));
        stringBuilder.append("End of Protocol");
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return printProtocol();
    }
    public static String generateDiscriminator(){
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        stringBuilder.append("#");
        stringBuilder.append(random.nextInt(10000));
        return stringBuilder.toString();
    }
}
