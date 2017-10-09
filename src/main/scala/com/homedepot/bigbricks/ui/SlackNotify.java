package com.homedepot.bigbricks.ui;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
public class SlackNotify {

    public static int notify(String message, String urlText) throws IOException {

        byte[] postData       = message.getBytes( StandardCharsets.UTF_8 );
        int    postDataLength = postData.length;
        URL url            = new URL( urlText);
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/json");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
            wr.write( postData );
        }
        return  conn.getResponseCode();
    }
}
