package org.bhg.openfire.chat.logs;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

public class ChatLogsPlugin implements PacketInterceptor, Plugin {

	private static final Logger log = LoggerFactory
			.getLogger(ChatLogsPlugin.class);

	private static PluginManager pluginManager;
	private InterceptorManager interceptorManager;

	public ChatLogsPlugin() {
		interceptorManager = InterceptorManager.getInstance();
	}


	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		if (session != null) {
			debug(packet, incoming, processed, session);
		}

		JID recipient = packet.getTo();
		if (recipient != null) {
			String username = recipient.getNode();
			// �㲥��Ϣ���ǲ�����/ûע����û�.
			if (username == null
					|| !UserManager.getInstance().isRegisteredUser(recipient)) {
				return;
			} else if (!XMPPServer.getInstance().getServerInfo()
					.getXMPPDomain().equals(recipient.getDomain())) {
				// �ǵ�ǰopenfire��������Ϣ
				return;
			} else if ("".equals(recipient.getResource())) {
			}
		}
		this.doAction(packet, incoming, processed, session);
	}

	/**
	 * <b>function:</b> ִ�б���/���������¼����
	 * 
	 * @author 
	 * @createDate 2013-3-24 ����12:20:56
	 * @param packet
	 *            ���ݰ�
	 * @param incoming
	 *            true��ʾ���ͷ�
	 * @param session
	 *            ��ǰ�û�session
	 */
	private void doAction(Packet packet, boolean incoming, boolean processed,
			Session session) {
		Packet copyPacket = packet.createCopy();
		if (packet instanceof Message) {
			Message message = (Message) copyPacket;

			// һ��һ���죬����ģʽ
			if (message.getType() == Message.Type.chat) {
				log.info("����������Ϣ��{}", message.toXML());
				debug("����������Ϣ��" + message.toXML());

				// ����ִ���У��Ƿ�Ϊ�����򷵻�״̬���Ƿ��ǵ�ǰsession�û�������Ϣ��
				if (processed || !incoming) {
					return;
				}
				//System.out.println(this.get(copyPacket, incoming, session));
				//logsManager.add(this.get(packet, incoming, session));
				ChatRedis.push(this.get(copyPacket, incoming, session));

				// Ⱥ���죬����ģʽ
			} else if (message.getType() == Message.Type.groupchat) {
				List<?> els = message.getElement().elements("x");
				if (els != null && !els.isEmpty()) {
					log.info("Ⱥ������Ϣ��{}", message.toXML());
					debug("Ⱥ������Ϣ��" + message.toXML());
				} else {
					log.info("Ⱥϵͳ��Ϣ��{}", message.toXML());
					debug("Ⱥϵͳ��Ϣ��" + message.toXML());
				}
				// ������Ϣ
			} else {
				log.info("������Ϣ��{}", message.toXML());
				debug("������Ϣ��" + message.toXML());
			}
		} else if (packet instanceof IQ) {
			IQ iq = (IQ) copyPacket;
			if (iq.getType() == IQ.Type.set && iq.getChildElement() != null
					&& "session".equals(iq.getChildElement().getName())) {
				log.info("�û���¼�ɹ���{}", iq.toXML());
				debug("�û���¼�ɹ���" + iq.toXML());
			}
		} else if (packet instanceof Presence) {
			Presence presence = (Presence) copyPacket;
			if (presence.getType() == Presence.Type.unavailable) {
				log.info("�û��˳��������ɹ���{}", presence.toXML());
				debug("�û��˳��������ɹ���" + presence.toXML());
			}
		}
	}

	/**
	 * <b>function:</b> ����һ�������¼ʵ����󣬲������������
	 * 
	 * @author hoojo
	 * @createDate 2013-3-27 ����04:44:54
	 * @param packet
	 *            ���ݰ�
	 * @param incoming
	 *            ���Ϊture�ͱ����Ƿ�����
	 * @param session
	 *            ��ǰ�û�session
	 * @return ����ʵ��
	 */
	private String get(Packet packet, boolean incoming, Session session) {
		Message message = (Message) packet;
		//ChatLogs logs = new ChatLogs();
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append("jnd:'").append(session.getAddress()).append("'");
		JID jid = session.getAddress();
		if (incoming) { // ������
			stringBuilder.append(",sender:'").append(jid.getNode()).append("'");
			JID recipient = message.getTo();
			stringBuilder.append(",receiver:'").append(recipient.getNode()).append("'");
		}
		stringBuilder.append(",content:'").append(message.getBody()).append("'");
		stringBuilder.append(",createdate:'").append(new Timestamp(new Date().getTime())).append("'");
		stringBuilder.append(",detail:'").append(message.toXML()).append("'");
		stringBuilder.append(",length:'").append(message.getBody().length()).append("'");
		stringBuilder.append(",state:'").append("0").append("'");
		stringBuilder.append(",sessionjid:'").append(jid.toString()).append("'");
		
		return String.format("%s"+stringBuilder.toString()+"%s","{","}");
	}

	/**
	 * <b>function:</b> ������Ϣ
	 * 
	 * @author
	 * @createDate 2013-3-27 ����04:44:31
	 * @param packet
	 *            ���ݰ�
	 * @param incoming
	 *            ���Ϊture�ͱ����Ƿ�����
	 * @param processed
	 *            ִ��
	 * @param session
	 *            ��ǰ�û�session
	 */
	private void debug(Packet packet, boolean incoming, boolean processed,
			Session session) {
		String info = "[ packetID: " + packet.getID() + ", to: "
				+ packet.getTo() + ", from: " + packet.getFrom()
				+ ", incoming: " + incoming + ", processed: " + processed
				+ " ]";

		long timed = System.currentTimeMillis();
		debug("################### start ###################" + timed);
		debug("id:" + session.getStreamID() + ", address: "
				+ session.getAddress());
		debug("info: " + info);
		debug("xml: " + packet.toXML());
		debug("################### end #####################" + timed);

		log.info("id:" + session.getStreamID() + ", address: "
				+ session.getAddress());
		log.info("info: {}", info);
		log.info("plugin Name: " + pluginManager.getName(this) + ", xml: "
				+ packet.toXML());
	}

	private void debug(Object message) {
		if (true) {
			System.out.println(message);
		}
	}


	public void destroyPlugin() {
		interceptorManager.removeInterceptor(this);
		debug("���������¼����ɹ���");
	}


	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		interceptorManager.addInterceptor(this);
		pluginManager = manager;

		debug("��װ�����¼����ɹ���");
	}

}
