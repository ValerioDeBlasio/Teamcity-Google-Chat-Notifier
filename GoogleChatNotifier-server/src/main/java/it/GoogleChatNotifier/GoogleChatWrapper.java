package it.GoogleChatNotifier;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;
import jetbrains.buildServer.Build;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by: valeriodeblasio with much love
 * Date: Fri 12/06/2020
 * Time: 17:01
 */
public class GoogleChatWrapper {
    private String googleChatWebhook;
    private String googleChatThread;

    public GoogleChatWrapper(String googleChatWebhook, String googleChatThread) {
        this.googleChatWebhook =  googleChatWebhook;
        this.googleChatThread = googleChatThread;
    }


    public void send(String project, String buildNumber, String branch, String message, String level, Build sRunningBuild, String rootUrl) throws UnirestException, IOException {

        StringBuilder builder = new StringBuilder("{");

        message = escape(escapeNewline(message));
        message = "<" + rootUrl + "/viewLog.html?buildId=" + sRunningBuild.getBuildId() + "&buildTypeId=" + sRunningBuild.getBuildNumber() + "|" + message + ">";



        String payload = escape(project) + "[" + escape(branch) + "]" + " #" + escape(buildNumber) + " " + message;

        switch (level) {
            case "ERROR": payload = payload + " ❌";
                    break;
            default: payload = payload + " ✔";
        }


        builder.append("\"text\": \"" + payload + "\"");
        builder.append("}");


        RequestBodyEntity bodyResponse = Unirest.post(this.googleChatWebhook + "&threadKey=" + this.googleChatThread)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(builder.toString());


        HttpResponse<JsonNode> post = bodyResponse.asJson();

        int status = post.getStatus();

        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException(post.getStatusText());

        }

    }


    private String escapeNewline(String s) {
        return s.replace("\n", "\\n");
    }

    private String escape(String s) {
        return s
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
