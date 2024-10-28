// Initialize global variables for WebSocket client and subscription
let stompClient = null;
let subscription = null;
let staticErrors; // Holds static error message elements

// Get references to DOM elements
let chatterName = document.getElementById("profile"); // User's profile name
let roomNameField = document.getElementById("chatName"); // Field for displaying room name
let inputChatName = document.getElementById("roomName"); // Input for room name
let expTime = document.getElementById("inputTime"); // Input for expiration time
let password = document.getElementById("roomPass"); // Input for room password

// Wait for the DOM to fully load
document.addEventListener('DOMContentLoaded', () => {
    // Get all error fields that are initially hidden
    var errorFields = document.getElementsByClassName("hidden");
    staticErrors = Array.from(errorFields); // Convert HTMLCollection to an array
});

// Handle window load event
window.addEventListener('load', async function() {
    // Verify if the user is logged in
    let response = await fetch(`/api/verifyLogin?login=${chatterName.textContent}`);
    if (response.ok) {
        // Check for existing session
        let responseSession =
            await fetch(`/api/checkForSession`, {
                method: "GET"
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text(); // Get the room name from response
            })
            .then(data => {
                roomNameField.textContent = data; // Set room name on the UI
                document.getElementById('response').innerHTML = ''; // Clear previous responses
                connectToRoom(roomNameField.textContent); // Connect to the chat room
                getLastMessages(roomNameField.textContent); // Retrieve last messages in the room
            });
    }
});

// Set the connection state of the buttons based on whether connected or not
function setConnected(connected) {
    document.getElementById('connect').disabled = connected; // Disable connect button if connected
    document.getElementById('disconnect').disabled = !connected; // Enable disconnect button if connected
}

// Function to create a new chat room
async function createRoom() {
    // Verify user login again
    let response = await fetch(`/api/verifyLogin?login=${chatterName.textContent}`);
    if (response.ok) {
        // Check room fields for validity
        let response = await fetch(`/api/verifyFields?roomName=${inputChatName.value}&time=${expTime.value}&password=${password.value}`);
        if (response.ok) {
            // Check if the room name is available
            response = await fetch(`/api/checkName/${inputChatName.value}`);
            let roomAvailable = await response.json();
            if (roomAvailable) {
                // Hide error messages if any are visible
                for (let error of staticErrors) {
                    if (!error.classList.contains("hidden")) {
                        error.classList.toggle("hidden");
                    }
                }
                roomNameField.textContent = inputChatName.value; // Set the room name
                connectToRoom(roomNameField.textContent); // Connect to the room
                submitAddForm(); // Submit the form to add the room
            } else {
                // Show error if the room already exists
                let roomExists = document.getElementById("roomExists");
                for (let error of staticErrors) {
                    if (!error.classList.contains("hidden")) {
                        error.classList.toggle("hidden");
                    }
                }
                if (roomExists.classList.contains("hidden")) {
                    roomExists.classList.toggle("hidden"); // Show room exists error
                }
            }
        } else {
            // Show invalid fields error if validation fails
            for (let error of staticErrors) {
                if (!error.classList.contains("hidden")) {
                    error.classList.toggle("hidden");
                }
            }
            let errorField = document.getElementById("invalidFields");
            if (errorField.classList.contains("hidden")) {
                errorField.classList.toggle("hidden"); // Show invalid fields error
            }
        }
    }
}

