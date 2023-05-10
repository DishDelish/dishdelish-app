import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
admin.initializeApp();

export const sendNotification = functions.database
  .ref("/notifications/{notificationId}")
  .onCreate(async (data, context) => {
    const notificationId = context.params.notificationId;
    const notif = data.val();
    const getDeviceTokensPromise = admin.database()
      .ref(`/users/${notif.receiver}/tokens`).once("value");

    const results = await Promise.all(
      [
        getDeviceTokensPromise,
      ]);
    const tokensSnapshot = results[0];

    if (!tokensSnapshot.hasChildren()) {
      return functions.logger.log(
        "There are no notification tokens to send to."
      );
    }

    const tokens = Object.keys(tokensSnapshot.val());
    const payload: admin.messaging.MulticastMessage = {
      notification: {
        title: `${notif.title}`,
        body: `${notif.body}`,
      },
      tokens: tokens,
    };

    if ("data" in notif) {
      payload.data = notif.data;
    }

    const response = await admin.messaging().sendEachForMulticast(payload);

    const cleanup: Promise<void>[] = [];
    response.responses.forEach((result, index) => {
      const error = result.error;
      if (error) {
        functions.logger.error(
          "Failure sending notification to",
          tokens[index],
          error
        );
        if (error.code === "messaging/invalid-registration-token" ||
          error.code === "messaging/registration-token-not-registered") {
          cleanup.push(tokensSnapshot.ref.child(tokens[index])
            .remove());
        }
      }
    });
    cleanup.push(admin.database().ref(`/notifications/${notificationId}`)
      .child("sent").set(true));
    return Promise.all(cleanup);
  });
