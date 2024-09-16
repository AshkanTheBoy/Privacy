/**
 * Initializes the timer based on the selected radio button.
 * Retrieves the selected timer duration and sets it to the timer field.
 */
function startTimer() {
    // Get all radio buttons with the class "radio"
//    let radios = document.getElementsByClassName("radio");
    let time = document.getElementById("inputTime");
    let input = time.value; // Variable to store the ID(duration) of the selected radio button

    // Loop through all radio buttons to find the checked one
//    for (let radio of radios) {
//        if (radio.checked) {
//            input = radio.id; // Store the ID(duration) of the checked radio button
//            break; // Exit the loop once the checked radio button is found
//        }
//    }

    // Proceed only if a valid input is found
    if (input.length > 0) {
        let timerField = document.getElementById("timer"); // Get the timer field element
        timerField.textContent = input; // Set the timer field to the selected duration
        updateTimer(timerField); // Start the timer update process
    }
}

/**
 * Updates the timer display every second.
 * Decrements the timer values and updates the display.
 * Calls blinkTimer() when the timer reaches "00:00:00".
 * 
 * @param {HTMLElement} timerField - The HTML element where the timer is displayed.
 */
function updateTimer(timerField) {
    // Set an interval to run every second
    let id = setInterval(() => {
        // Split the timer value into hours, minutes, and seconds
        let timer = timerField.textContent.split(":");
        let HMS = [Number(timer[0]), Number(timer[1]), Number(timer[2])];

        // Decrement seconds
        HMS[2]--;
        if (HMS[2] < 0) {
            HMS[1]--; // Decrement minutes if seconds reach -1
            HMS[2] = 59; // Reset seconds to 59
            if (HMS[1] < 0) {
                HMS[0]--; // Decrement hours if minutes reach -1
                HMS[1] = 59; // Reset minutes to 59
            }
        }

        // Format time values to always have two digits
        for (let i = 0; i < HMS.length; i++) {
            if (HMS[i] < 10) {
                HMS[i] = `0${HMS[i]}`; // Prefix with '0' if value is less than 10
            }
        }

        let timerValue = `${HMS[0]}:${HMS[1]}:${HMS[2]}`;

        // Check if the timer has reached "00:00:00"
//        if (HMS[0] === "0-1" && HMS[1] === "59" && HMS[2] === "59") {
          if (timerValue==="00:NaN:NaN"){
            clearInterval(id);
          } else if (timerValue==="0-1:59:59"){
            console.log("YOOOOOOOOOO");
            clearInterval(id); // Stop the timer
//            blinkTimer(timerField); // Start the blinking effect
            deleteRoom();
        } else {
//            timerField.textContent = `${HMS[0]}:${HMS[1]}:${HMS[2]}`; // Update the timer display
            sendTimerValue(timerValue);
        }
    }, 1000); // Interval set to 1 second
}