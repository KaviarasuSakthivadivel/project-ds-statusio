let stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    let current_user = $("#name").val();
    window.current_user = current_user
    const socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/notifications', function (greeting) {
            showGreeting(JSON.parse(greeting.body));
        });
    });
}

function disconnect() {
    window.current_user = null;
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    let current_user = $("#name").val();
    window.current_user = current_user
    stompClient.send("/consumer/connect", {}, JSON.stringify({'email': current_user}));
}

function showGreeting(message) {
    if($("#greetings tr").length === 0) {
        $("#greetings").append("<tr><td>Timestamp</td><td>Website Name</td><td>Website Url</td><td>Status</td></td></tr>");
    }
    if(message.email == window.current_user) {
        let status = "Up";
        if(message.status == 1) {
            status = "Down";
        }
        $("#greetings").append("<tr><td>" + message.date + "</td><td>"
            +  message.websiteName  + "</td><td>" + message.websiteUrl + "</td><td>" + status + "</td></tr>");
    }

}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});


function fetchdata() {
    // if(window.current_user != undefined) {
    //     $.ajax({
    //         url: '/notifications/',
    //         type: 'GET',
    //         success: function(response) {
    //             console.log(response);
    //             for(let i = 0; i < response.length; i++) {
    //                 let obj = response[i];
    //                 showGreeting(obj);
    //             }
    //         }
    //     });
    // }
}

$(document).ready(function(){
    setInterval(fetchdata,1000);
});