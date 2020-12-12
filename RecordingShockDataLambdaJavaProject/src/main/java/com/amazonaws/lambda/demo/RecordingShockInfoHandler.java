package com.amazonaws.lambda.demo;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class RecordingShockInfoHandler implements RequestHandler<Document, String> {

	private DynamoDB dynamoDb;
	private String DYNAMODB_TABLE_NAME = "ShockLogging";

	// DynamoDB에 ShockLogging 테이블에 아두이노 디바이스에서 얻은 데이터 저장
	@Override
	public String handleRequest(Document input, Context context) {
		this.initDynamoDbClient();
		context.getLogger().log("Input: " + input);

		// return null;
		return persistData(input);
	}

	private String persistData(Document document) throws ConditionalCheckFailedException {

		// Epoch Conversion Code: https://www.epochconverter.com/
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
		String timeString = sdf.format(new java.util.Date(document.timestamp * 1000));

		// SHOCK 값이 OFF일 경우 저장하지 않는다.
		// 충격이 발생하지 않았을 때는 DynamoDB에 저장하지 않음.
		if (document.current.state.reported.SHOCK.equals("OFF")) {
			return null;
		}

		return this.dynamoDb.getTable(DYNAMODB_TABLE_NAME)
				.putItem(new PutItemSpec().withItem(new Item().withPrimaryKey("deviceId", document.device)
						.withLong("time", document.timestamp)
						.withString("SHOCK", document.current.state.reported.SHOCK)
						.withString("timestamp", timeString)))
				.toString();
	}

	private void initDynamoDbClient() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("ap-northeast-2").build();

		this.dynamoDb = new DynamoDB(client);
	}

}

class Document {
    public Thing previous;       
    public Thing current;
    public long timestamp;
    public String device;       // AWS IoT에 등록된 사물 이름 
}

class Thing {
	public State state = new State();
	public long timestamp;
	public String clientToken;

	public class State {
		public Tag reported = new Tag();
		public Tag desired = new Tag();

		public class Tag {
			public String SHOCK;
		}
	}
}