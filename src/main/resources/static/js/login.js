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
            const loginResponse = await fetch('https://team4project.site/user/login', {
                method: 'POST',
                credentials : 'include',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!loginResponse.ok) {
                throw new Error('Login failed');
            } else location.href ="https://team4project.site/team4/home"

        } catch (error) {
            console.error('Error:', error);
        }
    });
});

document.getElementById('signup-button').addEventListener('click', function() {
    location.href ="https://team4project.site/team4/signup"
});
