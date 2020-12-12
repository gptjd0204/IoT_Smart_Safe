package com.amazonaws.lambda.demo;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.amazonaws.lambda.demo.Thing.State;
import com.amazonaws.lambda.demo.Thing.State.Tag;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class RecordingOpenInfoHandler implements RequestHandler<Document, String> {

	private DynamoDB dynamoDb;
	private String DYNAMODB_TABLE_NAME = "OpenLogging";

	// DynamoDB에 OpenLogging 테이블에 아두이노 디바이스에서 얻은 데이터 저장
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

		// SAFE 값이 동일하면 저장하지 않고, 값이 달라지면 저장한다.
		// 금고가 계속 열려 있거나 닫혀 있을 시, DynamoDB에 데이터가 계속 저장되는 것을 방지하기 위해
		if (document.current.state.reported.SAFE.equals(document.previous.state.reported.SAFE)) {
                return null;
        }

		return this.dynamoDb.getTable(DYNAMODB_TABLE_NAME)
				.putItem(new PutItemSpec().withItem(new Item().withPrimaryKey("deviceId", document.device)
						.withLong("time", document.timestamp)
						.withString("SAFE", document.current.state.reported.SAFE)
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
			public String SAFE;
		}
	}
}