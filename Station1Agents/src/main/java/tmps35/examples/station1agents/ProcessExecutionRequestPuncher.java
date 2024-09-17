/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station1agents;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

/**
 *
 * @author luiri60
 */
public class ProcessExecutionRequestPuncher extends AchieveREResponder {

    private PuncherAgent pa;

    public ProcessExecutionRequestPuncher(Agent a, MessageTemplate mt) {
        super(a, mt);
        this.pa = (PuncherAgent) a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        //This function should be implemented by the students
        ACLMessage reply = request.createReply();
        
        if (pa.isBusy()) {
            reply.setPerformative(ACLMessage.REFUSE);
        } else {
            reply.setPerformative(ACLMessage.AGREE);
            pa.setBusy(true);
        }
        return reply; //This line of code should be replace by the appropriate line
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        
        ACLMessage reply = request.createReply();
        String content = request.getContent();
        switch (content) {
            case "moveEtoP":
                if (pa.getPuncher().isPartAtPuncher()) {
                    pa.getPuncher().moveFromEntryToPuncher();
                    reply.setPerformative(ACLMessage.INFORM);
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("No part at entry to move");
                }
                break;
            case "eject":
                if (pa.getPuncher().isPartAtPuncher()) {
                    //pa.getPuncher().moveFromEntryToPuncher;
                    reply.setPerformative(ACLMessage.INFORM);
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("No part at puncher to eject");
                }
                break;
            default:
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("Unrecongnizable command");
        }
        pa.setBusy(false);
        return reply;
        
        //This function should be implemented by the students
        //return null; //This line of code should be replace by the appropriate line
    }

}
