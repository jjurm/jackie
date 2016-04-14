package net.talentum.jackie.comm;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import net.talentum.jackie.system.Config;

public class ButtonManager implements Runnable{

	private AtomicBoolean running = new AtomicBoolean(false);
	private Thread thread;
	
	private Commander commander;
	
	private TextInputProcessor textInputProcessor;
	
	private String[] commands;
	
	private boolean[] buttonStates;
	
	public ButtonManager(Commander commander, TextInputProcessor textInputProcessor) {
		thread = new Thread("buttonManagerThread");
		
		this.commander = commander;
		
		this.textInputProcessor = textInputProcessor;
		
		commands = new String[]{Config.get().getString("params/buttons/b1"), Config.get().getString("params/buttons/b1"), Config.get().getString("params/buttons/b1")};
		
		buttonStates = commander.readMultipleButtons(2);
		}
	
	public void start() {
		if(running.get()) 
			return;
		else
			running.set(true);
			thread.start();
		
	}
	
	public void stop() {
		if(!running.get()) 
			return;
		else {
			running.set(false);
			commander.writeLED(4, false);
			thread.interrupt();
		}
	}
	
	public void run() {
		while(running.get()) {
			try {
				Thread.sleep(50);
				commander.writeLED(4, false);
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			evaluate(commander.readMultipleButtons(2));
		}
	}

	private void evaluate(boolean[] buttons) {
		for(int i = 0; i < 3; i++) {
			if(buttons[i] && !buttonStates[i])
				try {
					textInputProcessor.performCommand(commands[i], null, new PrintWriter(System.out));
					commander.writeLED(4, true);
				} catch (StreamCloseRequest e) {
					e.printStackTrace();
				} 
		}
		buttonStates = buttons;
	}
}
