package com.golic.wycj.model;

public class User
{
	public String id;
	public String userName;
	public String password;
	public String xm;
	public String zrq;

	@Override
	public String toString()
	{
		return "User [id=" + id + ", userName=" + userName + ", password="
				+ password + ", xm=" + xm + ", zrq=" + zrq + "]";
	}
}