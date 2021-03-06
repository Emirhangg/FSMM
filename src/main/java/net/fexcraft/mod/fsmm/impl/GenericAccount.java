package net.fexcraft.mod.fsmm.impl;

import java.util.UUID;

import com.google.gson.JsonObject;

import net.fexcraft.mod.fsmm.api.Account;
import net.fexcraft.mod.fsmm.util.Config;
import net.fexcraft.mod.lib.util.common.Print;
import net.fexcraft.mod.lib.util.json.JsonUtil;
import net.minecraft.command.ICommandSender;

public class GenericAccount implements Account {
	
	private String type, id;
	private long balance;
	private UUID bank;
	private JsonObject data;
	
	public GenericAccount(String type, JsonObject obj){
		this.type = type;
		this.id = JsonUtil.getIfExists(obj, "id", obj.get("id").getAsString());
		this.balance = JsonUtil.getIfExists(obj, "balance", 0).longValue();
		this.bank = UUID.fromString(JsonUtil.getIfExists(obj, "bank", Config.DEFAULT_BANK));
		this.data = JsonUtil.getIfExists(obj, "data", new JsonObject()).getAsJsonObject();
	}

	@Override
	public String getId(){
		return id;
	}

	@Override
	public long getBalance(){
		return balance;
	}

	@Override
	public boolean modifyBalance(String action, long amount, ICommandSender sender){
		switch(action){
			case "set":{
				balance = amount;
				return true;
			}
			case "sub":{
				if(balance - amount >= 0){
					balance -= amount;
					return true;
				}
				else{
					Print.chat(sender, "Not enough money to subtract this amount! (B:" + Config.getWorthAsString(balance, false) + " - S:" + Config.getWorthAsString(amount, false) + ")");
					return false;
				}
			}
			case "add":{
				if(balance + amount >= Long.MAX_VALUE){
					Print.chat(sender, "Max Value reached.");
					return false;
				}
				else{
					balance += amount;
					return true;
				}
			}
			default: return false;
		}
	}
	
	/** Extend this class and Override this method to modify access to the account, to for example grand access to specific other accounts/players. */
	@Override
	public boolean canModifyBalance(String action, String type, String id){
		return action.equals("add") ? true : this.getType().equals(type) && this.getId().equals(id);
	}

	@Override
	public UUID getBankId(){
		return bank;
	}

	@Override
	public boolean setBankId(UUID uuid){
		return (bank = uuid) != null;
	}

	@Override
	public String getType(){
		return type;
	}

	@Override
	public JsonObject getData(){
		return data;
	}

	@Override
	public void setData(JsonObject obj){
		data = obj;
	}

}
