let stompClient = null;
const ScoreboardArea = document.querySelector('#scoreboard');
// State variables
let token = null;
let name = null;


function connect(event) {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({Authorization:token}, onConnected, onError);
    event.preventDefault();
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/newPlayer', onMessageReceived);
    stompClient.subscribe('/topic/click', onMessageReceived);

    document.querySelector('#gameBoard').classList.remove('hidden');
}

function joinGame() {
    event.preventDefault();
    if (token != null) {
        alert("You are already in game");
        return;
    }
    var roomCode = document.querySelector('#room_code').value.trim();
    var name = document.querySelector('#player_name').value.trim();

    let post = JSON.stringify({player: {name: name}, room_code: roomCode});

    const url = '/join';
    let xhr = new XMLHttpRequest()

    xhr.open('POST', url, true)
    xhr.setRequestHeader('Content-type', 'application/json; charset=UTF-8')
    xhr.send(post);

    xhr.onload = function () {
        if(xhr.status === 200) {
            // get the token from the response's 'token' parameter
            responseBody = JSON.parse(xhr.response);
            token = responseBody.session;
            name = responseBody.name;
            // hide the join game form
            document.querySelector('#joinGameForm').classList.add('hidden');
            // show the game board
            // show login welcome msg
            document.querySelector('#loginWelcomeMsg').innerHTML = 'Welcome, ' + name;
            document.querySelector('#loginWelcomeMsg').classList.remove('hidden');
            // connect to the websocket
            connect();

        } else {
            alert('Error: ' + xhr.status);
        }
    }




}

function gameClick() {

    if(stompClient) {

        stompClient.send("/app/gameplay.click");
    }
    event.preventDefault();

}

function onMessageReceived(payload) {
    // update scoreboard
    // This is an example message from the server:
    // {"id":2,"scores":[{"id":7,"player":{"id":3,"session":"28511eea-3846-4f45-9305-adceed7108e0","name":"Daniel"},"score":4}]}
    var scoreboard = JSON.parse(payload.body);

    ScoreboardArea.innerHTML = '';

    scoreboard.scores.forEach(function (score) {
        var scoreElement = document.createElement('li');
        scoreElement.appendChild(document.createTextNode(score.player.name + ': ' + score.score));
        ScoreboardArea.appendChild(scoreElement);
    } );
}

function onError(error) {
    alert("Could not connect to WebSocket server. Please refresh this page to try again!");
}