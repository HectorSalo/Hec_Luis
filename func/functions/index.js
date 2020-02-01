const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.notifyNewMessage = functions.firestore.document('Chats/{chats}/{messageID}/{message}')
	.onCreate((snap, context) => {
		const message = snap.data();
		const receptor = message['receptor'];
		const mensaje = message['mensaje'];
		const estado = message['estadoMensaje'];
		const emisor = message['emisor'];

		if (estado === "Recibiendo") {	
			return admin.firestore().doc('UsuariosChat/' + receptor).get().then(userToken => {
			const token = userToken.get('token');

			if (token !== ""){

			const payload = {
				notification: {
					title: "Tienes un mensaje nuevo",
					body: mensaje
				}, 
				data: {
					receptor: receptor,
					emisor: emisor
				}
			}

				return admin.messaging().sendToDevice(token, payload).then(reponse => {
					reponse.results.forEach((result, index) => {
						const error = result.error;
						if (error) {
							console.error("Error", error);
						} else {
							console.log("Exito", result);
						}
					})
					return result;
				})
			}
			return token;
		})
		}
		
	})
