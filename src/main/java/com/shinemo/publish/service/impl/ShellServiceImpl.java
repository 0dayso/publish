package com.shinemo.publish.service.impl;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.shinemo.publish.common.Result;
import com.shinemo.publish.service.ShellService;
import com.shinemo.publish.utils.ServerConfig;

@Service("shellService")
public class ShellServiceImpl implements ShellService {

	private static ExecutorService executorService = new ThreadPoolExecutor(5,
			10, 20, TimeUnit.MINUTES, new LinkedBlockingQueue(10));

	private Logger logger = LoggerFactory.getLogger(ShellServiceImpl.class);

	public Result<String> execRemoteShell(String command, String username,
			String pwd, String host, int port) {
		Result<String> result = new Result<String>();
		try {
			Connection conn = new Connection(host, port);
			conn.connect();

			File pemKeyFile = new File(ServerConfig.get("ssh.pubkey",
					"/home/shinemo-safe/.ssh/id_rsa"));
			boolean isAuth = conn.authenticateWithPublicKey(username,
					pemKeyFile, pwd);
			if (isAuth == false) {
				logger.error("Authentication failed.");
				result.setSuccess(false).setMsg("auth failed");
				return result;
			}

			Session sess = conn.openSession();
			sess.execCommand(command);
			StringBuffer sb = new StringBuffer();

			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stdout));

			InputStream stderr = new StreamGobbler(sess.getStderr());
			BufferedReader stderrReader = new BufferedReader(
					new InputStreamReader(stderr));

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}

			while (true) {
				line = stderrReader.readLine();
				if (line == null)
					break;
				sb.append(line).append("\n");
			}

			/* Show exit status, if available (otherwise "null") */
			logger.error("ExitCode: " + sess.getExitStatus() + " " + host + ":"
					+ command);
			sess.close();
			conn.close();
			stderrReader.close();
			br.close();
			result.setSuccess(true);
			result.setValue(command+"\n"+sb.toString());
			return result;
		} catch (Exception e) {
			result.setSuccess(false).setMsg(command+"\n"+e.getMessage());
			result.setValue(command+"\n"+e.getMessage());
			e.printStackTrace();
			// System.exit(2);
			return result;
		}
	}

	public void execForSock(final Channel channel, String hostname, int port,
			String user, String pwd, String cmd) {
		StringBuilder sb = new StringBuilder();
		Connection conn = null;
		Session session = null;
		try {
			conn = new Connection(hostname, port);
			conn.connect(); // 连接
			File pemKeyFile = new File(ServerConfig.get("ssh.pubkey",
					"/home/shinemo-safe/.ssh/id_rsa"));
			boolean isAuth = conn.authenticateWithPublicKey(user, pemKeyFile,
					pwd);
			if (isAuth) {
				session = conn.openSession(); // 打开一个会话
				session.execCommand(cmd);
				final InputStream stderr = session.getStderr();

				final StringBuilder sb2 = new StringBuilder();
				final InputStream stdout = session.getStdout();
				executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                    	processStdStream(stderr, channel, sb2);
                    }
                });
				
//				executorService.submit(new Runnable() {
//                    @Override
//                    public void run() {
//                    	processStdStream(stdout, channel, sb2);
//                    }
//                });
				processStdStream(stdout, channel, sb2);
			}else{
				channel.writeAndFlush(new TextWebSocketFrame(hostname+" auth failed!"));
			}
		} catch (Exception ex) {
			logger.error("exec fail,sshConfig:{},cmd:{}", cmd, ex);
		} finally {
			if (null != session)
				session.close();
			if (null != conn)
				conn.close();
		}
	}

	public Result<String> execLocalShell(String command) {
		Result<String> result = new Result<String>();
		Process process;
		try {
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec(command);
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			result.setValue(command+"\n"+sb.toString());
			result.setSuccess(true);
			is.close();
			isr.close();
			br.close();
		} catch (Exception e) {
			logger.error("exec shell " + command + " error!.", e);
			result.setMsg(command+"\n脚本执行出错:"+e.getMessage());
			result.setValue(command+"\n脚本执行出错:"+e.getMessage());
			result.setSuccess(false);
		}
		return result;
	}

	public Result<String> execLocalShell(String[] commands) {
		Result<String> result = new Result<String>();
		Process process;
		try {
			String[] initcmd = { "/bin/sh", "-c" };

//			List<String> tmp = Arrays.asList(initcmd);
//			tmp.addAll(Arrays.asList(commands));
//			String[] cmds = (String[]) tmp.toArray();

			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec(commands);
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			result.setValue(sb.toString());
			result.setSuccess(true);
			is.close();
			isr.close();
			br.close();
		} catch (Exception e) {
			logger.error(
					"exec shell " + Arrays.toString(commands) + " error!.", e);
			result.setMsg("exec shell " + Arrays.toString(commands)
					+ " error!." + e.getMessage());
			result.setSuccess(false);
		}
		return result;
	}

	private void processStdStream(InputStream in, final Channel channel,
			StringBuilder sb) {

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				
				final String linetmp = line;
				channel.eventLoop().execute(new Runnable() {
					@Override
					public void run() {
						channel.writeAndFlush(new TextWebSocketFrame(linetmp));
					}
				});
				
			}
		} catch (Exception e) {
			logger.error("processStderr err", e);
		} finally {
			try {
				in.close();
			} catch (Exception ex) {
			}
		}
	}


}
