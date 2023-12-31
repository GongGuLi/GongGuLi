package back.handler;

import java.io.*;
import java.net.Socket;

import back.response.ResponseCode;
import back.dao.user.CheckDAO;
import back.dao.GetInfoDAO;
import back.dao.user.AccountDAO;
import back.dao.user.FindUserDAO;
import back.dao.user.LogInDAO;
import back.dao.user.SignUpDAO;
import back.request.account.*;
import back.response.account.FindUserIdResponse;
import back.response.account.FindUserPasswordResponse;
import back.response.account.GetNickNameResponse;
import back.response.account.LoginResponse;

public class AccountHandler extends Thread {
	private ObjectInputStream objectInputStream = null;
	private ObjectOutputStream objectOutputStream = null;

	public AccountHandler(Socket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);

            OutputStream outputStream = clientSocket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (Exception exception) {
            exception.printStackTrace();

			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
        }
	}

	/*사용자 Request를 받는 메소드*/
	@Override
	public void run() {
		try {
			Object readObj = objectInputStream.readObject();
			if (readObj instanceof LoginRequest loginRequest) {
				LoginMethod(loginRequest);
			} else if (readObj instanceof SignUpRequest signUpRequest) {
				SignUpMethod(signUpRequest);
			} else if (readObj instanceof FindUserIdRequest findUserIdRequest) {
				FindUserIdMethod(findUserIdRequest);
			} else if (readObj instanceof FindUserPasswordRequest findUserPasswordRequest) {
				FindUserPasswordMethod(findUserPasswordRequest);
			} else if (readObj instanceof GetNickNameRequest getNickNameRequest) {
				getNickNameMethod(getNickNameRequest);
			} else if (readObj instanceof ModifyUserInfoRequest modifyUserInfoRequest) {
				modifyUserInfo(modifyUserInfoRequest);
			} else if (readObj instanceof NickNameCheckRequest nickNameCheckRequest) {
				nickNameCheckMethod(nickNameCheckRequest);
			} else if (readObj instanceof DeleteUserRequest deleteUserRequest) {
				deleteUserMethod(deleteUserRequest);
			} else if (readObj instanceof SignUpNickNameCheckRequest signUpNickNameCheckRequest) {
				signUpNickNameCheckMethod(signUpNickNameCheckRequest);
			} else if (readObj instanceof SignUpIDCheckRequest signUpIDCheckRequest) {
				signUpIDCheckMethod(signUpIDCheckRequest);
			}
		} catch (Exception exception) {
			exception.printStackTrace();

			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*회원가입 Response를 보내는 메소드*/
	private void SignUpMethod(SignUpRequest signUpRequest) {
		try {
			SignUpDAO signUpDAO = new SignUpDAO();

			if (signUpRequest.userId().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.ID_MISSING);
			} else if (signUpRequest.password().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISSING);
			} else if (!signUpRequest.password().equals(String.valueOf(signUpRequest.passwordCheck()))) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISMATCH);
			} else if (signUpRequest.password().length() < 8 ||
					!signUpRequest.password().matches(".*[a-zA-Z].*") ||
					!signUpRequest.password().matches(".*\\d.*") ||
					!signUpRequest.password().matches(".*[@#$%^&*+_=!].*")) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_CONDITIONS_NOT_MET);
			} else if (signUpRequest.name().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NAME_MISSING);
			} else if (signUpRequest.birth().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_MISSING);
			} else if (!signUpRequest.birth().matches("\\d{6}")) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_CONDITIONS_NOT_MET);
			} else if (signUpRequest.phoneNumber().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_MISSING);
			} else if (!signUpRequest.phoneNumber().matches("\\d{11}")) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_CONDITIONS_NOT_MET);
			} else if (signUpRequest.nickName().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NICKNAME_MISSING);
			} else if (signUpRequest.region().equals("거주 지역")) {
				objectOutputStream.writeObject(ResponseCode.RESIDENCE_AREA_NOT_SELECTED);
			} else {
				ResponseCode responseCode = signUpDAO.signUp(signUpRequest);
				objectOutputStream.writeObject(responseCode);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*로그인 Response를 보내는 메소드*/
	private void LoginMethod(LoginRequest loginRequest) {
		try {
			LogInDAO logInDAO = new LogInDAO();

			if (loginRequest.userId().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.ID_MISSING);
			} else if (loginRequest.password().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISSING);
			} else {
				String logInCheckResult = logInDAO.logIn(loginRequest);

				if (logInCheckResult.equals("Password Does Not Match")) {
					objectOutputStream.writeObject(ResponseCode.PASSWORD_MISMATCH_LOGIN);
				} else if (logInCheckResult.equals("Id Does Not Exist")) {
					objectOutputStream.writeObject(ResponseCode.ID_NOT_EXIST);
				} else if (!logInCheckResult.equals("Database or SQL Error")) {
					objectOutputStream.writeObject(ResponseCode.LOGIN_SUCCESS);

					LoginResponse loginResponse = new LoginResponse(logInCheckResult);
					objectOutputStream.writeObject(loginResponse);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*아이디 찾기 Response를 보내는 메소드*/
	private void FindUserIdMethod(FindUserIdRequest findUserIdRequest) {
		try {
			FindUserDAO findUserDAO = new FindUserDAO();

			if (findUserIdRequest.name().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NAME_MISSING);
			} else if (findUserIdRequest.birth().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_MISSING);
			} else if (findUserIdRequest.phoneNumber().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_MISSING);
			} else {
				FindUserIdResponse findUserIdResponse = findUserDAO.findID(findUserIdRequest);

				if (findUserIdResponse == null) {
					objectOutputStream.writeObject(ResponseCode.NO_MATCHING_USER);
				} else {
					objectOutputStream.writeObject(ResponseCode.FIND_ID_SUCCESS);

					objectOutputStream.writeObject(findUserIdResponse);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*비밀번호 찾기 Response를 보내는 메소드*/
	private void FindUserPasswordMethod(FindUserPasswordRequest findUserPasswordRequest) {
		try {
			FindUserDAO findUserDAO = new FindUserDAO();

			if (findUserPasswordRequest.name().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NAME_MISSING);
			} else if (findUserPasswordRequest.userId().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.ID_MISSING);
			} else if (findUserPasswordRequest.birth().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.BIRTHDAY_MISSING);
			} else if (findUserPasswordRequest.phoneNumber().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.PHONE_NUMBER_MISSING);
			} else {
				FindUserPasswordResponse findUserPasswordResponse = findUserDAO.findPassword(findUserPasswordRequest);

				if (findUserPasswordResponse == null) {
					objectOutputStream.writeObject(ResponseCode.NO_MATCHING_USER);
				} else {
					objectOutputStream.writeObject(ResponseCode.FIND_PASSWORD_SUCCESS);

					objectOutputStream.writeObject(findUserPasswordResponse);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*닉네임 정보 요청 Response를 보내는 메소드*/
	private void getNickNameMethod(GetNickNameRequest getNickNameRequest) {
		try {
			GetInfoDAO getInfoDAO = new GetInfoDAO();
			String nickName = getInfoDAO.getNickNameMethod(getNickNameRequest.uuid());
			GetNickNameResponse getNickNameResponse = new GetNickNameResponse(nickName);
			if (nickName != null) {
				objectOutputStream.writeObject(ResponseCode.GET_UUID_NICKNAME_FAILURE);
			} else {
				objectOutputStream.writeObject(ResponseCode.GET_UUID_NICKNAME_SUCCESS);
				objectOutputStream.writeObject(getNickNameResponse);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*회원 정보 변경 Reponse를 보내는 메소드*/
	private void modifyUserInfo(ModifyUserInfoRequest modifyUserInfoRequest) {
		try {
			AccountDAO accountDAO = new AccountDAO();

			if (modifyUserInfoRequest.password().isBlank() || modifyUserInfoRequest.passwordCheck().isBlank())
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISSING);
			else if (!modifyUserInfoRequest.password().equals(modifyUserInfoRequest.passwordCheck()))
				objectOutputStream.writeObject(ResponseCode.PASSWORD_MISMATCH);
			else if (modifyUserInfoRequest.password().length() < 8 ||
					!modifyUserInfoRequest.password().matches(".*[a-zA-Z].*") || // 영어 포함
					!modifyUserInfoRequest.password().matches(".*\\d.*") ||      // 숫자 포함
					!modifyUserInfoRequest.password().matches(".*[@#$%^&*+_=!].*")) { // 특수문자 포함
				objectOutputStream.writeObject(ResponseCode.PASSWORD_CONDITIONS_NOT_MET);
			} else if (modifyUserInfoRequest.region().equals(" --")) {
				objectOutputStream.writeObject(ResponseCode.REGION_NOT_SELECTED);
			} else if (modifyUserInfoRequest.nickName().isBlank()) {
				objectOutputStream.writeObject(ResponseCode.NICKNAME_MISSING);
			} else {
				int result = accountDAO.modifyUserInfo(modifyUserInfoRequest);
				if (result == 1) {
					objectOutputStream.writeObject(ResponseCode.MODIFY_USER_INFO_SUCCESS);
				} else if (result == 0) {
					objectOutputStream.writeObject(ResponseCode.MODIFY_USER_INFO_FAILURE);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	/*닉네임 체크 Reponse를 보내는 메소드*/
	private void nickNameCheckMethod(NickNameCheckRequest nickNameCheckRequest) {
		try {
			CheckDAO checkDAO = new CheckDAO();
			int res = checkDAO.nickNameCheck(nickNameCheckRequest.uuid(), nickNameCheckRequest.inpNickName());

			if (res == 1) {
				objectOutputStream.writeObject(ResponseCode.NICKNAME_CHECK_SUCCESS);
			} else if (res == 0) {
				objectOutputStream.writeObject(ResponseCode.NICKNAME_CHECK_FAILURE);
			} else if (res == 2) {
				objectOutputStream.writeObject(ResponseCode.NICKNAME_CHECK_MY_NAME);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	private void deleteUserMethod(DeleteUserRequest deleteUserRequest) {
		try {
			AccountDAO accountDAO = new AccountDAO();
			if (accountDAO.deleteUser(deleteUserRequest.uuid(), deleteUserRequest.password())) {
				objectOutputStream.writeObject(ResponseCode.DELETE_USER_SUCCESS);
			} else {
				objectOutputStream.writeObject(ResponseCode.DELETE_USER_FAILURE);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	private void signUpNickNameCheckMethod(SignUpNickNameCheckRequest signUpNickNameCheckRequest) {
		try {
			CheckDAO checkDAO = new CheckDAO();
			int res = checkDAO.nickNameCheck(signUpNickNameCheckRequest.inpNickName());

			if (res == 1) {
				objectOutputStream.writeObject(ResponseCode.NICKNAME_CHECK_SUCCESS);
			} else if (res == 0) {
				objectOutputStream.writeObject(ResponseCode.NICKNAME_CHECK_FAILURE);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	private void signUpIDCheckMethod(SignUpIDCheckRequest signUpIDCheckRequest) {
		try {

			CheckDAO checkDAO = new CheckDAO();
			int res = checkDAO.IDCheck(signUpIDCheckRequest.inpID());

			if (res == 1) {
				objectOutputStream.writeObject(ResponseCode.ID_NOT_DUPLICATE);
			} else {
				objectOutputStream.writeObject(ResponseCode.ID_DUPLICATE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				objectInputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}