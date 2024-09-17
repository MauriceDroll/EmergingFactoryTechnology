/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station2agents;

import com.mcl.automationlab.libraries.highlevel.station2.Conveyor;
import com.mcl.automationlab.libraries.highlevel.station2.Station2System;
import jade.core.Agent;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author luiri60
 */
public class ConveyorAgent extends Agent {

    private Conveyor conveyor;
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

    public Conveyor getConveyor() {
        return conveyor;
    }
        
    @Override
    protected void takeDown() {
        System.out.println("Agent " + this.getLocalName() + " taken down!");
    }

    @Override
    protected void setup() {
        Station2System system = Station2System.getInstance();
        conveyor = system.getConveyor();
        addBehaviour(tbf.wrap(new ProcessExecutionRequestConveyor(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
        addBehaviour(tbf.wrap(new ProcessExecutionRequestConveyor(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));                
        System.out.println("Agent " + this.getLocalName() + " setup completed and operating!");
    }

}