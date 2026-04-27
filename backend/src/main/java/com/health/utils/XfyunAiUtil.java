package com.health.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.health.vo.AiChatVO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class XfyunAiUtil {

    @Value("${xfyun.spark.app-id}")
    private String appid;

    @Value("${xfyun.spark.api-key}")
    private String apiKey;

    @Value("${xfyun.spark.api-secret}")
    private String apiSecret;

    // ====================== 这里改成了 Spark Lite ======================
    private static final String hostUrl = "https://spark-api.xf-yun.com/v1.1/chat";
    private static final String domain = "lite";
    // ====================================================================

    private final Gson gson = new Gson();

    public AiChatVO chat(String question) {
        AiChatVO vo = new AiChatVO();
        vo.setQuestion(question);
        StringBuilder totalAnswer = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        try {
            String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();

            WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                    super.onOpen(webSocket, response);
                    new Thread(() -> {
                        try {
                            JSONObject requestJson = new JSONObject();
                            JSONObject header = new JSONObject();
                            header.put("app_id", appid);
                            header.put("uid", UUID.randomUUID().toString().substring(0, 10));

                            JSONObject parameter = new JSONObject();
                            JSONObject chat = new JSONObject();
                            chat.put("domain", domain);
                            chat.put("temperature", 0.5);
                            chat.put("max_tokens", 4096);
                            parameter.put("chat", chat);

                            JSONObject payload = new JSONObject();
                            JSONObject message = new JSONObject();
                            JSONArray text = new JSONArray();

                            JSONObject roleContent = new JSONObject();
                            roleContent.put("role", "user");
                            roleContent.put("content", question);
                            text.add(roleContent);

                            message.put("text", text);
                            payload.put("message", message);

                            requestJson.put("header", header);
                            requestJson.put("parameter", parameter);
                            requestJson.put("payload", payload);

                            webSocket.send(requestJson.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            latch.countDown();
                        }
                    }).start();
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    super.onMessage(webSocket, text);
                    JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);

                    if (myJsonParse.header.code != 0) {
                        System.out.println("错误码：" + myJsonParse.header.code);
                        latch.countDown();
                        return;
                    }

                    if (myJsonParse.payload != null && myJsonParse.payload.choices != null) {
                        myJsonParse.payload.choices.text.forEach(t -> {
                            totalAnswer.append(t.content);
                        });
                    }

                    if (myJsonParse.header.status == 2) {
                        latch.countDown();
                    }
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                    t.printStackTrace();
                    latch.countDown();
                }
            });

            boolean success = latch.await(30, TimeUnit.SECONDS);
            if (!success) {
                vo.setAnswer("AI请求超时，请稍后重试");
            } else {
                vo.setAnswer(totalAnswer.toString());
            }
            webSocket.close(1000, "");

        } catch (Exception e) {
            e.printStackTrace();
            vo.setAnswer("调用失败：" + e.getMessage());
        }

        return vo;
    }

    private static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());

        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";

        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                apiKey, "hmac-sha256", "host date request-line", sha);

        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder()
                .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)))
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();

        return httpUrl.toString();
    }

    static class JsonParse {
        Header header;
        Payload payload;
    }

    static class Header {
        int code;
        int status;
        String sid;
    }

    static class Payload {
        Choices choices;
    }

    static class Choices {
        List<Text> text;
    }

    static class Text {
        String role;
        String content;
    }
}