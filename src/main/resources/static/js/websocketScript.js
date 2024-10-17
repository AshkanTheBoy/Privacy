var stompClient = null;
let subscription = null;
let staticErrors;

//window.addEventListener('beforeunload', () => {
//        let chatName = document.getElementById("chatName");
//    //    console.log(chatName);
//    //    let response = verifyRoom(chatName.textContent);
//    //    if (response.ok){
//    //
//    //    }
//        fetch(`/decrementRoomSlots/${chatName.textContent}`);
//        console.log("qwe");
//    });

document.addEventListener('DOMContentLoaded', () => {
    var errorFields = document.getElementsByClassName("hidden");
    staticErrors = Array.from(errorFields);
//    console.log('staticErrors:', staticErrors);
});

window.addEventListener('load', async function() {
    let chatName = document.getElementById("chatId");
    let roomNameField = document.getElementById("chatName");
    let responseSession =
        await fetch(`/api/checkForSession`,{
            method: "GET"
        })
        .then(response=>{
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data=>{
            roomNameField.textContent=data;
            document.getElementById('response').innerHTML = '';
            //        roomNameField.textContent = chatName.value;
            var socket = new SockJS(`/chat`);
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                setConnected(true);
                enableDiscButton();
                disableButtons()
                subscription = stompClient.subscribe(`/topic/${roomNameField.textContent}`, function(payload) {
                    showMessageOutput(JSON.parse(payload.body));
                });
            })
    //        connectToRoom();
    //        submitConnectForm();
            getLastMessages(roomNameField.textContent);
        })
//    connect();
    //if (responseSession.ok){

    //}
});

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;

}

async function createRoom(){
    let chatName = document.getElementById("chatId");
    let roomNameField = document.getElementById("chatName");
    let expTime = document.getElementById("inputTime")
    let password = document.getElementById("roomPass");
    let response = await fetch(`/api/verifyFields?roomName=${chatName.value}&time=${expTime.value}&password=${password.value}`);
    if (response.ok){
        response = await fetch(`/api/checkName/${chatName.value}`);
            let roomAvailable = await response.json();
            if (roomAvailable){
                for (let error of staticErrors){
                    if (!error.classList.contains("hidden")){
                        error.classList.toggle("hidden");
                    }
                }
                roomNameField.textContent = chatName.value;
                connectToRoom();
                submitAddForm();
            } else {
                let roomExists = document.getElementById("roomExists");
                for (let error of staticErrors){
                    if (!error.classList.contains("hidden")){
                        error.classList.toggle("hidden");
                    }
                }
                if (roomExists.classList.contains("hidden")){
                    roomExists.classList.toggle("hidden");
                }
            }
    } else {
        for (let error of staticErrors){
            if (!error.classList.contains("hidden")){
                error.classList.toggle("hidden");
            }
        }
        let errorField = document.getElementById("invalidFields");
        if (errorField.classList.contains("hidden")){
            errorField.classList.toggle("hidden");
        }
    }
}



async function connect() {
    let chatName = document.getElementById("chatId");
    let roomNameField = document.getElementById("chatName");
    if (chatName.value.length>0){
        let response = await fetch(`/api/verifyRoom?room=${chatName.value}`);
//        let roomExists = await response.json();
        if (response.ok){
            let response = await fetch(`/api/checkCapacity/${chatName.value}`);
            let roomNotFull = await response.json();
            if (roomNotFull){
                let password = document.getElementById("roomPass");
                let response = await fetch(`/api/verifyPassword/${chatName.value}/${password.value}`);
                if (response.ok){
                    document.getElementById('response').innerHTML = '';
                    roomNameField.textContent = chatName.value;
                    connectToRoom();
                    submitConnectForm();
                    getLastMessages(roomNameField.textContent);

                } else {
                    for (let error of staticErrors){
                        if (!error.classList.contains("hidden")){
                            error.classList.toggle("hidden");
                        }
                    }
                    let errorField = document.getElementById("incorrectPassword");
                    if (errorField.classList.contains("hidden")){
                        errorField.classList.toggle("hidden");
                    }
                }
            } else {
                for (let error of staticErrors){
                    if (!error.classList.contains("hidden")){
                        error.classList.toggle("hidden");
                    }
                }
                let errorField = document.getElementById("roomIsFull");
                if (errorField.classList.contains("hidden")){
                    errorField.classList.toggle("hidden");
                }
            }
        } else {
            for (let error of staticErrors){
                if (!error.classList.contains("hidden")){
                    error.classList.toggle("hidden");
                }
            }
            let errorField = document.getElementById("roomNotExists");
            if (errorField.classList.contains("hidden")){
                errorField.classList.toggle("hidden");
            }
        }
    } else{
        for (let error of staticErrors){
            if (!error.classList.contains("hidden")){
                error.classList.toggle("hidden");
            }
        }
        let errorField = document.getElementById("invalidFields");
        if (errorField.classList.contains("hidden")){
            errorField.classList.toggle("hidden");
        }
    }

}

