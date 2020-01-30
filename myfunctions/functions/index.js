const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp();


exports.notifyNewMessage = functions.firestore
    .document('Chats/{chat}/{messageCollection}/{messageID}')
    .onCreate((snap, context) => {
      
      const message = snap.data();

      
      const recepientID = message['receptor'];


      return admin.firestore().doc('UsuariosChat/' + recepientID).get().then(userDoc => {
      	const registrationTokens = userDoc.get('registrationTokens')

      	const notificationBody = message['mensaje'];

      	const payload = {
      		notification: {
      			title: "Tienes un mensaje nuevo.",
      			body: notificationBody, 
      			clickAction: "HomeActivity"
      		}
      	}

      	return admin.messaging().sendToDevice(registrationTokens, payload).then(response => {
      		const stillRegisteredTokens = registrationTokens

      		response.results.forEach((result, index) => {
      			const error = result.error
      			if (error) {
      				const failedRegistrationToken = registrationTokens[index]
      			}
      		})
      	})
      })
      
    });index.js 
