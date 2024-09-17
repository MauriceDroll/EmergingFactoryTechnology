/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station1agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 *
 * @author luiri60
 */
public class TestGateAgent extends Agent {
    
    private int count = 0;

    @Override
    protected void takeDown() {
        super.takeDown(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setup() {

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID("GateST1", false));
        msg.setContent("start");
        AchieveREInitiator startBehaviour = new AchieveREInitiator(this, msg) {
            @Override
            protected void handleInform(ACLMessage inform) {
                System.out.println("Gate start activated!");
            }

            @Override
            protected void handleRefuse(ACLMessage refuse) {
                 System.out.println("Gate start refused!");
            }

            @Override
            protected void handleAgree(ACLMessage agree) {
                System.out.println("Gate start agreed!");
            }

            @Override
            protected void handleFailure(ACLMessage failure) {
                System.out.println("Gate start failed!");
            }
                           
        };
        
        ACLMessage msg1 = new ACLMessage(ACLMessage.REQUEST);
        msg1.addReceiver(new AID("GateST1", false));
        msg1.setContent("stop");
        AchieveREInitiator stopBehaviour = new AchieveREInitiator(this, msg1) {
            @Override
            protected void handleInform(ACLMessage inform) {
                System.out.println("Gate stop activated!");
                System.out.println("Dots counted " + inform.getContent());
                count = Integer.parseInt(inform.getContent());
            }

            @Override
            protected void handleRefuse(ACLMessage refuse) {
                 System.out.println("Gate stop refused!");
            }

            @Override
            protected void handleAgree(ACLMessage agree) {
                System.out.println("Gate stop agreed!");
            }

            @Override
            protected void handleFailure(ACLMessage failure) {
                System.out.println("Gate start failed!");
            }
                           
        };

        addBehaviour(new WakerBehaviour(this, 1000) {
            @Override
            protected void onWake() {
                addBehaviour(startBehaviour);
                System.out.println("Measuring");                
            }                        
        });
        
        addBehaviour(new WakerBehaviour(this, 6000) {
            @Override
            protected void onWake() {
                addBehaviour(stopBehaviour);
                System.out.println("Measuring Finished " + count);  
            }                        
        });               
    }
}