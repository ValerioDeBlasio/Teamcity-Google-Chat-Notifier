package it.GoogleChatNotifier;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.VcsRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class AppServer implements Notificator {


    private static final String type = "Notification";

    private static final String googleChatWebhookKey = "googleChat.Webhook";
    private static final String googleChatThreadKey = "googleChat.Thread";

    private static final PropertyKey googleChatWebhook = new NotificatorPropertyKey(type, googleChatWebhookKey);
    private static final PropertyKey googleChatThread = new NotificatorPropertyKey(type, googleChatThreadKey);

    private SBuildServer myServer;

    public AppServer(NotificatorRegistry registry, SBuildServer server) {
        Loggers.SERVER.info("Google Chat Notification");

        registry.register(this, getUserPropertyInfosList());
        this.myServer = server;
    }



    @Override
    public void notifyBuildStarted(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "Build Start", "INFO", set, sRunningBuild);
    }

    @Override
    public void notifyBuildSuccessful(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "Build Complete", "INFO", set, sRunningBuild);
    }

    @Override
    public void notifyBuildFailed(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "Build Failed: " + sRunningBuild.getStatusDescriptor().getText(), "ERROR", set, sRunningBuild);
    }

    @Override
    public void notifyBuildFailedToStart(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {
        sendNotification(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), "Build Failed to start", "ERROR", set, sRunningBuild);
    }

    @Override
    public void notifyLabelingFailed(@NotNull Build build, @NotNull VcsRoot vcsRoot, @NotNull Throwable throwable, @NotNull Set<SUser> set) {
        sendNotification(build.getFullName(), build.getBuildNumber(), "Labeling failed", "ERROR", set, build);
    }

    @Override
    public void notifyBuildFailing(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyBuildProbablyHanging(@NotNull SRunningBuild sRunningBuild, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(@NotNull SBuildType sBuildType, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(@NotNull SBuildType sBuildType, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(@Nullable TestNameResponsibilityEntry testNameResponsibilityEntry, @NotNull TestNameResponsibilityEntry testNameResponsibilityEntry1, @NotNull SProject sProject, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(@Nullable TestNameResponsibilityEntry testNameResponsibilityEntry, @NotNull TestNameResponsibilityEntry testNameResponsibilityEntry1, @NotNull SProject sProject, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(@NotNull Collection<TestName> collection, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(@NotNull Collection<TestName> collection, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemResponsibleAssigned(@NotNull Collection<BuildProblemInfo> collection, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemResponsibleChanged(@NotNull Collection<BuildProblemInfo> collection, @NotNull ResponsibilityEntry responsibilityEntry, @NotNull SProject sProject, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyTestsMuted(@NotNull Collection<STest> collection, @NotNull MuteInfo muteInfo, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyTestsUnmuted(@NotNull Collection<STest> collection, @NotNull MuteInfo muteInfo, @Nullable SUser sUser, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemsMuted(@NotNull Collection<BuildProblemInfo> collection, @NotNull MuteInfo muteInfo, @NotNull Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemsUnmuted(@NotNull Collection<BuildProblemInfo> collection, @NotNull MuteInfo muteInfo, @Nullable SUser sUser, @NotNull Set<SUser> set) {

    }

    @NotNull
    @Override
    public String getNotificatorType() {
        return "Notification";
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Google Chat Notifier";
    }

    private ArrayList<UserPropertyInfo> getUserPropertyInfosList() {
        ArrayList<UserPropertyInfo> userPropertyInfos = new ArrayList<UserPropertyInfo>();

        userPropertyInfos.add(new UserPropertyInfo(googleChatThreadKey, "Google Chat Thread"));
        userPropertyInfos.add(new UserPropertyInfo(googleChatWebhookKey, "Google Chat Webhook"));

        return userPropertyInfos;
    }

    private void sendNotification(String project, String buildNumber, String message, String level, Set<SUser> users, Build sRunningBuild) {
        for (SUser user : users) {
            Loggers.SERVER.info("Send Notification");
            Loggers.SERVER.info("Username: " + user.getName());
            Loggers.SERVER.info("Webhook: " + user.getPropertyValue(googleChatWebhook));

            GoogleChatWrapper googleChatWrapper = new GoogleChatWrapper(user.getPropertyValue(googleChatWebhook), user.getPropertyValue(googleChatThread));
            try {
                googleChatWrapper.send(project, buildNumber, getBranch((SBuild) sRunningBuild), message, level, sRunningBuild, myServer.getRootUrl());
            } catch (Exception e) {
                e.printStackTrace();
                Loggers.SERVER.error(e);
            }
        }
    }

    private String getBranch(SBuild build) {
        Branch branch = build.getBranch();
        if (branch != null && branch.getName() != "<default>") {
            return branch.getDisplayName();
        } else {
            return "";
        }
    }
}