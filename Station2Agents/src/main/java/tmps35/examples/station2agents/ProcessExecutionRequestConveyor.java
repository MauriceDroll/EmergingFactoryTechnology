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
public class ProcessExecutionRequestConveyor extends AchieveREResponder {

    private ConveyorAgent ca;

    public ProcessExecutionRequestConveyor(Agent a, MessageTemplate mt) {
        super(a, mt);
        this.ca = (ConveyorAgent) a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
       //This function should be implemented by the students
        ACLMessage reply = request.createReply();
        
        // Check if Conveyor agent is busy
        // If not agree to action and set it to busy
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
         //This function should be implemented by the students
        ACLMessage reply = request.createReply();
        String content = request.getContent();
        
        switch (content) {
            // move from Stacker (Eingangsobjekt) to Station 
            case "moveStoS":
                ca.getConveyor().moveFromStackerToStation();
                reply.setPerformative(ACLMessage.INFORM);
                break;
            
            // Move the Objekt to the final Buffer 
            case "MoveToFB":
                if (ca.getConveyor().isPartAtStation()) {
                    ca.getConveyor().moveFromStationToFinalBuffer();
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
