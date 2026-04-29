
const registerForm = document.getElementById('registerForm');

if (registerForm) {
    registerForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const fullName = document.querySelector('input[name="fullName"]').value;
        const email = document.querySelector('input[name="email"]').value;
        const password = document.querySelector('input[name="password"]').value;

        try {
            const response = await fetch('/api/users/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fullName: fullName, email: email, password: password, phone: "" })
            });

            if (response.ok) {
                alert('Kayıt başarılı! Lütfen giriş yapın.');
                window.location.href = '/login';
            } else {
                alert('Kayıt başarısız. Lütfen bilgilerinizi kontrol edin.');
            }
        } catch (error) {
            console.error('Hata:', error);
            alert('Sunucuya bağlanılamadı.');
        }
    });
}


const loginForm = document.getElementById('loginForm');

if (loginForm) {
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const email = document.querySelector('input[name="username"]').value;
        const password = document.querySelector('input[name="password"]').value;

        try {
            const response = await fetch('/api/users/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, password: password })
            });

            if (response.ok) {
                const data = await response.json();
                if (data.token) {
                    localStorage.setItem('jwtToken', data.token);
                    alert('Giriş başarılı! Yönetim paneline yönlendiriliyorsunuz.');
                    window.location.href = '/admin-dashboard';
                }
            } else {
                alert('Giriş başarısız. E-posta veya şifrenizi kontrol edin.');
            }
        } catch (error) {
            console.error('Hata:', error);
            alert('Sunucuya bağlanılamadı.');
        }
    });
}
