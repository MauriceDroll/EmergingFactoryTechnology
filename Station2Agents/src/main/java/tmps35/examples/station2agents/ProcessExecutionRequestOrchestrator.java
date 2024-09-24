/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station2agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

/**
 *
 * @author luiri60
 */
public class ProcessExecutionRequestOrchestrator extends AchieveREResponder {

    private Station2Orchestrator orch;

    public ProcessExecutionRequestOrchestrator(Agent a, MessageTemplate mt) {
        super(a, mt);
        this.orch = (Station2Orchestrator) a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        // First create the reply
        ACLMessage reply = request.createReply();
        
        // Check if busy
        if (orch.isBusy()) {
            reply.setPerformative(ACLMessage.REFUSE);
            
        // Control the things to do
        // Move Conveyor 2 and move to Station
        } else if (request.getContent().equalsIgnoreCase("execute")) {
            reply.setPerformative(ACLMessage.AGREE);
            
            // Move Conveyor 2 and move to Station
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID("ConveyorST2", false));
            msg.setContent("moveStoS");
            Behaviour moveToStation = new AchieveREInitiator(myAgent, msg);
            
            // Move the Maschine 2
            ACLMessage msg_2 = new ACLMessage(ACLMessage.REQUEST);
            msg_2.addReceiver(new AID("MachineST2", false));
            msg_2.setContent("3000");
            Behaviour machineBehavior = new AchieveREInitiator(myAgent, msg_2);
            
            
            // Move the Maschine to FB2
            ACLMessage msg4 = new ACLMessage(ACLMessage.REQUEST);
            msg4.addReceiver(new AID("ConveyorST2", false));
            msg4.setContent("MoveToFB");
            Behaviour moveToEndBehaviour = new AchieveREInitiator(myAgent, msg4) {
                @Override
                protected void handleInform(ACLMessage inform) {
                    ACLMessage request = (ACLMessage) getDataStore().get(REQUEST_KEY);
                    ACLMessage reply = request.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    getParent().getDataStore().put(RESPONSE_KEY, reply);
                    orch.setBusy(false);
                }

                @Override
                protected void handleRefuse(ACLMessage refuse) {
                    ACLMessage request = (ACLMessage) getDataStore().get(REQUEST_KEY);
                    ACLMessage reply = request.createReply();
                    reply.setPerformative(ACLMessage.FAILURE);
                    getParent().getDataStore().put(RESPONSE_KEY, reply);
                    orch.setBusy(false);
                }

                @Override
                protected void handleFailure(ACLMessage failure) {
                    ACLMessage request = (ACLMessage) getDataStore().get(REQUEST_KEY);
                    ACLMessage reply = request.createReply();
                    reply.setPerformative(ACLMessage.FAILURE);
                    getDataStore().put(RESPONSE_KEY, reply);
                    orch.setBusy(false);
                }

            };
            
            // Add the behaviors
            SequentialBehaviour seq = new SequentialBehaviour();
            seq.addSubBehaviour(moveToStation);
            seq.addSubBehaviour(machineBehavior);
            seq.addSubBehaviour(moveToEndBehaviour);
            
            seq.setDataStore(this.getDataStore());
            registerPrepareResultNotification(seq);
            orch.setBusy(true);
            

        }
        return reply; //This line of code should be replace by the appropriate line
    }

}
