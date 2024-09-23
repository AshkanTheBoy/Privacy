var stompClient = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('response').innerHTML = '';

}

async function createRoom(){
    let chatName = document.getElementById("chatId");
    let roomNameField = document.getElementById("chatName");
    let expTime = document.getElementById("inputTime")
    let errorField = document.getElementById("invalidFields");
    let response = await fetch(`/api/verifyFields?roomName=${chatName.value}&time=${expTime.value}`);
    if (response.ok){
        response = await fetch(`/api/checkName/${chatName.value}`);
            let roomAvailable = await response.json();
            if (roomAvailable){
                if (!errorField.classList.contains("hidden")){
                    errorField.classList.toggle("hidden");
                }
                roomNameField.textContent = chatName.value;
                connectToRoom();
                submitAddForm();
            } else {
                console.log("This room already exists");
                let div = document.getElementById("left");
                let p = document.createElement('p');
                p.appendChild(document.createTextNode("This room already exists"));
                div.appendChild(p);
            }
    } else {
        if (errorField.classList.contains("hidden")){
            errorField.classList.toggle("hidden");
        }
        console.log("Invalid room name or timer value");
    }
}



async function connect() {
    let chatName = document.getElementById("chatId");
    let roomNameField = document.getElementById("chatName");
    if (chatName.value.length>0){
        let response = await fetch(`/api/checkCapacity/${chatName.value}`);
        let roomNotFull = await response.json();
        console.log(roomNotFull);
        if (roomNotFull){
                submitConnectForm();
//            let response = await fetch(`/api/verifyRoom?roomName=${chatName.value}`);
//            let roomExists = await response.json();
//            if (roomExists){
                roomNameField.textContent = chatName.value;
                connectToRoom();
        }
//            } else {
//
//            }
//
//        } else {
//            rejectConnection();
//            let div = document.getElementById("left");
//            let p = document.createElement('p');
//            p.appendChild(document.createTextNode("This room is full"));
//            div.appendChild(p);
//        }
    } else{
        console.log("Room name cannot be empty");
    }

}

function connectToRoom(){
    var chatId = document.getElementById("chatId");
//    console.log(`/topic/${chatId.value}`);
    var socket = new SockJS(`/chat`);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        setConnected(true);
//        console.log('Connected: ' +' '+ chatId.value +' '+ frame);
//        console.log(`/topic/${chatId.value}`);
        stompClient.subscribe(`/topic/${chatId.value}`, function(payload) {
            showMessageOutput(JSON.parse(payload.body));
        });
    })
}

async function sendMessage() {
    var chatId = document.getElementById("chatName");
    if (chatId.textContent.length>0){
        let textField = document.getElementById('textField');
        var from = document.getElementById('profile').textContent;
        const encodedLogin = encodeURIComponent(from);
        const encodedRoomName = encodeURIComponent(chatId.textContent);
        response = await fetch(`/api/verify?login=${encodedLogin}&roomName=${encodedRoomName}`);
        let responseJson = await response.json();
        if (responseJson){
            var text = textField.value;
            textField.value = '';
            saveMessage(text,from,chatId.textContent);
            stompClient.send(`/app/chat/${chatId.textContent}`, {},
              JSON.stringify({'chatterLogin':from, 'text':text}));
        } else {
            console.log("HTML values do not match with server values");
        }
    } else {
        console.log("Error: cannot find a room value");
    }
}

async function sendTimerValue(value){
    var chatName = document.getElementById("chatName");
    let chatterName = document.getElementById("profile");
    const encodedLogin = encodeURIComponent(chatterName.textContent);
    const encodedRoomName = encodeURIComponent(chatName.textContent);
    response = await fetch(`/api/verify?login=${encodedLogin}&roomName=${encodedRoomName}`);
    let responseJson = await response.json();
//    console.log(responseJson);
    if (responseJson){
        stompClient.send(`/app/chat/${chatName.textContent}`,{},JSON.stringify({'text':value}));
    } else {
        console.log("HTML values do not match with server values");
    }

}

function showMessageOutput(messageOutput) {
    if (messageOutput.from==="System"&&messageOutput.text==="clear"){
        let roomName = document.getElementById("chatName");
        let timer = document.getElementById("timer");
        roomName.textContent = null;
        timer.textContent = null;
        if(stompClient != null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    } else if (messageOutput.from==null){
        let timerField = document.getElementById("timer");
        timerField.textContent = messageOutput.text;
    } else {
        var response = document.getElementById("response");
        var p = document.createElement('p');
        p.appendChild(document.createTextNode(messageOutput.from + ": "
          + messageOutput.text + " (" + messageOutput.time + ")"));
        response.prepend(p);
    }
}

function saveMessage(text,from,chatId){
    fetch('http://localhost:8080/saveMessage',{
        method: "POST",
        headers:{
        'Content-Type':'application/json'},
        body: JSON.stringify({
            "text":`${text}`,
            "chatterLogin":`${from}`,
            "roomName":`${chatId}`
        })
    })
}

async function deleteRoom(){
    let chatName = document.getElementById("chatName");
    let chatterName = document.getElementById("profile");
    const encodedLogin = encodeURIComponent(chatterName.textContent);
    const encodedRoomName = encodeURIComponent(chatName.textContent);
    response = await fetch(`/api/verify?login=${encodedLogin}&roomName=${encodedRoomName}`);
    let responseJson = await response.json();
//    console.log(responseJson);
    if (responseJson){
        stopTimer(chatName.textContent);
        fetch(`/delete/${chatName.textContent}`,{
            method: "DELETE"
        })
        .then(response=>{
            if (response.ok){
                fetch(`/main/${chatterName.textContent}`,{
                    method: "GET"
                });
            }
        });
        stompClient.send(`/app/chat/${chatName.textContent}`, {},
            JSON.stringify({'chatterLogin':"System", 'text':"clear"}));
    } else {
        console.log("HTML values do not match with server values");
    }
}

function rejectConnection(){
    console.log("The room is full");
}

function disconnect() {
    deleteRoom();
}


function submitAddForm(){
    let form = document.getElementById("roomForm");
    const formData = new FormData(form);
    fetch('http://localhost:8080/addRoom',{
        method:"POST",
        body: formData
    })
    .then(response=>{
        if (!response.ok){
        throw new Error("Response is not ok")}
    })
    .then(()=>{
        startScheduledTimer();
    })
}


function submitConnectForm(){
    let form = document.getElementById("roomForm");
//    form.submit();
    const formData = new FormData(form);
    fetch(`/main/connectRoom`,{
        method:"POST",
        body: formData
    })
}

function startScheduledTimer(){
    let roomName = document.getElementById("chatName");
    let inputTime = document.getElementById("inputTime")
    fetch(`/startTimer/${inputTime.value}/${roomName.textContent}`,{
        method:"GET"
    })
}

function stopTimer(roomName){
    fetch(`/stopTimer/${roomName}`,{
        method:"GET"
    })
}

async function logOut(){
    let chatName = document.getElementById("chatName");
    let chatterName = document.getElementById("profile");
    const encodedLogin = encodeURIComponent(chatterName.textContent);
    const encodedRoomName = encodeURIComponent(chatName.textContent);
    response = await fetch(`/api/verifyLogin?login=${encodedLogin}`);
    let responseJson = await response.json();
    if (responseJson){
        if (stompClient!==null){
            stompClient.send(`/app/chat/${chatName.textContent}`, {}, JSON.stringify({'chatterLogin':"System", 'text':"clear"}));
        }
        window.location.href = 'http://localhost:8080/logout';
    }
}