function connectToRoom(){
    var chatId = document.getElementById("chatId");
    var socket = new SockJS(`/chat`);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        setConnected(true);
        disableButtons();
        enableDiscButton();
        subscription = stompClient.subscribe(`/topic/${chatId.value}`, function(payload) {
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

// async function sendTimerValue(value){
//     var chatName = document.getElementById("chatName");
//     let chatterName = document.getElementById("profile");
//     const encodedLogin = encodeURIComponent(chatterName.textContent);
//     const encodedRoomName = encodeURIComponent(chatName.textContent);
//     response = await fetch(`/api/verify?login=${encodedLogin}&roomName=${encodedRoomName}`);
//     let responseJson = await response.json();
//     if (responseJson){
//         stompClient.send(`/app/chat/${chatName.textContent}`,{},JSON.stringify({'text':value}));
//     } else {
//         console.log("HTML values do not match with server values");
//     }

// }

async function showMessageOutput(messageOutput) {
    if (messageOutput.from==="System"&&messageOutput.text==="clear"){
        let roomName = document.getElementById("chatName");
        let timer = document.getElementById("timer");
        await deleteRoom();
//        await stopTimer(roomName.textContent);
        roomName.textContent = null;
        timer.textContent = null;
        document.getElementById('response').innerHTML = '';
        if(stompClient != null) {
            subscription.unsubscribe();
//            stompClient.disconnect();
        }
        setConnected(false);
        disableDiscButton();
        console.log("Disconnected");
    } else if (messageOutput.from==null){
        let timerField = document.getElementById("timer");
        timerField.textContent = messageOutput.text;
    } else {
        var response = document.getElementById("response");
        var p = document.createElement('p');
//        p.appendChild(document.createTextNode(messageOutput.from + ": "
//          + messageOutput.text + " (" + messageOutput.time + ")"));
        p.appendChild(document.createTextNode("(" + messageOutput.time + ")|" + messageOutput.from + ": "
                + messageOutput.text));
        response.prepend(p);
    }
}

async function getLastMessages(roomName){
    let responseF = await fetch(`/api/getMessages/${roomName}`,{
        method:"GET"
    });
    let json = await responseF.json();
    console.log(json);
    for (let m of json){
        console.log(m.sendingTime+"|"+m.chatterLogin+"|"+m.text);
    }
    for (let message of json){
        var response = document.getElementById("response");
        var p = document.createElement('p');
        p.appendChild(document.createTextNode("(" + message.sendingTime + ")|" + message.chatterLogin + ": "
                + message.text));
        response.append(p);
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
//        stompClient.send(`/app/chat/${chatName.textContent}`, {},
//            JSON.stringify({'chatterLogin':"System", 'text':"clear"}));
    } else {
        console.log("HTML values do not match with server values");
    }
}

// function rejectConnection(){
//     console.log("The room is full");
// }

async function disconnect() {
//    await deleteRoom();
    document.getElementById('response').innerHTML = '';
    let chatName = document.getElementById("chatName");
    stompClient.send(`/app/chat/${chatName.textContent}`, {},
                JSON.stringify({'chatterLogin':"System", 'text':"clear"}));

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

async function verifyRoom(roomName){
    return await fetch(`/api/verifyRoom?room=${roomName}`);
}
