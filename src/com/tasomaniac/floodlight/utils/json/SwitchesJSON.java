package com.tasomaniac.floodlight.utils.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tasomaniac.floodlight.model.Port;
import com.tasomaniac.floodlight.model.Switch;
import com.tasomaniac.floodlight.utils.Deserializer;
import com.tasomaniac.floodlight.utils.FormatLong;
import com.tasomaniac.floodlight.utils.JSONArray;
import com.tasomaniac.floodlight.utils.JSONException;
import com.tasomaniac.floodlight.utils.JSONObject;

public class SwitchesJSON {
	
	static JSONObject obj;

	// This parses JSON from the restAPI to get all the switches connected to the controller
	public static List<Switch> getSwitches(String IP) {

		// Create empty lists for all our data
		List<String> switchDpids = new ArrayList<String>();
		List<Switch> switches = new ArrayList<Switch>();
		List<Port> ports = new ArrayList<Port>();

		try {
			JSONArray json = Deserializer.readJsonArrayFromURL((IP.contains("http") ? IP : "http://" + IP
					+ ":8080") + "/wm/core/controller/switches/json");
			for (int i = 0; i < json.length(); i++) {
				obj = json.getJSONObject(i);
				switchDpids.add(obj.getString("dpid"));
			}
		} catch (IOException e) {
			System.out.println("Failed to read JSON from URL, controller may not be running.");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String dpid : switchDpids) {
			// Create the new switch object
			// Automatically grabs flows
			Switch sw = new Switch(dpid);
			try {
				sw.setFlows(FlowJSON.getFlows(IP, sw.getDpid()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ports = new ArrayList<Port>();

			// Get the vendor information for each switch and add it to the
			// summary
			try {
				obj = Deserializer
						.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
								+ ":8080") + "/wm/core/switch/" + sw.getDpid()
								+ "/desc/json");
				obj = obj.getJSONArray(sw.getDpid()).getJSONObject(0);
				sw.setManufacturerDescription(obj
						.getString("manufacturerDescription"));
				sw.setHardwareDescription(obj.getString("hardwareDescription"));
				sw.setSoftwareDescription(obj.getString("softwareDescription"));
				sw.setSerialNumber(obj.getString("serialNumber"));
				sw.setDatapathDescription(obj.getString("datapathDescription"));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Get the packets/bytes/flows information for each switch and add
			// it to
			// the summary
			try {
				obj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
						+ ":8080") + "/wm/core/switch/" + sw.getDpid()
						+ "/aggregate/json");
				obj = obj.getJSONArray(sw.getDpid()).getJSONObject(0);
				sw.setPacketCount(String.valueOf(obj.getInt("packetCount")));
				sw.setByteCount(String.valueOf(obj.getInt("byteCount")));
				sw.setFlowCount(String.valueOf(obj.getInt("flowCount")));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
			}

			// Get the port information for each switch and add it to the
			// summary
			try {
				obj = Deserializer
						.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
								+ ":8080") + "/wm/core/switch/" + sw.getDpid()
								+ "/port/json");
				
				JSONObject objtwo = Deserializer
						.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
								+ ":8080") + "/wm/core/switch/" + sw.getDpid()
								+ "/features/json");
				
				JSONArray json = obj.getJSONArray(sw.getDpid());
				objtwo = objtwo.getJSONObject(sw.getDpid());
				JSONArray jsontwo = objtwo.getJSONArray("ports");
				
				for(int i = 0; i < json.length(); i++){
					// Here we get json info using the port option
					obj = (JSONObject) json.get(i);
					Port port = new Port(String.valueOf(obj.getInt("portNumber")));
					port.setReceivePackets(String.valueOf(obj.getLong("receivePackets")));
					port.setTransmitPackets(String.valueOf(obj.getLong("transmitPackets")));
					port.setReceiveBytes(String.valueOf(obj.getLong("receiveBytes")));
					port.setTransmitBytes(String.valueOf(obj.getLong("transmitBytes")));
					port.setReceiveDropped(String.valueOf(obj.getLong("receiveDropped")));
					port.setTransmitDropped(String.valueOf(obj.getLong("transmitDropped")));
					port.setReceiveErrors(String.valueOf(obj.getLong("receiveErrors")));
					port.setTransmitErrors(String.valueOf(obj.getLong("transmitErrors")));
					port.setReceieveFrameErrors(String.valueOf(obj.getInt("receiveFrameErrors")));
					port.setReceieveOverrunErrors(String.valueOf(obj.getInt("receiveOverrunErrors")));
					port.setReceiveCRCErrors(String.valueOf(obj.getInt("receiveCRCErrors")));
					port.setCollisions(String.valueOf(obj.getInt("collisions")));
					// Here we get json info using the features option
					if(!jsontwo.isNull(i)){
					obj = (JSONObject) jsontwo.get(i);
						port.setAdvertisedFeatures(String.valueOf(obj.getInt("advertisedFeatures")));
						port.setConfig(String.valueOf(obj.getInt("config")));
						port.setCurrentFeatures(String.valueOf(obj.getInt("currentFeatures")));
						port.setHardwareAddress(obj.getString("hardwareAddress"));
						port.setName(obj.getString("name"));
						port.setPeerFeatures(String.valueOf(obj.getInt("peerFeatures")));
						port.setState(String.valueOf(obj.getInt("state")));
						port.setSupportedFeatures(String.valueOf(obj.getInt("supportedFeatures")));
					}
						// Add the port to the list...
						ports.add(port);
						}
				// Add the ports to the switch
				sw.setPorts(ports);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
			}
	
			// Add the switch full of information to the list
			switches.add(sw);
		}
		return switches;
		}
	
	// This parses JSON from the restAPI to get a specified switch by it's DPID, mainly used in scenarios to save computing time
	public static Switch getSwitch(String IP, String Dpid) {

		Switch sw = new Switch(Dpid);
		List<Port> ports = new ArrayList<Port>();

		try {
			sw.setFlows(FlowJSON.getFlows(IP, sw.getDpid()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
		}
		
			// Get the vendor information for each switch and add it to the
			// summary
			try {
				obj = Deserializer
						.readJsonObjectFromURL( (IP.contains("http") ? IP : "http://" + IP
								+ ":8080") + "/wm/core/switch/" + sw.getDpid()
								+ "/desc/json");
				obj = obj.getJSONArray(sw.getDpid()).getJSONObject(0);
				sw.setManufacturerDescription(obj
						.getString("manufacturerDescription"));
				sw.setHardwareDescription(obj.getString("hardwareDescription"));
				sw.setSoftwareDescription(obj.getString("softwareDescription"));
				sw.setSerialNumber(obj.getString("serialNumber"));
				sw.setDatapathDescription(obj.getString("datapathDescription"));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
			}

			// Get the packets/bytes/flows information for each switch and add
			// it to
			// the summary
			try {
				obj = Deserializer.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
						+ ":8080") + "/wm/core/switch/" + sw.getDpid()
						+ "/aggregate/json");
				obj = obj.getJSONArray(sw.getDpid()).getJSONObject(0);
				sw.setPacketCount(String.valueOf(obj.getInt("packetCount")));
				sw.setByteCount(String.valueOf(obj.getInt("byteCount")));
				sw.setFlowCount(String.valueOf(obj.getInt("flowCount")));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
			}

			// Get the port information for each switch and add it to the
			// summary
			try {
				obj = Deserializer
						.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
								+ ":8080") + "/wm/core/switch/" + sw.getDpid()
								+ "/port/json");
				
				JSONObject objtwo = Deserializer
						.readJsonObjectFromURL((IP.contains("http") ? IP : "http://" + IP
								+ ":8080") + "/wm/core/switch/" + sw.getDpid()
								+ "/features/json");
				
				JSONArray json = obj.getJSONArray(sw.getDpid());
				objtwo = objtwo.getJSONObject(sw.getDpid());
				JSONArray jsontwo = objtwo.getJSONArray("ports");
				
				for(int i = 0; i < json.length(); i++){
					// Here we get json info using the port option
					
					obj = (JSONObject) json.get(i);
					Port port = new Port(String.valueOf(obj.getInt("portNumber")));
					port.setReceivePackets(FormatLong.formatPackets(obj.getLong("receivePackets"), false,false));
					port.setTransmitPackets(FormatLong.formatPackets(obj.getLong("transmitPackets"), false, false));
					port.setReceiveBytes(FormatLong.formatBytes(obj.getLong("receiveBytes"), true, false));
					port.setTransmitBytes(FormatLong.formatBytes(obj.getLong("transmitBytes"), true,false));
					port.setReceiveDropped(String.valueOf(obj.getLong("receiveDropped")));
					port.setTransmitDropped(String.valueOf(obj.getLong("transmitDropped")));
					port.setReceiveErrors(String.valueOf(obj.getLong("receiveErrors")));
					port.setTransmitErrors(String.valueOf(obj.getLong("transmitErrors")));
					port.setReceieveFrameErrors(String.valueOf(obj.getInt("receiveFrameErrors")));
					port.setReceieveOverrunErrors(String.valueOf(obj.getInt("receiveOverrunErrors")));
					port.setReceiveCRCErrors(String.valueOf(obj.getInt("receiveCRCErrors")));
					port.setCollisions(String.valueOf(obj.getInt("collisions")));
					// Here we get json info using the features option
					if(!jsontwo.isNull(i)){
					obj = (JSONObject) jsontwo.get(i);
						port.setAdvertisedFeatures(String.valueOf(obj.getInt("advertisedFeatures")));
						port.setConfig(String.valueOf(obj.getInt("config")));
						port.setCurrentFeatures(String.valueOf(obj.getInt("currentFeatures")));
						port.setHardwareAddress(obj.getString("hardwareAddress"));
						port.setName(obj.getString("name"));
						port.setPeerFeatures(String.valueOf(obj.getInt("peerFeatures")));
						port.setState(String.valueOf(obj.getInt("state")));
						port.setSupportedFeatures(String.valueOf(obj.getInt("supportedFeatures")));
					}
						// Add the port to the list...
						ports.add(port);
						}
				// Add the ports to the switch
				sw.setPorts(ports);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
			}

		return sw;
		}
	
	}
