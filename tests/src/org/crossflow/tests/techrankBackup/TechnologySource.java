package org.crossflow.tests.techrankBackup;

import java.util.Timer;
import java.util.TimerTask;

public class TechnologySource extends TechnologySourceBase {
	
	@Override
	public void produce() throws Exception {
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
			
				// TODO: Add implementation that instantiates, sets, and submits source objects (example below)
				Technology technology1 = new Technology();
				//	technology1.setName( String );
				//	technology1.setKeyword( String );
				//	technology1.setExtension( String );
		
				sendToTechnologies( technology1);
				
				
			}
		}, 0, 100);
	}


}
