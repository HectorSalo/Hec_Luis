const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

//exports.helloWorld = functions.https.onRequest((request, response) => {
//response.send("Hello from Firebase!");
//});

const admin = require('firebase-admin');
admin.initializeApp();


exports.notifyNewMessage = functions.firestore
    .document('Chats/{chats}/{messageCollection}/{messageID}')
    .onCreate((snap, context) => {
      
      const message = snap.data();
      
      const recepientID = message['receptor'];
      const content = message['mensaje'];

      return admin.firestore().doc('UsuariosChat/' + recepientID).get().then(userToken => {
      	const token = userToken.get('token')

      	const payload = {
      	notification: {
      		title: "sent you a message", 
      		body: content,
      		clickAction: "HomeActivity"	
      	  }
        };

        return admin.messaging().sendtoDevice(token, payload).then(response => {

        	response.results.forEach((result, index) => {
        		const error = result.error;
        		if (error){
        			console.log("Error", error);
        		} else {
        			console.log("Envio exitoso", response);
        		}
        	})

        	return response;
        	
        })
      })   
    
  })
      
   
