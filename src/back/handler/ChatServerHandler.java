package back.handler;

import back.dao.chatting.GetChattingMessagesDAO;
import back.dao.chatting.GetParticipantsChatRoomDAO;
import back.dao.chatting.InsertChattingMessageDAO;
import back.request.chatroom.*;
import back.response.ResponseCode;
import back.dao.GetInfoDAO;
import back.dao.chatting.IsMasterDAO;
import back.response.chatroom.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerHandler extends Thread {
	private ObjectOutputStream objectOutputStream = null;

	private ObjectInputStream objectInputStream = null;

	private ArrayList<ChatServerHandler> list = null;

	private int port;
	private InetAddress ip;

	private Boolean master = false; //방장 권한

	public ChatServerHandler(Socket socket, ArrayList<ChatServerHandler> list) {
		try {
			this.list = list;
			port = socket.getLocalPort();
			ip = socket.getLocalAddress();

			//서버 -> 클라이언트 Output Stream
			OutputStream outputStream = socket.getOutputStream();
			objectOutputStream = new ObjectOutputStream(outputStream);

			//서버 <- 클라이언트 Input Stream
			InputStream inputStream = socket.getInputStream();
			objectInputStream = new ObjectInputStream(inputStream);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			JoinMessageChatRoomRequest joinMessageChatRoomRequest = (JoinMessageChatRoomRequest) objectInputStream.readObject();
			IsMasterDAO isMasterDAO = new IsMasterDAO();
			GetInfoDAO getInfoDAO = new GetInfoDAO();
			String nickName = getInfoDAO.getNickNameMethod(joinMessageChatRoomRequest.uuid());

			getchattingMessage();
			sendAll(new JoinMessageChatRoomResponse(nickName, "이(가) 입장했습니다.\n"));

			if (isMasterDAO.isMaster(port, joinMessageChatRoomRequest.uuid())) {	//	해당 채팅방의 방장인지 판단하는 DAO
				master = true;
			}

			while (true) {
				Object readObj = objectInputStream.readObject();

				if (readObj instanceof MessageChatRoomRequest messageChatRoomRequest) { //클라이언트 -> 서버 메세지 요청
					if (master) { // 방장이 메세지를 보냈을 때
						ChattingMessageRequest chattingMessageRequest = new ChattingMessageRequest(messageChatRoomRequest.message(), messageChatRoomRequest.uuid() + "*", port);
						new InsertChattingMessageDAO().insertChattingMessage(chattingMessageRequest);

						MessageChatRoomResponse messageChatRoomResponse = new MessageChatRoomResponse(
								nickName + "(방장)",
								messageChatRoomRequest.message() + "\n"
						);

						sendAll(messageChatRoomResponse);
					} else { // 이외 사용자가 메세지를 보냈을 때
						ChattingMessageRequest chattingMessageRequest = new ChattingMessageRequest(messageChatRoomRequest.message(), messageChatRoomRequest.uuid(), port);
						new InsertChattingMessageDAO().insertChattingMessage(chattingMessageRequest);

						MessageChatRoomResponse messageChatRoomResponse = new MessageChatRoomResponse(
								nickName,
								messageChatRoomRequest.message() + "\n"
						);

						sendAll(messageChatRoomResponse);
					}
				} else if (readObj instanceof KickChatRoomRequest kickChatRoomRequest) { // 클라이언트 -> 서버 강퇴 요청
					if (master) { // 방장일 경우에만 강퇴 요청을 accept
						for (ChatServerHandler handler : list) {
							if (kickChatRoomRequest.target_nickName().equals(handler.getnickName())) {	//	???? getNickName() 이게 왜 필요한거??? -민재
								sendAll(new MessageChatRoomResponse("", kickChatRoomRequest.target_nickName() + "이(가) 강제퇴장 당했습니다.\n"));

								handler.objectInputStream.close();
								list.remove(handler);
								break;
							}
						}
						objectOutputStream.writeObject(ResponseCode.KICK_CHATROOM_SUCCESS);
					} else {
						objectOutputStream.writeObject(ResponseCode.KICK_CHATROOM_FAILURE);
					}
				} else if (readObj instanceof GetParticipantsChatRoomRequest getParticipantsChatRoomRequest) {
					// 채팅방 참여자 명단을 가져오는 작업을 수행해야함.
					GetParticipantsChatRoomDAO getParticipantsChatRoomDAO = new GetParticipantsChatRoomDAO();
					ArrayList<String> participantsNickNameList = getParticipantsChatRoomDAO.getParticipantsChatRoom(port);

					if (participantsNickNameList != null) {
						objectOutputStream.writeObject(ResponseCode.GET_PARTICIPANTS_SUCCESS);
						GetParticipantsChatRoomResponse getParticipantsChatRoomResponse = new GetParticipantsChatRoomResponse(participantsNickNameList);
						objectOutputStream.writeObject(getParticipantsChatRoomResponse);
					} else {
						objectOutputStream.writeObject(ResponseCode.GET_PARTICIPANTS_FAILURE);
					}
				} else if (readObj instanceof LeaveChatRoomRequest leaveChatRoomRequest) {
					sendAll(new JoinMessageChatRoomResponse(nickName, "이(가) 퇴장했습니다.\n"));
					break;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				System.out.println("[채팅방 퇴장] PORT : " + port + " IP : " + ip);

				objectInputStream.close();
				list.remove(this);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*메세지 전송 Response를 보내는 메소드*/
	private void sendAll(MessageChatRoomResponse messageChatRoomResponse) {
		try {
			for (ChatServerHandler handler : list) {
				handler.objectOutputStream.writeObject(messageChatRoomResponse);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/*새로운 유저 입장 Response를 보내는 메소드*/
	private void sendAll(JoinMessageChatRoomResponse joinMessageChatRoomResponse) {
		try {
			for (ChatServerHandler handler : list) {
				handler.objectOutputStream.writeObject(joinMessageChatRoomResponse);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void getchattingMessage() {
		try {
			List<ChattingMessageRequest> chattingMessageRequests = new GetChattingMessagesDAO().getChattingMessages(port);
			List<ChattingMessageResponse> chattingMessageResponses = new ArrayList<>();

			for (ChattingMessageRequest chattingMessageRequest : chattingMessageRequests) {
				String uuid = chattingMessageRequest.uuid();

				if (uuid.endsWith("*")) { // 방장이 작성한 메세지일 경우
					String nickName = new GetInfoDAO().getNickNameMethod(uuid.substring(0, uuid.length() - 1));

					ChattingMessageResponse chattingMessageResponse = new ChattingMessageResponse(chattingMessageRequest.message(), nickName + "(방장)");
					chattingMessageResponses.add(chattingMessageResponse);
				} else { // 이외에 경우
					String nickName = new GetInfoDAO().getNickNameMethod(uuid);

					ChattingMessageResponse chattingMessageResponse = new ChattingMessageResponse(chattingMessageRequest.message(), nickName);
					chattingMessageResponses.add(chattingMessageResponse);
				}
			}

			objectOutputStream.writeObject(chattingMessageResponses);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private String getnickName() {
		return getnickName();
	}
}