let webSocket = new WebSocket('wss://my.td.fort.ru:8897/');
webSocket.send("world");

var msg = {
    type: "message",
    text: "World",
    date: Date.now()
};

webSocket.send(JSON.stringify(msg));
webSocket.onmessage = function(evt) { /* Должен получить hello world */ };