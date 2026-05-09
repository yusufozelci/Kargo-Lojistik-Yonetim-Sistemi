window.showForgotPasswordForm = function() {
    document.getElementById('loginForm').classList.add('hidden');
    document.getElementById('resetPasswordForm').classList.add('hidden');
    document.getElementById('forgotPasswordForm').classList.remove('hidden');
};

window.showLoginForm = function() {
    document.getElementById('forgotPasswordForm').classList.add('hidden');
    document.getElementById('resetPasswordForm').classList.add('hidden');
    document.getElementById('loginForm').classList.remove('hidden');
};

let userEmailForReset = "";

const forgotPasswordForm = document.getElementById('forgotPasswordForm');
if (forgotPasswordForm) {
    forgotPasswordForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('forgotEmail').value;
        const alertBox = document.getElementById('forgotAlert');
        const btn = document.getElementById('forgotSubmitBtn');

        btn.textContent = "Gönderiliyor...";
        btn.disabled = true;

        try {
            const response = await fetch(`/api/users/forgot-password?email=${encodeURIComponent(email)}`, {
                method: 'POST'
            });
            const resultText = await response.text();

            if (!response.ok) throw new Error(resultText || "Bir hata oluştu.");

            userEmailForReset = email;
            document.getElementById('forgotPasswordForm').classList.add('hidden');
            document.getElementById('resetPasswordForm').classList.remove('hidden');

            const resetAlert = document.getElementById('resetAlert');
            resetAlert.className = "alert alert-success";
            resetAlert.textContent = "Doğrulama kodu e-posta adresinize gönderildi.";
            resetAlert.classList.remove('hidden');

        } catch (error) {
            alertBox.className = "alert alert-error";
            alertBox.textContent = error.message;
            alertBox.classList.remove('hidden');
        } finally {
            btn.textContent = "Doğrulama Kodu Gönder";
            btn.disabled = false;
        }
    });
}

const resetPasswordForm = document.getElementById('resetPasswordForm');
if (resetPasswordForm) {
    resetPasswordForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const otp = document.getElementById('resetOtp').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value; // 1. Değeri al
        const alertBox = document.getElementById('resetAlert');

        if (newPassword !== confirmPassword) {
            alertBox.className = "alert alert-error";
            alertBox.textContent = "Hata: Şifreler uyuşmuyor!";
            alertBox.classList.remove('hidden');
            return;
        }

        try {
            const response = await fetch(`/api/users/reset-password?email=${encodeURIComponent(userEmailForReset)}&otp=${encodeURIComponent(otp)}&newPassword=${encodeURIComponent(newPassword)}&confirmPassword=${encodeURIComponent(confirmPassword)}`, {
                method: 'POST'
            });

            const resultText = await response.text();

            if (!response.ok) {
                throw new Error(resultText || "Şifre güncellenemedi.");
            }

            alert("Şifreniz başarıyla güncellendi! Giriş yapabilirsiniz.");
            window.location.href = "/login";

        } catch (error) {
            alertBox.className = "alert alert-error";
            alertBox.textContent = error.message;
            alertBox.classList.remove('hidden');
        }
    });
}