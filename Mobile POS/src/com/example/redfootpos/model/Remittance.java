package com.example.redfootpos.model;

public class Remittance {
	public String invno;
	public String custname;
	public String invamt;
	public String takenby;
	public boolean isselect;
	
	public String _remit;
	
	public Remittance(){
		this.invno = "";
		this.custname = "";
		this.invamt = "";
		this.takenby = "";
		this.isselect = false;
		
		this._remit = "";
	}
}
