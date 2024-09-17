/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station2agents;

import com.mcl.automationlab.libraries.highlevel.station2.Station;
import com.mcl.automationlab.libraries.highlevel.station2.Station2System;
import jade.core.Agent;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author luiri60
 */
public class MachineAgent  extends Agent {
    
    private Station machine;
    private ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();
    private boolean busy = false;

    public boolean isBusy() {
        synchronized (this) {
            return busy;
        }
    }

    public void setBusy(boolean busy) {
        synchronized (this) {
            this.busy = busy;
        }
    }

    public Station getMachine() {
        return machine;
    }
        
    @Override
    protected void takeDown() {
        System.out.println("Agent " + this.getLocalName() + " taken down!");
    }

    @Override
    protected void setup() {
        Station2System system = Station2System.getInstance();
        machine = system.getStation();
        addBehaviour(tbf.wrap(new ProcessExecutionRequestMachine(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
        addBehaviour(tbf.wrap(new ProcessExecutionRequestMachine(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));                
        System.out.println("Agent " + this.getLocalName() + " setup completed and operating!");
    }

    
}
