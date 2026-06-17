import firebase_admin
from firebase_admin import credentials, messaging

# Initialize Firebase
cred = credentials.Certificate("firebase_service_account.json")
firebase_admin.initialize_app(cred)


def send_push_notification(fcm_token: str, title: str, body: str, data: dict = None):
    """
    Send a push notification to a single device via FCM.
    """
    try:
        message = messaging.Message(
            notification=messaging.Notification(
                title=title,
                body=body,
            ),
            data=data or {},
            token=fcm_token,
        )
        response = messaging.send(message)
        return {"success": True, "message_id": response}
    except Exception as e:
        return {"success": False, "error": str(e)}