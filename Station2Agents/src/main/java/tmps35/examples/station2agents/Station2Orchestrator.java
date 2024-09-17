/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tmps35.examples.station2agents;

import jade.core.Agent;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author luiri60
 */
public class Station2Orchestrator extends Agent {

    private ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();
    private boolean busy = false;
    private int count = 0;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }        

    @Override
    protected void takeDown() {
        System.out.println("Agent " + this.getLocalName() + " taken down!");
    }

    @Override
    protected void setup() {
        addBehaviour(tbf.wrap(new ProcessExecutionRequestOrchestrator(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
        addBehaviour(tbf.wrap(new ProcessExecutionRequestOrchestrator(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
        System.out.println("Agent " + this.getLocalName() + " setup completed and operating!");
    }

}