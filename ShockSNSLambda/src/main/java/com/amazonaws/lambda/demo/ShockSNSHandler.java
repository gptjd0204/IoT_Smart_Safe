package com.amazonaws.lambda.demo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ShockSNSHandler implements RequestHandler<Object, String> {

	// 금고에 충격 발생 시, 사용자의 메일로 경고 알림을 보냄
	@Override
	public String handleRequest(Object input, Context context) {
	    context.getLogger().log("Input: " + input);
	    String json = ""+input;
	    JsonParser parser = new JsonParser();
	    JsonElement element = parser.parse(json);
	    JsonElement state = element.getAsJsonObject().get("state");
	    JsonElement reported = state.getAsJsonObject().get("reported");
	    String shock = reported.getAsJsonObject().get("SHOCK").getAsString();
	    //String shock = reported.getAsJsonObject().get("SHOCK_RUN").getAsString();

	    final String AccessKey="AKIA2FW542NPQIAYNJHR";
	    final String SecretKey="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";
	    final String topicArn="arn:aws:sns:ap-northeast-2:699471287135:shock_warning_topic";

	    BasicAWSCredentials awsCreds = new BasicAWSCredentials(AccessKey, SecretKey);  
	    AmazonSNS sns = AmazonSNSClientBuilder.standard()
	                .withRegion(Regions.AP_NORTHEAST_2)
	                .withCredentials( new AWSStaticCredentialsProvider(awsCreds) )
	                .build();

	    final String msg = "*Warning*\n" + "The safe has been shock!";
	    final String subject = "Safe Shock Warning!";
	    
	    // 금고에 충격이 발생해 shock가 ON이 되면 메일을 보냄
	    if (shock.equals("ON")) {
	        PublishRequest publishRequest = new PublishRequest(topicArn, msg, subject);
	        PublishResult publishResponse = sns.publish(publishRequest);
	    }

	    return subject+ " The safe has been shock!";
	}

}