// Function to connect to an existing chat room
async function connect() {
    if (inputChatName.value.length > 0) { // Check if room name input is not empty
        let response = await fetch(`/api/verifyLogin?login=${chatterName.textContent}`);
        if (response.ok) {
            // Verify the room name
            let response = await fetch(`/api/verifyRoom?room=${inputChatName.value}`);
            if (response.ok) {
                // Check if the room is not full
                let response = await fetch(`/api/checkCapacity/${inputChatName.value}`);
                let roomNotFull = await response.json();
                if (roomNotFull) {
                    // Verify the room password
                    let response = await fetch(`/api/verifyPassword/${inputChatName.value}/${password.value}`);
                    if (response.ok) {
                        document.getElementById('response').innerHTML = ''; // Clear previous responses
                        roomNameField.textContent = inputChatName.value; // Set the room name
                        connectToRoom(roomNameField.textContent); // Connect to the room
                        submitConnectForm(); // Submit connection form
                        getLastMessages(roomNameField.textContent); // Get last messages in the room
                    } else {
                        // Show incorrect password error
                        for (let error of staticErrors) {
                            if (!error.classList.contains("hidden")) {
                                error.classList.toggle("hidden");
                            }
                        }
                        let errorField = document.getElementById("incorrectPassword");
                        if (errorField.classList.contains("hidden")) {
                            errorField.classList.toggle("hidden"); // Show incorrect password error
                        }
                    }
                } else {
                    // Show room is full error
                    for (let error of staticErrors) {
                        if (!error.classList.contains("hidden")) {
                            error.classList.toggle("hidden");
                        }
                    }
                    let errorField = document.getElementById("roomIsFull");
                    if (errorField.classList.contains("hidden")) {
                        errorField.classList.toggle("hidden"); // Show room is full error
                    }
                }
            } else {
                // Show room does not exist error
                for (let error of staticErrors) {
                    if (!error.classList.contains("hidden")) {
                        error.classList.toggle("hidden");
                    }
                }
                let errorField = document.getElementById("roomNotExists");
                if (errorField.classList.contains("hidden")) {
                    errorField.classList.toggle("hidden"); // Show room does not exist error
                }
            }
        } else {
            // Show invalid fields error if login verification fails
            for (let error of staticErrors) {
                if (!error.classList.contains("hidden")) {
                    error.classList.toggle("hidden");
                }
            }
            let errorField = document.getElementById("invalidFields");
            if (errorField.classList.contains("hidden")) {
                errorField.classList.toggle("hidden"); // Show invalid fields error
            }
        }
    }
}

// Function to connect to a chat room using WebSockets
function connectToRoom(roomName) {
    var socket = new SockJS(`/chat`); // Create a WebSocket connection
    stompClient = Stomp.over(socket); // Use STOMP protocol over the WebSocket
    stompClient.connect({}, function(frame) {
        setConnected(true); // Update connection status
        disableButtons(); // Disable buttons related to connection
        enableDiscButton(); // Enable disconnect button
        // Subscribe to the chat topic for receiving messages
        subscription = stompClient.subscribe(`/topic/${roomName}`, function(payload) {
            showMessageOutput(JSON.parse(payload.body)); // Show received messages
        });
    });
}

// Function to send a message in the chat
async function sendMessage() {
    if (roomNameField.textContent.length > 0) { // Check if connected to a room
        let textField = document.getElementById('textField'); // Get message input field
        let response = await doFieldsAlignWithSession(chatterName.textContent, roomNameField.textContent); // Verify session
        if (response.ok) {
            var text = textField.value; // Get message text
            textField.value = ''; // Clear the input field
            saveMessage(text, chatterName.textContent, roomNameField.textContent); // Save message
            // Send the message via WebSocket
            stompClient.send(`/app/chat/${roomNameField.textContent}`, {},
              JSON.stringify({'chatterLogin': chatterName.textContent, 'text': text}));
        } else {
            console.log("HTML values do not match with server values.\nReload the page"); // Log error if session mismatch
        }
    } else {
        console.log("Error: you're not connected to any room"); // Log error if not connected
    }
}

// Function to verify session fields align with the current session
async function doFieldsAlignWithSession(chatterLogin, roomName) {
    const encodedLogin = encodeURIComponent(chatterLogin); // Encode login for URL
    const encodedRoomName = encodeURIComponent(roomName); // Encode room name for URL
    response = await fetch(`/api/verify?login=${encodedLogin}&roomName=${encodedRoomName}`); // Verify session
    return response; // Return the response
}

