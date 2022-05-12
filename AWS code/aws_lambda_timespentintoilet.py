import json
import boto3
from datetime import datetime


def lambda_handler(event, context):
    notification = {
        "default": "test",
        "GCM": "{\"data\": { \"body\": \"TimeSpentInToilet: 노약자의 화장실 이용시간이 60분을 초과했습니다. 신속하게 안전을 확인해주세요\", 
        \"title\":\"[안전바 응급 호출 도우미]\",
        \"click_action\":\"SpentTimeInToiletMoreThan60\",
        \"time\":\"60\"} 
        }"
    }

    client = boto3.client('sns')
    response = client.publish(
        TargetArn="[ARN]",
        Message=json.dumps({'default': notification}),
        MessageStructure='json'
    )

    return {
        'statusCode': 200,
        'body': json.dumps(response)
    }
