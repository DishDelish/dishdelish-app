import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
admin.initializeApp();

// // Start writing functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

export const sendNotification = functions.database
  .ref("/notifications/{notificationId}")
  .onWrite(async (change, context) => {
    const notificationId = context.params.notificationId;
    if (change.after.val() && !change.after.val().sent) {
      const notif = change.after.val();
      const getDeviceTokensPromise = admin.database()
        .ref(`/users/${notif.receiver}/tokens`).once("value");
      const getSenderProfilePromise = admin.database()
        .ref(`/users/${notif.sender}`).once("value");

      const results = await Promise.all(
        [
          getDeviceTokensPromise,
          getSenderProfilePromise,
        ]);
      const tokensSnapshot = results[0];
      const sender = results[1].val();

      if (!tokensSnapshot.hasChildren()) {
        return functions.logger.log(
          "There are no notification tokens to send to."
        );
      }

      const tokens = Object.keys(tokensSnapshot.val());
      const payload = {
        notification: {
          title: `${sender.displayName} is interested in` +
            ` ${notif.ingredient.ingredient}`,
          body: `${sender.displayName} is interested in` +
            ` ${notif.ingredient.unit.value} ${notif.ingredient.unit.info}` +
            ` ${notif.ingredient.ingredient}. Answer him now!`,
        },
        tokens: tokens,
      };

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
    }
  });
