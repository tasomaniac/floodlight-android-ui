package com.tasomaniac.floodlight.utils.flowmanager;

import com.tasomaniac.floodlight.model.Action;

public class ActionToTable {

	// Gets an action by it's index and formats it so that it can be displayed
	// in a table
	public static String[][] getActionTableFormat(Action action) {

		if (action.getType().equals("strip-vlan")) {
			String[][] act = { { "Type", action.getType() } };
			return act;
		} else if (action.getType().equals("enqueue")) {
			StringBuilder param = new StringBuilder(action.getParam());
			StringBuilder value = new StringBuilder(action.getValue());
			String[][] act = {
					{ param.substring(0, param.indexOf(":")),
							value.substring(0, value.indexOf(":")) },
					{
							param.substring(param.indexOf(":") + 1,
									param.length()),
							value.substring(value.indexOf(":") + 1,
									value.length()) },
					{ "Type", action.getType() } };
			return act;
		} else {
			String[][] act = { { action.getParam(), action.getValue() },
					{ "Type", action.getType() } };
			return act;
		}
	}


	// Checks the values for valid entries, also checks if port entries are
	// valid
//	public static boolean errorChecksPassed(Switch sw, String currAction,
//			TableItem[] items) {
//
//		List<Port> ports = sw.getPorts();
//		boolean checkPorts = false;
//
//		if (currAction.equals("output") || currAction.equals("set-vlan-id")
//				|| currAction.equals("set-vlan-priority")
//				|| currAction.equals("set-tos-bits")
//				|| currAction.equals("set-src-port")
//				|| currAction.equals("set-dst-port")) {
//			checkPorts = true;
//			if (!ErrorCheck.isNumeric(items[0].getText(1))) {
//				ActionManager
//						.displayError("The value number must be an integer. Please check your entry.");
//				return false;
//			}
//		}
//
//		if (currAction.equals("enqueue")) {
//			if (!ErrorCheck.isNumeric(items[0].getText(1))
//					|| !ErrorCheck.isNumeric(items[1].getText(1))) {
//				ActionManager.displayError("an integer");
//				return false;
//			}
//		}
//
//		if (currAction.equals("set-src-mac")
//				|| currAction.equals("set-dst-mac")) {
//			if (!ErrorCheck.isMac(items[0].getText(1))) {
//				ActionManager
//						.displayError("The value number must be a proper MAC address. Please check your entry.");
//				return false;
//			}
//		}
//
//		if (currAction.equals("set-src-ip") || currAction.equals("set-dst-ip")) {
//			if (!ErrorCheck.isIP(items[0].getText(1))) {
//				ActionManager
//						.displayError("The value number must be a proper IP address. Please check your entry.");
//				return false;
//			}
//		}
//
//		if (checkPorts) {
//			for (Port port : ports) {
//				if (items[0].getText(1).equals(port.getPortNumber())) {
//					return true;
//				}
//			}
//
//			ActionManager
//					.displayError("That port does not exist on the switch!");
//			return false;
//		}
//
//		return true;
//	}
}