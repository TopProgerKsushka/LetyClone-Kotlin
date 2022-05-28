function showInterlayer() {
    const il = document.getElementById("interlayer");
    il.style.display = "flex";
}

function hideInterlayer() {
    const il = document.getElementById("interlayer");
    for (const child of il.children) {
        child.style.display = "none";
    }
    il.style.display = "none";
}

function showLoginForm() {
    hideInterlayer();
    showInterlayer();
    const lf = document.getElementById("login-form");
    lf.style.display = "flex";
}

function showRegisterForm() {
    hideInterlayer();
    showInterlayer();
    const rf = document.getElementById("register-form");
    rf.style.display = "flex";
}

function showMessageBox(message) {
    hideInterlayer();
    showInterlayer();
    const mb = document.getElementById("message-box");
    const text = mb.querySelector("#message-box-text");
    text.innerText = message;
    mb.style.display = "flex";
}

function askLogin() {
    showMessageBox("Сначала войди или зарегистрируйся");
}

const il = document.getElementById("interlayer");
il.addEventListener("click", function (event) {
    if (event.target.id === "interlayer") {
        hideInterlayer();
    }
});