// Function to handle displaying received messages
async function showMessageOutput(messageOutput) {
    if (messageOutput.from === "System" && messageOutput.text === "clear") {
        let timer = document.getElementById("timer");
        //await deleteRoom(); // Delete the room on system message
        roomNameField.textContent = null; // Clear room name display
        timer.textContent = null; // Clear timer display
        document.getElementById('response').innerHTML = ''; // Clear previous messages
        if (stompClient != null) {
            subscription.unsubscribe(); // Unsubscribe from the chat topic
        }
        setConnected(false); // Update connection status
        disableDiscButton(); // Disable disconnect button
        enableButtons();
        console.log("Disconnected"); // Log disconnection
    } else if (messageOutput.from == null) {
        let timerField = document.getElementById("timer");
        timerField.textContent = messageOutput.text; // Update timer display
    } else {
        var response = document.getElementById("response"); // Get response display area
        var p = document.createElement('p'); // Create new paragraph for message
        p.appendChild(document.createTextNode("(" + messageOutput.time + ")|" + messageOutput.from + ": " + messageOutput.text)); // Format message
        response.prepend(p); // Add new message to the top of the list
    }
}

// Function to get last messages from the server for the chat room
async function getLastMessages(roomName) {
    let responseF = await fetch(`/api/getMessages/${roomName}`, {
        method: "GET"
    });
    let json = await responseF.json(); // Parse JSON response
    for (let m of json) {
        console.log(m.sendingTime + "|" + m.chatterLogin + "|" + m.text); // Log each message to console
    }
    for (let message of json) {
        var response = document.getElementById("response"); // Get response display area
        var p = document.createElement('p'); // Create new paragraph for message
        p.appendChild(document.createTextNode("(" + message.sendingTime + ")|" + message.chatterLogin + ": " + message.text)); // Format message
        response.append(p); // Add message to the display
    }
}

// Function to save a message to the server
function saveMessage(text, from, roomName) {
    fetch('http://localhost:8080/saveMessage', {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            "text": `${text}`,
            "chatterLogin": `${from}`,
            "roomName": `${roomName}`
        })
    });
}

// Function to delete the chat room
async function deleteRoom() {
    let response = await doFieldsAlignWithSession(chatterName.textContent, roomNameField.textContent); // Verify session
    if (response.ok) {
        stopTimer(roomNameField.textContent); // Stop any associated timer
        fetch(`/main/${chatterName.textContent}`, {
            method: "GET" // Redirect to main after deletion
        });
//        fetch(`/delete/${roomNameField.textContent}`, {
//            method: "DELETE"
//        })
//        .then(response => {
//            if (response.ok) {
//                fetch(`/main/${chatterName.textContent}`, {
//                    method: "GET" // Redirect to main after deletion
//                });
//            }
//        });
    } else {
        console.log("Error: either the request is not authorized or the room was already deleted"); // Log error
    }
}

// Function to disconnect from the chat room
async function disconnect() {
    document.getElementById('response').innerHTML = ''; // Clear response display
    await deleteRoom();
    // Send a system message to clear the room
    stompClient.send(`/app/chat/${roomNameField.textContent}`, {},
        JSON.stringify({'chatterLogin': "System", 'text': "clear"}));

}

// Function to submit the form to add a room
function submitAddForm() {
    let form = document.getElementById("roomForm"); // Get the room form
    const formData = new FormData(form); // Create FormData object from the form
    fetch('http://localhost:8080/addRoom', {
        method: "POST",
        body: formData // Submit the form data
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Response is not ok");
        }
    })
    .then(() => {
        startScheduledTimer(); // Start timer for the room
    });
}

// Function to submit the connection form
function submitConnectForm() {
    let form = document.getElementById("roomForm"); // Get the room form
    const formData = new FormData(form); // Create FormData object from the form
    fetch(`/main/connectRoom`, {
        method: "POST",
        body: formData // Submit the form data
    });
}

// Function to start the scheduled timer for the room
function startScheduledTimer() {
    fetch(`/startTimer/${inputTime.value}/${roomNameField.textContent}`, {
        method: "GET"
    });
}

// Function to stop the timer for the room
function stopTimer(roomName) {
    fetch(`/stopTimer/${roomName}`, {
        method: "GET"
    });
}

// Function to log out the user
async function logOut() {
    let response = await fetch(`/api/verifyLogin?login=${chatterName.textContent}`); // Verify user login session
    if (response.ok) {
        window.location.href = 'http://localhost:8080/logout'; // Redirect to logout
    }

}

// Function to verify if a room exists
async function verifyRoom(roomName) {
    return await fetch(`/api/verifyRoom?room=${roomName}`); // Return the fetch promise for verification
}
