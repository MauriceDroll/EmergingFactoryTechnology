/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station1agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 *
 * @author luiri60
 */
public class TestOrchestrator extends Agent {
    
     @Override
    protected void takeDown() {
        super.takeDown(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setup() {
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID("OrchestratorST2", false));
                msg.setContent("execute");
                addBehaviour(new AchieveREInitiator(myAgent, msg){
                    @Override
                    protected void handleInform(ACLMessage inform) {
                        System.out.println("INFORM");
                    }

                    @Override
                    protected void handleRefuse(ACLMessage refuse) {
                        System.out.println("REFUSE");
                    }

                    @Override
                    protected void handleAgree(ACLMessage agree) {
                        System.out.println("AGREE");
                    }

                    @Override
                    protected void handleFailure(ACLMessage failure) {
                        System.out.println("FAILURE");
                        System.out.println(failure.getContent());
                    }
                                                            
                });
            }
        });
    }
    
}
