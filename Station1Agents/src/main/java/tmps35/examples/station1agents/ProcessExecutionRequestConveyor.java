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
public class ProcessExecutionRequestConveyor extends AchieveREResponder {

    private ConveyorAgent ca;

    public ProcessExecutionRequestConveyor(Agent a, MessageTemplate mt) {
        super(a, mt);
        this.ca = (ConveyorAgent) a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        ACLMessage reply = request.createReply();
        if (ca.isBusy()) {
            reply.setPerformative(ACLMessage.REFUSE);
        } else {
            reply.setPerformative(ACLMessage.AGREE);
            ca.setBusy(true);
        }
        return reply;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        ACLMessage reply = request.createReply();
        String content = request.getContent();
        switch (content) {
            case "moveEtoP":
                if (ca.getConveyor().isPartAtBeginning()) {
                    ca.getConveyor().moveFromEntryToPuncher();
                    reply.setPerformative(ACLMessage.INFORM);
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("No part at entry to move");
                }
                break;
            case "eject":
                if (ca.getConveyor().isPartAtPuncher()) {
                    ca.getConveyor().ejectFromPuncher();
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
        ca.setBusy(false);
        return reply;
    }

}
