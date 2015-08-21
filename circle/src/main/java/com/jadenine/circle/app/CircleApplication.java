package com.jadenine.circle.app;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jadenine.circle.BuildConfig;
import com.jadenine.circle.domain.Account;
import com.jadenine.circle.domain.Circle;
import com.jadenine.circle.domain.Group;
import com.jadenine.circle.domain.dagger.DaggerDomainComponentProduction;
import com.jadenine.circle.domain.dagger.DomainComponentProduction;
import com.jadenine.circle.domain.dagger.DomainModule;
import com.jadenine.circle.model.db.CircleDatabase;
import com.jadenine.circle.model.entity.Bomb;
import com.jadenine.circle.model.entity.DirectMessageEntity;
import com.jadenine.circle.mortar.DaggerScope;
import com.jadenine.circle.mortar.DaggerService;
import com.jadenine.circle.ui.avatar.AvatarBinder;
import com.jadenine.circle.utils.Device;
import com.raizlabs.android.dbflow.DatabaseHelperListener;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import mortar.MortarScope;
import timber.log.Timber;

/**
 * Created by linym on 6/6/15.
 */
public class CircleApplication extends Application {
    public static final String ROOT_SCOPE_NAME = "Root";
    public static final String CUSTOM_NOTIFICATION_TYPE_TOPIC = "topic";
    public static final String CUSTOM_NOTIFICATION_TYPE_CHAT = "chat";
    private MortarScope rootScope;

    private RefWatcher refWatcher;

    @Inject
    Account account;

    @Inject
    Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        DomainComponentProduction domainComponent = DaggerDomainComponentProduction.builder()
                .domainModule(new DomainModule(Device.getDeviceId(this))).build();

        com.jadenine.circle.domain.dagger.DaggerService.setComponent(domainComponent);

        AppComponent appComponent = DaggerCircleApplication_AppComponent.builder()
                .domainComponentProduction(domainComponent)
                .appModule(new AppModule(this)).build();

        MortarScope.Builder builder = MortarScope.buildRootScope();
        builder.withService(DaggerService.SERVICE_NAME, appComponent);

        rootScope = builder.build(ROOT_SCOPE_NAME);

        appComponent.inject(this);

        registerUmengMessageHandler();

        FlowManager.init(this);

        FlowManager.setDatabaseListener(CircleDatabase.NAME, new DatabaseHelperListener() {
            @Override
            public void onOpen(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                int currentVersion = oldVersion;
                if (1 == currentVersion) {
                    sqLiteDatabase.execSQL("drop table Bomb;");
                    sqLiteDatabase.execSQL("drop table DirectMessageEntity;");
                    currentVersion = 2;
                }
                if (2 == currentVersion) {
                    sqLiteDatabase.execSQL("drop table Bomb;");
                    sqLiteDatabase.execSQL("drop table Timeline;");
                    sqLiteDatabase.execSQL("drop table TimelineRangeCursor;");
                    sqLiteDatabase.execSQL("drop table DirectMessageEntity;");
                    sqLiteDatabase.execSQL("drop table UserApEntity;");
                    currentVersion = 3;
                }
            }
        });
        refWatcher = installLeakCanary();

        Timber.w("onCreate %s", this);
    }

    private void registerUmengMessageHandler() {
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            @Override
            public Notification getNotification(Context context, UMessage uMessage) {
                Timber.i("getNotification " + uMessage.custom);

                if(!TextUtils.isEmpty(uMessage.custom)) {
                    try {
                        CustomNotificationType customNotification = gson.fromJson(uMessage.custom, CustomNotificationType.class);

                        if (CUSTOM_NOTIFICATION_TYPE_TOPIC.equalsIgnoreCase(customNotification.type)) {
                            Bomb bomb = gson.fromJson(uMessage.custom, CustomNotificationDataTopic.class).data;
                            updateCircleUnRead(bomb);
                            Circle circle = account.getCircle(bomb.getCircle());
                            uMessage.title = circle.getName();
                        } else if(CUSTOM_NOTIFICATION_TYPE_CHAT.equalsIgnoreCase(customNotification.type)) {
                            DirectMessageEntity chatMessage = gson.fromJson(uMessage.custom, CustomNotificationDataChat.class).data;
                            updateChatUnread(chatMessage);
                            Circle circle = account.getCircle(chatMessage.getCircle());
                            uMessage.title = circle.getName();
                        }
                    } catch (JsonSyntaxException e) {
                        Timber.e(e, uMessage.custom);
                    } catch (Throwable t) {
                        Timber.e(t, "wtf");
                    }
                }

                return super.getNotification(context, uMessage);
            }

            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                Timber.d("dealWithCustomMessage " + msg.custom);
            }

            private void updateCircleUnRead(Bomb bomb) {
                Circle circle = account.getCircle(bomb.getCircle());
                if(null == circle){
                    Timber.w("Cant find circle %s in NotificationService", bomb.getCircle());
                    return;
                }
                Group<Bomb> topic = circle.getTopic(bomb.getGroupId());
                Long lastRead = null;
                if(null != topic) {
                    Bomb latestBomb = topic.getLatest();
                    lastRead = latestBomb.getId();
                }
                if(null != circle && (null == lastRead || bomb.getId() <
                        lastRead)) {
                    circle.setHasUnread(true);
                }
            }

            private void updateChatUnread(DirectMessageEntity chatMessage) {
                Group<DirectMessageEntity> chat = account.getChat(chatMessage.getCircle(), Long
                        .valueOf(chatMessage.getTopicId()), chatMessage.getRootUser(),
                        chatMessage.getGroupId());

                if(null == chat) {
                    Timber.w("Cant find chat %s in NotificationService", chatMessage.getCircle());
                    return;
                }
                Long lastRead = null;
                if(null != chat) {
                    DirectMessageEntity latestMessage = chat.getLatest();
                    lastRead = latestMessage.getId();
                }

                if(null == lastRead || chatMessage.getId() < lastRead) {
                    account.setHasUnreadChat(true);
                }
            }
        };

        PushAgent pushAgent = PushAgent.getInstance(this);
//        pushAgent.setNotificaitonOnForeground(false);
        pushAgent.setMessageHandler(messageHandler);
    }

    @Override
    public Object getSystemService(String name) {
        if (null!=rootScope && rootScope.hasService(name)) {
            return rootScope.getService(name);
        }

        return super.getSystemService(name);
    }

    public static RefWatcher getRefWatcher(Context context) {
        CircleApplication application = (CircleApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    protected RefWatcher installLeakCanary() {
        if(BuildConfig.DEBUG) {
            return LeakCanary.install(this);
        }
        return RefWatcher.DISABLED;
    }

    @DaggerScope(CircleApplication.class)
    @dagger.Component(dependencies = DomainComponentProduction.class, modules = {AppModule.class})
    public interface AppComponent {
        void inject(CircleApplication app);
        Account account();
        AvatarBinder avatarBinder();
        Context appContext();
    }

    @Module
    public static class AppModule {

        private final Context appContext;
        public AppModule(Application application){
            this.appContext = application;
        }

        @Provides
        @DaggerScope(CircleApplication.class)
        AvatarBinder provideAvatarBinder() {
            return new AvatarBinder(appContext);
        }

        @Provides
        @DaggerScope(CircleApplication.class)
        Context provideAppContext(){return appContext;}
    }

    private static class CustomNotificationType {
        public String type;
    }
    private static class CustomNotificationDataTopic {
        public Bomb data;
    }
    private static class CustomNotificationDataChat {
        public DirectMessageEntity data;
    }
}
