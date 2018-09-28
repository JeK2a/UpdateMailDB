let webSocket;
let messages = document.getElementById("messages");

function openSocket() {
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
        writeResponse("WebSocket is already opened.");
        return;
    }

    webSocket = new WebSocket("wss://localhost:8080/EchoChamber/echo");

    /**
     * Binds functions to the listeners for the websocket.
     */
    webSocket.onopen = function(event) {
        if (event.data === undefined) {
            return;
        }

        writeResponse(event.data);
    };

    webSocket.onmessage = function(event) {
        writeResponse(event.data);
    };

    webSocket.onclose = function(event) {
        writeResponse("Connection closed");
    };
}

/**
 * Sends the value of the text input to the server
 */
function send() {
    let text = document.getElementById("messageinput").value;
    webSocket.send(text);
}

function closeSocket() {
    webSocket.close();
}

function writeResponse(text) {
    messages.innerHTML += "<br/>" + text;
}