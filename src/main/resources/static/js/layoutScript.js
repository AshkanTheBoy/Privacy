document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('roomForm');
    form.classList.add('elHidden');
    form.style.display = 'none';
});

let connectButton = document.getElementById("connect");
let createButton = document.getElementById("create");

let form = document.getElementById("roomForm");
let input = document.getElementById("inputTime");
let confirmJoin = document.getElementById("confirmJoin");
let confirmCreate = document.getElementById("confirmCreate");

let disconnectButton = document.getElementById("disconnect");
let logoutButton = document.getElementById("logout");

connectButton.addEventListener("click",function(){
    if (form.style.display ==='none'){
        form.style.display="grid";
        form.classList.toggle("elHidden");
    }
    if (!input.classList.contains("elHidden")){
        input.classList.toggle("elHidden");
    }
    if (confirmJoin.classList.contains("elHidden")){
        confirmJoin.classList.toggle("elHidden");
        if (confirmJoin.disabled===true){
            confirmJoin.disabled = false;
        }
    }
    if (!confirmCreate.classList.contains("elHidden")){
        confirmCreate.classList.toggle("elHidden");
    }
})

createButton.addEventListener("click",function(){
    if (form.style.display ==='none'){
        form.style.display="grid";
        form.classList.toggle("elHidden");
    }
    if (input.classList.contains("elHidden")){
        input.classList.toggle("elHidden");
    }
    if (!confirmJoin.classList.contains("elHidden")){
        confirmJoin.classList.toggle("elHidden");
    }
    if (confirmCreate.classList.contains("elHidden")){
        confirmCreate.classList.toggle("elHidden");
        if (confirmCreate.disabled===true){
            confirmCreate.disabled = false;
        }
    }
})

function addHidingListener(element){
    element.addEventListener("click",function(){
        form.style.display ="none";
        form.classList.toggle("elHidden");
    })
}

function disableButtons(){
    if (form.style.display !=='none'){
        form.style.display="none";
        if (!form.classList.contains("elHidden")){
            form.classList.toggle("elHidden");
        }
    }
    let buttons = [connectButton,createButton,confirmCreate,confirmJoin];
    for (let button of buttons){
        if (!button.disabled){
            button.disabled = true;
        }
    }
}

function enableButtons(){
    form.style.display="grid";
    if (form.classList.contains("elHidden")){
        form.classList.toggle("elHidden");
    }
    let buttons = [connectButton,createButton,confirmCreate,confirmJoin];
    for (let button of buttons){
        if (button.disabled){
            button.disabled = false;
        }
    }
    if (input.classList.contains("elHidden")){
        if (!confirmCreate.classList.contains("elHidden")){
            confirmCreate.classList.toggle("elHidden");
            confirmCreate.disabled=true;
        }
    } else if (!input.classList.contains("elHidden")){
        if (!confirmJoin.classList.contains("elHidden")){
            confirmJoin.classList.toggle("elHidden");
            confirmJoin.disabled=true;
        }
    }
}

function disableDiscButton(){
    if(!disconnectButton.classList.contains("elHidden")){
        disconnectButton.classList.add("elHidden");
    }
    if (!disconnectButton.disabled){
        disconnectButton.disabled=true;
    }
}

function enableDiscButton(){
    if(disconnectButton.classList.contains("elHidden")){
        disconnectButton.classList.remove("elHidden");
    }
    if (disconnectButton.disabled){
        disconnectButton.disabled=false;
    }
}