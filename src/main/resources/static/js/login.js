document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');

    loginForm.addEventListener('submit', async function(event) {
        event.preventDefault(); // 폼의 기본 제출을 막음

        const formData = new FormData(loginForm);
        const data = {
            userEmail: formData.get('userEmail'),
            userPassword: formData.get('userPassword')
        };

        try {
            const loginResponse = await fetch('http://localhost:8090/user/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!loginResponse.ok) {
                throw new Error('Login failed');
            } else location.href ="http://localhost:8090/team4/home"

        } catch (error) {
            console.error('Error:', error);
        }
    });
});

document.getElementById('signup-button').addEventListener('click', function() {
    location.href ="http://localhost:8090/team4/signup"
});