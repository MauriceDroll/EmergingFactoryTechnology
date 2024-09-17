/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station1agents;

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

    private Station1Orchestrator orch;

    public ProcessExecutionRequestOrchestrator(Agent a, MessageTemplate mt) {
        super(a, mt);
        this.orch = (Station1Orchestrator) a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        ACLMessage reply = request.createReply();
        if (orch.isBusy()) {
            reply.setPerformative(ACLMessage.REFUSE);
        } else if (request.getContent().equalsIgnoreCase("execute")) {
            reply.setPerformative(ACLMessage.AGREE);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID("GateST1", false));
            msg.setContent("start");
            Behaviour startMeasuringBehaviour = new AchieveREInitiator(myAgent, msg);

            ACLMessage msg1 = new ACLMessage(ACLMessage.REQUEST);
            msg1.addReceiver(new AID("ConveyorST1", false));
            msg1.setContent("moveEtoP");
            Behaviour moveToPuncherBehaviour = new AchieveREInitiator(myAgent, msg1);
            
            ACLMessage msg3 = new ACLMessage(ACLMessage.REQUEST);
            msg3.addReceiver(new AID("PuncherST1", false));
            OrchestrationPuncherBehaviour punchBehaviour = new OrchestrationPuncherBehaviour(myAgent, msg3);

            ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
            msg2.addReceiver(new AID("GateST1", false));
            msg2.setContent("stop");
            Behaviour stopMeasuringBehaviour = new AchieveREInitiator(myAgent, msg2) {
                @Override
                protected void handleInform(ACLMessage inform) {
                    System.out.println("Gate stop activated!");
                    System.out.println("Dots counted " + inform.getContent());
                    punchBehaviour.getMsg().setContent(inform.getContent());
                }
            };           
            
            ACLMessage msg4 = new ACLMessage(ACLMessage.REQUEST);
            msg4.addReceiver(new AID("ConveyorST1", false));
            msg4.setContent("eject");
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

            SequentialBehaviour seq = new SequentialBehaviour();
            seq.addSubBehaviour(startMeasuringBehaviour);
            seq.addSubBehaviour(moveToPuncherBehaviour);
            seq.addSubBehaviour(stopMeasuringBehaviour);
            seq.addSubBehaviour(punchBehaviour);
            seq.addSubBehaviour(moveToEndBehaviour);

            seq.setDataStore(this.getDataStore());
            registerPrepareResultNotification(seq);
            orch.setBusy(true);
        }
        return reply;
    }

    private class OrchestrationPuncherBehaviour extends AchieveREInitiator {
        
        private ACLMessage msg;

        public OrchestrationPuncherBehaviour(Agent a, ACLMessage msg) {
            super(a, msg);
            this.msg = msg;          
        }   

        public ACLMessage getMsg() {
            return msg;
        }
     
    }

}
