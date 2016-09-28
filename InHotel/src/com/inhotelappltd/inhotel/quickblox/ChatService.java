package com.inhotelappltd.inhotel.quickblox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.inhotelappltd.inhotel.AcessCodeActivity;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

/**
 * Created by igorkhomenko on 4/28/15.
 */
public class ChatService {

    private static final String TAG = ChatService.class.getSimpleName();

    public static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;

    private static ChatService instance;

    public static synchronized ChatService getInstance() {
        if(instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

    public static boolean initIfNeed(Context ctx) {
        if (!QBChatService.isInitialized()) {
            QBChatService.setDebugEnabled(true);
            QBChatService.init(ctx);
            Log.d(TAG, "Initialise QBChatService");

            return true;
        }

        return false;
    }

    private QBChatService chatService;

    public ChatService() {
        chatService = QBChatService.getInstance();
        chatService.addConnectionListener(chatConnectionListener);
    }

    public void addConnectionListener(ConnectionListener listener){
        chatService.addConnectionListener(listener);
    }

    public void removeConnectionListener(ConnectionListener listener){
        chatService.removeConnectionListener(listener);
    }

    public void login(final QBUser user, final QBEntityCallback callback){

        // Create REST API session
        //
        QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle args) {

                user.setId(session.getUserId());

                // login to Chat
                //
                loginToChat(user, new QBEntityCallbackImpl() {

                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(List errors) {
                        callback.onError(errors);
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {
            	Log.e("LOGIN BACK SESSION","dsfs"+errors);
                callback.onError(errors);
            }
        });
    }
    
    
    
    public void register(final QBUser user,final QBEntityCallback callback){
    	
    	
		QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
		
			@Override
			public void onSuccess(QBSession session, Bundle params) {				
				super.onSuccess();
				
				
				
				
				// Register new user
				final QBUser user = new QBUser(SystemUtils.USER_LOGIN, SystemUtils.USER_PASSWORD);
				 
				QBUsers.signUp(user, new QBEntityCallbackImpl<QBUser>() {
				    @Override
				    public void onSuccess(QBUser user, Bundle args) {
				    	
				    	 AcessCodeActivity.my_quickbloxID=user.getId();
				    	final QBUser userz = new QBUser(SystemUtils.USER_LOGIN, SystemUtils.USER_PASSWORD);
						
				    	  // AcessCodeActivity.my_quickbloxID=userz.getId();
				    	 callback.onSuccess();
					        
						/*ChatService.getInstance().login(userz, new QBEntityCallbackImpl() {
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								super.onSuccess();				
								   AcessCodeActivity.my_quickbloxID=userz.getId();
								
								 try {
					                 chatService.startAutoSendPresence(30);
					             } catch (SmackException.NotLoggedInException e) {
					                 e.printStackTrace();
					             }
								 
								 callback.onSuccess();
								
							}

							@Override
							public void onError(List errors) {
								// TODO Auto-generated method stub
								super.onError(errors);								
								callback.onError(errors); 

							}
						});*/
				    	
				    	
				    	
				    	/*
				      
				    	 Log.e("signUp onSuccess","user"+user+"  "+args);
				    	// Login
				    	 
				    		final QBUser userz = new QBUser(SystemUtils.USER_LOGIN, SystemUtils.USER_PASSWORD);
				    	QBUsers.signIn(userz, new QBEntityCallbackImpl<QBUser>() {
				    	    @Override
				    	    public void onSuccess(QBUser user, Bundle args) {
				    	        Log.e("signIn onSuccess","user"+user+"  "+args);
				    	        AcessCodeActivity.my_quickbloxID=user.getId();
				    	        try {
				                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
				                } catch (SmackException.NotLoggedInException e) {
				                    e.printStackTrace();
				                }
				    	        callback.onSuccess();
				    	    }
				    	 
				    	    @Override
				    	    public void onError(List<String> errors) {
				    	    	 Log.e("signIn onError","errors");
				    	    }
				    	});
				    */}
				 
				    @Override
				    public void  onError (List<String> errors) {
				    	callback.onError(errors);
				    	 Log.e("signUp onError","errors");
				    }
				});	
				
				
				
				
				
				
			}	
				
				
				
				
				
	/*			// Register new user
				final QBUser userz = new QBUser();
				userz.setLogin(SystemUtils.USER_LOGIN);
				userz.setPassword(SystemUtils.USER_PASSWORD);
				Log.e("createSession signUp",userz+" user**** "+SystemUtils.USER_PASSWORD);
				
				
				 
				QBUsers.signUp(userz, new QBEntityCallbackImpl() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						super.onSuccess();
						Log.e("signUp",userz+" user**** ");
						AcessCodeActivity.my_quickbloxID=userz.getId();
					
						try {
		                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
		                } catch (SmackException.NotLoggedInException e) {
		                    e.printStackTrace();
		                }
						
						 callback.onSuccess();
					}
					
					@Override
					public void onError(final List errors) {
						
						super.onError(errors);
						Log.e("signUp signUp", errors + "signUp");
						 callback.onError(errors);
					}
				});*/
				
				/*
				try {
                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }*/
				
		/*		
				QBUsers.signUp(userz, new QBEntityCallbackImpl() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						super.onSuccess();
						AcessCodeActivity.my_quickbloxID=user.getId();
						Log.e("signUp finisg",userz+" user**** "+SystemUtils.USER_PASSWORD);
						try {
		                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
		                } catch (SmackException.NotLoggedInException e) {
		                    e.printStackTrace();
		                }
					}
					
					@Override
					public void onError(final List errors) {
						Log.e("signUp signUp", errors + "signUp");
						super.onError(errors);
					}
				});
					*/
			
					/*
					@Override
					public void onSuccess(QBUser user, Bundle args) {
						
						user.setLogin(prefs.getString("user_id", ""));
						user.setPassword("inhotel_" + prefs.getString("user_id", "")+ "123");
						Log.e("signUp finisg",user+" user**** "+user.getId());
						
						
						AcessCodeActivity.my_quickbloxID=user.getId();
						try {
		                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
		                } catch (SmackException.NotLoggedInException e) {
		                    e.printStackTrace();
		                }
						
						Log.e("signUp finisg",user+" user**** "+SystemUtils.USER_PASSWORD);
						
						final QBUser userz = new QBUser();
						userz.setLogin(SystemUtils.USER_LOGIN);
						userz.setPassword(SystemUtils.USER_PASSWORD);
						QBUsers.signIn(userz,new QBEntityCallbackImpl<QBUser>() {
									@Override
									public void onSuccess(QBUser user,
											Bundle args) {
										
										
										Log.e("signIn signIn",user.getId()+"@@@"+user);

							                // Start sending presences
							                //
							                try {
							                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
							                } catch (SmackException.NotLoggedInException e) {
							                    e.printStackTrace();
							                }

							                callback.onSuccess();
							                
									}

									@Override
									public void onError(List<String> errors) {
										Log.e(" SIGN IN EROOR", errors + "");
										
									}
								});
						  callback.onSuccess();
					}

					
				});*/
			//}

			@Override
			public void onError(List<String> errors) {
				Log.e("register errors", errors + "register");
				super.onError(errors);
			}
		});
    }

    public void logout(){
        chatService.logout(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
            	Log.e("LOGOUT","DONE");
            	
            }

            @Override
            public void onError(List list) {
            	Log.e("LOGOUT","Eroor"+list);
            }
        });
    }

    public void loginToChat(final QBUser user, final QBEntityCallback callback){

        chatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {

                // Start sending presences
                //
                try {
                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }
                Log.e("BACK LOGIN", "onSuccess22222");
                callback.onSuccess();
            }

            @Override
            public void onError(List errors) {
                callback.onError(errors);
                Log.e("BACK LOGIN",errors+"22222");
            }
        });
    }

    public void getDialogs(final QBEntityCallback callback){
        // get dialogs
        //
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);

        QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args) {

                // collect all occupants ids
                //
                List<Integer> usersIDs = new ArrayList<Integer>();
                for(QBDialog dialog : dialogs){
                    usersIDs.addAll(dialog.getOccupants());
                }

                // Get all occupants info
                //
                QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
                requestBuilder.setPage(1);
                requestBuilder.setPerPage(usersIDs.size());
                //
                QBUsers.getUsersByIDs(usersIDs, requestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {

                        // Save users
                        //
                        setDialogsUsers(users);

                        callback.onSuccess(dialogs, null);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        callback.onError(errors);
                    }

                });
            }

            @Override
            public void onError(List<String> errors) {
                callback.onError(errors);
            }
        });
    }


    private Map<Integer, QBUser> dialogsUsers = new HashMap<Integer, QBUser>();

    public Map<Integer, QBUser> getDialogsUsers() {
        return dialogsUsers;
    }

    public void setDialogsUsers(List<QBUser> setUsers) {
        dialogsUsers.clear();

        
        for (QBUser user : setUsers) {
            dialogsUsers.put(user.getId(), user);
        }
    }

    public void addDialogsUsers(List<QBUser> newUsers) {
        for (QBUser user : newUsers) {
            dialogsUsers.put(user.getId(), user);
        }
    }

    public QBUser getCurrentUser(){
        return QBChatService.getInstance().getUser();
    }

    public Integer getOpponentIDForPrivateDialog(QBDialog dialog){
        Integer opponentID = -1;
        for(Integer userID : dialog.getOccupants()){
            if(!userID.equals(getCurrentUser().getId())){
                opponentID = userID;
                break;
            }
        }
        return opponentID;
    }
    
    
   /* public Integer getMyIDForPrivateDialog(QBDialog dialog){
        Integer MyID = -1;
        for(Integer userID : dialog.getOccupants()){
            if(userID.equals(getCurrentUser().getId())){
            	MyID = userID;
                break;
            }
        }
        return MyID;
    }*/


    ConnectionListener chatConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            Log.i(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection connection) {
            Log.i(TAG, "authenticated");
        }

        @Override
        public void connectionClosed() {
            Log.i(TAG, "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(final Exception e) {
            Log.i(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());
        }

        @Override
        public void reconnectingIn(final int seconds) {
            if(seconds % 5 == 0) {
                Log.i(TAG, "reconnectingIn: " + seconds);
            }
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i(TAG, "reconnectionSuccessful");
        }

        @Override
        public void reconnectionFailed(final Exception error) {
            Log.i(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
        }
    };
}
