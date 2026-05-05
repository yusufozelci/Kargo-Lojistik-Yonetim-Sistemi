const params = new URLSearchParams(window.location.search);

const errorAlert = document.getElementById("errorAlert");
const logoutAlert = document.getElementById("logoutAlert");

if (params.get("error") === "true") {
    errorAlert.textContent = "E-posta veya şifre hatalı. Lütfen tekrar dene.";
    errorAlert.style.display = "block";
}

if (params.has("unauthorized")) {
    errorAlert.textContent = "Bu panele sadece admin kullanıcıları giriş yapabilir.";
    errorAlert.style.display = "block";
}

if (params.has("logout")) {
    logoutAlert.style.display = "block";
}