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
public class ProcessExecutionRequestGate extends AchieveREResponder {
    
    private GateAgent ga;

    public ProcessExecutionRequestGate(Agent a, MessageTemplate mt) {
        super(a, mt);
        this.ga = (GateAgent) a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        ACLMessage reply = request.createReply();
        String content = request.getContent();
        if (ga.isBusy() && !content.equalsIgnoreCase("stop")) {
            reply.setPerformative(ACLMessage.REFUSE);
        } else {
            reply.setPerformative(ACLMessage.AGREE);           
            ga.setBusy(true);
        }
        return reply;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        ACLMessage reply = request.createReply();
        String content = request.getContent();
        switch (content) {
            case "start":
                ga.getMeasuringStation().startCount();
                ga.setBusy(true);
                reply.setPerformative(ACLMessage.INFORM);
                break;
            case "stop":
                int count = ga.getMeasuringStation().stopCount();                              
                reply.setContent(String.valueOf(count));
                reply.setPerformative(ACLMessage.INFORM);
                ga.setBusy(false);
                break;
            default:
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("Unrecongnizable command");
        }       
        return reply;
    }
    
}
