package com.tasomaniac.floodlight.utils.flowmanager;

import com.tasomaniac.floodlight.model.Flow;

public class FlowToTable {

	// This returns a table representation of the specified flow
	public static String[][] getFlowTableFormat(Flow flow) {

		String[][] f = { { "Name", flow.getName() }, { "Actions", "..." },
				{ "Match", "..." },
				{ "Priority", flow.getPriority() },
				{ "Cookie", flow.getCookie() },
				{ "Idle Timeout", flow.getIdleTimeOut() },
				{ "Hard Timeout", flow.getHardTimeOut() },
				{ "Out Port", flow.getOutPort() } };
		return f;
	}

	// This returns a table representation of a new flow
	public static String[][] getNewFlowTableFormat() {

		String[][] f = { { "Name", }, { "Actions", "..." }, { "Match", "..." },
				{ "Priority" },
				// {"BufferID", String.valueOf(obj.getInt("bufferId"))},
				{ "Cookie" }, { "Idle Timeout" }, { "Hard Timeout" },
				// {"Flags", String.valueOf(obj.getInt("flags"))},
				// {"Command", String.valueOf(obj.getInt("command"))},
				{ "Out Port" } };
		// {"Type", obj.getString("type")},
		// {"Version", String.valueOf(obj.getInt("version"))},
		// {"X ID", String.valueOf(obj.getInt("xid"))}};

		return f;
	}
	
		// Checks the entries for valid values
//		public static boolean errorChecksPassed(TableItem[] items){
//				
//					if(!ErrorCheck.isNumeric(items[3].getText(1)) && !(items[3].getText(1).isEmpty())){
//						System.out.println(ErrorCheck.isNumeric(items[3].getText(1)));
//						StaticFlowManager.displayError("Priority must be a valid number.");
//						return false;
//					}
//				
//					if(!ErrorCheck.isNumeric(items[4].getText(1)) && !(items[4].getText(1).isEmpty())){
//						StaticFlowManager.displayError("Cookie must be a valid number.");
//						return false;
//					}
//					
//					if(!ErrorCheck.isNumeric(items[5].getText(1)) && !(items[5].getText(1).isEmpty())){
//						StaticFlowManager.displayError("Idle Timeout must be a valid number.");
//						return false;
//					}
//				
//					if(!ErrorCheck.isNumeric(items[6].getText(1)) && !(items[6].getText(1).isEmpty())){
//						StaticFlowManager.displayError("Hard Timeout must be a valid number.");
//						return false;
//					}
//					
//					if(!ErrorCheck.isNumeric(items[7].getText(1)) && !(items[7].getText(1).isEmpty())){
//						StaticFlowManager.displayError("Out Port must be a valid number.");
//						return false;
//					}
//				
//				return true;
//			}

}
