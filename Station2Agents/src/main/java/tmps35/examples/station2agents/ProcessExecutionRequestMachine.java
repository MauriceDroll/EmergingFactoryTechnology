/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station2agents;

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
public class ProcessExecutionRequestMachine extends AchieveREResponder {

    private MachineAgent ma;

    public ProcessExecutionRequestMachine(Agent a, MessageTemplate mt) {
        super(a, mt);
        this.ma = (MachineAgent) a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        ACLMessage reply = request.createReply();
        if (ma.isBusy()) {
            reply.setPerformative(ACLMessage.REFUSE);
        } else {
            reply.setPerformative(ACLMessage.AGREE);
            ma.setBusy(true);
        }
        return reply;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        ACLMessage reply = request.createReply();
        String content = request.getContent();
        int time = Integer.parseInt(content);
        if (ma.getMachine().isPartAtStation()) {
            ma.getMachine().producePart(time);
            reply.setPerformative(ACLMessage.INFORM);
        } else {
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent("No part at machine to work on!");
        }
        
        ma.setBusy(false);
        return reply;
    }

}
