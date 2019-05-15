package server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import server.model.CommandType;
import server.model.JSonDoc;
import server.model.Communication;

public class ServerController {
	private HashMap<String, Communication> allMember;
	
	public ServerController() {
		this.allMember = new HashMap<>();
	}

	public void processInput(String receive) {

				
	}
	
	public Set<String> getMemberList(){
		return allMember.keySet();
	}

	public void sendMessage(String receive) {
		for(Communication rec : allMember.values()) {
			rec.sendMessage(receive);
		}
		
	}

	public void unregister(String userName) {
		if(allMember.containsKey(userName)) {
			Communication comm = allMember.get(userName);
			allMember.remove(userName);
			comm.stop();
			JSonDoc doc = new JSonDoc();
			doc.append("command", CommandType.REMOVE_MEMBER.toString());
			doc.append("username", userName);
			for(Communication co : allMember.values()) {
				co.sendMessage(doc.toJson());
			}
		}
		
	}

	public boolean register(String userName, Communication comm) {
		if(allMember.keySet().contains(userName)) {
			return false;
		}else {
			JSonDoc doc = new JSonDoc();
			doc.append("command", CommandType.ADD_MEMBER.toString());
			doc.append("username", userName);
			for(Communication co : allMember.values()) {
				co.sendMessage(doc.toJson());
			}
			allMember.put(userName, comm);
			return true;
		}

		
	}



	
}
