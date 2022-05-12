import json
import boto3
from datetime import datetime


def lambda_handler(event, context):
    notification = {
        "default": "test",
        "GCM": "{\"data\": { \"body\": \"응급호출버튼: 노약자의 안전을 신속하게 확인해주세요\", 
        \"title\":\"[안전바 응급 호출 도우미]\",
        \"click_action\":\"Push_emergency_button\",
        \"time\":\"0\"} 
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
