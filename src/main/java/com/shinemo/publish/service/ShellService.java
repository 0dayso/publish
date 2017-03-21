package com.shinemo.publish.service;

import io.netty.channel.Channel;

import com.shinemo.publish.common.Result;

public interface ShellService {

	public Result<String> execRemoteShell(String command, String username,
			String pwd, String host, int port);

	public void execForSock(Channel channel, String hostname,
			int port, String user, String pwd, String cmd) ;

	public Result<String> execLocalShell(String command);

	public Result<String> execLocalShell(String[] commands);
}
